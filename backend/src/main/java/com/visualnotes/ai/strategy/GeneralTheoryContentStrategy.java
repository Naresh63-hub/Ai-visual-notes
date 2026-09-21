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

        DiagramDataDto diagram = diagramEngine.generateDiagram(topic, plannedDiagramType, overallPrompt);

        List<SectionDto> sections = new ArrayList<>();
        sections.add(SectionDto.builder()
                .heading("1. Core Principles & Foundational Architecture")
                .content(topic + " is characterized by structured principles, established theoretical mechanisms, and practical applications.")
                .badge("Foundations")
                .bulletPoints(List.of(
                        "Primary Objective: Systematic modeling and robust analytical understanding",
                        "Key Concepts: Essential entities, relationships, and operational rules",
                        "Practical Value: Real-world problem solving and domain implementation"
                ))
                .highlights(List.of("Systematic structure", "Domain principles", "Practical application"))
                .build());

        sections.add(SectionDto.builder()
                .heading("2. Key Mechanisms & Operations")
                .content("The core workflow of " + topic + " progresses through structured stages ensuring consistency and precision.")
                .badge("Mechanisms")
                .bulletPoints(List.of(
                        "Stage 1: Core foundation and initial conditions",
                        "Stage 2: Operational dynamics and transformations",
                        "Stage 3: Application, synthesis, and key outcomes"
                ))
                .highlights(List.of("Foundation", "Operational dynamics", "Key outcomes"))
                .build());

        ExampleDto example = ExampleDto.builder()
                .title("Practical Application Example")
                .scenario("Application demonstration for " + topic)
                .stepByStep(List.of(
                        "1. Identify core system parameters and context.",
                        "2. Apply domain principles and rules methodically.",
                        "3. Verify and interpret the final result."
                ))
                .outputOrResult("Clear and validated practical outcome achieved.")
                .takeaway("Consistent application of principles guarantees reliable results.")
                .build();

        return PageContentDto.builder()
                .documentTitle(topic)
                .pageNumber(pageNumber)
                .totalPages(totalPages)
                .topicTitle(topic)
                .topicSubtitle(focusArea != null ? focusArea : "Comprehensive Visual Study Guide")
                .categoryBadge(audience != null ? audience : "Academic Notes")
                .difficultyLevel(difficulty != null ? difficulty : "Intermediate")
                .pagePartTitle(totalPages > 1 ? ("Part " + pageNumber + " of " + totalPages + ": Conceptual Overview") : "Complete Study Notes")
                .definition(topic + " is an important academic topic with structured principles, operational frameworks, and wide practical applications.")
                .mainIdea("Understand the core concepts, internal dynamics, and practical outcomes.")
                .simpleExplanation("In simple terms, " + topic + " provides a clear, structured method to understand and solve domain problems.")
                .sections(sections)
                .diagram(diagram)
                .example(example)
                .advantages(List.of("Clear structured understanding", "Broad real-world application", "Consistent and predictable outcomes"))
                .limitations(List.of("Requires foundational domain prerequisites", "Complexity increases with advanced use cases"))
                .keyPoints(List.of(
                        KeyPointDto.builder().point("Master the fundamental definitions and principles first.").starred(true).category("Rule").build(),
                        KeyPointDto.builder().point("Study the visual relationships and diagrams to build strong intuition.").starred(true).category("Intuition").build()
                ))
                .examTips(List.of(
                        ExamTipDto.builder().tip("In exams, define the topic clearly, draw labeled diagrams, and structure answers in numbered points.").mnemonic("Define ➔ Draw ➔ Detail").build()
                ))
                .quickTakeaways(List.of(
                        "Master the definition & fundamental principles",
                        "Review the visual diagram & core mechanisms",
                        "Remember key takeaways & exam high-yield points"
                ))
                .continuesOnNextPage(pageNumber < totalPages)
                .isContinuation(pageNumber > 1)
                .layoutHint("handwritten-part1")
                .styleTheme(style)
                .build();
    }
}
