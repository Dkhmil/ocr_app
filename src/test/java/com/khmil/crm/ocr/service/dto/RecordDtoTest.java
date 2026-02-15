package com.khmil.crm.ocr.service.dto;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RecordDtoTest {

    @Test
    void shouldExposeCommandResultFields() {
        final CommandResult result = new CommandResult(0, "ok");

        assertEquals(0, result.exitCode());
        assertEquals("ok", result.output());
    }

    @Test
    void shouldExposeOcrWorkFilesFields() {
        final OcrWorkFiles files = new OcrWorkFiles("token", Path.of("in.pdf"), Path.of("out.pdf"));

        assertEquals("token", files.token());
        assertEquals(Path.of("in.pdf"), files.inputPath());
        assertEquals(Path.of("out.pdf"), files.outputPath());
    }
}
