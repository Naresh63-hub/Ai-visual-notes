package com.visualnotes.service;

import com.visualnotes.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExportServiceTest {

    @Mock
    private NoteService noteService;

    private ExportService exportService;

    @BeforeEach
    void setUp() {
        exportService = new ExportService(noteService);
    }

    @Test
    void testExportPdfWithAllSectionsAndUnicode() throws IOException {
        PageContentDto content = new PageContentDto();
        content.setTopicTitle("Force & Momentum: \u03a3F = dp/dt");
        content.setTopicSubtitle("Classical Mechanics Principles");
        content.setDomain("PHYSICS");
        content.setDefinition("Newton's 2nd Law defines force as F = m \u00d7 a with vectors \u2192.");
        content.setSimpleExplanation("Net force causes acceleration directly proportional to force.");
        
        FormulaDto formula = FormulaDto.builder()
                .title("Second Law")
                .expression("F_{net} = m \u00b7 a = \\frac{dp}{dt}")
                .explanation("Force relates to momentum derivative")
                .build();
        content.setFormula(formula);

        AlgorithmStepDto step1 = new AlgorithmStepDto(1, "System Isolation", "Draw free body diagram", "FBD");
        AlgorithmStepDto step2 = new AlgorithmStepDto(2, "Sum Forces", "\u03a3F_x = m\u00b7a_x, \u03a3F_y = m\u00b7a_y", "Vectors");
        content.setAlgorithm(List.of(step1, step2));

        content.setPseudocode("for i in 1..n:\n  net_force += f[i]\naccel = net_force / mass");
        
        ComplexityDto complexity = ComplexityDto.builder()
                .timeComplexitySummary("O(1) constant")
                .spaceComplexitySummary("O(1) aux")
                .explanation("Direct vector algebra evaluation")
                .build();
        content.setComplexity(complexity);

        ExampleDto example = ExampleDto.builder()
                .title("Elevator Problem")
                .scenario("Mass 100 kg accelerating at 2 m/s\u00b2")
                .input("m = 100 kg, a = 2 m/s\u00b2")
                .stepByStep(List.of("Identify forces: Normal force up, Gravity down", "Apply N - mg = ma", "Solve N = m(g + a) = 100(9.8 + 2) = 1180 N"))
                .outputOrResult("N = 1180 N")
                .takeaway("Apparent weight increases during upward acceleration")
                .build();
        content.setExample(example);

        ExamTipDto tip = ExamTipDto.builder()
                .tip("Always establish a sign convention (+up, -down) before summing vectors \u2192!")
                .commonMistake("Forgetting gravity in vertical acceleration problems")
                .mnemonic("F=MA (Find Mass & Acceleration)")
                .build();
        content.setExamTips(List.of(tip));

        KeyPointDto kp = KeyPointDto.builder()
                .point("Direction of acceleration is identical to net force vector \u2192.")
                .starred(true)
                .category("Critical")
                .build();
        content.setKeyPoints(List.of(kp));

        NotePageDto pageDto = NotePageDto.builder()
                .id(1L)
                .pageNumber(1)
                .topicTitle("Force & Momentum")
                .content(content)
                .build();

        NoteDocumentDto docDto = NoteDocumentDto.builder()
                .id(1L)
                .title("Newton's 2nd Law & Binary Search \u2264 \u2265 \u2260 \u03a9 \u0398 \u2192 \u2190 \u2022")
                .style("Handwritten")
                .pages(List.of(pageDto))
                .build();

        when(noteService.getDocumentById(1L)).thenReturn(docDto);

        byte[] pdfBytes = exportService.generatePdf(1L);
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 500, "Generated PDF must contain valid header and content bytes");
        
        String header = new String(pdfBytes, 0, Math.min(5, pdfBytes.length));
        assertEquals("%PDF-", header);
    }
}
