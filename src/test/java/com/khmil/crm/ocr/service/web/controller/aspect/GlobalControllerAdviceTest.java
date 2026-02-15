package com.khmil.crm.ocr.service.web.controller.aspect;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalControllerAdviceTest {

    @Test
    void shouldHandleIllegalArgument() {
        final GlobalControllerAdvice advice = new GlobalControllerAdvice();

        final var response = advice.handleIllegalArgument(new IllegalArgumentException("bad"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("bad", response.getBody());
    }

    @Test
    void shouldHandleIoException() {
        final GlobalControllerAdvice advice = new GlobalControllerAdvice();

        final var response = advice.handleIo(new IOException("io"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("OCR failed: io", response.getBody());
    }

    @Test
    void shouldHandleUnexpectedException() {
        final GlobalControllerAdvice advice = new GlobalControllerAdvice();

        final var response = advice.handleGeneric(new RuntimeException("boom"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Unexpected error occurred", response.getBody());
    }
}
