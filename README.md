# ocr_app

Spring Boot (Maven) service that accepts a PDF upload and performs OCR using `ocrmypdf` (with Tesseract under the hood).

## Requirements
- Java 21+
- Maven 3.8+
- `ocrmypdf` installed on the host if you run outside Docker

Docker image already includes `ocrmypdf` and language packs (`eng`, `ukr`).

## Configure
`src/main/resources/application.yaml` supports command templating:
```
app:
  ocr:
    command: "ocrmypdf --skip-text %s %s"
```

Placeholder order:
- 1st `%s`: input PDF path
- 2nd `%s`: output PDF path

Optional language-aware variant:
```
app:
  ocr:
    command: "ocrmypdf --skip-text -l %s %s %s"
```
- 1st `%s`: OCR language (for example `eng`, `ukr`, `eng+ukr`)
- 2nd `%s`: input PDF path
- 3rd `%s`: output PDF path

## Build & Run
```
mvn spring-boot:run
```

## API
- POST `/v1/ocr` (multipart form)
  - form field: `file` -> the PDF file to OCR
  - optional query/form field: `lang` -> OCR language code (default: `eng`), e.g. `eng`, `ukr`, `eng+ukr`

Example with `curl`:
```
curl -X POST "http://localhost:8080/v1/ocr" \
  -H "Accept: application/pdf" \
  -F "file=@/path/to/your.pdf" \
  -F "lang=eng+ukr"
```
Response: A new PDF with a searchable text layer.

## Notes
- This is a minimal skeleton. For production, consider:
  - Input validation and file size limits
  - Async processing for large PDFs
  - Error handling/logging and tracing
  - Containerizing with `ocrmypdf` installed
