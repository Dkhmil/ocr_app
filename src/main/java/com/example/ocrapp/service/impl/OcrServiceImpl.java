package com.example.ocrapp.service.impl;

import com.example.ocrapp.service.OcrService;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class OcrServiceImpl implements OcrService {

    private static final String DEFAULT_LANGUAGE = "eng";
    private static final int RENDER_DPI = 300;

    private final String tesseractDataPath;

    public OcrServiceImpl(@Value("${tesseract.datapath:}") String tesseractDataPath) {
        this.tesseractDataPath = tesseractDataPath;
    }

    @Override
    public byte[] extractTextFromPdf(byte[] pdfBytes, String lang) throws IOException {
        try (PDDocument sourceDocument = PDDocument.load(new ByteArrayInputStream(pdfBytes));
             PDDocument outputDocument = new PDDocument()) {
            PDFRenderer renderer = new PDFRenderer(sourceDocument);
            ITesseract tesseract = createTesseract(lang);
            int pageCount = sourceDocument.getNumberOfPages();
            for (int pageIndex = 0; pageIndex < pageCount; pageIndex++) {
                BufferedImage image = renderer.renderImageWithDPI(pageIndex, RENDER_DPI, ImageType.RGB);
                String pageText = extractPageText(tesseract, image, pageIndex);
                PDPage sourcePage = sourceDocument.getPage(pageIndex);
                PDPage outputPage = new PDPage(sourcePage.getMediaBox());
                outputDocument.addPage(outputPage);
                addImageAndText(outputDocument, outputPage, image, pageText);
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputDocument.save(outputStream);
            return outputStream.toByteArray();
        }
    }

    private ITesseract createTesseract(String lang) {
        ITesseract tesseract = new Tesseract();
        if (tesseractDataPath != null && !tesseractDataPath.isBlank()) {
            tesseract.setDatapath(tesseractDataPath);
        }
        tesseract.setLanguage(resolveLanguage(lang));
        return tesseract;
    }

    private String resolveLanguage(String lang) {
        return (lang == null || lang.isBlank()) ? DEFAULT_LANGUAGE : lang;
    }

    private void addImageAndText(PDDocument document, PDPage page, BufferedImage image, String text) throws IOException {
        PDRectangle mediaBox = page.getMediaBox();
        PDImageXObject pageImage = LosslessFactory.createFromImage(document, image);
        try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.OVERWRITE, true, true)) {
            contentStream.drawImage(pageImage, 0, 0, mediaBox.getWidth(), mediaBox.getHeight());
            if (text != null && !text.isBlank()) {
                PDExtendedGraphicsState graphicsState = new PDExtendedGraphicsState();
                graphicsState.setNonStrokingAlphaConstant(0f);
                contentStream.setGraphicsStateParameters(graphicsState);
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 10);
                contentStream.setLeading(12f);
                float margin = 20f;
                contentStream.newLineAtOffset(margin, mediaBox.getHeight() - margin);
                for (String line : text.split("\\R")) {
                    contentStream.showText(line.replace("\t", " "));
                    contentStream.newLine();
                }
                contentStream.endText();
            }
        }
    }

    private String extractPageText(ITesseract tesseract, BufferedImage image, int pageIndex) throws IOException {
        try {
            return tesseract.doOCR(image);
        } catch (Exception e) {
            throw new IOException("Tesseract OCR failed on page " + (pageIndex + 1) + ": " + e.getMessage(), e);
        }
    }
}
