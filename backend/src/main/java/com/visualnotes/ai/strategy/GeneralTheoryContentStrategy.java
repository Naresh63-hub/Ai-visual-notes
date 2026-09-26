package com.visualnotes.ai.strategy;

import com.visualnotes.diagram.DiagramEngine;
import com.visualnotes.dto.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GeneralTheoryContentStrategy {

    private final DiagramEngine diagramEngine;

    public GeneralTheoryContentStrategy(DiagramEngine diagramEngine) {
        this.diagramEngine = diagramEngine;
    }

    public PageContentDto generate(
            String topic,
            String overallPrompt,
            int pageNumber,
            int totalPages,
            String style,
            String audience,
            String difficulty,
            String focusArea,
            String plannedDiagramType,
            List<String> requirements) {

        DiagramDataDto diagram = diagramEngine.generateDiagram(topic, plannedDiagramType != null ? plannedDiagramType : "concept-map", overallPrompt);

        List<SectionDto> sections = new ArrayList<>();

        // Section 1: Core Concept Breakdown
        sections.add(SectionDto.builder()
                .heading("1. Essential Concepts & Working Mechanism")
                .content(topic + " represents an essential academic and practical foundation with clear principles and systematic behavior:")
                .badge("Fundamentals")
                .bulletPoints(List.of(
                        "Core Purpose: Provides systematic methodology to understand, analyze, and solve core domain problems.",
                        "Governing Rules: Relies on established mathematical, structural, or logical foundations.",
                        "Real-World Impact: Directly applied in engineering, scientific modeling, and computational systems."
                ))
                .highlights(List.of("Systematic structure", "Formal principles", "Practical application"))
                .build());

        // Section 2: Key Properties or Steps
        sections.add(SectionDto.builder()
                .heading("2. Key Properties & Operational Workflow")
                .content("To properly understand and apply " + topic + ", the following structured properties must be observed:")
                .badge("Properties")
                .bulletPoints(List.of(
                        "Input / Preconditions: Well-defined starting parameters or state configurations.",
                        "Processing / Behavior: Systematic transitions or algorithmic steps ensuring deterministic outcomes.",
                        "Output / Verification: Clear evaluation criteria, correctness criteria, and results."
                ))
                .highlights(List.of("Preconditions", "Processing", "Verification"))
                .build());

        // Concrete Example
        ExampleDto example = ExampleDto.builder()
                .title("Applied Demonstration of " + topic)
                .scenario("Step-by-step application of " + topic + " in a typical exam or practical scenario.")
                .input("Standard configuration / test case for " + topic)
                .stepByStep(List.of(
                        "1. Identify system inputs and initial parameters",
                        "2. Execute the governing rules or transformations",
                        "3. Verify consistency and observe the resulting state"
                ))
                .outputOrResult("Deterministic, valid outcome adhering to core rules.")
                .takeaway("Consistent application of fundamental principles guarantees correct results.")
                .build();

        List<ExamTipDto> examTips = List.of(
                ExamTipDto.builder()
                        .tip("Exam Advice: Always begin by defining the core terminology clearly before detailing properties or drawing diagrams.")
                        .commonMistake("Omitting boundary conditions or failing to label diagram elements clearly.")
                        .mnemonic("D ➔ P ➔ E (Definition ➔ Properties ➔ Example)")
                        .build()
        );

        return PageContentDto.builder()
                .documentTitle(topic)
                .pageNumber(pageNumber)
                .totalPages(totalPages)
                .topicTitle(topic)
                .topicSubtitle(focusArea != null ? focusArea : "Academic Concept & Examination Revision")
                .categoryBadge("Study Notes")
                .difficultyLevel(difficulty)
                .definition(topic + " is an essential concept characterized by structured principles and practical importance.")
                .sections(sections)
                .example(example)
                .diagram(diagram)
                .examTips(examTips)
                .keyPoints(List.of(
                        KeyPointDto.builder().point("Understand the foundational definition and underlying rules before approaching problem solving.").starred(true).category("Concept").build(),
                        KeyPointDto.builder().point("Structure answers in exams with definition, key properties, diagrams, and a worked example.").starred(true).category("Exam").build()
                ))
                .build();
    }
}
