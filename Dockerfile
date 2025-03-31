# Use Maven to build the app
FROM maven:3.9.6-openjdk-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Use the built JAR in a slim runtime image
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=builder /app/target/pkm-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
