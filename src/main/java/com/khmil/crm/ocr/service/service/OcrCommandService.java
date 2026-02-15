package com.khmil.crm.ocr.service.service;

import com.khmil.crm.ocr.service.dto.OcrWorkFiles;

import java.io.IOException;

public interface OcrCommandService {

    String resolveLanguage(final String requestedLanguage);

    String buildCommand(final OcrWorkFiles files, final String language, final boolean windows) throws IOException;
}
