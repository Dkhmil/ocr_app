package com.khmil.crm.ocr.service.service;

import java.io.IOException;

public interface OcrService {

    byte[] extractTextFromPdf(final byte[] pdfBytes, final String lang) throws IOException;
}
