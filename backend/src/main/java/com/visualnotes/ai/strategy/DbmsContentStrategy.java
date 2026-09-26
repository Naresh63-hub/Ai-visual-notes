package com.visualnotes.ai.strategy;

import com.visualnotes.ai.domain.DomainType;
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

        if (lower.contains("join") || lower.contains("joins") || lower.contains("inner join") || lower.contains("outer join")) {
            return generateSqlJoinContent(topic, overallPrompt, pageNumber, totalPages, style, audience, difficulty);
        } else if (lower.contains("normalization") || lower.contains("normal form") || lower.contains("1nf") || lower.contains("2nf") || lower.contains("3nf") || lower.contains("bcnf")) {
            return generateNormalizationContent(topic, overallPrompt, pageNumber, totalPages, style, audience, difficulty);
        } else {
            return generateGeneralDbmsContent(topic, overallPrompt, pageNumber, totalPages, style, audience, difficulty, focusArea);
        }
    }

    private PageContentDto generateSqlJoinContent(
            String topic,
            String overallPrompt,
            int pageNumber,
            int totalPages,
            String style,
            String audience,
            String difficulty) {

        DiagramDataDto diagram = diagramEngine.generateDiagram("SQL JOIN Types", "sql-join-venn", overallPrompt);

        List<SectionDto> sections = new ArrayList<>();

        sections.add(SectionDto.builder()
                .heading("1. Relational JOIN Types in SQL")
                .content("A JOIN clause combines rows from two or more tables based on a related column between them:")
                .badge("Relational Algebra")
                .bulletPoints(List.of(
                        "INNER JOIN: Returns ONLY records having matching values in BOTH tables.",
                        "LEFT (OUTER) JOIN: Returns ALL records from the Left table + matched records from the Right table (NULL if no match).",
                        "RIGHT (OUTER) JOIN: Returns ALL records from the Right table + matched records from the Left table (NULL if no match).",
                        "FULL (OUTER) JOIN: Returns ALL records when there is a match in EITHER left or right table.",
                        "CROSS JOIN: Cartesian product of both tables (m × n rows, no ON condition)."
                ))
                .highlights(List.of("INNER = Intersection", "LEFT = All Left + Matches", "FULL = Union of All", "CROSS = Cartesian Product"))
                .build());

        ComparisonTableDto table = ComparisonTableDto.builder()
                .title("SQL JOIN Comparison Matrix")
                .headers(List.of("Join Type", "Set Operation", "Result Content", "Unmatched Rows Handling"))
                .rows(List.of(
                        List.of("INNER JOIN", "Intersection (A ∩ B)", "Only rows with matching keys in both tables", "Excluded completely"),
                        List.of("LEFT JOIN", "A ∪ (A ∩ B)", "All Left table rows + matching Right table rows", "Right columns filled with NULL"),
                        List.of("RIGHT JOIN", "(A ∩ B) ∪ B", "All Right table rows + matching Left table rows", "Left columns filled with NULL"),
                        List.of("FULL OUTER", "A ∪ B", "All rows from both tables combined", "Non-matching columns filled with NULL"),
                        List.of("CROSS JOIN", "A × B", "Every row of A paired with every row of B", "No condition / all pairs returned")
                ))
                .conclusion("Performance Tip: Always ensure the join predicate (ON A.id = B.id) is indexed to avoid expensive full-table cartesian scans.")
                .build();

        ExampleDto example = ExampleDto.builder()
                .title("Standard SQL Query Syntax")
                .scenario("Connecting Students (id, name) with Enrollments (student_id, course)")
                .input("SELECT S.name, E.course FROM Students S INNER JOIN Enrollments E ON S.id = E.student_id;")
                .stepByStep(List.of(
                        "1. Evaluate ON condition: S.id = E.student_id",
                        "2. Match rows with equal key values",
                        "3. Project SELECT columns: name, course"
                ))
                .outputOrResult("Table containing student names paired with their enrolled courses.")
                .takeaway("Use INNER JOIN for mandatory associations and LEFT JOIN when preserving parent records without children.")
                .build();

        List<ExamTipDto> examTips = List.of(
                ExamTipDto.builder()
                        .tip("Exam Rule: 'WHERE A.id = B.id' is an implicit INNER JOIN. Modern standard requires explicit 'FROM A INNER JOIN B ON A.id = B.id' for clarity.")
                        .commonMistake("Confusing WHERE filters with ON join conditions in LEFT JOINs (filtering in WHERE can accidentally turn a LEFT JOIN into an INNER JOIN).")
                        .mnemonic("INNER = In Both | LEFT = Left Whole | FULL = Full Union")
                        .build()
        );

        return PageContentDto.builder()
                .documentTitle("SQL JOINs — Relational Operations")
                .pageNumber(pageNumber)
                .totalPages(totalPages)
                .topicTitle("SQL JOIN Operations & Relational Combinations")
                .topicSubtitle("INNER, LEFT, RIGHT, FULL OUTER & CROSS JOIN Mechanics")
                .categoryBadge("Database Management Systems")
                .difficultyLevel("Core University Standard")
                .domain(DomainType.DBMS.name())
                .subdomain("Relational Query Operations")
                .definition("A SQL JOIN is an operation used to query and combine data from multiple relational tables linked by foreign key relationships.")
                .sections(sections)
                .comparisonTable(table)
                .example(example)
                .diagram(diagram)
                .examTips(examTips)
                .keyPoints(List.of(
                        KeyPointDto.builder().point("INNER JOIN filters out any non-matching rows from both sides.").starred(true).category("INNER").build(),
                        KeyPointDto.builder().point("LEFT JOIN guarantees all rows from the primary left table are retained in the result set.").starred(true).category("LEFT").build(),
                        KeyPointDto.builder().point("Foreign key indexing on joined columns dramatically reduces join query execution time.").starred(true).category("Optimization").build()
                ))
                .build();
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
                        "2NF (Second Normal Form): Table is in 1NF AND contains NO Partial Dependency (no non-prime attribute depends on a proper subset of a composite candidate key).",
                        "3NF (Third Normal Form): Table is in 2NF AND contains NO Transitive Dependency (X ➔ Y ➔ Z where non-prime attribute Z depends on non-prime Y).",
                        "BCNF (Boyce-Codd Normal Form): Strict 3NF variant — for EVERY functional dependency X ➔ Y, X MUST be a Super Key."
                ))
                .highlights(List.of("1NF = Atomic", "2NF = No Partial Key", "3NF = No Transitive", "BCNF = Super Key Left"))
                .build());

        ComparisonTableDto table = ComparisonTableDto.builder()
                .title("Summary Table: Normal Form Hierarchy")
                .headers(List.of("Normal Form", "Mandatory Prerequisite", "Eliminated Dependency / Flaw", "Core Condition"))
                .rows(List.of(
                        List.of("1NF", "None (Raw Relation)", "Multi-valued & Composite attributes", "All attribute values must be atomic"),
                        List.of("2NF", "Must be in 1NF", "Partial Functional Dependencies", "No Non-Prime attribute depends on part of Candidate Key"),
                        List.of("3NF", "Must be in 2NF", "Transitive Functional Dependencies", "For X ➔ Y: X is Super Key OR Y is Prime Attribute"),
                        List.of("BCNF", "Must be in 3NF", "All anomalies from overlapping keys", "For every FD X ➔ Y: X MUST be a Super Key")
                ))
                .conclusion("Rule of Thumb: In real-world enterprise architectures, 3NF/BCNF provides the optimal balance between zero redundancy and high join performance.")
                .build();

        ExampleDto example = ExampleDto.builder()
                .title("3NF Decomposition Example")
                .scenario("Relation R(StudentID, CourseID, Professor, Office) with FDs: {StudentID, CourseID} ➔ Professor; Professor ➔ Office")
                .input("R(StudentID, CourseID, Professor, Office)")
                .stepByStep(List.of(
                        "1. Identify Candidate Key: {StudentID, CourseID}",
                        "2. Detect Transitive Dependency: {StudentID, CourseID} ➔ Professor ➔ Office (Office depends on non-prime Professor)",
                        "3. Decompose into 3NF: R1(StudentID, CourseID, Professor) and R2(Professor, Office)"
                ))
                .outputOrResult("R1 and R2 are in 3NF with lossless join preservation.")
                .takeaway("Decomposition separates the independent entity (Professor Office) into its own clean table.")
                .build();

        List<ExamTipDto> examTips = List.of(
                ExamTipDto.builder()
                        .tip("Exam Rule: If a table has a single-attribute primary key (non-composite), it is AUTOMATICALLY in 2NF once it satisfies 1NF.")
                        .commonMistake("Confusing 3NF with BCNF: 3NF allows X ➔ Y if Y is prime; BCNF strictly requires X to be a super key regardless of Y.")
                        .mnemonic("1: Atoms ➔ 2: Whole Key ➔ 3: Nothing Else (No Transitive) ➔ BCNF: Super Key Only")
                        .build()
        );

        return PageContentDto.builder()
                .documentTitle("DBMS Normalization — 1NF to BCNF")
                .pageNumber(pageNumber)
                .totalPages(totalPages)
                .topicTitle("Relational Database Normalization (1NF, 2NF, 3NF, BCNF)")
                .topicSubtitle("Functional Dependencies, Anomaly Elimination & Decomposition")
                .categoryBadge("Database Management Systems")
                .difficultyLevel(difficulty)
                .domain(DomainType.DBMS.name())
                .subdomain("Relational Normalization Theory")
                .definition("Normalization is a systematic database design technique that decomposes tables to eliminate data redundancy and prevent insert/update/delete anomalies.")
                .sections(sections)
                .comparisonTable(table)
                .example(example)
                .diagram(diagram)
                .examTips(examTips)
                .keyPoints(List.of(
                        KeyPointDto.builder().point("1NF guarantees scalar atomic values across all records.").starred(true).category("1NF").build(),
                        KeyPointDto.builder().point("2NF eliminates partial dependencies where attributes depend on only part of a composite key.").starred(true).category("2NF").build(),
                        KeyPointDto.builder().point("3NF removes transitive dependencies (A ➔ B and B ➔ C).").starred(true).category("3NF").build(),
                        KeyPointDto.builder().point("BCNF is a stricter form of 3NF where every determinant must be a candidate key.").starred(true).category("BCNF").build()
                ))
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

        DiagramDataDto diagram = diagramEngine.generateDiagram(topic, "concept-map", overallPrompt);

        List<SectionDto> sections = new ArrayList<>();
        sections.add(SectionDto.builder()
                .heading("1. Database Foundations & Architecture")
                .content(topic + " provides essential data consistency, relational constraints, and storage management.")
                .badge("Database Architecture")
                .bulletPoints(List.of(
                        "Atomicity & Consistency: Transactional all-or-nothing execution guarantees.",
                        "Isolation & Durability: Concurrency serializability and reliable crash recovery.",
                        "Indexing & Query Optimization: Efficient disk page retrieval and query execution trees."
                ))
                .highlights(List.of("ACID Properties", "Relational Integrity", "Serializability"))
                .build());

        return PageContentDto.builder()
                .documentTitle(topic)
                .pageNumber(pageNumber)
                .totalPages(totalPages)
                .topicTitle(topic)
                .categoryBadge("DBMS Study Notes")
                .domain(DomainType.DBMS.name())
                .definition(topic + " is an essential database management concept ensuring data integrity and query efficiency.")
                .sections(sections)
                .diagram(diagram)
                .build();
    }
}
