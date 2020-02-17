FROM maven:3-jdk-11 as builder

RUN mkdir -p /build
WORKDIR /build
COPY pom.xml /build

RUN mvn -B dependency:resolve dependency:resolve-plugins

COPY src /build/src

RUN mvn package -DskipTests

FROM openjdk:11-slim as runtime
EXPOSE 8080

ENV APP_HOME /app
RUN mkdir $APP_HOME
WORKDIR $APP_HOME

COPY --from=builder /build/target/*.jar app.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=docker","-jar","app.jar"]
