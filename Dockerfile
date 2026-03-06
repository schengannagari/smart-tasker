# ===============================
# 1️⃣ Build stage
# ===============================
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy parent + module poms (cache-friendly)
COPY pom.xml .
COPY app-core/pom.xml app-core/pom.xml
COPY db-migration/pom.xml db-migration/pom.xml

RUN mvn -B -q -e -DskipTests dependency:go-offline

# Copy full source
COPY app-core app-core
COPY db-migration db-migration

RUN mvn -B -DskipTests -pl app-core,db-migration -am clean package


# ===============================
# 2️⃣ db-migration image
# ===============================
FROM eclipse-temurin:17-jre AS db-migration

WORKDIR /app

COPY --from=build /app/db-migration/target/db-migration-1.0.0-SNAPSHOT.jar app.jar
COPY ./cockroach-certs/ca.crt /app/certs/ca.crt

ENTRYPOINT ["java", "-jar", "app.jar"]


# ===============================
# 3️⃣ app-core image
# ===============================
FROM eclipse-temurin:17-jre AS app-core

WORKDIR /app

COPY --from=build /app/app-core/target/app-core-1.0.0-SNAPSHOT.jar app.jar
COPY ./cockroach-certs/ca.crt /app/certs/ca.crt

EXPOSE 8089

ENTRYPOINT ["java", "-jar", "app.jar"]

