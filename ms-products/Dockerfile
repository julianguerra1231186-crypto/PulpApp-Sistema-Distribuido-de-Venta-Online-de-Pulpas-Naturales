FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /build

COPY pom.xml mvnw mvnw.cmd ./
COPY .mvn .mvn
COPY src src

RUN ./mvnw -q -DskipTests package

FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]
