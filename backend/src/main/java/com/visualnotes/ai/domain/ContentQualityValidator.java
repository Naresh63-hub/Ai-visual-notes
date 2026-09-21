package com.visualnotes.ai.domain;

import com.visualnotes.dto.PageContentDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ContentQualityValidator {

    private static final Logger log = LoggerFactory.getLogger(ContentQualityValidator.class);

    public void validateAndSanitize(PageContentDto page, DomainType domain, String requestedTopic) {
        if (page == null) return;

        // 1. Enforce Domain Purity & Prevent Template Contamination
        if (domain != DomainType.ALGORITHMS && domain != DomainType.DATA_STRUCTURES && domain != DomainType.PROGRAMMING) {
            // Non-CS topics MUST NOT contain software algorithm steps or Big-O complexity tables
            if (page.getAlgorithm() != null && !page.getAlgorithm().isEmpty()) {
                boolean hasGenericCsKeywords = page.getAlgorithm().stream()
                        .anyMatch(step -> step.getInstruction() != null && (
                                step.getInstruction().toLowerCase().contains("validateinput")
                                || step.getInstruction().toLowerCase().contains("applycoretransformation")
                                || step.getInstruction().toLowerCase().contains("statetransition")
                                || step.getInstruction().toLowerCase().contains("return finaloutcome")
                        ) || (step.getCodeSnippet() != null && step.getCodeSnippet().contains("validateInput")));
                if (hasGenericCsKeywords) {
                    log.warn("Detected CS template contamination in non-CS domain {}. Removing algorithm steps.", domain);
                    page.setAlgorithm(null);
                    page.setPseudocode(null);
                }
            }

            if (page.getComplexity() != null) {
                // If Big-O complexity is found in Physics/Biology/Chemistry/DBMS, clear it
                if (page.getComplexity().getTimeWorst() != null && page.getComplexity().getTimeWorst().contains("O(")) {
                    log.warn("Detected Big-O complexity in non-algorithmic domain {}. Clearing complexity.", domain);
                    page.setComplexity(null);
                }
            }
        }

        // 2. Ensure Topic Title is clean and free of instructional suffixes
        if (page.getTopicTitle() != null) {
            String title = page.getTopicTitle();
            title = title.replaceAll("(?i)\\s+(with physical vector equations|with vector equations|everyday examples|with algorithm|and complexity|for 10 marks|for 5 marks|for 2 marks).*$", "");
            page.setTopicTitle(title.trim());
        }

        // 3. Ensure Diagram metadata is clean & human readable
        if (page.getDiagram() != null) {
            if (page.getDiagram().getTitle() == null || page.getDiagram().getTitle().isBlank()) {
                page.getDiagram().setTitle(page.getTopicTitle() + " Visual Architecture");
            }
        }
    }
}
