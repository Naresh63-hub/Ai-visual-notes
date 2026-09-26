package com.visualnotes.ai.domain;

import com.visualnotes.dto.PageContentDto;
import com.visualnotes.dto.SectionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ContentQualityValidator {

    private static final Logger log = LoggerFactory.getLogger(ContentQualityValidator.class);

    private static final Set<String> SUSPICIOUS_GENERIC_HEADINGS = Set.of(
            "core principles",
            "foundational architecture",
            "operational mechanics",
            "workflow",
            "advantages & strengths",
            "limitations & trade-offs",
            "complexity & exams",
            "step-by-step algorithm",
            "pseudocode implementation"
    );

    public void validateAndSanitize(
            PageContentDto page,
            TopicUnderstandingEngine.TopicUnderstandingResult understanding,
            ContentPlanningEngine.TeachingPlan plan) {

        if (page == null) return;
        DomainType domain = understanding.getDomain();
        String topic = understanding.getNormalizedTopic();

        // 1. Topic Title Cleaning
        if (page.getTopicTitle() != null) {
            String title = page.getTopicTitle();
            title = title.replaceAll("(?i)\\s+(with physical vector equations|with vector equations|everyday examples|with algorithm|and complexity|for 10 marks|for 5 marks|for 2 marks).*$", "");
            page.setTopicTitle(title.trim());
        }

        // 2. Section-by-Section Relevance Scoring & Template Contamination Removal
        if (page.getSections() != null && !page.getSections().isEmpty()) {
            List<SectionDto> cleanedSections = new ArrayList<>();
            for (SectionDto section : page.getSections()) {
                double relevanceScore = calculateSectionRelevance(section, understanding, plan);
                if (relevanceScore >= 0.5) {
                    cleanedSections.add(section);
                } else {
                    log.warn("Critic Pass: Purging low-relevance/template-contaminated section '{}' (score: {}) for topic '{}'",
                            section.getHeading(), relevanceScore, topic);
                }
            }
            page.setSections(cleanedSections.isEmpty() ? null : cleanedSections);
        }

        // 3. Domain Purity: Remove CS algorithms & Big-O complexity on non-algorithmic topics
        if (domain != DomainType.ALGORITHMS && domain != DomainType.DATA_STRUCTURES && domain != DomainType.PROGRAMMING) {
            if (page.getAlgorithm() != null && !page.getAlgorithm().isEmpty()) {
                log.warn("Critic Pass: Detected algorithm in non-CS domain {}. Removing algorithm steps.", domain);
                page.setAlgorithm(null);
                page.setPseudocode(null);
            }

            if (page.getComplexity() != null) {
                log.warn("Critic Pass: Detected Big-O complexity in non-algorithmic domain {}. Clearing complexity.", domain);
                page.setComplexity(null);
            }
        }

        // 4. Pure Definition Rule: If user requested "Define X", do not generate code or execution algorithms
        if (understanding.getIntent() == TopicUnderstandingEngine.UserIntent.DEFINE) {
            if (page.getAlgorithm() != null && !page.getAlgorithm().isEmpty()) {
                log.warn("Critic Pass: User asked for DEFINE only. Clearing algorithm steps.");
                page.setAlgorithm(null);
                page.setPseudocode(null);
            }
            if (page.getComplexity() != null) {
                page.setComplexity(null);
            }
        }

        // 5. Comparison Table Rule: Only include comparison table if comparing or multi-topic
        if (!plan.isIncludeComparisonTable() && page.getComparisonTable() != null) {
            log.warn("Critic Pass: Removing unrequested comparison table.");
            page.setComparisonTable(null);
        }

        // 6. Generic Advantages / Limitations Purge (unless explicitly required)
        if (page.getAdvantages() != null && !page.getAdvantages().isEmpty() && !plan.isIncludeComparisonTable()) {
            boolean isGeneric = page.getAdvantages().stream().anyMatch(a -> a.toLowerCase().contains("high throughput") || a.toLowerCase().contains("scalable"));
            if (isGeneric && domain != DomainType.SOFTWARE_ENGINEERING && domain != DomainType.COMPUTER_NETWORKS) {
                log.warn("Critic Pass: Removing generic advantages filler.");
                page.setAdvantages(null);
            }
        }

        // 7. Ensure Diagram Metadata is clean
        if (page.getDiagram() != null) {
            if (page.getDiagram().getTitle() == null || page.getDiagram().getTitle().isBlank()) {
                page.getDiagram().setTitle(page.getTopicTitle() + " Concept Diagram");
            }
        }
    }

    private double calculateSectionRelevance(
            SectionDto section,
            TopicUnderstandingEngine.TopicUnderstandingResult understanding,
            ContentPlanningEngine.TeachingPlan plan) {

        if (section == null || section.getHeading() == null) return 0.0;
        String headingLower = section.getHeading().toLowerCase();

        // If heading matches a generic template heading without specific topic keywords
        for (String generic : SUSPICIOUS_GENERIC_HEADINGS) {
            if (headingLower.contains(generic) && !containsTopicKeyword(headingLower, understanding.getNormalizedTopic())) {
                // If it's pure generic filler, penalize heavily
                return 0.3;
            }
        }

        // Check if section aligns with user's intent or primary subtopics
        for (String sub : understanding.getPrimarySubtopics()) {
            if (headingLower.contains(sub.toLowerCase())) {
                return 1.0;
            }
        }

        return 0.85; // Standard high relevance
    }

    private boolean containsTopicKeyword(String text, String topic) {
        String[] words = topic.toLowerCase().split("\\s+");
        for (String w : words) {
            if (w.length() > 2 && text.contains(w)) return true;
        }
        return false;
    }
}
