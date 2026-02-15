package com.khmil.crm.ocr.service.service.impl;

import com.khmil.crm.ocr.service.config.AppOcrProperties;
import com.khmil.crm.ocr.service.dto.OcrWorkFiles;
import com.khmil.crm.ocr.service.service.OcrCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Locale;
import java.util.regex.Pattern;

import static com.khmil.crm.ocr.service.util.Constants.DEFAULT_LANGUAGE;
import static com.khmil.crm.ocr.service.util.Constants.ERROR_INVALID_COMMAND_PLACEHOLDERS;
import static com.khmil.crm.ocr.service.util.Constants.ERROR_INVALID_LANGUAGE;
import static com.khmil.crm.ocr.service.util.Constants.LANGUAGE_PATTERN;
import static com.khmil.crm.ocr.service.util.Constants.OCR_COMMAND_TEMPLATE_DEFAULT;
import static com.khmil.crm.ocr.service.util.Constants.PLACEHOLDER_STRING;
import static com.khmil.crm.ocr.service.util.Constants.THREE_PLACEHOLDERS;
import static com.khmil.crm.ocr.service.util.Constants.TWO_PLACEHOLDERS;
import static com.khmil.crm.ocr.service.util.Constants.UNIX_ESCAPED_SINGLE_QUOTE;
import static com.khmil.crm.ocr.service.util.Constants.UNIX_SINGLE_QUOTE;
import static com.khmil.crm.ocr.service.util.Constants.WINDOWS_DOUBLE_QUOTE;
import static com.khmil.crm.ocr.service.util.Constants.WINDOWS_ESCAPED_DOUBLE_QUOTE;

@RequiredArgsConstructor
@Service
public class OcrCommandServiceImpl implements OcrCommandService {
    private static final Pattern LANGUAGE_REGEX = Pattern.compile(LANGUAGE_PATTERN);

    private final AppOcrProperties appOcrProperties;

    @Override
    public String resolveLanguage(final String requestedLanguage) {
        if (requestedLanguage == null || requestedLanguage.isBlank()) {
            return DEFAULT_LANGUAGE;
        }
        final String normalizedLanguage = requestedLanguage.trim().toLowerCase(Locale.ROOT);
        if (!LANGUAGE_REGEX.matcher(normalizedLanguage).matches()) {
            throw new IllegalArgumentException(ERROR_INVALID_LANGUAGE);
        }
        return normalizedLanguage;
    }

    @Override
    public String buildCommand(final OcrWorkFiles files, final String language, final boolean windows) throws IOException {
        final String ocrCommandTemplate = resolveCommandTemplate(appOcrProperties.getCommand());
        final int placeholders = countPlaceholders(ocrCommandTemplate);
        if (placeholders == TWO_PLACEHOLDERS) {
            return String.format(
                    ocrCommandTemplate,
                    shellQuote(files.inputPath().toString(), windows),
                    shellQuote(files.outputPath().toString(), windows)
            );
        }
        if (placeholders == THREE_PLACEHOLDERS) {
            return String.format(
                    ocrCommandTemplate,
                    language,
                    shellQuote(files.inputPath().toString(), windows),
                    shellQuote(files.outputPath().toString(), windows)
            );
        }
        throw new IOException(ERROR_INVALID_COMMAND_PLACEHOLDERS);
    }

    private String resolveCommandTemplate(final String configuredTemplate) {
        if (configuredTemplate == null || configuredTemplate.isBlank()) {
            return OCR_COMMAND_TEMPLATE_DEFAULT;
        }
        return configuredTemplate.trim();
    }

    private int countPlaceholders(final String template) {
        int count = 0;
        int idx = 0;
        while ((idx = template.indexOf(PLACEHOLDER_STRING, idx)) != -1) {
            count++;
            idx += PLACEHOLDER_STRING.length();
        }
        return count;
    }

    private String shellQuote(final String value, final boolean windows) {
        if (windows) {
            return WINDOWS_DOUBLE_QUOTE + value.replace(WINDOWS_DOUBLE_QUOTE, WINDOWS_ESCAPED_DOUBLE_QUOTE) + WINDOWS_DOUBLE_QUOTE;
        }
        return UNIX_SINGLE_QUOTE + value.replace(UNIX_SINGLE_QUOTE, UNIX_ESCAPED_SINGLE_QUOTE) + UNIX_SINGLE_QUOTE;
    }
}
