package com.khmil.crm.ocr.service.web.controller.v1;

import com.khmil.crm.ocr.service.service.OcrService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.khmil.crm.ocr.service.util.Constants.API_BASE_PATH_V1_OCR;
import static com.khmil.crm.ocr.service.util.Constants.CONTENT_DISPOSITION_INLINE;
import static com.khmil.crm.ocr.service.util.Constants.DEFAULT_LANGUAGE;
import static com.khmil.crm.ocr.service.util.Constants.ERROR_EMPTY_FILE;
import static com.khmil.crm.ocr.service.util.Constants.OUTPUT_FILE_NAME;
import static com.khmil.crm.ocr.service.util.Constants.REQUEST_PARAM_FILE;
import static com.khmil.crm.ocr.service.util.Constants.REQUEST_PARAM_LANG;

@RequiredArgsConstructor
@RestController
@RequestMapping(API_BASE_PATH_V1_OCR)
public class OcrController {

    private final OcrService ocrService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> ocrPdf(@RequestParam(REQUEST_PARAM_FILE) final MultipartFile file,
                                         @RequestParam(value = REQUEST_PARAM_LANG, required = false, defaultValue = DEFAULT_LANGUAGE) final String lang) throws IOException {
        validateFile(file);
        final byte[] searchablePdf = ocrService.extractTextFromPdf(file.getBytes(), lang);
        return buildPdfResponse(searchablePdf);
    }

    private void validateFile(final MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(ERROR_EMPTY_FILE);
        }
    }

    private ResponseEntity<byte[]> buildPdfResponse(final byte[] searchablePdf) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, CONTENT_DISPOSITION_INLINE + OUTPUT_FILE_NAME)
                .contentType(MediaType.APPLICATION_PDF)
                .body(searchablePdf);
    }
}
