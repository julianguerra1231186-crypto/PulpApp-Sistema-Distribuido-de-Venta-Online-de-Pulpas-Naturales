# =========================================================
# Dockerfile — ms-users (Cloud Run Ready)
# =========================================================
# PROBLEMA ANTERIOR:
#   eclipse-temurin:17-jre-alpine causaba fallos de startup
#   en Cloud Run. Alpine usa musl libc que puede ser
#   incompatible con algunas dependencias de Spring Boot 4.x.
#   Además, HEALTHCHECK de Docker no funciona en Cloud Run.
#
# SOLUCIÓN:
#   Usar eclipse-temurin:17-jre (Debian-based, estable).
#   Sin HEALTHCHECK (Cloud Run usa su propio readiness probe).
#   Puerto dinámico via variable PORT.
# =========================================================

FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /build
COPY pom.xml ./
RUN mvn dependency:go-offline -q
COPY src src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /build/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
