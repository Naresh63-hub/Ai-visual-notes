package com.visualnotes.ai.strategy;

import com.visualnotes.diagram.DiagramEngine;
import com.visualnotes.dto.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DbmsContentStrategy {

    private final DiagramEngine diagramEngine;

    public DbmsContentStrategy(DiagramEngine diagramEngine) {
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

        String lower = (topic + " " + overallPrompt).toLowerCase();

        if (lower.contains("normalization") || lower.contains("normal form") || lower.contains("1nf") || lower.contains("2nf") || lower.contains("3nf") || lower.contains("bcnf")) {
            return generateNormalizationContent(topic, overallPrompt, pageNumber, totalPages, style, audience, difficulty);
        } else {
            return generateGeneralDbmsContent(topic, overallPrompt, pageNumber, totalPages, style, audience, difficulty, focusArea);
        }
    }

    private PageContentDto generateNormalizationContent(
            String topic,
            String overallPrompt,
            int pageNumber,
            int totalPages,
            String style,
            String audience,
            String difficulty) {

        DiagramDataDto diagram = diagramEngine.generateDiagram("DBMS Normalization", "dbms-normalization", overallPrompt);

        List<SectionDto> sections = new ArrayList<>();

        sections.add(SectionDto.builder()
                .heading("1. Purpose & Database Anomalies (Why Normalize?)")
                .content("Normalization organizes database tables to minimize data redundancy and eliminate insertion, update, and deletion anomalies:")
                .badge("Data Integrity")
                .bulletPoints(List.of(
                        "Insertion Anomaly: Inability to record certain facts without adding unrelated attributes (e.g. cannot add a new course without enrolling a student).",
                        "Update Anomaly: Inconsistent data caused by changing a duplicated attribute in one row but not in other copies.",
                        "Deletion Anomaly: Unintended loss of crucial information when a related record is deleted (e.g. deleting the last student deletes the whole course)."
                ))
                .highlights(List.of("Eliminate Redundancy", "Insertion/Update/Deletion Anomalies", "Lossless-Join Decomposition"))
                .build());

        sections.add(SectionDto.builder()
                .heading("2. Hierarchy of Normal Forms (1NF ➔ BCNF)")
                .content("Each successive normal form imposes stricter mathematical constraints on Functional Dependencies (FDs: X ➔ Y):")
                .badge("Normal Form Rules")
                .bulletPoints(List.of(
                        "1NF (First Normal Form): Every table cell holds ATOMIC (indivisible) values. No repeating groups or arrays.",
                        "2NF (Second Normal Form): Must be in 1NF AND have NO PARTIAL DEPENDENCY (every non-prime attribute must depend on the WHOLE candidate key, not a proper subset).",
                        "3NF (Third Normal Form): Must be in 2NF AND have NO TRANSITIVE DEPENDENCY (for every FD X ➔ Y, either X is a Super Key or Y is a Prime Attribute).",
                        "BCNF (Boyce-Codd Normal Form): Stricter 3NF. For EVERY functional dependency X ➔ Y, X MUST be a Super Key."
                ))
                .highlights(List.of("1NF: Atomic values", "2NF: No partial dependency", "3NF: No transitive dependency", "BCNF: X must be Super Key"))
                .build());

        ExampleDto example = ExampleDto.builder()
                .title("Decomposition Walkthrough: 2NF ➔ 3NF")
                .scenario("Student_Course Relation: (StudentID, CourseID, Professor, Office)")
                .stepByStep(List.of(
                        "Key: Candidate Key is (StudentID, CourseID).",
                        "Dependencies: (StudentID, CourseID) ➔ Professor, and Professor ➔ Office (Transitive).",
                        "Problem: Office depends on Professor, which is not a candidate key (Transitive Dependency Violates 3NF).",
                        "Solution Decomposition: Split into Table 1 (StudentID, CourseID, Professor) and Table 2 (Professor, Office)."
                ))
                .outputOrResult("Decomposition is Lossless Join and Preserves Functional Dependencies.")
                .takeaway("Separate independent real-world entities into their own tables linked by foreign keys.")
                .build();

        FormulaDto formula = FormulaDto.builder()
                .title("Functional Dependency & Normal Form Testing Criteria")
                .expression("1NF: Atomic Domains  |  2NF: No α ⊂ CandidateKey ➔ NonPrime  |  3NF: X ➔ Y (X is SuperKey OR Y is Prime)  |  BCNF: X ➔ Y (X is SuperKey)")
                .explanation("X ➔ Y indicates attribute set X functionally determines attribute set Y in relation R.")
                .build();

        return PageContentDto.builder()
                .documentTitle("DBMS Normalization")
                .pageNumber(pageNumber)
                .totalPages(totalPages)
                .topicTitle("DBMS Normalization (1NF to BCNF)")
                .topicSubtitle("Functional Dependencies, Anomaly Elimination & Lossless Decomposition")
                .categoryBadge("Relational Database Design ★★★")
                .difficultyLevel(difficulty)
                .pagePartTitle(totalPages > 1 ? ("Part " + pageNumber + " of " + totalPages + ": Normal Forms & Dependencies") : "Complete DBMS Normalization Study Notes")
                .definition("Normalization is a formal mathematical process in relational database design that systematically decomposes unnormalized tables to minimize redundancy and prevent modification anomalies.")
                .mainIdea("Ensure every non-key attribute provides a fact about the key, the whole key, and nothing but the key (so help me Codd).")
                .simpleExplanation("Instead of storing a student's address and course instructor in one giant messy spreadsheet, split them into clean separate tables so you never store the same address twice.")
                .sections(sections)
                .diagram(diagram)
                .example(example)
                .formula(formula)
                .advantages(List.of(
                        "Eliminates duplicate storage and prevents data inconsistencies.",
                        "Enforces strict relational constraints and cleaner table schemas."
                ))
                .limitations(List.of(
                        "Over-normalization creates too many small tables, requiring expensive SQL JOIN operations.",
                        "In analytical/OLAP data warehousing, intentional denormalization (Star Schema) is preferred for read performance."
                ))
                .keyPoints(List.of(
                        KeyPointDto.builder().point("Prime Attribute: An attribute that is part of ANY candidate key.").starred(true).category("Definition").build(),
                        KeyPointDto.builder().point("2NF eliminates partial key dependencies (relevant only for composite primary keys).").starred(true).category("2NF Rule").build(),
                        KeyPointDto.builder().point("BCNF is strictly stronger than 3NF; all BCNF relations are in 3NF, but not vice versa.").starred(true).category("Hierarchy").build()
                ))
                .examTips(List.of(
                        ExamTipDto.builder()
                                .tip("Famous Mnemonic: 'Every non-key attribute must depend on the key (1NF), the whole key (2NF), and nothing but the key (3NF)'")
                                .commonMistake("Testing for 2NF when the table has a single-attribute primary key (tables with single-attribute primary keys are automatically in 2NF).")
                                .mnemonic("1: Atoms ➔ 2: Whole Key ➔ 3: Nothing Else (No Transitive) ➔ BCNF: Super Key Only")
                                .build()
                ))
                .quickTakeaways(List.of(
                        "1NF: Atomic values only (no lists/repeating columns)",
                        "2NF: No partial key dependency (Whole Key)",
                        "3NF: No transitive dependency (Nothing but Key)",
                        "BCNF: Left side of every FD must be a Super Key"
                ))
                .continuesOnNextPage(pageNumber < totalPages)
                .isContinuation(pageNumber > 1)
                .layoutHint("handwritten-part1")
                .styleTheme(style)
                .build();
    }

    private PageContentDto generateGeneralDbmsContent(
            String topic,
            String overallPrompt,
            int pageNumber,
            int totalPages,
            String style,
            String audience,
            String difficulty,
            String focusArea) {

        DiagramDataDto diagram = diagramEngine.generateDiagram(topic, "dbms-normalization", overallPrompt);

        List<SectionDto> sections = new ArrayList<>();
        sections.add(SectionDto.builder()
                .heading("1. Database Foundations & ACID Transactions")
                .content(topic + " provides essential data consistency, concurrency control, and storage management.")
                .badge("Database Architecture")
                .bulletPoints(List.of(
                        "Atomicity & Consistency: All-or-nothing transaction execution",
                        "Isolation & Durability: Concurrency serializability and crash recovery",
                        "Indexing & Query Optimization: B+ Trees, Hashing, and Cost-Based Query Planning"
                ))
                .highlights(List.of("ACID Properties", "Relational Integrity", "Serializability"))
                .build());

        return PageContentDto.builder()
                .documentTitle(topic)
                .pageNumber(pageNumber)
                .totalPages(totalPages)
                .topicTitle(topic)
                .topicSubtitle(focusArea != null ? focusArea : "Database Management Systems & Relational Theory")
                .categoryBadge("DBMS Study Notes")
                .difficultyLevel(difficulty)
                .pagePartTitle(totalPages > 1 ? ("Part " + pageNumber + " of " + totalPages + ": Concepts & Architecture") : "Complete DBMS Notes")
                .definition(topic + " is a fundamental database concept ensuring structural integrity, persistent storage, and efficient query retrieval.")
                .mainIdea("Manage large structured datasets reliably under concurrent multi-user workloads.")
                .simpleExplanation("In databases, " + topic + " ensures information is organized reliably, without corruption or duplicate records.")
                .sections(sections)
                .diagram(diagram)
                .keyPoints(List.of(
                        KeyPointDto.builder().point("Relational databases enforce declarative constraints and transactional integrity.").starred(true).category("Principle").build()
                ))
                .examTips(List.of(
                        ExamTipDto.builder().tip("In DBMS exam questions, always draw entity-relationship or table schemas and specify primary/foreign keys.").mnemonic("Schema ➔ Keys ➔ Dependencies").build()
                ))
                .continuesOnNextPage(pageNumber < totalPages)
                .isContinuation(pageNumber > 1)
                .layoutHint("handwritten-part1")
                .styleTheme(style)
                .build();
    }
}
