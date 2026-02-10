package com.example.ocrapp.service;

import java.io.IOException;

public interface OcrService {

    byte[] extractTextFromPdf(byte[] pdfBytes, String lang) throws IOException;
}