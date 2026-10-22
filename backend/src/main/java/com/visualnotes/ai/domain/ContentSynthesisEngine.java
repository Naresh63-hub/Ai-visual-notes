package com.visualnotes.ai.domain;

import com.visualnotes.diagram.DiagramEngine;
import com.visualnotes.dto.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ContentSynthesisEngine {

    private final DiagramEngine diagramEngine;

    public ContentSynthesisEngine(DiagramEngine diagramEngine) {
        this.diagramEngine = diagramEngine;
    }

    public PageContentDto synthesizePage(
            TopicUnderstandingEngine.TopicUnderstandingResult understanding,
            ContentPlanningEngine.TeachingPlan plan,
            int pageNumber,
            int totalPages,
            String style,
            String audience,
            String difficulty) {

        String topic = plan.getTopic();
        DomainType domain = plan.getDomain();

        PageContentDto page = new PageContentDto();
        page.setDocumentTitle(topic + (totalPages > 1 ? " (" + totalPages + " Pages)" : ""));
        page.setPageNumber(pageNumber);
        page.setTotalPages(totalPages);
        page.setTopicTitle(topic);
        page.setCategoryBadge(domain.getDisplayName());
        page.setDifficultyLevel(difficulty != null ? difficulty : understanding.getDifficulty());
        page.setStyleTheme(style != null ? style : "Handwritten");

        if (totalPages > 1) {
            page.setPagePartTitle("Part " + pageNumber + " of " + totalPages);
            page.setIsContinuation(pageNumber > 1);
            page.setContinuesOnNextPage(pageNumber < totalPages);
        }

        // 1. Definition & Core Intuition (Page 1)
        if (pageNumber == 1) {
            page.setDefinition(synthesizeDefinition(topic, domain, understanding));
            page.setSimpleExplanation(synthesizeSimpleExplanation(topic, domain, understanding));
            if (understanding.getPrerequisiteConcept() != null) {
                page.setPurpose("Prerequisite & Context: Requires understanding of " + understanding.getPrerequisiteConcept() + ".");
            }
        }

        // 2. Sections: dynamically allocate sections across pages
        List<SectionDto> synthesizedSections = new ArrayList<>();
        List<ContentPlanningEngine.PlannedSection> plannedSections = plan.getSections();

        if (plannedSections != null && !plannedSections.isEmpty()) {
            int sectionsPerPage = (int) Math.ceil((double) plannedSections.size() / totalPages);
            int startIdx = (pageNumber - 1) * sectionsPerPage;
            int endIdx = Math.min(plannedSections.size(), startIdx + sectionsPerPage);

            for (int i = startIdx; i < endIdx; i++) {
                ContentPlanningEngine.PlannedSection ps = plannedSections.get(i);
                synthesizedSections.add(synthesizeSectionContent(topic, domain, ps, understanding));
            }
        }
        page.setSections(synthesizedSections.isEmpty() ? null : synthesizedSections);

        // 3. Diagram (Render on Page 1 or Page 2)
        if (plan.isIncludeDiagram() && (pageNumber == 1 || (totalPages >= 2 && pageNumber == 1))) {
            DiagramDataDto diag = diagramEngine.generateDiagram(topic, plan.getDiagramType(), understanding.getRawPrompt());
            page.setDiagram(diag);
        }

        // 4. Comparison Table (Render on last page or single page)
        if (plan.isIncludeComparisonTable() && (pageNumber == totalPages || totalPages == 1 || pageNumber == 2)) {
            page.setComparisonTable(synthesizeComparisonTable(understanding));
        }

        // 5. Mathematical / Scientific Formula
        if (plan.isIncludeFormula() && (pageNumber == 1 || totalPages == 1 || (totalPages >= 2 && pageNumber == 2))) {
            page.setFormula(synthesizeFormula(topic, domain, understanding));
        }

        // 6. Algorithm & Pseudocode
        if (plan.isIncludeAlgorithm() && (pageNumber == totalPages || totalPages == 1 || pageNumber == 2)) {
            page.setAlgorithm(synthesizeAlgorithmSteps(topic, domain));
            if (plan.isIncludePseudocode()) {
                page.setPseudocode(synthesizePseudocode(topic, domain));
            }
        }

        // 7. Complexity Analysis
        if (plan.isIncludeComplexity() && (pageNumber == totalPages || totalPages == 1)) {
            page.setComplexity(synthesizeComplexity(topic));
        }

        // 8. Worked Example
        if (plan.isIncludeWorkedExample() && (pageNumber == totalPages || totalPages == 1 || pageNumber == 2)) {
            page.setExample(synthesizeWorkedExample(topic, domain, understanding));
        }

        // 9. Key Study Points & Exam Tips
        if (pageNumber == totalPages || totalPages == 1) {
            page.setKeyPoints(synthesizeKeyPoints(topic, domain, understanding));
            if (plan.isIncludeExamTips()) {
                page.setExamTips(synthesizeExamTips(topic, domain, understanding));
            }
        }

        return page;
    }

    public SemanticDocumentDto synthesizeDocument(
            TopicUnderstandingEngine.TopicUnderstandingResult understanding,
            ContentPlanningEngine.TeachingPlan plan,
            int totalPages,
            String style,
            String audience,
            String difficulty) {

        String topic = plan.getTopic();
        DomainType domain = plan.getDomain();

        SemanticDocumentDto doc = new SemanticDocumentDto();
        doc.setTopicTitle(topic);
        doc.setCategoryBadge("[Offline Mode] " + domain.getDisplayName());
        doc.setDomain(domain.name());
        doc.setSubdomain(understanding.getSubdomain());
        doc.setDifficultyLevel(difficulty != null ? difficulty : understanding.getDifficulty());

        doc.setDefinition(synthesizeDefinition(topic, domain, understanding));
        doc.setSimpleExplanation(synthesizeSimpleExplanation(topic, domain, understanding));
        if (understanding.getPrerequisiteConcept() != null) {
            doc.setPurpose("Prerequisite & Context: Requires understanding of " + understanding.getPrerequisiteConcept() + ".");
        }

        List<SectionDto> sections = new ArrayList<>();
        if (plan.getSections() != null) {
            for (ContentPlanningEngine.PlannedSection ps : plan.getSections()) {
                sections.add(synthesizeSectionContent(topic, domain, ps, understanding));
            }
        }
        doc.setSections(sections);

        if (plan.isIncludeDiagram()) {
            doc.setDiagram(diagramEngine.generateDiagram(topic, plan.getDiagramType(), understanding.getRawPrompt()));
        }
        if (plan.isIncludeComparisonTable()) {
            doc.setComparisonTable(synthesizeComparisonTable(understanding));
        }
        if (plan.isIncludeFormula()) {
            doc.setFormula(synthesizeFormula(topic, domain, understanding));
        }
        if (plan.isIncludeAlgorithm()) {
            doc.setAlgorithm(synthesizeAlgorithmSteps(topic, domain));
            if (plan.isIncludePseudocode()) {
                doc.setPseudocode(synthesizePseudocode(topic, domain));
            }
        }
        if (plan.isIncludeComplexity()) {
            doc.setComplexity(synthesizeComplexity(topic));
        }
        if (plan.isIncludeWorkedExample()) {
            doc.setExample(synthesizeWorkedExample(topic, domain, understanding));
        }

        doc.setKeyPoints(synthesizeKeyPoints(topic, domain, understanding));
        if (plan.isIncludeExamTips()) {
            doc.setExamTips(synthesizeExamTips(topic, domain, understanding));
        }

        return doc;
    }

    private String synthesizeDefinition(String topic, DomainType domain, TopicUnderstandingEngine.TopicUnderstandingResult u) {
        String lower = (topic + " " + u.getRawPrompt()).toLowerCase();

        if (lower.contains("topology")) {
            return "A Network Topology defines the physical or logical arrangement of computing nodes, links, and transmission paths within a telecommunications network.";
        }
        if (lower.contains("osi")) {
            return "The Open Systems Interconnection (OSI) 7-layer reference model is a standardized conceptual framework developed by ISO to partition telecommunication functions into modular protocol layers.";
        }
        if (lower.contains("tcp") && lower.contains("udp")) {
            return "TCP (Transmission Control Protocol) and UDP (User Datagram Protocol) are core Transport Layer protocols providing reliable connection-oriented stream delivery versus lightweight connectionless datagram transport.";
        }
        if (lower.contains("stack") && lower.contains("queue")) {
            return "Stack and Queue are fundamental linear data structures distinguished by their data access order: LIFO (Last-In, First-Out) for Stacks versus FIFO (First-In, First-Out) for Queues.";
        }
        if (lower.contains("recursion")) {
            return "Recursion is a computational and mathematical method where a function solves a problem by calling itself with smaller sub-inputs until reaching a well-defined terminating base case.";
        }
        if (lower.contains("polymorphism")) {
            return "Polymorphism is an Object-Oriented Programming pillar that allows entities (functions, objects, or operators) to execute differently based on object context, encompassing compile-time overloading and runtime overriding.";
        }
        if (lower.contains("quadratic")) {
            return "The Quadratic Formula provides the exact algebraic roots for any second-degree polynomial equation ax² + bx + c = 0 (where a ≠ 0) through the method of completing the square.";
        }
        if (lower.contains("deadlock")) {
            return "A Deadlock in operating systems is a condition where a set of concurrent processes are permanently stalled because each holds resources while waiting for other resources held by other processes in the set.";
        }
        if (lower.contains("transistor")) {
            return "A BJT or MOSFET operates as an electronic switch by toggling between Cut-off (high impedance / OFF state) and Saturation (low impedance / ON state) controlled by base/gate voltage.";
        }
        if (lower.contains("mitosis")) {
            return "Mitosis is the process of eukaryotic cell division that replicates and segregates chromosomes equally, yielding two genetically identical diploid daughter cells.";
        }
        if (lower.contains("nfa") && lower.contains("dfa")) {
            return "Finite Automata are abstract computational state machines recognizing Regular Languages. A DFA transitions deterministically to exactly one state per input symbol, while an NFA can branch into multiple states or transition spontaneously on ε.";
        }
        if (lower.contains("binary search")) {
            return "Binary Search is an optimal O(log n) divide-and-conquer algorithm that locates a target key in a strictly sorted array by repeatedly halving the search interval.";
        }
        if (lower.contains("newton")) {
            return "Newton's Laws of Motion form the bedrock of classical mechanics, describing how external forces govern the acceleration, momentum, and equilibrium of physical bodies.";
        }
        if (lower.contains("normalization")) {
            return "Database Normalization is the systematic process of decomposing relational tables to eliminate data redundancy, avoid update anomalies, and enforce functional dependencies.";
        }
        if (lower.contains("ohm")) {
            return "Ohm's Law states that electric current flowing through a conductor between two points is directly proportional to the potential difference (voltage) across them at constant temperature: V = I · R.";
        }
        if (lower.contains("photosynthesis")) {
            return "Photosynthesis is the biochemical process by which photoautotrophic organisms convert light energy, carbon dioxide, and water into organic glucose and oxygen gas within chloroplasts.";
        }

        return topic + " is a fundamental concept in " + domain.getDisplayName() + " providing formal principles, structural rules, and practical operational models.";
    }

    private String synthesizeSimpleExplanation(String topic, DomainType domain, TopicUnderstandingEngine.TopicUnderstandingResult u) {
        String lower = (topic + " " + u.getRawPrompt()).toLowerCase();

        if (lower.contains("topology")) {
            return "In simple terms: Network topology is the layout map showing how computers, cables, and switches are connected together to share data.";
        }
        if (lower.contains("osi")) {
            return "In simple terms: A 7-step assembly line for data traveling across the internet, where each layer adds its own label and performs a specific task.";
        }
        if (lower.contains("tcp") && lower.contains("udp")) {
            return "In simple terms: TCP is like a registered postal letter with delivery confirmation; UDP is like a postcard tossed in the mail for maximum speed.";
        }
        if (lower.contains("stack") && lower.contains("queue")) {
            return "In simple terms: A Stack is like a stack of cafeteria trays (take from top); a Queue is like people waiting in a ticket line (first person served first).";
        }
        if (lower.contains("recursion")) {
            return "In simple terms: Solve a big problem by breaking it into identical smaller subproblems, making sure you know when to stop (the base case).";
        }
        if (lower.contains("polymorphism")) {
            return "In simple terms: One common interface with many specific implementations (e.g. speak() method works for Dog = 'Bark', Cat = 'Meow').";
        }
        if (lower.contains("quadratic")) {
            return "In simple terms: A universal formula that instantly gives you the x-intercepts of any parabola ax² + bx + c = 0.";
        }
        if (lower.contains("deadlock")) {
            return "In simple terms: A four-way traffic gridlock where no car can move forward because each car is blocked by another waiting car.";
        }
        if (lower.contains("transistor")) {
            return "In simple terms: A small voltage at the base turns the main switch ON (saturation) or OFF (cut-off) with no moving parts.";
        }
        if (lower.contains("mitosis")) {
            return "In simple terms: How a body cell makes an exact duplicate of itself for growth and tissue repair.";
        }
        if (lower.contains("nfa") && lower.contains("dfa")) {
            return "In simple terms: A DFA follows one predictable track, while an NFA can explore multiple tracks at the same time.";
        }
        if (lower.contains("binary search")) {
            return "In simple terms: Look at the middle item. If your target is smaller, search the left half; if larger, search the right half. Halves the remaining work every step.";
        }
        if (lower.contains("newton")) {
            return "In simple terms: Objects resist change in motion (1st), harder push means faster acceleration (2nd), every action has an equal reaction (3rd).";
        }
        if (lower.contains("normalization")) {
            return "In simple terms: Store each piece of data in exactly one place so you never have conflicting duplicates or accidental deletions.";
        }
        if (lower.contains("photosynthesis")) {
            return "In simple terms: Plants use sunlight, water, and CO₂ to make sugar for food and release clean oxygen.";
        }

        return "Core Intuition: " + topic + " establishes systematic rules and relationships to model and solve key problems in " + domain.getDisplayName() + ".";
    }

    private SectionDto synthesizeSectionContent(
            String topic,
            DomainType domain,
            ContentPlanningEngine.PlannedSection ps,
            TopicUnderstandingEngine.TopicUnderstandingResult u) {

        List<String> bullets = new ArrayList<>();
        List<String> highlights = new ArrayList<>();

        for (String concept : ps.getTargetConcepts()) {
            String bullet = generateConceptBullet(concept, topic, domain);
            bullets.add(bullet);
            highlights.add(concept);
        }

        return SectionDto.builder()
                .heading(ps.getHeading())
                .badge(ps.getBadge())
                .content(ps.getPedagogicalPurpose())
                .bulletPoints(bullets)
                .highlights(highlights)
                .build();
    }

    private String generateConceptBullet(String concept, String topic, DomainType domain) {
        String cLow = concept.toLowerCase();

        // Topology types
        if (cLow.contains("star")) {
            return "• Star Topology: All nodes connect directly to a central hub/switch. Easy to isolate cable faults; central hub failure disconnects entire network.";
        }
        if (cLow.contains("bus")) {
            return "• Bus Topology: Single shared coaxial backbone cable with terminators at both ends. Inexpensive cabling; cable break disrupts all connected nodes.";
        }
        if (cLow.contains("ring")) {
            return "• Ring Topology: Circular point-to-point token passing where data travels unidirectionally. Predictable token latency; break in ring halts circulation unless dual-ring.";
        }
        if (cLow.contains("mesh")) {
            return "• Mesh Topology: Dedicated point-to-point links between every node (n(n-1)/2 links for full mesh). Maximum redundancy and fault tolerance; high cabling cost.";
        }
        if (cLow.contains("tree")) {
            return "• Tree (Hierarchical) Topology: Multi-level hierarchy branching from a root node to secondary switches. Scalable for large enterprises; root failure isolates branches.";
        }
        if (cLow.contains("hybrid")) {
            return "• Hybrid Topology: Combination of two or more distinct topologies (e.g. Star-Bus or Star-Ring) to balance scalability, reliability, and cost.";
        }

        // OSI Layers
        if (cLow.contains("application") || cLow.contains("l7")) {
            return "• Layer 7 (Application): Direct user interface to network protocols (HTTP, HTTPS, DNS, SMTP, SSH).";
        }
        if (cLow.contains("presentation") || cLow.contains("l6")) {
            return "• Layer 6 (Presentation): Data syntax translation, TLS/SSL encryption/decryption, and compression.";
        }
        if (cLow.contains("session") || cLow.contains("l5")) {
            return "• Layer 5 (Session): Manages communication sessions, dialogue control, and recovery checkpoints.";
        }
        if (cLow.contains("transport") || cLow.contains("l4")) {
            return "• Layer 4 (Transport): End-to-end reliable transmission, port addressing (TCP/UDP), flow control, and segmentation.";
        }
        if (cLow.contains("network") || cLow.contains("l3")) {
            return "• Layer 3 (Network): Logical IP addressing, packet forwarding, subnetting, and routing (Routers, ICMP, OSPF).";
        }
        if (cLow.contains("data link") || cLow.contains("l2")) {
            return "• Layer 2 (Data Link): Physical MAC addressing, frame encapsulation, error detection (CRC), and local switching.";
        }
        if (cLow.contains("physical") || cLow.contains("l1")) {
            return "• Layer 1 (Physical): Raw binary bitstream transmission over electrical, fiber-optic, or wireless physical media.";
        }

        // Deadlock conditions
        if (cLow.contains("mutual exclusion")) {
            return "• Mutual Exclusion: At least one resource must be held in a non-shareable mode by only one process at a time.";
        }
        if (cLow.contains("hold and wait")) {
            return "• Hold and Wait: A process holding allocated resources can request additional resources held by other processes.";
        }
        if (cLow.contains("no preemption")) {
            return "• No Preemption: Resources cannot be forcibly confiscated; they are released only voluntarily upon process completion.";
        }
        if (cLow.contains("circular wait")) {
            return "• Circular Wait: A closed chain of processes {P₀, P₁, ..., Pₙ} exists where P₀ waits for P₁, P₁ waits for P₂, and Pₙ waits for P₀.";
        }

        // Mitosis phases
        if (cLow.contains("prophase")) {
            return "• Prophase: Chromatin condenses into distinct chromosomes; nucleolus disappears; mitotic spindle forms from centrosomes.";
        }
        if (cLow.contains("metaphase")) {
            return "• Metaphase: Chromosomes align along the central equatorial Metaphase Plate attached to spindle fibers by kinetochores.";
        }
        if (cLow.contains("anaphase")) {
            return "• Anaphase: Sister chromatids separate at the centromere and are pulled toward opposite cell poles by spindle fibers.";
        }
        if (cLow.contains("telophase") || cLow.contains("cytokinesis")) {
            return "• Telophase & Cytokinesis: Nuclear envelopes reassemble around separated genomes; cleavage furrow divides cytoplasm into two daughter cells.";
        }

        // Normalization
        if (cLow.contains("1nf")) {
            return "• 1NF: Eliminates repeating groups and arrays by enforcing atomic attribute values in every column.";
        }
        if (cLow.contains("2nf")) {
            return "• 2NF: In 1NF and removes Partial Functional Dependencies on composite candidate keys.";
        }
        if (cLow.contains("3nf")) {
            return "• 3NF: In 2NF and removes Transitive Functional Dependencies (non-key attribute determining non-key).";
        }
        if (cLow.contains("bcnf")) {
            return "• BCNF: Strict normal form requiring every determinant X in X ➔ Y to be a Super Key.";
        }

        // General Domain Bullet
        return "• " + concept + ": Defines operational behavior, structural constraints, and analytical relationships for " + topic + ".";
    }

    private ComparisonTableDto synthesizeComparisonTable(TopicUnderstandingEngine.TopicUnderstandingResult u) {
        String lower = (u.getNormalizedTopic() + " " + u.getRawPrompt()).toLowerCase();

        if (lower.contains("topology")) {
            return ComparisonTableDto.builder()
                    .title("Comparative Analysis of Network Topologies")
                    .headers(List.of("Topology Type", "Cabling Cost", "Fault Tolerance", "Central Point of Failure", "Typical Use Case"))
                    .rows(List.of(
                            List.of("Star", "Moderate (per-node run)", "High (single node cable break isolated)", "Yes (Central Switch/Hub)", "LANs, Offices, Home Networks"),
                            List.of("Bus", "Lowest (single cable)", "Low (backbone break halts all)", "No (distributed backbone)", "Legacy small networks, Coaxial"),
                            List.of("Ring", "Low to Moderate", "Low (single break stops token loop)", "No (distributed loop)", "FDDI, Token Ring, Metro fiber"),
                            List.of("Mesh (Full)", "Highest (n(n-1)/2 links)", "Maximum (multiple redundant paths)", "No", "WAN backbones, Mission-critical"),
                            List.of("Tree", "Moderate to High", "Moderate (subtree isolated if parent fails)", "Yes (Root switch)", "Large enterprise corporate networks"),
                            List.of("Hybrid", "Varies by design", "High (tailored redundancy)", "Varies", "Modern campus-wide networks")
                    ))
                    .conclusion("Summary: Star topology is standard for modern LANs due to simple management; Mesh is reserved for resilient WAN backbones.")
                    .build();
        }

        if (lower.contains("tcp") && lower.contains("udp")) {
            return ComparisonTableDto.builder()
                    .title("Key Protocol Differences: TCP vs UDP")
                    .headers(List.of("Feature / Parameter", "TCP (Transmission Control Protocol)", "UDP (User Datagram Protocol)"))
                    .rows(List.of(
                            List.of("Connection Mode", "Connection-oriented (3-way handshake SYN/ACK)", "Connectionless (No handshake)"),
                            List.of("Reliability", "Guaranteed delivery with ACKs & retransmissions", "Best-effort delivery (No ACKs / potential loss)"),
                            List.of("Packet Ordering", "Strict sequence numbering & reassembly", "No sequence ordering (packets may arrive out of order)"),
                            List.of("Header Overhead", "20 to 60 bytes with control flags", "8 bytes fixed lightweight header"),
                            List.of("Flow & Congestion Control", "Sliding window, AIMD congestion avoidance", "None (application manages transmission rate)"),
                            List.of("Primary Use Cases", "Web (HTTP/HTTPS), File Transfer (FTP), Email (SMTP)", "Live Video Streaming, VoIP, Online Gaming, DNS Queries")
                    ))
                    .conclusion("Takeaway: Use TCP for data integrity where packet loss is unacceptable; use UDP for low-latency real-time streams.")
                    .build();
        }

        if (lower.contains("stack") && lower.contains("queue")) {
            return ComparisonTableDto.builder()
                    .title("Data Structure Comparison: Stack vs Queue")
                    .headers(List.of("Characteristic", "Stack (LIFO)", "Queue (FIFO)"))
                    .rows(List.of(
                            List.of("Ordering Discipline", "Last-In, First-Out (LIFO)", "First-In, First-Out (FIFO)"),
                            List.of("Insertion Operation", "Push (at Top pointer)", "Enqueue (at Rear pointer)"),
                            List.of("Deletion Operation", "Pop (from Top pointer)", "Dequeue (from Front pointer)"),
                            List.of("Active Pointers", "Single 'Top' pointer", "Two pointers: 'Front' and 'Rear'"),
                            List.of("Time Complexity", "O(1) push and pop", "O(1) enqueue and dequeue"),
                            List.of("Typical Applications", "Function call stack, Undo buffers, Syntax parsing", "CPU scheduling queues, Print spoolers, BFS traversal")
                    ))
                    .conclusion("Takeaway: Choose Stack for backtracking and nested state; choose Queue for fair sequential processing.")
                    .build();
        }

        if (lower.contains("nfa") && lower.contains("dfa")) {
            return ComparisonTableDto.builder()
                    .title("Key Differences: DFA vs NFA")
                    .headers(List.of("Parameter / Feature", "Deterministic (DFA)", "Non-Deterministic (NFA)"))
                    .rows(List.of(
                            List.of("Transition Rule (δ)", "δ : Q × Σ ➔ Q (Unique next state)", "δ : Q × (Σ ∪ {ε}) ➔ 2^Q (Set of states)"),
                            List.of("Null (ε) Transitions", "Strictly Forbidden", "Permitted (State change without input)"),
                            List.of("Execution Path", "Single deterministic path", "Multiple parallel computational branches"),
                            List.of("HW/SW Execution", "Fast and Direct O(n)", "Requires backtracking or subset construction"),
                            List.of("Language Power", "Recognizes Regular Languages", "Recognizes Regular Languages (Equal Power)")
                    ))
                    .conclusion("Equivalence Theorem: Every NFA can be converted to an equivalent DFA (using Subset Construction with up to 2ⁿ states).")
                    .build();
        }

        if (lower.contains("join") || (lower.contains("sql") && lower.contains("inner"))) {
            return ComparisonTableDto.builder()
                    .title("Comparison of SQL JOIN Types")
                    .headers(List.of("JOIN Type", "Matching Condition", "Unmatched Rows Handled"))
                    .rows(List.of(
                            List.of("INNER JOIN", "Returns rows when keys match in BOTH tables", "Excluded completely"),
                            List.of("LEFT JOIN", "Returns ALL rows from left table + matched right rows", "Right columns filled with NULL"),
                            List.of("RIGHT JOIN", "Returns ALL rows from right table + matched left rows", "Left columns filled with NULL"),
                            List.of("FULL OUTER JOIN", "Returns all rows from both tables", "Non-matching columns filled with NULL"),
                            List.of("CROSS JOIN", "Cartesian product of both tables", "Produces (M × N) row combinations")
                    ))
                    .conclusion("Summary: Use INNER JOIN for strict matching; LEFT/RIGHT OUTER JOIN to preserve incomplete records.")
                    .build();
        }

        if (lower.contains("scheduling") || lower.contains("fcfs") || lower.contains("round robin") || lower.contains("sjf")) {
            return ComparisonTableDto.builder()
                    .title("Comparison of CPU Scheduling Algorithms")
                    .headers(List.of("Algorithm", "Preemption Mode", "Key Strength", "Potential Drawback"))
                    .rows(List.of(
                            List.of("FCFS (First-Come, First-Served)", "Non-preemptive", "Simple FIFO queue implementation", "Convoy effect; high average waiting time"),
                            List.of("SJF (Shortest Job First)", "Non-preemptive / Preemptive (SRTF)", "Optimal minimum average waiting time", "Starvation of long processes; burst time unknown"),
                            List.of("Round Robin (RR)", "Preemptive (Time Quantum q)", "Fair CPU share; optimal response time", "High context-switch overhead if quantum is too short"),
                            List.of("Priority Scheduling", "Preemptive / Non-preemptive", "Handles urgent priority tasks", "Starvation of low-priority tasks (mitigated by aging)")
                    ))
                    .conclusion("Summary: Round Robin suits interactive time-sharing; SJF minimizes theoretical wait times.")
                    .build();
        }

        if (lower.contains("normalization") || lower.contains("1nf") || lower.contains("2nf") || lower.contains("3nf")) {
            return ComparisonTableDto.builder()
                    .title("Normal Form Hierarchy (1NF to BCNF)")
                    .headers(List.of("Normal Form", "Core Requirement", "Anomaly Eliminated"))
                    .rows(List.of(
                            List.of("1NF", "Atomic attributes (no repeating groups/arrays)", "Multi-valued attributes & repeating columns"),
                            List.of("2NF", "In 1NF + No Partial Functional Dependencies", "Update & Delete anomalies on composite keys"),
                            List.of("3NF", "In 2NF + No Transitive Dependencies (X ➔ Y)", "Transitive data redundancy & phantom updates"),
                            List.of("BCNF", "Strict: For every X ➔ Y, X must be a Super Key", "All functional dependency anomalies")
                    ))
                    .conclusion("Goal: Reach 3NF or BCNF while ensuring lossless join and dependency preservation.")
                    .build();
        }

        // Dynamic 2-entity comparison table
        List<String> subtopics = u.getTargetEntities().isEmpty() ? u.getPrimarySubtopics() : u.getTargetEntities();
        String itemA = subtopics.size() > 0 ? subtopics.get(0) : "Concept A";
        String itemB = subtopics.size() > 1 ? subtopics.get(1) : "Concept B";

        return ComparisonTableDto.builder()
                .title("Comparative Summary: " + itemA + " vs " + itemB)
                .headers(List.of("Characteristic", itemA, itemB))
                .rows(List.of(
                        List.of("Primary Definition", "Core theoretical foundations of " + itemA, "Core theoretical foundations of " + itemB),
                        List.of("Operational Mechanism", "Direct execution model", "Alternative execution model"),
                        List.of("Key Strengths", "High efficiency and deterministic behavior", "Flexibility and broad expressiveness"),
                        List.of("Trade-offs / Limitations", "Specific boundary conditions apply", "Requires additional overhead or complexity")
                ))
                .conclusion("Summary: Choose " + itemA + " or " + itemB + " depending on specific workload and system requirements.")
                .build();
    }

    private FormulaDto synthesizeFormula(String topic, DomainType domain, TopicUnderstandingEngine.TopicUnderstandingResult u) {
        String lower = (topic + " " + u.getRawPrompt()).toLowerCase();

        if (lower.contains("quadratic")) {
            return FormulaDto.builder()
                    .title("Quadratic Formula & Discriminant Analysis")
                    .expression("x = \\frac{-b \\pm \\sqrt{b^2 - 4ac}}{2a} \\quad | \\quad \\Delta = b^2 - 4ac")
                    .explanation("Where Δ > 0 yields 2 distinct real roots, Δ = 0 yields 1 repeated real root, and Δ < 0 yields 2 complex conjugate roots.")
                    .build();
        }
        if (lower.contains("transistor")) {
            return FormulaDto.builder()
                    .title("BJT Transistor Switching Equations")
                    .expression("I_C = \\beta I_B \\quad | \\quad V_{CE} = V_{CC} - I_C R_C \\quad | \\quad I_{B(sat)} \\ge \\frac{I_{C(sat)}}{\\beta}")
                    .explanation("For full ON saturation switch, base drive current IB must exceed IC(sat)/β to pull VCE down to ~0.2V.")
                    .build();
        }
        if (lower.contains("osi")) {
            return FormulaDto.builder()
                    .title("Protocol Data Unit (PDU) Across OSI Layers")
                    .expression("\\text{Data (L7-L5)} \\longrightarrow \\text{Segment (L4)} \\longrightarrow \\text{Packet (L3)} \\longrightarrow \\text{Frame (L2)} \\longrightarrow \\text{Bits (L1)}")
                    .explanation("Headers and trailers are prepended during encapsulation going down the stack, then stripped during decapsulation going up.")
                    .build();
        }
        if (lower.contains("nfa") || lower.contains("dfa")) {
            return FormulaDto.builder()
                    .title("5-Tuple Formal Mathematical Definition")
                    .expression("M = (Q, \\Sigma, \\delta, q_0, F)")
                    .explanation("Q = finite set of states, Σ = input alphabet, δ = transition function, q₀ ∈ Q = initial state, F ⊆ Q = set of accept states.")
                    .build();
        }
        if (lower.contains("binary search")) {
            return FormulaDto.builder()
                    .title("Safe Midpoint Calculation & Recurrence Relation")
                    .expression("mid = low + \\lfloor \\frac{high - low}{2} \\rfloor \\quad | \\quad T(n) = T(n/2) + O(1) \\implies O(\\log n)")
                    .explanation("Using low + (high - low)/2 strictly avoids integer arithmetic overflow when low + high > Integer.MAX_VALUE.")
                    .build();
        }
        if (lower.contains("newton")) {
            return FormulaDto.builder()
                    .title("Fundamental Force & Vector Dynamics")
                    .expression("\\vec{F}_{net} = m \\cdot \\vec{a} = \\frac{d\\vec{p}}{dt}")
                    .explanation("Net external force is the time rate of change of linear momentum (p = mv). If mass is constant, F = m·a.")
                    .build();
        }
        if (lower.contains("ohm")) {
            return FormulaDto.builder()
                    .title("Ohm's Law & Electrical Power Relations")
                    .expression("V = I \\cdot R \\quad | \\quad P = V \\cdot I = I^2 R = \\frac{V^2}{R}")
                    .explanation("V = Voltage (Volts), I = Current (Amperes), R = Resistance (Ohms Ω), P = Dissipated Power (Watts).")
                    .build();
        }
        if (lower.contains("photosynthesis") || lower.contains("calvin") || lower.contains("chloroplast") || domain == DomainType.BIOLOGY) {
            return FormulaDto.builder()
                    .title("Overall Chemical Reaction Equation for Photosynthesis")
                    .expression("6CO_2 + 6H_2O + \\text{Light Energy} \\xrightarrow{\\text{Chlorophyll}} C_6H_{12}O_6 + 6O_2")
                    .explanation("Carbon dioxide and water, in the presence of solar energy absorbed by chlorophyll, synthesize glucose and release oxygen.")
                    .build();
        }
        if (lower.contains("scheduling") || lower.contains("gantt") || domain == DomainType.OPERATING_SYSTEMS) {
            return FormulaDto.builder()
                    .title("CPU Scheduling Performance Metrics")
                    .expression("\\text{TAT} = \\text{Completion Time} - \\text{Arrival Time} \\quad | \\quad \\text{WT} = \\text{TAT} - \\text{Burst Time}")
                    .explanation("Turnaround Time (TAT) measures total elapsed time; Waiting Time (WT) measures duration spent in ready queue.")
                    .build();
        }
        if (lower.contains("gradient") || lower.contains("descent") || domain == DomainType.AI_ML) {
            return FormulaDto.builder()
                    .title("Gradient Descent Parameter Update Rule")
                    .expression("\\theta_{t+1} = \\theta_t - \\alpha \\cdot \\nabla J(\\theta_t)")
                    .explanation("\\theta = \\text{parameter vector}, \\alpha = \\text{learning rate}, \\nabla J(\\theta) = \\text{gradient of objective loss function}.")
                    .build();
        }
        return FormulaDto.builder()
                .title("Governing Relationship for " + topic)
                .expression("f(x) = \\sum_{i=1}^{n} w_i \\cdot x_i + b")
                .explanation("Mathematical formulation establishing proportional relationships and equilibrium states.")
                .build();
    }

    private List<AlgorithmStepDto> synthesizeAlgorithmSteps(String topic, DomainType domain) {
        List<AlgorithmStepDto> steps = new ArrayList<>();
        steps.add(AlgorithmStepDto.builder()
                .stepNumber(1)
                .instruction("Initialize boundaries and validate precondition: set low = 0, high = n - 1.")
                .build());
        steps.add(AlgorithmStepDto.builder()
                .stepNumber(2)
                .instruction("Compute safe midpoint: mid = low + (high - low) / 2.")
                .build());
        steps.add(AlgorithmStepDto.builder()
                .stepNumber(3)
                .instruction("Evaluate target vs A[mid]: if equal, return mid; if target < A[mid], set high = mid - 1; else set low = mid + 1.")
                .build());
        steps.add(AlgorithmStepDto.builder()
                .stepNumber(4)
                .instruction("Terminate when low > high (element not found) or match is returned.")
                .build());
        return steps;
    }

    private String synthesizePseudocode(String topic, DomainType domain) {
        return "function binarySearch(A, target):\n" +
                "    low = 0, high = A.length - 1\n" +
                "    while low <= high:\n" +
                "        mid = low + floor((high - low) / 2)\n" +
                "        if A[mid] == target: return mid\n" +
                "        else if A[mid] > target: high = mid - 1\n" +
                "        else: low = mid + 1\n" +
                "    return -1 // Target not found";
    }

    private ComplexityDto synthesizeComplexity(String topic) {
        return ComplexityDto.builder()
                .timeBest("O(1)")
                .timeAverage("O(log n)")
                .timeWorst("O(log n)")
                .space("O(1)")
                .build();
    }

    private ExampleDto synthesizeWorkedExample(String topic, DomainType domain, TopicUnderstandingEngine.TopicUnderstandingResult u) {
        String lower = (topic + " " + u.getRawPrompt()).toLowerCase();

        if (lower.contains("recursion")) {
            return ExampleDto.builder()
                    .title("Step-by-Step Recursion Trace: Factorial of n = 4")
                    .scenario("Trace execution stack of factorial(4) where factorial(n) = n * factorial(n-1) and base case factorial(1) = 1.")
                    .stepByStep(List.of(
                            "Winding Phase (Push Frames): factorial(4) ➔ 4 * factorial(3) ➔ 3 * factorial(2) ➔ 2 * factorial(1).",
                            "Base Case Encountered: factorial(1) returns 1 without further recursive calls.",
                            "Unwinding Phase (Pop Frames): 2 * 1 = 2 ➔ 3 * 2 = 6 ➔ 4 * 6 = 24."
                    ))
                    .outputOrResult("Final Return Value: factorial(4) = 24. Total call stack depth = 4 activation frames.")
                    .build();
        }

        if (lower.contains("polymorphism")) {
            return ExampleDto.builder()
                    .title("Runtime Polymorphism Example: Shape Hierarchy")
                    .scenario("Base class Shape with virtual method draw(). Subclasses Circle and Rectangle override draw().")
                    .stepByStep(List.of(
                            "Declaration: Shape s1 = new Circle(); Shape s2 = new Rectangle();",
                            "Invocation: s1.draw(); s2.draw();",
                            "Dynamic Dispatch: JVM vtable resolves s1.draw() to Circle::draw and s2.draw() to Rectangle::draw at runtime."
                    ))
                    .outputOrResult("Output: 'Drawing Circle' followed by 'Drawing Rectangle' using a unified Shape reference.")
                    .build();
        }

        if (lower.contains("quadratic")) {
            return ExampleDto.builder()
                    .title("Solving 2x² - 4x - 6 = 0 via Quadratic Formula")
                    .scenario("Given a = 2, b = -4, c = -6.")
                    .stepByStep(List.of(
                            "Step 1: Calculate Discriminant Δ = b² - 4ac = (-4)² - 4(2)(-6) = 16 + 48 = 64 (Δ > 0).",
                            "Step 2: Apply formula: x = (-(-4) ± √64) / (2 * 2) = (4 ± 8) / 4.",
                            "Step 3: Root 1: x₁ = (4 + 8) / 4 = 12 / 4 = 3. Root 2: x₂ = (4 - 8) / 4 = -4 / 4 = -1."
                    ))
                    .outputOrResult("Roots: x = 3 and x = -1.")
                    .build();
        }

        if (lower.contains("transistor")) {
            return ExampleDto.builder()
                    .title("BJT Switch Design Example")
                    .scenario("Design an NPN switch to drive an LED at IC = 20 mA from VCC = 5V with β = 100.")
                    .stepByStep(List.of(
                            "Step 1: Minimum base current required: IB(min) = IC / β = 20 mA / 100 = 0.2 mA.",
                            "Step 2: Apply overdrive factor 3 for hard saturation: IB(sat) = 3 * 0.2 mA = 0.6 mA.",
                            "Step 3: Base resistor calculation: RB = (Vin - VBE) / IB = (5V - 0.7V) / 0.6 mA = 7.16 kΩ (Use 6.8 kΩ)."
                    ))
                    .outputOrResult("Transistor fully saturates at VCE ≈ 0.2V, delivering clean 20 mA to LED.")
                    .build();
        }

        if (lower.contains("nfa") || lower.contains("dfa")) {
            return ExampleDto.builder()
                    .title("State Transition Trace on String w = '101'")
                    .scenario("Trace language L = { strings ending in '01' } over alphabet Σ = {0, 1}.")
                    .stepByStep(List.of(
                            "Step 1: Start in state q₀. Read symbol '1' ➔ Transition δ(q₀, 1) = q₀.",
                            "Step 2: Read symbol '0' ➔ Transition δ(q₀, 0) = q₁.",
                            "Step 3: Read symbol '1' ➔ Transition δ(q₁, 1) = q₂ (Accept state ∈ F)."
                    ))
                    .outputOrResult("String '101' terminates in state q₂ ∈ F ➔ ACCEPTED.")
                    .build();
        }
        if (lower.contains("binary search")) {
            return ExampleDto.builder()
                    .title("Step-by-Step Search Trace for Target = 15")
                    .scenario("Given sorted array A = [2, 5, 7, 11, 15, 18, 21].")
                    .stepByStep(List.of(
                            "Iteration 1: Low = 0, High = 6 ➔ Mid = 3 (A[3]=11 < 15) ➔ Search Right: Low = 4.",
                            "Iteration 2: Low = 4, High = 6 ➔ Mid = 5 (A[5]=18 > 15) ➔ Search Left: High = 4.",
                            "Iteration 3: Low = 4, High = 4 ➔ Mid = 4 (A[4]=15 == 15) ➔ MATCH FOUND."
                    ))
                    .outputOrResult("Element 15 located successfully at Index 4 in 3 iterations.")
                    .build();
        }
        if (lower.contains("newton")) {
            return ExampleDto.builder()
                    .title("Vehicle Acceleration Calculation (2nd Law)")
                    .scenario("A car of mass m = 1200 kg is accelerated by a net engine force F = 3600 N.")
                    .stepByStep(List.of(
                            "Formula: a = F_net / m",
                            "Substitution: a = 3600 N / 1200 kg",
                            "Calculation: a = 3.0 m/s² forward"
                    ))
                    .outputOrResult("Acceleration a = 3.0 m/s² in the direction of the applied force.")
                    .build();
        }

        return ExampleDto.builder()
                .title("Applied Demonstration of " + topic)
                .scenario("Applying governing principles to analyze a representative academic scenario for " + topic + ".")
                .stepByStep(List.of(
                        "1. Identify input parameters and boundary conditions for " + topic + ".",
                        "2. Execute the governing analytical steps and rules systematically.",
                        "3. Evaluate the output against theoretical correctness criteria."
                ))
                .outputOrResult("Valid analytical result consistent with theoretical principles.")
                .build();
    }

    private List<KeyPointDto> synthesizeKeyPoints(String topic, DomainType domain, TopicUnderstandingEngine.TopicUnderstandingResult u) {
        String lower = (topic + " " + u.getRawPrompt()).toLowerCase();
        List<KeyPointDto> list = new ArrayList<>();

        if (lower.contains("topology")) {
            list.add(KeyPointDto.builder().point("Star topology is the dominant standard in modern Ethernet LANs due to centralized switch management.").starred(true).category("Architecture").build());
            list.add(KeyPointDto.builder().point("Full mesh topology requires n(n-1)/2 physical links, providing maximal fault tolerance.").starred(true).category("Exam Metric").build());
            list.add(KeyPointDto.builder().point("Bus topology requires 50Ω terminators at both ends to prevent signal reflection.").starred(false).category("Hardware").build());
            return list;
        }

        if (lower.contains("osi")) {
            list.add(KeyPointDto.builder().point("Routers operate at Layer 3 (Network); Switches at Layer 2 (Data Link); Hubs/Cables at Layer 1 (Physical).").starred(true).category("Exam Favorite").build());
            list.add(KeyPointDto.builder().point("Transport Layer (L4) provides end-to-end process-to-process communication using port numbers.").starred(true).category("Core Role").build());
            return list;
        }

        list.add(KeyPointDto.builder()
                .point("Understand core mathematical and structural principles of " + topic + " first.")
                .starred(true)
                .category("Core Concept")
                .build());
        list.add(KeyPointDto.builder()
                .point("Always draw the labeled diagram or structural model to illustrate system behavior.")
                .starred(true)
                .category("Visual Architecture")
                .build());
        list.add(KeyPointDto.builder()
                .point("Verify boundary conditions and preconditions before applying formulas.")
                .starred(false)
                .category("Exam Tip")
                .build());
        return list;
    }

    private List<ExamTipDto> synthesizeExamTips(String topic, DomainType domain, TopicUnderstandingEngine.TopicUnderstandingResult u) {
        String lower = (topic + " " + u.getRawPrompt()).toLowerCase();

        if (lower.contains("topology")) {
            return List.of(ExamTipDto.builder()
                    .tip("In exam questions, always distinguish between Physical Topology (cabling layout) and Logical Topology (data path flow).")
                    .commonMistake("Confusing Star-wired Bus (physical star, logical bus) with pure Star topology.")
                    .mnemonic("Star = Central Switch | Bus = Single Backbone | Mesh = n(n-1)/2 Links")
                    .build());
        }

        if (lower.contains("osi")) {
            return List.of(ExamTipDto.builder()
                    .tip("Always list all 7 layers in exact numerical order (L1 to L7 or L7 to L1) with corresponding PDUs.")
                    .commonMistake("Confusing Layer 2 (Data Link - Frames) with Layer 3 (Network - Packets).")
                    .mnemonic("All People Seem To Need Data Processing (Application ➔ Physical)")
                    .build());
        }

        return List.of(ExamTipDto.builder()
                .tip("State formal definitions clearly, draw labeled diagrams first, and show step-by-step reasoning.")
                .commonMistake("Writing vague descriptive paragraphs without specific terminology or numbered points.")
                .mnemonic("Concept ➔ Diagram ➔ Formula ➔ Application")
                .build());
    }
}
