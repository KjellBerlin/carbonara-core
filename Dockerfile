# Stage 1: Build the application with Gradle
FROM gradle:8.2.1-jdk17-alpine AS build
# Set working directory
WORKDIR /home/gradle/src
# Copy project files
COPY --chown=gradle:gradle . .
# Cache dependencies
RUN gradle build --no-daemon -x test

# Stage 2: Create a lightweight image for running the application
FROM openjdk:17-jdk-slim
# Create app directory
RUN mkdir /app
# Copy the JAR file from the build stage
COPY --from=build /home/gradle/src/build/libs/*.jar /app/core.jar
# Expose the application port
EXPOSE 8080

# Define the entry point for the container
ENTRYPOINT ["java", "-jar", "/app/core.jar"]
