package com.khmil.crm.ocr.service.web.controller.aspect;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

import static com.khmil.crm.ocr.service.util.Constants.ERROR_BAD_REQUEST_LOG;
import static com.khmil.crm.ocr.service.util.Constants.ERROR_IO_LOG;
import static com.khmil.crm.ocr.service.util.Constants.ERROR_IO_RESPONSE_PREFIX;
import static com.khmil.crm.ocr.service.util.Constants.ERROR_UNEXPECTED_LOG;
import static com.khmil.crm.ocr.service.util.Constants.ERROR_UNEXPECTED_RESPONSE;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(final IllegalArgumentException ex) {
        log.warn(ERROR_BAD_REQUEST_LOG, ex.getMessage());
        return textResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIo(final IOException ex) {
        log.error(ERROR_IO_LOG, ex);
        return textResponse(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_IO_RESPONSE_PREFIX + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneric(final Exception ex) {
        log.error(ERROR_UNEXPECTED_LOG, ex);
        return textResponse(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_UNEXPECTED_RESPONSE);
    }

    private ResponseEntity<String> textResponse(final HttpStatus status, final String body) {
        return ResponseEntity.status(status)
                .contentType(MediaType.TEXT_PLAIN)
                .body(body);
    }
}
