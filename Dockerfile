# Stage 1: Build
FROM eclipse-temurin:17-jdk as builder
WORKDIR /app

# Copy pom.xml and download dependencies using cache
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2/repository,id=aadhar-parser-mvn-cache \
    mvn -q -e -DskipTests dependency:go-offline

# Copy source code and build using cached dependencies
COPY src ./src
RUN --mount=type=cache,target=/root/.m2/repository,id=aadhar-parser-mvn-cache \
    mvn -q -e -DskipTests package

# Stage 2: Runtime
FROM eclipse-temurin:17-jre
WORKDIR /app
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

# Copy built jar
COPY --from=builder /app/target/aadhar-parser-api-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
