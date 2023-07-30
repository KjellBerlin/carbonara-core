FROM gradle:8.2.1-jdk17-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM openjdk:17-jdk
EXPOSE 8080
RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/*.jar /app/core.jar

ENTRYPOINT ["java","-jar","/app/core.jar"]