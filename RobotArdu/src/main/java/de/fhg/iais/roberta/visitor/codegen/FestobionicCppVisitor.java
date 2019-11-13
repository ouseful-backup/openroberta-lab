package de.fhg.iais.roberta.visitor.codegen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import de.fhg.iais.roberta.bean.CodeGeneratorSetupBean;
import de.fhg.iais.roberta.bean.UsedHardwareBean;
import de.fhg.iais.roberta.components.Category;
import de.fhg.iais.roberta.components.ConfigurationAst;
import de.fhg.iais.roberta.components.ConfigurationComponent;
import de.fhg.iais.roberta.components.UsedSensor;
import de.fhg.iais.roberta.mode.action.MotorMoveMode;
import de.fhg.iais.roberta.syntax.BlocklyConstants;
import de.fhg.iais.roberta.syntax.Phrase;
import de.fhg.iais.roberta.syntax.SC;
import de.fhg.iais.roberta.syntax.action.display.ClearDisplayAction;
import de.fhg.iais.roberta.syntax.action.display.ShowTextAction;
import de.fhg.iais.roberta.syntax.action.generic.PinWriteValueAction;
import de.fhg.iais.roberta.syntax.action.light.LightAction;
import de.fhg.iais.roberta.syntax.action.light.LightStatusAction;
import de.fhg.iais.roberta.syntax.action.motor.MotorOnAction;
import de.fhg.iais.roberta.syntax.action.sound.ToneAction;
import de.fhg.iais.roberta.syntax.actors.arduino.RelayAction;
import de.fhg.iais.roberta.syntax.lang.blocksequence.MainTask;
import de.fhg.iais.roberta.syntax.lang.expr.ColorConst;
import de.fhg.iais.roberta.syntax.lang.expr.Expr;
import de.fhg.iais.roberta.syntax.lang.expr.RgbColor;
import de.fhg.iais.roberta.syntax.lang.expr.Var;
import de.fhg.iais.roberta.syntax.sensor.generic.DropSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.HumiditySensor;
import de.fhg.iais.roberta.syntax.sensor.generic.InfraredSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.KeysSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.LightSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.MoistureSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.MotionSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.PinGetValueSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.PulseSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.RfidSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.TemperatureSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.UltrasonicSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.VoltageSensor;
import de.fhg.iais.roberta.util.dbc.DbcException;
import de.fhg.iais.roberta.visitor.IVisitor;
import de.fhg.iais.roberta.visitor.hardware.IArduinoVisitor;
import de.fhg.iais.roberta.visitor.hardware.IFestobionicVisitor;

/**
 * This class is implementing {@link IVisitor}. All methods are implemented and they append a human-readable C representation of a phrase to a StringBuilder.
 * <b>This representation is correct C code for Arduino.</b> <br>
 */
public final class FestobionicCppVisitor extends AbstractCommonArduinoCppVisitor implements IFestobionicVisitor<Void> {

    /**
     * Initialize the C++ code generator visitor.
     *
     * @param phrases to generate the code from
     */
    public FestobionicCppVisitor(
        UsedHardwareBean usedHardwareBean,
        CodeGeneratorSetupBean codeGeneratorSetupBean,
        ConfigurationAst brickConfiguration,
        ArrayList<ArrayList<Phrase<Void>>> phrases) {
        super(usedHardwareBean, codeGeneratorSetupBean, brickConfiguration, phrases);

    }

