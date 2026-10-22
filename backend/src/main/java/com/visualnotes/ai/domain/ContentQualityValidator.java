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
            "foundations & rules",
            "core mechanics",
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

        // 1. Category Badge & Domain Purity
        if (page.getCategoryBadge() == null || page.getCategoryBadge().isBlank() || page.getCategoryBadge().contains("General Academic Theory") || page.getCategoryBadge().contains("Study Notes")) {
            page.setCategoryBadge(domain.getDisplayName());
        }

        // 2. Topic Title Cleaning
        if (page.getTopicTitle() != null) {
            String title = page.getTopicTitle();
            title = title.replaceAll("(?i)\\s+(with physical vector equations|with vector equations|everyday examples|with algorithm|and complexity|for 10 marks|for 5 marks|for 2 marks).*$", "");
            page.setTopicTitle(title.trim());
        }

        // 3. Section-by-Section Relevance Scoring & Template Contamination Removal
        if (page.getSections() != null && !page.getSections().isEmpty()) {
            List<SectionDto> cleanedSections = new ArrayList<>();
            for (SectionDto section : page.getSections()) {
                double relevanceScore = calculateSectionRelevance(section, understanding, plan);
                if (relevanceScore >= 0.4) {
                    cleanedSections.add(section);
                } else {
                    log.warn("[VALIDATION] Purged low-relevance/template section '{}' (score: {}) for topic '{}'",
                            section.getHeading(), relevanceScore, topic);
                }
            }
            page.setSections(cleanedSections.isEmpty() ? null : cleanedSections);
        }

        // 4. Domain Purity: Remove CS algorithms & Big-O complexity on non-algorithmic topics
        if (domain != DomainType.ALGORITHMS && domain != DomainType.DATA_STRUCTURES && domain != DomainType.PROGRAMMING) {
            if (page.getAlgorithm() != null && !page.getAlgorithm().isEmpty()) {
                log.warn("[VALIDATION] Detected algorithm in non-CS domain {}. Removing algorithm steps.", domain);
                page.setAlgorithm(null);
                page.setPseudocode(null);
            }

            if (page.getComplexity() != null) {
                log.warn("[VALIDATION] Detected Big-O complexity in non-algorithmic domain {}. Clearing complexity.", domain);
                page.setComplexity(null);
            }
        }

        // 5. Pure Definition Rule: If user requested "Define X", do not generate code or execution algorithms
        if (understanding.getIntent() == TopicUnderstandingEngine.UserIntent.DEFINE) {
            if (page.getAlgorithm() != null && !page.getAlgorithm().isEmpty()) {
                log.warn("[VALIDATION] User asked for DEFINE only. Clearing algorithm steps.");
                page.setAlgorithm(null);
                page.setPseudocode(null);
            }
            if (page.getComplexity() != null) {
                page.setComplexity(null);
            }
        }

        // 6. Comparison Table Rule: Only include comparison table if comparing, multi-topic, or classification
        if (!plan.isIncludeComparisonTable() && page.getComparisonTable() != null) {
            log.warn("[VALIDATION] Removing unrequested comparison table.");
            page.setComparisonTable(null);
        }

        // 7. Generic Advantages / Limitations Purge (unless explicitly required)
        if (page.getAdvantages() != null && !page.getAdvantages().isEmpty() && !plan.isIncludeComparisonTable()) {
            boolean isGeneric = page.getAdvantages().stream().anyMatch(a -> a.toLowerCase().contains("high throughput") || a.toLowerCase().contains("scalable"));
            if (isGeneric && domain != DomainType.SOFTWARE_ENGINEERING && domain != DomainType.COMPUTER_NETWORKS) {
                log.warn("[VALIDATION] Removing generic advantages filler.");
                page.setAdvantages(null);
            }
        }

        // 8. Ensure Diagram Metadata is clean
        if (page.getDiagram() != null && (page.getDiagram().getTitle() == null || page.getDiagram().getTitle().isBlank())) {
            page.getDiagram().setTitle(topic + " Diagram");
        }
    }

    private double calculateSectionRelevance(
            SectionDto section,
            TopicUnderstandingEngine.TopicUnderstandingResult u,
            ContentPlanningEngine.TeachingPlan plan) {

        String heading = section.getHeading() != null ? section.getHeading().toLowerCase() : "";
        String content = section.getContent() != null ? section.getContent().toLowerCase() : "";

        // Check against suspicious generic template headings
        for (String generic : SUSPICIOUS_GENERIC_HEADINGS) {
            if (heading.contains(generic)) {
                // If it's a known generic heading, penalize heavily unless it contains the exact topic name
                if (!heading.contains(u.getNormalizedTopic().toLowerCase()) && !content.contains(u.getNormalizedTopic().toLowerCase())) {
                    return 0.1;
                }
            }
        }

        double score = 0.5; // baseline

        // Topic presence check
        String topicLower = u.getNormalizedTopic().toLowerCase();
        if (heading.contains(topicLower) || content.contains(topicLower)) {
            score += 0.3;
        }

        // Subtopics / Scope presence check
        if (u.getRequestedScope() != null) {
            for (String sub : u.getRequestedScope()) {
                if (content.contains(sub.toLowerCase()) || heading.contains(sub.toLowerCase())) {
                    score += 0.3;
                    break;
                }
            }
        }

        // Bullet points quality check
        if (section.getBulletPoints() != null && !section.getBulletPoints().isEmpty()) {
            long meaningfulBullets = section.getBulletPoints().stream()
                    .filter(b -> b.length() > 20 && !b.contains("governs state progression"))
                    .count();
            if (meaningfulBullets >= 2) {
                score += 0.2;
            }
        }

        return Math.min(1.0, score);
    }

    private static final Set<String> ALLOWED_DIAGRAM_TYPES = Set.of(
            "concept-map", "flowchart", "layer-stack", "network-topology",
            "tree-traversal", "circuit-diagram", "formula-math",
            "dbms-normalization", "protocol-comparison", "sorting-partition", "binary-search-array"
    );

    public void validateAndSanitize(com.visualnotes.dto.SemanticDocumentDto doc, com.visualnotes.dto.SemanticAnalysisDto analysis) {
        if (doc == null) return;
        String domainStr = analysis != null && analysis.getDomain() != null ? analysis.getDomain() : "GENERAL_ACADEMIC";
        String mainTopic = analysis != null && analysis.getMainTopic() != null ? analysis.getMainTopic() : doc.getTopicTitle();

        // 1. Title & Badge Purity
        if (doc.getTopicTitle() == null || doc.getTopicTitle().isBlank()) {
            doc.setTopicTitle(mainTopic);
        } else {
            String title = doc.getTopicTitle();
            title = title.replaceAll("(?i)\\s+(with physical vector equations|with vector equations|everyday examples|with algorithm|and complexity|for 10 marks|for 5 marks|for 2 marks).*$", "");
            doc.setTopicTitle(title.trim());
        }

        if (doc.getCategoryBadge() == null || doc.getCategoryBadge().isBlank() || doc.getCategoryBadge().contains("General Academic Theory") || doc.getCategoryBadge().contains("Study Notes")) {
            doc.setCategoryBadge(domainStr.replace("_", " "));
        }

        // 2. Deterministic Formula Validation
        if (doc.getFormula() != null) {
            String latex = doc.getFormula().getLatex();
            if (latex == null || latex.isBlank() || !hasBalancedBraces(latex)) {
                log.warn("[VALIDATION] Malformed or unbalanced LaTeX formula '{}'. Removing formula block.", latex);
                doc.setFormula(null);
            }
        }

        // 3. Deterministic Diagram Type Validation
        if (doc.getDiagram() != null) {
            String dType = doc.getDiagram().getType();
            if (dType == null || !ALLOWED_DIAGRAM_TYPES.contains(dType.toLowerCase().trim())) {
                log.warn("[VALIDATION] Unrecognized diagram type '{}'. Normalizing to 'concept-map'.", dType);
                doc.getDiagram().setType("concept-map");
            }
            if (doc.getDiagram().getTitle() == null || doc.getDiagram().getTitle().isBlank()) {
                doc.getDiagram().setTitle(doc.getTopicTitle() + " Diagram");
            }
        }

        // 4. Comparison Table Consistency
        if (doc.getComparisonTable() != null) {
            var table = doc.getComparisonTable();
            if (table.getHeaders() == null || table.getHeaders().isEmpty() || table.getRows() == null || table.getRows().isEmpty()) {
                log.warn("[VALIDATION] Empty or incomplete comparison table. Removing table block.");
                doc.setComparisonTable(null);
            } else {
                int colCount = table.getHeaders().size();
                List<List<String>> validRows = new ArrayList<>();
                for (List<String> row : table.getRows()) {
                    if (row != null && row.size() == colCount) {
                        validRows.add(row);
                    }
                }
                if (validRows.isEmpty()) {
                    doc.setComparisonTable(null);
                } else {
                    table.setRows(validRows);
                }
            }
        }

        // 5. Domain Purity: No CS algorithms/complexity on non-CS topics
        boolean isCsDomain = domainStr.contains("ALGORITHM") || domainStr.contains("DATA_STRUCTURE") || domainStr.contains("PROGRAMMING") || domainStr.contains("SOFTWARE");
        if (!isCsDomain) {
            if (doc.getAlgorithm() != null && !doc.getAlgorithm().isEmpty()) {
                log.warn("[VALIDATION] Detected CS algorithm steps in non-CS domain {}. Removing algorithm block.", domainStr);
                doc.setAlgorithm(null);
                doc.setPseudocode(null);
            }
            if (doc.getComplexity() != null) {
                doc.setComplexity(null);
            }
        }

        // 6. Generic Filler Purge
        if (doc.getSections() != null) {
            List<SectionDto> sanitizedSections = new ArrayList<>();
            for (SectionDto s : doc.getSections()) {
                String heading = s.getHeading() != null ? s.getHeading().toLowerCase() : "";
                String content = s.getContent() != null ? s.getContent().toLowerCase() : "";
                if (content.contains("governs state progression") || content.contains("system stabilizes") || heading.contains("concept architecture & core mechanics")) {
                    log.warn("[VALIDATION] Purged generic boilerplate section '{}'", s.getHeading());
                    continue;
                }
                sanitizedSections.add(s);
            }
            doc.setSections(sanitizedSections.isEmpty() ? null : sanitizedSections);
        }
    }

    private boolean hasBalancedBraces(String s) {
        int count = 0;
        for (char c : s.toCharArray()) {
            if (c == '{') count++;
            else if (c == '}') {
                count--;
                if (count < 0) return false;
            }
        }
        return count == 0;
    }
}
