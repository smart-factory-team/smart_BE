# usermanagement/Dockerfile
# Multi-stage build for optimized Spring Boot application

# Stage 1: Build stage - 확실히 존재하는 이미지 사용
FROM maven:3-openjdk-17 AS builder

# Set working directory
WORKDIR /app

# Copy pom.xml first for better caching
COPY pom.xml .

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application (skip tests for Docker build)
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime stage - 확실히 존재하는 이미지 사용
FROM eclipse-temurin:17-jre

# Add metadata
LABEL maintainer="Smart BE Team"
LABEL service="usermanagement"
LABEL version="1.0.0"

# Create non-root user for security
RUN groupadd -r spring && useradd -r -g spring spring

# Set working directory
WORKDIR /app

# Copy JAR file from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Change ownership to spring user
RUN chown -R spring:spring /app

# Switch to non-root user
USER spring

# Expose port
EXPOSE 8080

# Environment variables for JVM optimization
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Environment variables for Spring profiles
ENV SPRING_PROFILES_ACTIVE=docker

# Run the application
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]