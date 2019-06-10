FROM maven:3.3-jdk-8-alpine

ENV MAVEN_HOME /usr/share/maven
VOLUME "$USER_HOME_DIR/.m2"
ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"

# USER root
COPY ./ ./app
WORKDIR /app

RUN apk update \
	&& apk upgrade \
	&& apk add maven bash sudo

RUN sudo mvn install

FROM openjdk:8-jre-alpine

WORKDIR /maven

RUN apk add bash

COPY --from=0 /app/target /maven
COPY --from=0 /app/src/main/scripts/artifact_runner.sh /maven

EXPOSE 8080

CMD sh artifact_runner.sh auth-1.1.1.jar
