package com.khmil.crm.ocr.service.service;

import com.khmil.crm.ocr.service.dto.OcrWorkFiles;

import java.io.IOException;

public interface TempPdfFileService {

    OcrWorkFiles createWorkFiles() throws IOException;

    void writeInput(final OcrWorkFiles files, final byte[] pdfBytes) throws IOException;

    byte[] readOutput(final OcrWorkFiles files) throws IOException;

    void cleanup(final OcrWorkFiles files);
}
