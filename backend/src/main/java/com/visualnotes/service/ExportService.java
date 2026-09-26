package com.visualnotes.service;

import com.visualnotes.dto.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class ExportService {

    private static final Logger log = LoggerFactory.getLogger(ExportService.class);

    private final NoteService noteService;

    public ExportService(NoteService noteService) {
        this.noteService = noteService;
    }

    public byte[] generatePdf(Long documentId) throws IOException {
        NoteDocumentDto documentDto = noteService.getDocumentById(documentId);

        try (PDDocument document = new PDDocument()) {
            PDType1Font fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font fontRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            PDType1Font fontOblique = new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE);
            PDType1Font fontCourier = new PDType1Font(Standard14Fonts.FontName.COURIER);

            for (NotePageDto pageDto : documentDto.getPages()) {
                PDPage page = new PDPage(PDRectangle.A4);
                document.addPage(page);

                PageContentDto content = pageDto.getContent();

                try (PDPageContentStream stream = new PDPageContentStream(document, page)) {
                    float margin = 36;
                    float startY = 800;
                    float y = startY;

                    // Border box
                    stream.setLineWidth(1.5f);
                    stream.setStrokingColor(0.2f, 0.35f, 0.75f);
                    stream.addRect(margin, margin, PDRectangle.A4.getWidth() - 2 * margin, PDRectangle.A4.getHeight() - 2 * margin);
                    stream.stroke();

                    // Header: Topic Title
                    stream.beginText();
                    stream.setFont(fontBold, 16);
                    stream.setNonStrokingColor(0.1f, 0.2f, 0.5f);
                    stream.newLineAtOffset(margin + 12, y);
                    stream.showText(cleanText(content.getTopicTitle()));
                    stream.endText();

                    // Page number badge
                    stream.beginText();
                    stream.setFont(fontBold, 9);
                    stream.setNonStrokingColor(0.4f, 0.4f, 0.4f);
                    stream.newLineAtOffset(PDRectangle.A4.getWidth() - margin - 80, y);
                    stream.showText("Page " + content.getPageNumber() + " of " + content.getTotalPages());
                    stream.endText();

                    y -= 20;

                    // Subtitle / Category
                    if (content.getTopicSubtitle() != null || content.getCategoryBadge() != null) {
                        String sub = (content.getCategoryBadge() != null ? "[" + content.getCategoryBadge() + "] " : "") +
                                (content.getTopicSubtitle() != null ? content.getTopicSubtitle() : "");
                        stream.beginText();
                        stream.setFont(fontOblique, 10);
                        stream.setNonStrokingColor(0.3f, 0.3f, 0.4f);
                        stream.newLineAtOffset(margin + 12, y);
                        stream.showText(cleanText(sub));
                        stream.endText();
                        y -= 16;
                    }

                    // Divider rule
                    stream.setLineWidth(0.8f);
                    stream.setStrokingColor(0.75f, 0.75f, 0.8f);
                    stream.moveTo(margin + 12, y);
                    stream.lineTo(PDRectangle.A4.getWidth() - margin - 12, y);
                    stream.stroke();
                    y -= 16;

                    // 1. Definition
                    if (content.getDefinition() != null && y > 120) {
                        stream.beginText();
                        stream.setFont(fontBold, 11);
                        stream.setNonStrokingColor(0.1f, 0.1f, 0.1f);
                        stream.newLineAtOffset(margin + 12, y);
                        stream.showText("1. Definition & Core Concept");
                        stream.endText();
                        y -= 13;

                        y = renderWrappedText(stream, fontRegular, 9.5f, content.getDefinition(), margin + 12, y, 500);
                        y -= 8;
                    }

                    // 2. Simple Explanation
                    if (content.getSimpleExplanation() != null && y > 120) {
                        stream.beginText();
                        stream.setFont(fontBold, 10.5f);
                        stream.setNonStrokingColor(0.1f, 0.45f, 0.25f);
                        stream.newLineAtOffset(margin + 12, y);
                        stream.showText("2. Simple Intuition:");
                        stream.endText();
                        y -= 13;

                        y = renderWrappedText(stream, fontRegular, 9.5f, content.getSimpleExplanation(), margin + 12, y, 500);
                        y -= 8;
                    }

                    // Comparison Table (if present)
                    if (content.getComparisonTable() != null && y > 120) {
                        stream.beginText();
                        stream.setFont(fontBold, 10.5f);
                        stream.setNonStrokingColor(0.1f, 0.2f, 0.5f);
                        stream.newLineAtOffset(margin + 12, y);
                        stream.showText("Comparison: " + cleanText(content.getComparisonTable().getTitle()));
                        stream.endText();
                        y -= 13;

                        // Headers
                        if (content.getComparisonTable().getHeaders() != null) {
                            String headerLine = String.join(" | ", content.getComparisonTable().getHeaders());
                            y = renderWrappedText(stream, fontBold, 9.0f, headerLine, margin + 18, y, 490);
                        }
                        // Rows
                        if (content.getComparisonTable().getRows() != null) {
                            for (java.util.List<String> row : content.getComparisonTable().getRows()) {
                                if (y < 80) break;
                                String rowLine = String.join(" | ", row);
                                y = renderWrappedText(stream, fontRegular, 8.5f, "• " + rowLine, margin + 18, y, 490);
                            }
                        }
                        if (content.getComparisonTable().getConclusion() != null && y > 80) {
                            y = renderWrappedText(stream, fontOblique, 8.5f, "Takeaway: " + content.getComparisonTable().getConclusion(), margin + 18, y, 490);
                        }
                        y -= 8;
                    }

                    // 3. Formula Box (if present)
                    if (content.getFormula() != null && y > 120) {
                        stream.beginText();
                        stream.setFont(fontBold, 10.5f);
                        stream.setNonStrokingColor(0.45f, 0.15f, 0.6f);
                        stream.newLineAtOffset(margin + 12, y);
                        stream.showText("Formulation: " + cleanText(content.getFormula().getTitle()));
                        stream.endText();
                        y -= 13;

                        stream.beginText();
                        stream.setFont(fontCourier, 9.5f);
                        stream.setNonStrokingColor(0.2f, 0.1f, 0.4f);
                        stream.newLineAtOffset(margin + 18, y);
                        stream.showText(cleanText(content.getFormula().getExpression()));
                        stream.endText();
                        y -= 12;

                        if (content.getFormula().getExplanation() != null) {
                            y = renderWrappedText(stream, fontRegular, 9.0f, content.getFormula().getExplanation(), margin + 18, y, 490);
                        }
                        y -= 8;
                    }

                    // 4. Algorithm / Pseudocode (if present)
                    if (content.getAlgorithm() != null && !content.getAlgorithm().isEmpty() && y > 120) {
                        stream.beginText();
                        stream.setFont(fontBold, 10.5f);
                        stream.setNonStrokingColor(0.1f, 0.2f, 0.5f);
                        stream.newLineAtOffset(margin + 12, y);
                        stream.showText("Step-by-Step Algorithm:");
                        stream.endText();
                        y -= 13;

                        for (AlgorithmStepDto step : content.getAlgorithm()) {
                            if (y < 90) break;
                            String stepLine = step.getStepNumber() + ". " + step.getInstruction() +
                                    (step.getCodeSnippet() != null ? " -> [" + step.getCodeSnippet() + "]" : "");
                            y = renderWrappedText(stream, fontRegular, 9.0f, stepLine, margin + 18, y, 490);
                        }
                        y -= 8;
                    }

                    // 5. Structured Sections
                    if (content.getSections() != null) {
                        for (SectionDto section : content.getSections()) {
                            if (y < 90) break;
                            stream.beginText();
                            stream.setFont(fontBold, 10.5f);
                            stream.setNonStrokingColor(0.2f, 0.2f, 0.2f);
                            stream.newLineAtOffset(margin + 12, y);
                            stream.showText(cleanText(section.getHeading()));
                            stream.endText();
                            y -= 13;

                            if (section.getContent() != null) {
                                y = renderWrappedText(stream, fontRegular, 9.0f, section.getContent(), margin + 18, y, 490);
                            }
                            if (section.getBulletPoints() != null) {
                                for (String bp : section.getBulletPoints()) {
                                    if (y < 80) break;
                                    y = renderWrappedText(stream, fontRegular, 9.0f, "- " + bp, margin + 22, y, 485);
                                }
                            }
                            y -= 6;
                        }
                    }

                    // 6. Worked Example (if present)
                    if (content.getExample() != null && y > 100) {
                        stream.beginText();
                        stream.setFont(fontBold, 10.5f);
                        stream.setNonStrokingColor(0.6f, 0.35f, 0.05f);
                        stream.newLineAtOffset(margin + 12, y);
                        stream.showText("Worked Example: " + cleanText(content.getExample().getTitle()));
                        stream.endText();
                        y -= 13;

                        if (content.getExample().getScenario() != null) {
                            y = renderWrappedText(stream, fontOblique, 9.0f, content.getExample().getScenario(), margin + 18, y, 490);
                        }
                        if (content.getExample().getStepByStep() != null) {
                            for (String s : content.getExample().getStepByStep()) {
                                if (y < 80) break;
                                y = renderWrappedText(stream, fontRegular, 8.5f, "* " + s, margin + 22, y, 485);
                            }
                        }
                        if (content.getExample().getOutputOrResult() != null && y > 80) {
                            y = renderWrappedText(stream, fontBold, 9.0f, "Result: " + content.getExample().getOutputOrResult(), margin + 18, y, 490);
                        }
                        y -= 6;
                    }

                    // 7. Complexity (if present)
                    if (content.getComplexity() != null && y > 90) {
                        stream.beginText();
                        stream.setFont(fontBold, 10.5f);
                        stream.setNonStrokingColor(0.6f, 0.15f, 0.1f);
                        stream.newLineAtOffset(margin + 12, y);
                        stream.showText("Complexity Analysis");
                        stream.endText();
                        y -= 13;

                        String compText = "Time (Best/Avg/Worst): " + content.getComplexity().getTimeBest() + " / " +
                                content.getComplexity().getTimeAverage() + " / " + content.getComplexity().getTimeWorst() +
                                " | Space: " + content.getComplexity().getSpace();
                        y = renderWrappedText(stream, fontRegular, 9.0f, compText, margin + 18, y, 490);
                        y -= 6;
                    }

                    // 8. Exam Tips (if present)
                    if (content.getExamTips() != null && !content.getExamTips().isEmpty() && y > 80) {
                        ExamTipDto tip = content.getExamTips().get(0);
                        stream.beginText();
                        stream.setFont(fontBold, 10.0f);
                        stream.setNonStrokingColor(0.7f, 0.1f, 0.2f);
                        stream.newLineAtOffset(margin + 12, y);
                        stream.showText("Exam High-Yield Tip:");
                        stream.endText();
                        y -= 12;

                        y = renderWrappedText(stream, fontRegular, 8.5f, tip.getTip(), margin + 18, y, 490);
                        if (tip.getMnemonic() != null && y > 70) {
                            y = renderWrappedText(stream, fontBold, 8.5f, "Mnemonic: " + tip.getMnemonic(), margin + 18, y, 490);
                        }
                    }
                }
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);
            return outputStream.toByteArray();
        }
    }

    private float renderWrappedText(PDPageContentStream stream, PDType1Font font, float fontSize, String text, float x, float y, float maxWidth) throws IOException {
        if (text == null || text.trim().isEmpty()) return y;

        String cleaned = cleanText(text);
        String[] words = cleaned.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;
            float width = font.getStringWidth(testLine) / 1000 * fontSize;

            if (width > maxWidth && currentLine.length() > 0) {
                if (y < 60) return y;
                stream.beginText();
                stream.setFont(font, fontSize);
                stream.setNonStrokingColor(0.15f, 0.15f, 0.15f);
                stream.newLineAtOffset(x, y);
                stream.showText(currentLine.toString());
                stream.endText();
                y -= (fontSize + 3.5f);
                currentLine = new StringBuilder(word);
            } else {
                currentLine = new StringBuilder(testLine);
            }
        }

        if (currentLine.length() > 0 && y >= 50) {
            stream.beginText();
            stream.setFont(font, fontSize);
            stream.setNonStrokingColor(0.15f, 0.15f, 0.15f);
            stream.newLineAtOffset(x, y);
            stream.showText(currentLine.toString());
            stream.endText();
            y -= (fontSize + 3.5f);
        }

        return y;
    }

    private String cleanText(String text) {
        if (text == null) return "";
        return text
                .replace("→", "->")
                .replace("➔", "->")
                .replace("⟹", "==>")
                .replace("⟷", "<->")
                .replace("Σ", "Sum ")
                .replace("∑", "Sum ")
                .replace("F⃗", "F_vec")
                .replace("v⃗", "v_vec")
                .replace("a⃗", "a_vec")
                .replace("p⃗", "p_vec")
                .replace("r⃗", "r_vec")
                .replace("∇", "Grad ")
                .replace("θ", "theta")
                .replace("η", "eta")
                .replace("ℓ", "loss")
                .replace("²", "^2")
                .replace("³", "^3")
                .replace("½", "1/2")
                .replace("₁", "1")
                .replace("₂", "2")
                .replace("₃", "3")
                .replace("₄", "4")
                .replace("₅", "5")
                .replace("₆", "6")
                .replace("₇", "7")
                .replace("₈", "8")
                .replace("₉", "9")
                .replace("₀", "0")
                .replace("⁺", "+")
                .replace("⁻", "-")
                .replace("✓", "[OK]")
                .replace("✗", "[X]")
                .replace("★", "*")
                .replace("•", "-")
                .replace("Ω", " Ohm")
                .replace("ρ", "rho")
                .replaceAll("[^\\x20-\\x7E]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }
}
