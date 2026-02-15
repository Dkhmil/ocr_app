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

# Install OCRmyPDF and OCR runtime dependencies
RUN apt-get update && \
    apt-get install -y \
    ocrmypdf \
    tesseract-ocr-eng \
    tesseract-ocr-ukr \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Normalize tessdata path across distro package versions (e.g. 4.00 vs 5).
RUN set -eux; \
    TESSDATA_DIR="$(find /usr/share -type d -path '*/tessdata' | head -n 1)"; \
    test -n "$TESSDATA_DIR"; \
    ln -s "$TESSDATA_DIR" /usr/share/tessdata

# Create app directory
WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Create directory for temporary PDF processing
RUN mkdir -p /tmp/ocr && chmod 777 /tmp/ocr

# Expose the application port
EXPOSE 8080

# Set environment variables
ENV TESSDATA_PREFIX=/usr/share/tessdata/
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
