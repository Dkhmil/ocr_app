package com.khmil.crm.ocr.service.service;

import com.khmil.crm.ocr.service.dto.CommandResult;

import java.io.IOException;

public interface ShellCommandService {

    CommandResult run(final String command) throws IOException;

    boolean isWindows();
}
