# ── Stage 1: Build (Compilación y Empaquetado) ────────────────────────────────
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /workspace

# Optimización de caché de dependencias: copiar pom.xml primero
COPY pom.xml ./
RUN mvn dependency:go-offline -B || true

# Copiar código fuente y compilar artefacto productivo
COPY src/ src/
RUN mvn clean package -DskipTests -B

# ── Stage 2: Runtime (Entorno de Ejecución Seguro y Ligero) ───────────────────
FROM eclipse-temurin:21-jre-alpine AS runner
WORKDIR /app

# Crear usuario y grupo de sistema sin privilegios
RUN addgroup -S spring && adduser -S spring -G spring

# Copiar artefacto desde el builder
COPY --from=builder /workspace/target/*.jar app.jar
RUN chown -R spring:spring /app

# Ejecutar como usuario no privilegiado
USER spring:spring

EXPOSE 8080

# Parámetros JVM optimizados para contenedores y perfil productivo
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+ExitOnOutOfMemoryError -Djava.security.egd=file:/dev/./urandom"
ENV SPRING_PROFILES_ACTIVE=prod

# Sonda de salud nativa mediante /api/health
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/health || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
