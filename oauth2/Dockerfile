FROM openjdk:22-jdk-slim
WORKDIR /app
COPY target/oauth2-0.0.1-SNAPSHOT.jar oauth2.jar
ENTRYPOINT ["java", "-jar", "oauth2.jar"]
