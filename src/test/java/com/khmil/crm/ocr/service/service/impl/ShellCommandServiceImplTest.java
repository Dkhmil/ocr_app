package com.khmil.crm.ocr.service.service.impl;

import com.khmil.crm.ocr.service.dto.CommandResult;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShellCommandServiceImplTest {

    @Test
    void shouldRunSimpleCommand() throws IOException {
        final ShellCommandServiceImpl service = new ShellCommandServiceImpl();

        final CommandResult result = service.run("echo hello");

        assertEquals(0, result.exitCode());
        assertTrue(result.output().toLowerCase().contains("hello"));
    }

    @Test
    void shouldReturnNonZeroExitCodeForFailingCommand() throws IOException {
        final ShellCommandServiceImpl service = new ShellCommandServiceImpl();

        final CommandResult result = service.run("exit 7");

        assertEquals(7, result.exitCode());
    }

    @Test
    void shouldThrowWhenInterrupted() {
        final ShellCommandServiceImpl service = new ShellCommandServiceImpl();
        Thread.currentThread().interrupt();

        try {
            assertThrows(IOException.class, () -> service.run("echo hello"));
        } finally {
            Thread.interrupted();
        }
    }
}
