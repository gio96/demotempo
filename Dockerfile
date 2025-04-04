# Dockerfile
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY target/demo-*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]