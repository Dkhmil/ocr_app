# ocr_app

Spring Boot (Maven) skeleton that accepts a PDF upload and performs OCR using Tesseract (via Tess4J) and PDFBox.

## Requirements
- Java 21+
- Maven 3.8+
- Tesseract OCR installed on the host (for best results) and language data (`tessdata`)
  - Windows (typical): `C:\\Program Files\\Tesseract-OCR`
  - Linux: `/usr/share/tesseract-ocr`

## Configure
Edit `src/main/resources/application.yaml` and set:
```
tesseract:
  datapath: C:\Program Files\Tesseract-OCR
```
Adjust to your OS/path. Keep empty to rely on system defaults if available.

## Build & Run
```
mvn spring-boot:run
```

## API
- POST `/api/ocr/pdf` (multipart form)
  - form field: `file` → the PDF file to OCR
  - optional query/form field: `lang` → Tesseract language code (default: `eng`)

Example with `curl`:
```
curl -X POST "http://localhost:8080/api/ocr/pdf" \
  -H "Accept: application/pdf" \
  -F "file=@/path/to/your.pdf" \
  -F "lang=eng"
```
Response: A new PDF with a searchable text layer.

## Notes
- This is a minimal skeleton. For production, consider:
  - Input validation and file size limits
  - Async processing for large PDFs
  - Error handling/logging and tracing
  - Containerizing with native Tesseract binaries available
