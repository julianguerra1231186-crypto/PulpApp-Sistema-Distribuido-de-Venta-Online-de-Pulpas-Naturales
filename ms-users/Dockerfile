# =========================================================
# Dockerfile — ms-users
# =========================================================
# Multi-stage build optimizado para Cloud Run y Docker.
#
# ANTES: Usaba eclipse-temurin:17-jdk (imagen pesada ~400MB)
# AHORA: Usa eclipse-temurin:17-jre-alpine (imagen ligera ~150MB)
#
# COMPATIBILIDAD:
#   - Docker Compose: SERVER_PORT se inyecta desde docker-compose.yml
#   - Cloud Run: PORT se inyecta automáticamente por la plataforma
#   - Local: usa el puerto por defecto definido en application.yml
# =========================================================

FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /build
COPY pom.xml ./
RUN mvn dependency:go-offline -q
COPY src src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /build/target/*.jar app.jar

# Cloud Run inyecta PORT automáticamente
ENV PORT=8081
EXPOSE ${PORT}

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s \
  CMD wget --no-verbose --tries=1 --spider http://localhost:${PORT}/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
