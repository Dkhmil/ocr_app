# Multi-stage build for Spring Boot OCR service with Tesseract

# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy pom.xml and download dependencies (cached layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime image
FROM eclipse-temurin:21-jre-jammy

# Install Tesseract OCR and dependencies
RUN apt-get update && \
    apt-get install -y \
    tesseract-ocr \
    tesseract-ocr-eng \
    tesseract-ocr-ukr \
    ghostscript \
    poppler-utils \
    && rm -rf /var/lib/apt/lists/*

# Create app directory
WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Create directory for temporary PDF processing
RUN mkdir -p /tmp/ocr && chmod 777 /tmp/ocr

# Expose the application port
EXPOSE 8080

# Set environment variables
ENV TESSDATA_PREFIX=/usr/share/tesseract-ocr/5/tessdata/
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
