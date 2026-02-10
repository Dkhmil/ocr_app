package com.example.ocrapp.web.controller.v1;

import com.example.ocrapp.service.OcrService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/v1/ocr")
public class OcrController {

    private final OcrService ocrService;

    public OcrController(OcrService ocrService) {
        this.ocrService = ocrService;
    }

    @PostMapping(value = "/pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> ocrPdf(@RequestParam("file") MultipartFile file,
                                         @RequestParam(value = "lang", required = false, defaultValue = "eng") String lang) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("No file uploaded or file is empty");
        }
        byte[] searchablePdf = ocrService.extractTextFromPdf(file.getBytes(), lang);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=ocr-searchable.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(searchablePdf);
    }
}
