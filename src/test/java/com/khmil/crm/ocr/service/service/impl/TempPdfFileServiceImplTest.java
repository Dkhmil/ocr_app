package com.khmil.crm.ocr.service.service.impl;

import com.khmil.crm.ocr.service.dto.OcrWorkFiles;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TempPdfFileServiceImplTest {

    @Test
    void shouldCreateWriteReadAndCleanupFiles() throws IOException {
        final TempPdfFileServiceImpl service = new TempPdfFileServiceImpl();
        final OcrWorkFiles files = service.createWorkFiles();
        final byte[] content = "pdf-bytes".getBytes();

        service.writeInput(files, content);
        final byte[] actual = service.readOutput(new OcrWorkFiles(files.token(), files.inputPath(), files.inputPath()));

        assertArrayEquals(content, actual);
        assertTrue(files.inputPath().toFile().exists() || files.outputPath().toFile().exists());

        service.cleanup(files);

        assertFalse(files.inputPath().toFile().exists());
        assertFalse(files.outputPath().toFile().exists());
    }

    @Test
    void cleanupShouldNotThrowWhenFilesDoNotExist() {
        final TempPdfFileServiceImpl service = new TempPdfFileServiceImpl();
        final OcrWorkFiles files = new OcrWorkFiles("x", java.nio.file.Path.of("not-exist-in.pdf"), java.nio.file.Path.of("not-exist-out.pdf"));

        service.cleanup(files);
    }
}
