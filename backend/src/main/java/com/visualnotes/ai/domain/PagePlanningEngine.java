package com.visualnotes.ai.domain;

import com.visualnotes.dto.PagePlanDto;
import com.visualnotes.dto.PagePlanItemDto;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class PagePlanningEngine {

    public PagePlanDto plan(
            String rawPrompt,
            int requestedPages,
            TopicUnderstandingEngine.TopicUnderstandingResult understanding,
            ContentPlanningEngine.TeachingPlan teachingPlan) {

        int pagesNum = Math.max(1, Math.min(10, requestedPages));
        String topic = understanding.getNormalizedTopic();
        List<String> subtopics = understanding.getPrimarySubtopics();
        List<PagePlanItemDto> pages = new ArrayList<>();

        if (subtopics.size() > 1 && pagesNum >= subtopics.size()) {
            // Multi-topic / Comparison distribution
            for (int i = 0; i < pagesNum; i++) {
                String pageTopic = (i < subtopics.size()) ? subtopics.get(i) : (topic + " — Synthesis & Comparison");
                pages.add(PagePlanItemDto.builder()
                        .pageNumber(i + 1)
                        .pageTitle(pageTopic)
                        .topics(List.of(pageTopic))
                        .focusArea("Definition, formal properties, visual model, and key invariants for " + pageTopic)
                        .plannedDiagramType(i == 0 ? teachingPlan.getDiagramType() : "concept-map")
                        .estimatedDensity("Balanced")
                        .keyConceptsSummary("Dedicated study page for " + pageTopic)
                        .build());
            }
        } else if (pagesNum == 1) {
            // Single page unified synthesis
            pages.add(PagePlanItemDto.builder()
                    .pageNumber(1)
                    .pageTitle(topic)
                    .topics(List.of(topic))
                    .focusArea("Comprehensive single-page synthesis: Core definitions, visual diagram, governing rules, and high-yield takeaways")
                    .plannedDiagramType(teachingPlan.getDiagramType())
                    .estimatedDensity("High Yield")
                    .keyConceptsSummary("Complete one-page handwritten study note")
                    .build());
        } else if (pagesNum == 2) {
            // 2-page logical progression
            pages.add(PagePlanItemDto.builder()
                    .pageNumber(1)
                    .pageTitle(topic + " — Part 1: Foundations & Architecture")
                    .topics(List.of(topic))
                    .focusArea("Definitions, core principles, intuition, and primary visual state diagram")
                    .plannedDiagramType(teachingPlan.getDiagramType())
                    .estimatedDensity("Spacious & Clear")
                    .keyConceptsSummary("Foundational Intuition and Visual Architecture")
                    .build());

            pages.add(PagePlanItemDto.builder()
                    .pageNumber(2)
                    .pageTitle(topic + " — Part 2: Formulations, Trace & Exam Prep")
                    .topics(List.of(topic))
                    .focusArea(understanding.getDomain() == DomainType.ALGORITHMS
                            ? "Step-by-step algorithm, trace example, complexity proof, and exam points"
                            : "Governing equations, comparison table, worked application, and exam tips")
                    .plannedDiagramType("formula-math")
                    .estimatedDensity("Spacious & Clear")
                    .keyConceptsSummary("Detailed Dynamics, Worked Example, and Exam High-Yield Points")
                    .build());
        } else {
            // Multi-page (3+ pages)
            for (int i = 1; i <= pagesNum; i++) {
                pages.add(PagePlanItemDto.builder()
                        .pageNumber(i)
                        .pageTitle(topic + " — Part " + i + " of " + pagesNum)
                        .topics(List.of(topic))
                        .focusArea("In-depth analytical breakdown part " + i + " for " + topic)
                        .plannedDiagramType(i == 1 ? teachingPlan.getDiagramType() : "concept-map")
                        .estimatedDensity("Spacious")
                        .keyConceptsSummary("Academic Section " + i)
                        .build());
            }
        }

        String docTitle = topic + (pagesNum > 1 ? (" (" + pagesNum + " Pages)") : "");

        return PagePlanDto.builder()
                .documentTitle(docTitle)
                .totalPages(pagesNum)
                .overallSummary("Pedagogically planned handwritten notes spanning " + pagesNum + " page(s) in " + understanding.getDomain().getDisplayName() + ".")
                .style("Handwritten")
                .audience(understanding.getAcademicAudience())
                .difficulty(understanding.getDifficulty())
                .pages(pages)
                .build();
    }
}
