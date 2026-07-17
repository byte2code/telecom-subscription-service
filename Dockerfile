# syntax=docker/dockerfile:1
FROM eclipse-temurin:17-jre-alpine

LABEL org.opencontainers.image.title="Telecom Subscription Service"
LABEL org.opencontainers.image.description="Spring Boot 2.7 telecom billing microservice"
LABEL org.opencontainers.image.source="https://github.com/byte2code/telecom-subscription-service"

# Non-root user for security
RUN addgroup -S telecom && adduser -S telecom -G telecom

WORKDIR /app

# Copy the fat JAR built by Maven
COPY target/SubscriptionService-*.jar app.jar

RUN chown telecom:telecom app.jar
USER telecom

# JVM tuning for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -Djava.security.egd=file:/dev/./urandom"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
