FROM jupyter/minimal-notebook

USER root

WORKDIR /

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

#EXPOSE 1999

#cd into /openroberta-lab then run:
#CMD ["/openroberta-lab/ora.sh", "start-from-git"]

WORKDIR ${HOME}
USER ${NB_USER}

RUN pip install jupyter-server-proxy
