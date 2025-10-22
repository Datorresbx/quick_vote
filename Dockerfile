# Stage 1: Build
FROM gradle:8.5-jdk21 AS build
WORKDIR /app

# Copiar archivos de configuración de Gradle
COPY gradle gradle
COPY gradlew .
COPY settings.gradle.kts .
COPY build.gradle.kts .

# Dar permisos de ejecución a gradlew
RUN chmod +x gradlew

# Descargar dependencias (cache layer)
RUN ./gradlew dependencies --no-daemon

# Copiar código fuente
COPY src src

# Build de la aplicación
RUN ./gradlew bootJar --no-daemon

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copiar el JAR generado
COPY --from=build /app/build/libs/*.jar app.jar

# Exponer puerto
EXPOSE 8080

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]