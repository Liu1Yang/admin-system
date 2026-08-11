# Build
FROM maven:3.9-eclipse-temurin-8 AS build
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests -q

# Run
FROM eclipse-temurin:8-jre-jammy
WORKDIR /app

RUN mkdir -p /app/uploads

COPY --from=build /build/target/admin-system-1.0.0.jar /app/app.jar

EXPOSE 8080

# Profile via SPRING_PROFILES_ACTIVE env (docker-compose sets it)
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
