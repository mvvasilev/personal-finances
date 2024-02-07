FROM eclipse-temurin:21-jdk-alpine

COPY ./build/libs/pefi-login-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081

ENTRYPOINT exec java $JAVA_OPTS -jar /app.jar $ARGS