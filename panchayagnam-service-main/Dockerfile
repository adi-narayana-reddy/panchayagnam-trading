# Multi-stage Dockerfile for Panchayagnam Core Service
# Stage 1: Build JAR using Maven
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
# Download dependencies offline cache
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime image using Eclipse Temurin JRE 17
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Run as non-root user for security
RUN groupadd -r pyapp && useradd -r -g pyapp -d /app -s /sbin/nologin pyapp
USER pyapp

COPY --from=builder /app/target/panchayagnam-service-*.jar /app/app.jar

ENV PORT=8083 \
    SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC"

EXPOSE 8083

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
