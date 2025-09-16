# syntax=docker/dockerfile:1.7-labs
# Stage 1: Build
FROM maven:3.9.8-eclipse-temurin-17 as builder
WORKDIR /app

# Copy pom.xml and download dependencies using cache
COPY pom.xml .
RUN mvn -q -e -DskipTests dependency:go-offline

# Copy source code and build using cached dependencies
COPY src ./src
RUN mvn -q -e -DskipTests package

# Stage 2: Runtime
FROM eclipse-temurin:17-jre
WORKDIR /app
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

# Copy built jar
COPY --from=builder /app/target/aadhar-parser-api-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
