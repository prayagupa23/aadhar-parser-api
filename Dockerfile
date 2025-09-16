FROM eclipse-temurin:17-jdk as builder
WORKDIR /app
COPY pom.xml .
RUN --mount=type=cache,id=maven-cache,target=/root/.m2 mvn -q -e -DskipTests dependency:go-offline
COPY src ./src
RUN --mount=type=cache,id=maven-cache,target=/root/.m2 mvn -q -e -DskipTests package

FROM eclipse-temurin:17-jre
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"
WORKDIR /app
COPY --from=builder /app/target/aadhar-parser-api-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]


