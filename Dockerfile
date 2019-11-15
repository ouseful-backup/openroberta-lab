#To build:
#docker build -t psychemedia/robertalab .
#To probe/debug the build...
#docker run -it psychemedia/robertalab bash
#To run:
#docker run -p 1999:1999 psychemedia/robertalab
#To expose to other machines on the local netwrok, the docker VM needs port forwarding set up
#https://blog.ouseful.info/2016/05/22/exposing-services-running-in-a-docker-container-running-in-virtualbox-to-other-computers-on-a-local-network/

FROM ubuntu:bionic

RUN apt-get clean -y && apt-get -y update && apt-get -y upgrade 

RUN apt-get install -y git phantomjs maven
RUN apt-get clean -y

RUN apt-get install -y openjdk-8-jdk

# Define commonly used JAVA_HOME variable
ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64

RUN git clone https://github.com/OpenRoberta/openroberta-lab.git

WORKDIR openroberta-lab

RUN mvn clean install

RUN yes | /openroberta-lab/ora.sh create-empty-db

EXPOSE 1999

CMD ["/openroberta-lab/ora.sh", "start-from-git"]
