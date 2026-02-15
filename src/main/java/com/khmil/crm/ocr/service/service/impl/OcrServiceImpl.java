package com.khmil.crm.ocr.service.service.impl;

import com.khmil.crm.ocr.service.dto.CommandResult;
import com.khmil.crm.ocr.service.dto.OcrWorkFiles;
import com.khmil.crm.ocr.service.service.OcrCommandService;
import com.khmil.crm.ocr.service.service.OcrService;
import com.khmil.crm.ocr.service.service.ShellCommandService;
import com.khmil.crm.ocr.service.service.TempPdfFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;

import static com.khmil.crm.ocr.service.util.Constants.ERROR_OCR_FAILED_MIDDLE;
import static com.khmil.crm.ocr.service.util.Constants.ERROR_OCR_FAILED_PREFIX;
import static com.khmil.crm.ocr.service.util.Constants.LOG_COMMAND_DEBUG;
import static com.khmil.crm.ocr.service.util.Constants.LOG_COMMAND_FAILED;
import static com.khmil.crm.ocr.service.util.Constants.LOG_COMMAND_SUCCESS;
import static com.khmil.crm.ocr.service.util.Constants.LOG_EXECUTING_COMMAND;
import static com.khmil.crm.ocr.service.util.Constants.LOG_OCR_COMPLETED;
import static com.khmil.crm.ocr.service.util.Constants.LOG_OCR_STARTED;

@Slf4j
@RequiredArgsConstructor
@Service
public class OcrServiceImpl implements OcrService {
    private final TempPdfFileService tempPdfFileService;
    private final OcrCommandService ocrCommandService;
    private final ShellCommandService shellCommandService;

    @Override
    public byte[] extractTextFromPdf(final byte[] pdfBytes, final String lang) throws IOException {
        final OcrWorkFiles workFiles = tempPdfFileService.createWorkFiles();
        final String resolvedLanguage = ocrCommandService.resolveLanguage(lang);

        log.info(LOG_OCR_STARTED, workFiles.token(), pdfBytes.length, resolvedLanguage);

        try {
            tempPdfFileService.writeInput(workFiles, pdfBytes);
            runOcrCommand(workFiles, resolvedLanguage);
            final byte[] result = tempPdfFileService.readOutput(workFiles);
            log.info(LOG_OCR_COMPLETED, workFiles.token(), result.length);
            return result;
        } finally {
            tempPdfFileService.cleanup(workFiles);
        }
    }

    private void runOcrCommand(final OcrWorkFiles workFiles, final String language) throws IOException {
        final String command = ocrCommandService.buildCommand(workFiles, language, shellCommandService.isWindows());
        log.info(LOG_EXECUTING_COMMAND,
                workFiles.inputPath().getFileName(), workFiles.outputPath().getFileName(), language);
        log.debug(LOG_COMMAND_DEBUG, command);

        final CommandResult result = shellCommandService.run(command);
        if (result.exitCode() != 0 || !Files.exists(workFiles.outputPath())) {
            log.error(LOG_COMMAND_FAILED,
                    workFiles.token(), result.exitCode(), result.output());
            throw new IOException(ERROR_OCR_FAILED_PREFIX + result.exitCode() + ERROR_OCR_FAILED_MIDDLE + result.output());
        }
        log.info(LOG_COMMAND_SUCCESS, workFiles.token(), 0);
    }
}
