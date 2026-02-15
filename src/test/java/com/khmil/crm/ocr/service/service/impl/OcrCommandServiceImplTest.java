package com.khmil.crm.ocr.service.service.impl;

import com.khmil.crm.ocr.service.config.AppOcrProperties;
import com.khmil.crm.ocr.service.dto.OcrWorkFiles;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OcrCommandServiceImplTest {

    @Test
    void shouldUseDefaultLanguageForBlankInput() {
        final AppOcrProperties properties = new AppOcrProperties();
        final OcrCommandServiceImpl service = new OcrCommandServiceImpl(properties);

        final String language = service.resolveLanguage("  ");

        assertEquals("eng", language);
    }

    @Test
    void shouldRejectInvalidLanguageFormat() {
        final AppOcrProperties properties = new AppOcrProperties();
        final OcrCommandServiceImpl service = new OcrCommandServiceImpl(properties);

        assertThrows(IllegalArgumentException.class, () -> service.resolveLanguage("eng;rm -rf"));
    }

    @Test
    void shouldBuildTwoPlaceholderCommand() throws IOException {
        final AppOcrProperties properties = new AppOcrProperties();
        properties.setCommand("ocrmypdf --skip-text %s %s");
        final OcrCommandServiceImpl service = new OcrCommandServiceImpl(properties);
        final OcrWorkFiles files = new OcrWorkFiles("t1", Path.of("in.pdf"), Path.of("out.pdf"));

        final String command = service.buildCommand(files, "eng", false);

        assertEquals("ocrmypdf --skip-text 'in.pdf' 'out.pdf'", command);
    }

    @Test
    void shouldBuildThreePlaceholderCommand() throws IOException {
        final AppOcrProperties properties = new AppOcrProperties();
        properties.setCommand("ocrmypdf -l %s %s %s");
        final OcrCommandServiceImpl service = new OcrCommandServiceImpl(properties);
        final OcrWorkFiles files = new OcrWorkFiles("t1", Path.of("in.pdf"), Path.of("out.pdf"));

        final String command = service.buildCommand(files, "eng+ukr", false);

        assertEquals("ocrmypdf -l eng+ukr 'in.pdf' 'out.pdf'", command);
    }

    @Test
    void shouldFallbackToDefaultCommandWhenConfiguredBlank() throws IOException {
        final AppOcrProperties properties = new AppOcrProperties();
        properties.setCommand("  ");
        final OcrCommandServiceImpl service = new OcrCommandServiceImpl(properties);
        final OcrWorkFiles files = new OcrWorkFiles("t1", Path.of("in.pdf"), Path.of("out.pdf"));

        final String command = service.buildCommand(files, "eng", false);

        assertEquals("ocrmypdf --skip-text 'in.pdf' 'out.pdf'", command);
    }

    @Test
    void shouldFailForInvalidPlaceholderCount() {
        final AppOcrProperties properties = new AppOcrProperties();
        properties.setCommand("ocrmypdf %s");
        final OcrCommandServiceImpl service = new OcrCommandServiceImpl(properties);
        final OcrWorkFiles files = new OcrWorkFiles("t1", Path.of("in.pdf"), Path.of("out.pdf"));

        assertThrows(IOException.class, () -> service.buildCommand(files, "eng", false));
    }
}