    @Override
    public Void visitLightAction(LightAction<Void> lightAction) {
        if ( !lightAction.getMode().toString().equals(BlocklyConstants.DEFAULT) ) {
            this.sb.append("digitalWrite(_led_" + lightAction.getPort() + ", " + lightAction.getMode().getValues()[0] + ");");
        } else {
            if ( lightAction.getRgbLedColor().getClass().equals(ColorConst.class) ) {
                String hexValue = ((ColorConst<Void>) lightAction.getRgbLedColor()).getHexValueAsString();
                hexValue = hexValue.split("#")[1];
                int R = Integer.decode("0x" + hexValue.substring(0, 2));
                int G = Integer.decode("0x" + hexValue.substring(2, 4));
                int B = Integer.decode("0x" + hexValue.substring(4, 6));
                Map<String, Integer> colorConstChannels = new HashMap<>();
                colorConstChannels.put("red", R);
                colorConstChannels.put("green", G);
                colorConstChannels.put("blue", B);
                colorConstChannels.forEach((k, v) -> {
                    this.sb.append("analogWrite(_led_");
                    this.sb.append(k);
                    this.sb.append("_");
                    this.sb.append(lightAction.getPort());
                    this.sb.append(", ");
                    this.sb.append(v);
                    this.sb.append(");");
                    nlIndent();
                });
                return null;
            }
            if ( lightAction.getRgbLedColor().getClass().equals(Var.class) ) {
                String tempVarName = "___" + ((Var<Void>) lightAction.getRgbLedColor()).getValue();
                this.sb.append("analogWrite(_led_red_" + lightAction.getPort() + ", RCHANNEL(");
                this.sb.append(tempVarName);
                this.sb.append("));");
                nlIndent();
                this.sb.append("analogWrite(_led_green_" + lightAction.getPort() + ", GCHANNEL(");
                this.sb.append(tempVarName);
                this.sb.append("));");
                nlIndent();
                this.sb.append("analogWrite(_led_blue_" + lightAction.getPort() + ", BCHANNEL(");
                this.sb.append(tempVarName);
                this.sb.append("));");
                nlIndent();
                return null;
            }
            Map<String, Expr<Void>> Channels = new HashMap<>();
            Channels.put("red", ((RgbColor<Void>) lightAction.getRgbLedColor()).getR());
            Channels.put("green", ((RgbColor<Void>) lightAction.getRgbLedColor()).getG());
            Channels.put("blue", ((RgbColor<Void>) lightAction.getRgbLedColor()).getB());
            Channels.forEach((k, v) -> {
                this.sb.append("analogWrite(_led_" + k + "_" + lightAction.getPort() + ", ");
                v.accept(this);
                this.sb.append(");");
                nlIndent();
            });
        }
        return null;
    }

    @Override
    public Void visitLightStatusAction(LightStatusAction<Void> lightStatusAction) {
        String[] colors =
            {
                "red",
                "green",
                "blue"
            };
        for ( int i = 0; i < 3; i++ ) {
            this.sb.append("analogWrite(_led_" + colors[i] + "_" + lightStatusAction.getPort() + ", 0);");
            nlIndent();
        }
        return null;
    }

    @Override
    public Void visitMotorOnAction(MotorOnAction<Void> motorOnAction) {
        boolean step = motorOnAction.getParam().getDuration() != null;
        if ( step ) {//step motor
            this.sb.append("Motor_" + motorOnAction.getUserDefinedPort() + ".setSpeed(");
            motorOnAction.getParam().getSpeed().accept(this);
            this.sb.append(");");
            nlIndent();
            this.sb.append("Motor_" + motorOnAction.getUserDefinedPort() + ".step(_SPU_" + motorOnAction.getUserDefinedPort() + "*(");
            motorOnAction.getDurationValue().accept(this);
            this.sb.append(")");
            if ( motorOnAction.getDurationMode().equals(MotorMoveMode.DEGREE) ) {
                this.sb.append("/360");
            }
            this.sb.append(");");
        } else {//servo motor
            this.sb.append("_servo_" + motorOnAction.getUserDefinedPort() + ".write(");
            motorOnAction.getParam().getSpeed().accept(this);
            this.sb.append(");");
        }
        return null;
    }

