package com.khmil.crm.ocr.service.web.controller.v1;

import com.khmil.crm.ocr.service.service.OcrService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

import static com.khmil.crm.ocr.service.util.Constants.ERROR_EMPTY_FILE;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OcrControllerTest {

    @Mock
    private OcrService ocrService;

    @Test
    void shouldReturnPdfResponse() throws IOException {
        final OcrController controller = new OcrController(ocrService);
        final byte[] expected = "out".getBytes();
        final MockMultipartFile file = new MockMultipartFile("file", "x.pdf", "application/pdf", "in".getBytes());
        when(ocrService.extractTextFromPdf(file.getBytes(), "eng")).thenReturn(expected);

        final var response = controller.ocrPdf(file, "eng");

        assertEquals(MediaType.APPLICATION_PDF, response.getHeaders().getContentType());
        assertEquals("inline; filename=ocr-searchable.pdf", response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION));
        assertArrayEquals(expected, response.getBody());
        verify(ocrService).extractTextFromPdf(file.getBytes(), "eng");
    }

    @Test
    void shouldRejectEmptyFile() {
        final OcrController controller = new OcrController(ocrService);
        final MockMultipartFile file = new MockMultipartFile("file", new byte[0]);

        final IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> controller.ocrPdf(file, "eng"));
        assertEquals(ERROR_EMPTY_FILE, ex.getMessage());
    }
}
