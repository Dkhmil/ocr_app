package com.khmil.crm.ocr.service.service.impl;

import com.khmil.crm.ocr.service.dto.CommandResult;
import com.khmil.crm.ocr.service.service.ShellCommandService;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import static com.khmil.crm.ocr.service.util.Constants.EMPTY_STRING;
import static com.khmil.crm.ocr.service.util.Constants.ERROR_PROCESS_INTERRUPTED;
import static com.khmil.crm.ocr.service.util.Constants.OS_NAME_PROPERTY;
import static com.khmil.crm.ocr.service.util.Constants.UNIX_SHELL;
import static com.khmil.crm.ocr.service.util.Constants.UNIX_SHELL_FLAG;
import static com.khmil.crm.ocr.service.util.Constants.WINDOWS_OS_NAME_TOKEN;
import static com.khmil.crm.ocr.service.util.Constants.WINDOWS_SHELL;
import static com.khmil.crm.ocr.service.util.Constants.WINDOWS_SHELL_FLAG;

@Service
public class ShellCommandServiceImpl implements ShellCommandService {

    @Override
    public CommandResult run(final String command) throws IOException {
        final ProcessBuilder processBuilder = createProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        final Process process = processBuilder.start();

        String processOutput;
        try (final InputStream inputStream = process.getInputStream();
             final ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            inputStream.transferTo(outputStream);
            processOutput = outputStream.toString(StandardCharsets.UTF_8);
        }

        int exitCode;
        try {
            exitCode = process.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException(ERROR_PROCESS_INTERRUPTED, e);
        }

        return new CommandResult(exitCode, processOutput);
    }

    private ProcessBuilder createProcessBuilder(final String command) {
        if (isWindows()) {
            return new ProcessBuilder(WINDOWS_SHELL, WINDOWS_SHELL_FLAG, command);
        }
        return new ProcessBuilder(UNIX_SHELL, UNIX_SHELL_FLAG, command);
    }

    @Override
    public boolean isWindows() {
        return System.getProperty(OS_NAME_PROPERTY, EMPTY_STRING).toLowerCase(Locale.ROOT).contains(WINDOWS_OS_NAME_TOKEN);
    }
}
