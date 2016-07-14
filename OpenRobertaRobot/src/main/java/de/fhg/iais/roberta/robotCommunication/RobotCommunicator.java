package de.fhg.iais.roberta.robotCommunication;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fhg.iais.roberta.robotCommunication.RobotCommunicationData.State;
import de.fhg.iais.roberta.util.dbc.Assert;

/**
 * class, that synchronizes the communication between the bricks and the web-app. Thread-safe. See class {@link RobotCommunicationData} for further
 * explanations.<br>
 * <br>
 * The class <b>must</b> be used as a singleton. Use <b>GUICE</b> to enforce that.
 *
 * @author rbudde
 */
public class RobotCommunicator {
    private static final Logger LOG = LoggerFactory.getLogger(RobotCommunicator.class);
    private static final long PUSH_TIMER_INTERVALL = 2000;
    private static final long PUSH_TIMEOUT_INTERVALL = 10000;

    private final Map<String, RobotCommunicationData> allStates = new ConcurrentHashMap<>();

    public RobotCommunicator() {
        Runnable pushTimerThread = new Runnable() {
            @Override
            public void run() {
                pushTimerRunner();
            }
        };
        new Thread(null, pushTimerThread, "PushTimer").start();
        LOG.info("timer thread created");
    }

    /**
     * check the new registration ticket. only used by brickWantsTokenToBeApproved(), extracted for testing
     *
     * @throws assertions for various types of issues
     * @return true if the ticket has been accepted
     */
    public boolean addNewRegistration(RobotCommunicationData newRobotCommunicationData) {
        String token = newRobotCommunicationData.getToken();
        String newIdentificator = newRobotCommunicationData.getRobotIdentificator();
        Assert.isTrue(token != null && newIdentificator != null);
        RobotCommunicationData existingRobotCommunicationData = this.allStates.get(token);
        if ( existingRobotCommunicationData != null ) {
            String existingIdentificator = existingRobotCommunicationData.getRobotIdentificator();
            if ( existingIdentificator == null
                || !existingIdentificator.equals(newIdentificator)
                || existingIdentificator.equals("usb")
                || existingIdentificator.equals("unknown") ) {
                LOG.info("token already used. New token required");
                return false;
            }
        }
        for ( String storedToken : this.allStates.keySet() ) {
            RobotCommunicationData storedState = this.allStates.get(storedToken);
            if ( newIdentificator.equals(storedState.getRobotIdentificator()) && !newIdentificator.equals("usb") && !newIdentificator.equals("unknown") ) {
                LOG.error("Token approval request for robot [" + newIdentificator + "], but an old request is pending. Old request aborted.");
                storedState.abortPush(); // notifyAll() executed
                this.allStates.remove(storedToken);
            }
        }
        this.allStates.put(token, newRobotCommunicationData);
        return true;
    }

    public boolean brickWantsTokenToBeApproved(RobotCommunicationData registration) {
        if(!addNewRegistration(registration)) {
          return false;
        }
        return registration.robotTokenAgreementRequest(); // this will freeze the request until another issues a notifyAll()
    }

    /**
     * called by the robot to inform the server about the fact, that the robot is still connected and ready to get a command pushed to it
     *
     * @param token identifying the robot
     * @param batteryvoltage changes over time
     * @param nepoExitValue the return value of the last user program, that was executed. Is 0 if no exitvalue is provided by the robot system.
     * @return a legal command for the robot (in 99% a "repeat" :)
     */
    public String brickWaitsForAServerPush(String token, String batteryvoltage, int nepoExitValue) {
        RobotCommunicationData state = getState(token);
        if ( state != null ) {
            state.setBattery(batteryvoltage);
            state.setNepoExitValue(nepoExitValue);
            state.robotHasSentAPushRequest();
            return state.getCommand();
        } else {
            LOG.error("a push request from a robot arrived, but no matching state was found in the server - we provoke a server error");
            return null;
        }
    }

    public boolean aTokenAgreementWasSent(String token) {
        RobotCommunicationData state = this.allStates.get(token);
        if ( state == null ) {
            LOG.info("token " + token + " is not waited for. Typing error of the user?");
            return false;
        } else {
            // todo: version check!
            state.userApprovedTheRobotToken();
            LOG.info("token " + token + " is approved by a user.");
            return true;
        }
    }

    public void disconnect(String token) {
        RobotCommunicationData state = this.allStates.get(token);
        if ( state == null ) {
            LOG.info("token " + token + " is not waited for. Ok.");
        } else {
            state.abortPush(); // notifyAll() executed
            this.allStates.remove(token);
            LOG.info("Robot [" + state.getRobotIdentificator() + "] token " + token + " disconnected.");
        }
    }

    public boolean theRunButtonWasPressed(String token, String programName) {
        RobotCommunicationData state = getState(token);
        return state.runButtonPressed(programName);
    }

    public boolean firmwareUpdateRequested(String token) {
        RobotCommunicationData state = getState(token);
        return state.firmwareUpdate();
    }

    public RobotCommunicationData getState(String token) {
        RobotCommunicationData state = this.allStates.get(token);
        //        if ( state == null ) {
        //            LOG.error("a communication state for token " + token + " is created. THIS IS A TEMPORARY FIX"); // TODO: make brick comm more robust
        //            state = new BrickCommunicationData(token, "robot-id-generated-" + new Date().getTime(), "robot-name-generated", null);
        //            this.allStates.put(token, state);
        //        }
        return state;
    }

    private void pushTimerRunner() {
        while ( true ) {
            try {
                Thread.sleep(PUSH_TIMER_INTERVALL);
            } catch ( InterruptedException e ) {
                // OK
            }
            for ( RobotCommunicationData state : this.allStates.values() ) {
                if ( state.getState() == State.ROBOT_WAITING_FOR_PUSH_FROM_SERVER && state.getElapsedMsecOfStartOfLastRequest() > PUSH_TIMEOUT_INTERVALL ) {
                    state.terminatePushAndRequestNextPush();
                }
            }
        }
    }

}
