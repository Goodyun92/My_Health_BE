FROM openjdk:11-jdk
COPY *.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]