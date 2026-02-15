package com.khmil.crm.ocr.service.service.impl;

import com.khmil.crm.ocr.service.dto.OcrWorkFiles;
import com.khmil.crm.ocr.service.service.TempPdfFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static com.khmil.crm.ocr.service.util.Constants.INPUT_FILE_PREFIX;
import static com.khmil.crm.ocr.service.util.Constants.LOG_TEMP_CLEANUP;
import static com.khmil.crm.ocr.service.util.Constants.LOG_TEMP_CLEANUP_FAILED;
import static com.khmil.crm.ocr.service.util.Constants.OUTPUT_FILE_PREFIX;
import static com.khmil.crm.ocr.service.util.Constants.PDF_EXTENSION;
import static com.khmil.crm.ocr.service.util.Constants.TMP_DIR;

@Slf4j
@Service
public class TempPdfFileServiceImpl implements TempPdfFileService {
    @Override
    public OcrWorkFiles createWorkFiles() throws IOException {
        final Path workDir = Path.of(TMP_DIR);
        Files.createDirectories(workDir);
        final String token = UUID.randomUUID().toString();
        final Path inputPath = workDir.resolve(INPUT_FILE_PREFIX + token + PDF_EXTENSION);
        final Path outputPath = workDir.resolve(OUTPUT_FILE_PREFIX + token + PDF_EXTENSION);
        return new OcrWorkFiles(token, inputPath, outputPath);
    }

    @Override
    public void writeInput(final OcrWorkFiles files, final byte[] pdfBytes) throws IOException {
        Files.write(files.inputPath(), pdfBytes);
    }

    @Override
    public byte[] readOutput(final OcrWorkFiles files) throws IOException {
        return Files.readAllBytes(files.outputPath());
    }

    @Override
    public void cleanup(final OcrWorkFiles files) {
        try {
            final boolean inputDeleted = Files.deleteIfExists(files.inputPath());
            final boolean outputDeleted = Files.deleteIfExists(files.outputPath());
            log.debug(LOG_TEMP_CLEANUP,
                    files.token(), inputDeleted, outputDeleted);
        } catch (final IOException e) {
            log.warn(LOG_TEMP_CLEANUP_FAILED, files.token(), e.getMessage());
        }
    }
}
