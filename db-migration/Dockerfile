FROM 3.9.12-eclipse-temurin-11-alpine AS build
WORKDIR /build
COPY ../pom.xml .
COPY ../db-migration .
RUN mvn -pl db-migration clean package

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build ./build/db-migration-1.0.0-SNAPSHOT.jar /app/app.jar
ENTRYPOINT ["java","-jar","app.jar"]
