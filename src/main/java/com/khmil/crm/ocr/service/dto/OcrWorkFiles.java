package com.khmil.crm.ocr.service.dto;

import java.nio.file.Path;

public record OcrWorkFiles(String token, Path inputPath, Path outputPath) {
}
