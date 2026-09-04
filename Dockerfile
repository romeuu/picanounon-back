# -------------------------------------------------------------
# Etapa 1: Compilación (Maven)
# -------------------------------------------------------------
FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Copiamos primeiro os ficheiros de dependencias para aproveitar a caché de Docker
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiamos o código fonte e compilamos o jar (saltando tests)
COPY src ./src
RUN mvn clean package -DskipTests

# -------------------------------------------------------------
# Etapa 2: Runtime lixeiro
# -------------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Crear cartafol para a base de datos SQLite e darlle permisos
RUN mkdir -p /data

# Copiar o JAR xerado na etapa anterior
COPY --from=builder /app/target/*.jar app.jar

# Variables de contorno para apuntar SQLite ao volume persistente
ENV SPRING_DATASOURCE_URL=jdbc:sqlite:/data/picanounon.db
ENV SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.sqlite.JDBC
ENV SPRING_JPA_DATABASE_PLATFORM=org.hibernate.community.dialect.SQLiteDialect

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]