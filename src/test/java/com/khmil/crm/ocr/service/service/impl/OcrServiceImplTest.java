package com.khmil.crm.ocr.service.service.impl;

import com.khmil.crm.ocr.service.dto.CommandResult;
import com.khmil.crm.ocr.service.dto.OcrWorkFiles;
import com.khmil.crm.ocr.service.service.OcrCommandService;
import com.khmil.crm.ocr.service.service.ShellCommandService;
import com.khmil.crm.ocr.service.service.TempPdfFileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OcrServiceImplTest {

    @Mock
    private TempPdfFileService tempPdfFileService;

    @Mock
    private OcrCommandService ocrCommandService;

    @Mock
    private ShellCommandService shellCommandService;

    @InjectMocks
    private OcrServiceImpl ocrService;

    @Test
    void shouldReturnOcrBytesOnSuccess() throws Exception {
        final Path outputPath = Files.createTempFile("ocr-test-", ".pdf");
        final OcrWorkFiles files = new OcrWorkFiles("token", Path.of("input.pdf"), outputPath);
        final byte[] expected = "pdf-bytes".getBytes();

        when(tempPdfFileService.createWorkFiles()).thenReturn(files);
        when(ocrCommandService.resolveLanguage("eng")).thenReturn("eng");
        when(shellCommandService.isWindows()).thenReturn(false);
        when(ocrCommandService.buildCommand(files, "eng", false)).thenReturn("ocrmypdf ...");
        when(shellCommandService.run("ocrmypdf ...")).thenReturn(new CommandResult(0, "ok"));
        when(tempPdfFileService.readOutput(files)).thenReturn(expected);

        final byte[] actual = ocrService.extractTextFromPdf("in".getBytes(), "eng");

        assertArrayEquals(expected, actual);
        verify(tempPdfFileService).cleanup(files);
        Files.deleteIfExists(outputPath);
    }

    @Test
    void shouldThrowWhenCommandExitCodeIsNonZero() throws Exception {
        final Path outputPath = Files.createTempFile("ocr-test-", ".pdf");
        final OcrWorkFiles files = new OcrWorkFiles("token", Path.of("input.pdf"), outputPath);

        when(tempPdfFileService.createWorkFiles()).thenReturn(files);
        when(ocrCommandService.resolveLanguage("eng")).thenReturn("eng");
        when(shellCommandService.isWindows()).thenReturn(false);
        when(ocrCommandService.buildCommand(files, "eng", false)).thenReturn("ocrmypdf ...");
        when(shellCommandService.run("ocrmypdf ...")).thenReturn(new CommandResult(2, "failed"));

        assertThrows(IOException.class, () -> ocrService.extractTextFromPdf("in".getBytes(), "eng"));
        verify(tempPdfFileService).cleanup(files);
        Files.deleteIfExists(outputPath);
    }

    @Test
    void shouldThrowWhenOutputFileMissing() throws Exception {
        final Path outputPath = Path.of("non-existing-output.pdf");
        final OcrWorkFiles files = new OcrWorkFiles("token", Path.of("input.pdf"), outputPath);

        when(tempPdfFileService.createWorkFiles()).thenReturn(files);
        when(ocrCommandService.resolveLanguage("eng")).thenReturn("eng");
        when(shellCommandService.isWindows()).thenReturn(false);
        when(ocrCommandService.buildCommand(files, "eng", false)).thenReturn("ocrmypdf ...");
        when(shellCommandService.run("ocrmypdf ...")).thenReturn(new CommandResult(0, "ok"));

        assertThrows(IOException.class, () -> ocrService.extractTextFromPdf("in".getBytes(), "eng"));
        verify(tempPdfFileService).cleanup(files);
    }

    @Test
    void shouldCleanupWhenWriteFails() throws Exception {
        final OcrWorkFiles files = new OcrWorkFiles("token", Path.of("input.pdf"), Path.of("output.pdf"));

        when(tempPdfFileService.createWorkFiles()).thenReturn(files);
        when(ocrCommandService.resolveLanguage("eng")).thenReturn("eng");
        doThrow(new IOException("boom")).when(tempPdfFileService).writeInput(files, "in".getBytes());

        assertThrows(IOException.class, () -> ocrService.extractTextFromPdf("in".getBytes(), "eng"));
        verify(tempPdfFileService).cleanup(files);
    }
}
