# this Dockerfile is responsible for hosting api
FROM openjdk:8-jdk-alpine
COPY target/tc-auth-service*.jar tc-auth-service.jar

ARG COMMIT_HASH
ENV COMMIT_HASH=$COMMIT_HASH

ARG BUILD_NUMBER
ENV BUILD_NUMBER=$BUILD_NUMBER

ENV JAVA_OPTS="-Dspring.profiles.active=production"

EXPOSE 8080

ENTRYPOINT exec java $JAVA_OPTS -jar tc-auth-service.jar