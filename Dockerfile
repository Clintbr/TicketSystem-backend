# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
# Kopiere pom.xml und lade Abhängigkeiten herunter (Caching)
COPY pom.xml .
RUN mvn dependency:go-offline
# Kopiere Quellcode und baue das JAR
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
# Port-Freigabe (Render nutzt oft 8080 oder 10000)
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]