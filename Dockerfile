FROM maven:3.6.1-jdk-11-slim as build
WORKDIR /identity
COPY pom.xml pom.xml
COPY src src
COPY conf conf
COPY assets assets
RUN mvn package

FROM openjdk:11-jdk-slim
WORKDIR /identity
COPY --from=build /identity/target/identity-1.0.0.jar app.jar
COPY conf conf
COPY assets assets
COPY found.csv found.csv
COPY rings.csv rings.csv
EXPOSE 80
CMD ["java", "-jar", "app.jar"]
