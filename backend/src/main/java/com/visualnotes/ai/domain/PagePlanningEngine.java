package com.visualnotes.ai.domain;

import com.visualnotes.dto.PageContentDto;
import com.visualnotes.dto.PagePlanDto;
import com.visualnotes.dto.PagePlanItemDto;
import com.visualnotes.dto.SectionDto;
import com.visualnotes.dto.SemanticDocumentDto;
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
        List<PagePlanItemDto> pages = new ArrayList<>();

        if (understanding.isMultiEntityComparison() && understanding.getTargetEntities().size() >= 2 && pagesNum >= 2) {
            // Multi-entity comparison (e.g. TCP vs UDP, Stack vs Queue)
            List<String> entities = understanding.getTargetEntities();
            for (int i = 0; i < pagesNum; i++) {
                if (i < entities.size()) {
                    String entity = entities.get(i);
                    pages.add(PagePlanItemDto.builder()
                            .pageNumber(i + 1)
                            .pageTitle(entity + " — Operational Principles & Model")
                            .topics(List.of(entity))
                            .focusArea("Definition, core mechanism, characteristics, and architectural role of " + entity)
                            .plannedDiagramType(i == 0 ? teachingPlan.getDiagramType() : "concept-map")
                            .estimatedDensity("Balanced")
                            .keyConceptsSummary("Dedicated study page for " + entity)
                            .build());
                } else {
                    pages.add(PagePlanItemDto.builder()
                            .pageNumber(i + 1)
                            .pageTitle(topic + " — Comparative Trade-offs & Analysis")
                            .topics(List.of(topic))
                            .focusArea("Side-by-side comparison table, performance trade-offs, and practical selection criteria")
                            .plannedDiagramType("concept-map")
                            .estimatedDensity("High Yield")
                            .keyConceptsSummary("Comprehensive comparison and summary")
                            .build());
                }
            }
        } else if (pagesNum == 1) {
            // Single page unified synthesis
            pages.add(PagePlanItemDto.builder()
                    .pageNumber(1)
                    .pageTitle(topic)
                    .topics(List.of(topic))
                    .focusArea("Comprehensive synthesis: Formal definition, visual diagram, core mechanics, and key takeaways")
                    .plannedDiagramType(teachingPlan.getDiagramType())
                    .estimatedDensity("High Yield")
                    .keyConceptsSummary("Complete one-page handwritten study note")
                    .build());
        } else if (pagesNum == 2) {
            // 2-page logical progression for single topic with scope
            String part1Title = topic + " — Part 1: Foundations & Architecture";
            String part2Title = topic + (understanding.getRequestedScope() != null && !understanding.getRequestedScope().isEmpty()
                    ? " — Part 2: Detailed Types & Analysis"
                    : " — Part 2: Formulations, Trace & Applications");

            pages.add(PagePlanItemDto.builder()
                    .pageNumber(1)
                    .pageTitle(part1Title)
                    .topics(List.of(topic))
                    .focusArea("Core definition, foundational intuition, and primary visual architecture diagram")
                    .plannedDiagramType(teachingPlan.getDiagramType())
                    .estimatedDensity("Spacious & Clear")
                    .keyConceptsSummary("Foundational Intuition and Visual Architecture")
                    .build());

            pages.add(PagePlanItemDto.builder()
                    .pageNumber(2)
                    .pageTitle(part2Title)
                    .topics(List.of(topic))
                    .focusArea(understanding.getDomain() == DomainType.ALGORITHMS
                            ? "Step-by-step algorithm trace, complexity analysis, and worked examples"
                            : "Detailed taxonomy, comparison table, worked application, and high-yield exam takeaways")
                    .plannedDiagramType("formula-math")
                    .estimatedDensity("Spacious & Clear")
                    .keyConceptsSummary("Detailed Analysis, Worked Examples, and Exam Takeaways")
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

    public PageContentDto distributeNoteDocument(
            SemanticDocumentDto doc,
            int pageNumber,
            int totalPages,
            String style,
            String audience,
            String difficulty) {

        String topic = doc.getTopicTitle();
        PageContentDto page = new PageContentDto();
        page.setDocumentTitle(topic + (totalPages > 1 ? " (" + totalPages + " Pages)" : ""));
        page.setPageNumber(pageNumber);
        page.setTotalPages(totalPages);
        page.setTopicTitle(topic);
        page.setTopicSubtitle(doc.getTopicSubtitle());
        page.setCategoryBadge(doc.getCategoryBadge());
        page.setDomain(doc.getDomain());
        page.setSubdomain(doc.getSubdomain());
        page.setDifficultyLevel(difficulty != null ? difficulty : doc.getDifficultyLevel());
        page.setStyleTheme(style != null ? style : "Handwritten");

        if (totalPages > 1) {
            page.setPagePartTitle("Part " + pageNumber + " of " + totalPages);
            page.setIsContinuation(pageNumber > 1);
            page.setContinuesOnNextPage(pageNumber < totalPages);
        }

        // Definition & simple explanation on Page 1
        if (pageNumber == 1) {
            page.setDefinition(doc.getDefinition());
            page.setSimpleExplanation(doc.getSimpleExplanation());
            page.setPurpose(doc.getPurpose());
        }

        // Distribute sections across pages
        List<SectionDto> allSections = doc.getSections();
        if (allSections != null && !allSections.isEmpty()) {
            if (allSections.size() >= totalPages) {
                int startIdx = (pageNumber - 1) * allSections.size() / totalPages;
                int endIdx = pageNumber * allSections.size() / totalPages;
                if (startIdx < endIdx) {
                    page.setSections(new ArrayList<>(allSections.subList(startIdx, endIdx)));
                }
            } else {
                // If fewer sections than pages, distribute each section to early pages
                if (pageNumber <= allSections.size()) {
                    page.setSections(new ArrayList<>(List.of(allSections.get(pageNumber - 1))));
                }
            }
        }

        // Diagram on Page 1 (or Page 2 if totalPages > 2)
        if (doc.getDiagram() != null && (pageNumber == 1 || (totalPages > 2 && pageNumber == 2))) {
            page.setDiagram(doc.getDiagram());
        }

        // Comparison Table on last page or single page or page 2
        if (doc.getComparisonTable() != null && (pageNumber == totalPages || totalPages == 1 || (totalPages <= 3 && pageNumber == 2))) {
            page.setComparisonTable(doc.getComparisonTable());
        }

        // Formula on Page 1 or Page 2
        if (doc.getFormula() != null && (pageNumber == 1 || totalPages == 1 || (totalPages >= 2 && pageNumber == 2))) {
            page.setFormula(doc.getFormula());
        }

        // Algorithm & Pseudocode
        if (doc.getAlgorithm() != null && (pageNumber == totalPages || totalPages == 1 || pageNumber == 2 || (totalPages >= 3 && pageNumber == 3))) {
            page.setAlgorithm(doc.getAlgorithm());
            page.setPseudocode(doc.getPseudocode());
        }

        // Complexity
        if (doc.getComplexity() != null && (pageNumber == totalPages || totalPages == 1)) {
            page.setComplexity(doc.getComplexity());
        }

        // Worked Example: On penultimate page for 4+ pages, or Page 2 for 2-3 pages, or Page 1 for 1 page
        if (doc.getExample() != null) {
            if (totalPages == 1 && pageNumber == 1) {
                page.setExample(doc.getExample());
            } else if (totalPages <= 3 && pageNumber == 2) {
                page.setExample(doc.getExample());
            } else if (totalPages >= 4 && (pageNumber == totalPages - 1 || pageNumber == totalPages)) {
                page.setExample(doc.getExample());
            }
        }

        // Key Points & Exam Tips on last page or single page
        if (pageNumber == totalPages || totalPages == 1) {
            if (doc.getKeyPoints() != null && !doc.getKeyPoints().isEmpty()) {
                page.setKeyPoints(doc.getKeyPoints());
            }
            if (doc.getExamTips() != null && !doc.getExamTips().isEmpty()) {
                page.setExamTips(doc.getExamTips());
            }
        }

        // Failsafe Guard: Ensure NO page in a multi-page document is ever blank!
        boolean hasContent = (page.getSections() != null && !page.getSections().isEmpty())
                || page.getDefinition() != null
                || page.getDiagram() != null
                || page.getFormula() != null
                || page.getAlgorithm() != null
                || page.getExample() != null
                || page.getComparisonTable() != null
                || (page.getKeyPoints() != null && !page.getKeyPoints().isEmpty())
                || (page.getExamTips() != null && !page.getExamTips().isEmpty());

        if (!hasContent) {
            if (doc.getExample() != null && page.getExample() == null) {
                page.setExample(doc.getExample());
            } else if (doc.getComparisonTable() != null && page.getComparisonTable() == null) {
                page.setComparisonTable(doc.getComparisonTable());
            } else if (doc.getFormula() != null && page.getFormula() == null) {
                page.setFormula(doc.getFormula());
            } else if (allSections != null && !allSections.isEmpty()) {
                int fallbackIdx = Math.min(allSections.size() - 1, Math.max(0, pageNumber - 1));
                page.setSections(new ArrayList<>(List.of(allSections.get(fallbackIdx))));
            }
        }

        return page;
    }
}
