package com.khmil.crm.ocr.service.config;

import org.junit.jupiter.api.Test;

import static com.khmil.crm.ocr.service.util.Constants.OCR_COMMAND_TEMPLATE_DEFAULT;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AppOcrPropertiesTest {

    @Test
    void shouldUseDefaultCommand() {
        final AppOcrProperties properties = new AppOcrProperties();

        assertEquals(OCR_COMMAND_TEMPLATE_DEFAULT, properties.getCommand());
    }

    @Test
    void shouldSetCommand() {
        final AppOcrProperties properties = new AppOcrProperties();
        final String command = "ocrmypdf --force-ocr %s %s";

        properties.setCommand(command);

        assertEquals(command, properties.getCommand());
    }
}
