FROM openjdk:22-jdk-slim
WORKDIR /app
COPY target/emailSender-0.0.1-SNAPSHOT.jar emailSender.jar
ENTRYPOINT ["java", "-jar", "emailSender.jar"]
