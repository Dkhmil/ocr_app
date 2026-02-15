# OCR App - Docker Setup

This Spring Boot application provides OCR functionality for PDF files using OCRmyPDF (with Tesseract).

## Prerequisites

- Docker and Docker Compose installed on your machine
- Or just Docker if you prefer using docker commands directly

## Quick Start

### Option 1: Using Docker Compose (Recommended)

```bash
# Build and start the service
docker-compose up -d

# View logs
docker-compose logs -f

# Stop the service
docker-compose down
```

The service will be available at `http://localhost:8080`

### Option 2: Using Docker Commands

```bash
# Build the image
docker build -t ocr-app:latest .

# Run the container
docker run -d \
  -p 8080:8080 \
  --name ocr-app \
  -e JAVA_OPTS="-Xmx1g -Xms512m" \
  ocr-app:latest

# View logs
docker logs -f ocr-app

# Stop and remove
docker stop ocr-app && docker rm ocr-app
```

## Testing the Service

```bash
# Upload a PDF for OCR processing
curl -X POST \
  -F "file=@/path/to/your/document.pdf" \
  -F "lang=eng+ukr" \
  http://localhost:8080/v1/ocr \
  --output searchable-output.pdf
```

## Configuration

### Environment Variables

- `SPRING_PROFILES_ACTIVE`: Spring profile to use (default: `docker`)
- `JAVA_OPTS`: JVM options (default: `-Xmx1g -Xms512m`)
- `TESSDATA_PREFIX`: Tesseract data directory (set automatically)

### OCR Languages

The Dockerfile includes English and Ukrainian language packs. Use API `lang` like `eng`, `ukr`, or `eng+ukr`.
To add more languages:

1. Edit the Dockerfile and add the desired language pack:
   ```dockerfile
   tesseract-ocr-fra \  # French
   tesseract-ocr-deu \  # German
   tesseract-ocr-spa \  # Spanish
   ```

2. Rebuild the image:
   ```bash
   docker-compose build
   ```

## Production Deployment

### Building for Production

```bash
# Build the image with a version tag
docker build -t ocr-app:1.0.0 .

# Tag for your registry
docker tag ocr-app:1.0.0 your-registry.com/ocr-app:1.0.0

# Push to registry
docker push your-registry.com/ocr-app:1.0.0
```

### Resource Limits

For production, consider adding resource limits to prevent memory issues:

```yaml
# In docker-compose.yml
services:
  ocr-service:
    # ... other config ...
    deploy:
      resources:
        limits:
          cpus: '2'
          memory: 2G
        reservations:
          cpus: '1'
          memory: 1G
```

## Troubleshooting

### Common Issues

1. **Out of Memory Errors**
   - Increase the heap size in `JAVA_OPTS`: `-Xmx2g -Xms1g`
   - Increase Docker container memory limit

2. **Tesseract Not Found**
   - Check if OCRmyPDF is installed: `docker exec ocr-app ocrmypdf --version`
   - Verify `TESSDATA_PREFIX` is set correctly
   - Check if Tesseract is installed: `docker exec ocr-app tesseract --version`
   - Check language data exists: `docker exec ocr-app ls /usr/share/tessdata/eng.traineddata`

3. **PDF Processing Fails**
   - Ensure OCRmyPDF is installed (included in Dockerfile)
   - Check file permissions on `/tmp/ocr` directory

### Debug Mode

Run the container with debug logging:

```bash
docker run -p 8080:8080 \
  -e JAVA_OPTS="-Xmx1g -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005" \
  -p 5005:5005 \
  ocr-app:latest
```

## Performance Optimization

- **Multi-stage build**: The Dockerfile uses multi-stage builds to keep the final image small
- **Layer caching**: Dependencies are downloaded in a separate layer for faster rebuilds
- **Minimal base image**: Uses JRE instead of JDK for smaller image size

## Image Size

Expected image size: ~700-1000MB (including OCRmyPDF, Tesseract, and all dependencies)