    @Override
    public Void visitMainTask(MainTask<Void> mainTask) {

        mainTask.getVariables().accept(this);
        nlIndent();
        generateConfigurationVariables();
        if ( this.usedHardwareBean.isSensorUsed(SC.TIMER) ) {
            this.sb.append("unsigned long __time = millis();");
            nlIndent();
        }
        long numberConf =
            this.programPhrases
                .stream()
                .filter(phrase -> phrase.getKind().getCategory() == Category.METHOD && !phrase.getKind().hasName("METHOD_CALL"))
                .count();
        if ( (this.configuration.getConfigurationComponents().isEmpty() || this.usedHardwareBean.isSensorUsed(SC.TIMER)) && numberConf == 0 ) {
            nlIndent();
        }
        generateUserDefinedMethods();
        if ( numberConf != 0 ) {
            nlIndent();
        }
        this.sb.append("void setup()");
        nlIndent();
        this.sb.append("{");
        incrIndentation();
        nlIndent();
        this.sb.append("Serial.begin(9600); ");
        nlIndent();
        generateConfigurationSetup();
        generateUsedVars();
        this.sb.delete(this.sb.lastIndexOf("\n"), this.sb.length());
        decrIndentation();
        nlIndent();
        this.sb.append("}");
        nlIndent();
        return null;
    }

    @Override
    protected void generateProgramPrefix(boolean withWrapping) {
        if ( !withWrapping ) {
            return;
        } else {
            decrIndentation();
        }
        this.sb.append("// This file is automatically generated by the Open Roberta Lab.");
        nlIndent();
        nlIndent();
        this.sb.append("#include <math.h>");
        nlIndent();
        LinkedHashSet<String> headerFiles = new LinkedHashSet<>();
        for ( ConfigurationComponent usedConfigurationBlock : this.configuration.getConfigurationComponentsValues() ) {
            switch ( usedConfigurationBlock.getComponentType() ) {
                case SC.SERVOMOTOR:
                    headerFiles.add("#include <ESP32_Servo.h>");
                    break;
                case SC.LED:
                    break;
                default:
                    throw new DbcException("Sensor is not supported: " + usedConfigurationBlock.getComponentType());
            }
        }
        for ( String header : headerFiles ) {
            this.sb.append(header);
            nlIndent();
        }
        this.sb.append("#include <RobertaFunctions.h>   // Open Roberta library");
        nlIndent();
        if ( this.usedHardwareBean.isListsUsed() ) {
            this.sb.append("#include <ArduinoSTL.h>");
            nlIndent();
            this.sb.append("#include <list>");
            nlIndent();
        }
//        this.sb.append("#include <NEPODefs.h>");
        nlIndent();
        nlIndent();
        this.sb.append("RobertaFunctions rob;");
    }

    @Override
    protected void generateProgramSuffix(boolean withWrapping) {
        // nothing to do because the arduino loop closes the program
    }

    private void generateConfigurationSetup() {
        for ( ConfigurationComponent usedConfigurationBlock : this.configuration.getConfigurationComponentsValues() ) {
            switch ( usedConfigurationBlock.getComponentType() ) {
                case SC.LED:
                    this.sb.append("pinMode(_led_" + usedConfigurationBlock.getUserDefinedPortName() + ", OUTPUT);");
                    nlIndent();
                    break;
                case SC.SERVOMOTOR:
                    this.sb
                        .append("_servo_" + usedConfigurationBlock.getUserDefinedPortName() + ".attach(" + usedConfigurationBlock.getProperty(SC.PULSE) + ");");
                    nlIndent();
                    break;
                default:
                    throw new DbcException("Sensor is not supported: " + usedConfigurationBlock.getComponentType());
            }
        }
    }

    private void generateConfigurationVariables() {
        for ( ConfigurationComponent cc : this.configuration.getConfigurationComponentsValues() ) {
            String blockName = cc.getUserDefinedPortName();
            switch ( cc.getComponentType() ) {
                case SC.LED:
                    this.sb.append("int _led_" + blockName + " = ").append(cc.getProperty("INPUT")).append(";");
                    nlIndent();
                    break;
                case SC.SERVOMOTOR:
                    this.sb.append("Servo _servo_" + blockName + ";");
                    nlIndent();
                    break;
                default:
                    throw new DbcException("Configuration block is not supported: " + cc.getComponentType());
            }
        }
    }
}
