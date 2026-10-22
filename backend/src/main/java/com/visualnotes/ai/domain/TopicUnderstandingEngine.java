package com.visualnotes.ai.domain;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.*;

@Component
public class TopicUnderstandingEngine {

    public enum UserIntent {
        DEFINE,
        EXPLAIN,
        COMPARE,
        PROCESS_MECHANISM,
        DERIVE_FORMULATION,
        ALGORITHM_TRACE,
        CLASSIFY_TYPES,
        SUMMARIZE_REVISION,
        WORKED_EXAMPLE,
        GENERAL_STUDY
    }

    public enum TopicCategory {
        THEORY_CONCEPT,
        MATHEMATICAL_RELATIONSHIP,
        ALGORITHM_PROCEDURE,
        DATA_STRUCTURE,
        PHYSICAL_LAW,
        BIOLOGICAL_PROCESS,
        CHEMICAL_REACTION,
        SYSTEM_ARCHITECTURE,
        NETWORK_PROTOCOL,
        DATABASE_SCHEMA,
        AUTOMATA_LANGUAGE,
        OPERATING_SYSTEM_MECHANIC,
        PROCESS_MECHANISM,
        COMPARATIVE_ANALYSIS,
        PROGRAMMING_PARADIGM,
        GENERAL_ACADEMIC
    }

    public static class TopicUnderstandingResult {
        private String rawPrompt;
        private String normalizedTopic;
        private String mainConcept;
        private UserIntent intent;
        private TopicCategory category;
        private DomainType domain;
        private String subdomain;
        private List<String> requestedScope = new ArrayList<>();
        private List<String> targetEntities = new ArrayList<>();
        private boolean multiEntityComparison;
        private List<String> primarySubtopics = new ArrayList<>();
        private String prerequisiteConcept;
        private Integer requestedPageCount;
        private int suggestedPageCount;
        private String academicAudience;
        private String difficulty;
        private boolean requiresDiagram;
        private String suggestedDiagramType;
        private boolean requiresFormula;
        private boolean requiresComparisonTable;
        private boolean requiresAlgorithm;
        private boolean requiresPseudocode;
        private boolean requiresWorkedExample;
        private Set<String> explicitlyExcludedBlocks = new HashSet<>();
        private Set<String> requiredBlocks = new HashSet<>();
        private String pedagogicalGoal;

        public String getRawPrompt() { return rawPrompt; }
        public void setRawPrompt(String rawPrompt) { this.rawPrompt = rawPrompt; }
        public String getNormalizedTopic() { return normalizedTopic; }
        public void setNormalizedTopic(String normalizedTopic) { this.normalizedTopic = normalizedTopic; }
        public String getMainConcept() { return mainConcept; }
        public void setMainConcept(String mainConcept) { this.mainConcept = mainConcept; }
        public UserIntent getIntent() { return intent; }
        public void setIntent(UserIntent intent) { this.intent = intent; }
        public TopicCategory getCategory() { return category; }
        public void setCategory(TopicCategory category) { this.category = category; }
        public DomainType getDomain() { return domain; }
        public void setDomain(DomainType domain) { this.domain = domain; }
        public String getSubdomain() { return subdomain; }
        public void setSubdomain(String subdomain) { this.subdomain = subdomain; }
        public List<String> getRequestedScope() { return requestedScope; }
        public void setRequestedScope(List<String> requestedScope) { this.requestedScope = requestedScope; }
        public List<String> getTargetEntities() { return targetEntities; }
        public void setTargetEntities(List<String> targetEntities) { this.targetEntities = targetEntities; }
        public boolean isMultiEntityComparison() { return multiEntityComparison; }
        public void setMultiEntityComparison(boolean multiEntityComparison) { this.multiEntityComparison = multiEntityComparison; }
        public List<String> getPrimarySubtopics() { return primarySubtopics; }
        public void setPrimarySubtopics(List<String> primarySubtopics) { this.primarySubtopics = primarySubtopics; }
        public String getPrerequisiteConcept() { return prerequisiteConcept; }
        public void setPrerequisiteConcept(String prerequisiteConcept) { this.prerequisiteConcept = prerequisiteConcept; }
        public Integer getRequestedPageCount() { return requestedPageCount; }
        public void setRequestedPageCount(Integer requestedPageCount) { this.requestedPageCount = requestedPageCount; }
        public int getSuggestedPageCount() { return suggestedPageCount; }
        public void setSuggestedPageCount(int suggestedPageCount) { this.suggestedPageCount = suggestedPageCount; }
        public String getAcademicAudience() { return academicAudience; }
        public void setAcademicAudience(String academicAudience) { this.academicAudience = academicAudience; }
        public String getDifficulty() { return difficulty; }
        public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
        public boolean isRequiresDiagram() { return requiresDiagram; }
        public void setRequiresDiagram(boolean requiresDiagram) { this.requiresDiagram = requiresDiagram; }
        public String getSuggestedDiagramType() { return suggestedDiagramType; }
        public void setSuggestedDiagramType(String suggestedDiagramType) { this.suggestedDiagramType = suggestedDiagramType; }
        public boolean isRequiresFormula() { return requiresFormula; }
        public void setRequiresFormula(boolean requiresFormula) { this.requiresFormula = requiresFormula; }
        public boolean isRequiresComparisonTable() { return requiresComparisonTable; }
        public void setRequiresComparisonTable(boolean requiresComparisonTable) { this.requiresComparisonTable = requiresComparisonTable; }
        public boolean isRequiresAlgorithm() { return requiresAlgorithm; }
        public void setRequiresAlgorithm(boolean requiresAlgorithm) { this.requiresAlgorithm = requiresAlgorithm; }
        public boolean isRequiresPseudocode() { return requiresPseudocode; }
        public void setRequiresPseudocode(boolean requiresPseudocode) { this.requiresPseudocode = requiresPseudocode; }
        public boolean isRequiresWorkedExample() { return requiresWorkedExample; }
        public void setRequiresWorkedExample(boolean requiresWorkedExample) { this.requiresWorkedExample = requiresWorkedExample; }
        public Set<String> getExplicitlyExcludedBlocks() { return explicitlyExcludedBlocks; }
        public void setExplicitlyExcludedBlocks(Set<String> explicitlyExcludedBlocks) { this.explicitlyExcludedBlocks = explicitlyExcludedBlocks; }
        public Set<String> getRequiredBlocks() { return requiredBlocks; }
        public void setRequiredBlocks(Set<String> requiredBlocks) { this.requiredBlocks = requiredBlocks; }
        public String getPedagogicalGoal() { return pedagogicalGoal; }
        public void setPedagogicalGoal(String pedagogicalGoal) { this.pedagogicalGoal = pedagogicalGoal; }
    }

    public TopicUnderstandingResult understand(String rawPrompt, Integer explicitPageCount) {
        TopicUnderstandingResult result = new TopicUnderstandingResult();
        result.setRawPrompt(rawPrompt);
        String lower = rawPrompt.toLowerCase().trim();

        // 1. Page count constraint
        Integer pages = explicitPageCount;
        if (pages == null) {
            Matcher pMatch = Pattern.compile("(?i)\\b(?:in|with|exactly|total of)\\s+(\\d+)\\s+pages?\\b").matcher(rawPrompt);
            if (pMatch.find()) {
                try {
                    pages = Integer.parseInt(pMatch.group(1));
                } catch (Exception ignored) {}
            } else if (lower.contains("one page") || lower.contains("1 page") || lower.contains("single page")) {
                pages = 1;
            }
        }
        result.setRequestedPageCount(pages);
        result.setSuggestedPageCount(pages != null && pages > 0 ? pages : 2);

        // 2. Classify Academic Domain
        DomainType domain = classifyDomain(lower);
        result.setDomain(domain);
        result.setSubdomain(determineSubdomain(domain, lower));

        // 3. User Intent Extraction
        UserIntent intent = extractUserIntent(lower);
        result.setIntent(intent);

        // 4. Topic Category Extraction
        TopicCategory category = determineTopicCategory(lower, domain, intent);
        result.setCategory(category);

        // 5. Semantic Parsing: Main Concept vs Scope vs Multi-entity distinction
        parseSemanticEntitiesAndScope(rawPrompt, lower, domain, result);

        // 6. Prerequisites
        result.setPrerequisiteConcept(extractPrerequisite(lower, domain));

        // 7. Audience and Difficulty
        result.setAcademicAudience(deriveAcademicAudience(domain, lower));
        result.setDifficulty(lower.contains("advanced") || lower.contains("gate") || lower.contains("deep") ? "Advanced" : "Standard University");

        // 8. Dynamic Pedagogical Inclusions & Exclusions
        configurePedagogicalRequirements(result, lower, domain, intent, category);

        result.setPedagogicalGoal(derivePedagogicalGoal(result.getNormalizedTopic(), intent, domain));

        return result;
    }

    private void parseSemanticEntitiesAndScope(String rawPrompt, String lower, DomainType domain, TopicUnderstandingResult result) {
        // First check for explicit comparison or two independent concepts (e.g. "TCP vs UDP", "Stack and Queue", "NFA and DFA")
        boolean isExplicitComparison = lower.contains(" vs ") || lower.contains(" versus ") || lower.contains("difference between") || lower.contains("compare ");

        // Check if prompt has "X and types", "X and its Y", "X with Y", "X and normal forms", etc.
        // These are MODIFIERS / SCOPE, NOT two separate topics!
        Pattern modifierPattern = Pattern.compile("(?i)^(?:explain|describe|what is|what are|notes on|write study notes on)?\\s*([a-zA-Z0-9\\s\\-'.]+?)\\s+(?:and\\s+(?:its\\s+)?(?:\\w+\\s+)?(types|layers|stages|phases|normal forms|conditions|applications|advantages|examples?|properties|characteristics|components|mechanism|handshake)|with\\s+(?:an?\\s+)?(?:\\w+\\s+)?(examples?|worked examples?|diagrams?|derivations?|traces?|proofs?|5-tuple))\\s*.*$");
        Matcher modMatcher = modifierPattern.matcher(rawPrompt);

        if (modMatcher.find()) {
            String mainTopicRaw = modMatcher.group(1).trim();
            String scopeType = modMatcher.group(2) != null ? modMatcher.group(2).trim() : modMatcher.group(3).trim();

            String cleanMain = extractCleanTopicTitle(mainTopicRaw);
            if (cleanMain.equalsIgnoreCase("Topology")) cleanMain = "Network Topology";
            if (cleanMain.equalsIgnoreCase("OSI") || cleanMain.equalsIgnoreCase("OSI Model")) cleanMain = "OSI 7-Layer Reference Model";

            result.setNormalizedTopic(cleanMain);
            result.setMainConcept(cleanMain);
            result.setMultiEntityComparison(false);
            result.setRequestedScope(deriveScopeItems(cleanMain, scopeType, domain, lower));
            result.setPrimarySubtopics(List.of(cleanMain));
            if (scopeType.equalsIgnoreCase("types") || scopeType.equalsIgnoreCase("normal forms")) {
                result.setIntent(UserIntent.CLASSIFY_TYPES);
            } else if (scopeType.equalsIgnoreCase("stages") || scopeType.equalsIgnoreCase("phases")) {
                result.setIntent(UserIntent.PROCESS_MECHANISM);
            } else if (scopeType.equalsIgnoreCase("example") || scopeType.equalsIgnoreCase("worked example")) {
                result.setRequiresWorkedExample(true);
            }
            return;
        }

        // Check for genuine multi-entity comparison (e.g. "Compare TCP and UDP", "difference between X and Y")
        Pattern comparisonPattern = Pattern.compile("(?i)^(?:compare|difference between|differentiate between|contrast)\\s+([a-zA-Z0-9\\s\\-'.]+?)\\s+(?:and|vs|versus)\\s+([a-zA-Z0-9\\s\\-'.]+?)\\s*(?:with\\b.*)?$");
        Matcher compMatcher = comparisonPattern.matcher(rawPrompt);
        boolean compMatches = compMatcher.find();

        // Check for dual-entity definitions like "Define NFA and DFA", "Explain the difference between Stack and Queue"
        Pattern dualEntityPattern = Pattern.compile("(?i)^(?:define|what are|explain the difference between)\\s+([a-zA-Z0-9\\-'.]+)\\s+and\\s+([a-zA-Z0-9\\-'.]+)\\s*$");
        Matcher dualMatcher = dualEntityPattern.matcher(rawPrompt.trim());
        boolean dualMatches = dualMatcher.find();

        if (compMatches || dualMatches || isExplicitComparison) {
            String entity1 = "";
            String entity2 = "";
            if (compMatches) {
                entity1 = extractCleanTopicTitle(compMatcher.group(1));
                entity2 = extractCleanTopicTitle(compMatcher.group(2));
            } else if (dualMatches) {
                entity1 = extractCleanTopicTitle(dualMatcher.group(1));
                entity2 = extractCleanTopicTitle(dualMatcher.group(2));
            } else if (rawPrompt.contains(" vs ")) {
                String[] parts = rawPrompt.split("(?i)\\s+vs\\s+");
                entity1 = extractCleanTopicTitle(parts[0]);
                entity2 = extractCleanTopicTitle(parts[1]);
            } else if (rawPrompt.contains(" versus ")) {
                String[] parts = rawPrompt.split("(?i)\\s+versus\\s+");
                entity1 = extractCleanTopicTitle(parts[0]);
                entity2 = extractCleanTopicTitle(parts[1]);
            }

            // Check if entity2 is just a scope word (like "types", "examples", "layers")
            if (isScopeWord(entity2)) {
                String cleanMain = entity1;
                if (cleanMain.equalsIgnoreCase("Topology")) cleanMain = "Network Topology";
                result.setNormalizedTopic(cleanMain);
                result.setMainConcept(cleanMain);
                result.setMultiEntityComparison(false);
                result.setRequestedScope(deriveScopeItems(cleanMain, entity2, domain, lower));
                result.setPrimarySubtopics(List.of(cleanMain));
                return;
            }

            if (!entity1.isEmpty() && !entity2.isEmpty()) {
                String combinedTitle = entity1 + " vs " + entity2;
                result.setNormalizedTopic(combinedTitle);
                result.setMainConcept(combinedTitle);
                result.setTargetEntities(List.of(entity1, entity2));
                result.setMultiEntityComparison(true);
                result.setPrimarySubtopics(List.of(entity1, entity2));
                result.setIntent(UserIntent.COMPARE);
                return;
            }
        }

        // Single Topic
        String cleanTopic = extractCleanTopicTitle(rawPrompt);
        if (cleanTopic.equalsIgnoreCase("Topology")) cleanTopic = "Network Topology";
        if (cleanTopic.equalsIgnoreCase("OSI") || cleanTopic.equalsIgnoreCase("OSI Model")) cleanTopic = "OSI 7-Layer Reference Model";

        result.setNormalizedTopic(cleanTopic);
        result.setMainConcept(cleanTopic);
        result.setMultiEntityComparison(false);
        result.setPrimarySubtopics(List.of(cleanTopic));
    }

    private boolean isScopeWord(String word) {
        if (word == null) return false;
        String w = word.toLowerCase().trim();
        return w.equals("types") || w.equals("layers") || w.equals("stages") || w.equals("phases")
                || w.equals("examples") || w.equals("normal forms") || w.equals("conditions")
                || w.equals("applications") || w.equals("properties") || w.equals("diagram") || w.equals("trace");
    }

    private List<String> deriveScopeItems(String topic, String scopeType, DomainType domain, String lower) {
        String tLow = topic.toLowerCase();
        if (tLow.contains("topology") || scopeType.equalsIgnoreCase("types")) {
            return List.of("Topology Types", "Star Topology", "Bus Topology", "Ring Topology", "Mesh Topology", "Tree Topology", "Hybrid Topology");
        }
        if (tLow.contains("osi") || scopeType.equalsIgnoreCase("layers")) {
            return List.of("OSI 7 Layers", "Application (L7)", "Presentation (L6)", "Session (L5)", "Transport (L4)", "Network (L3)", "Data Link (L2)", "Physical (L1)");
        }
        if (tLow.contains("normalization") || scopeType.equalsIgnoreCase("normal forms")) {
            return List.of("Normal Forms", "1NF (Atomic Attributes)", "2NF (No Partial Dependency)", "3NF (No Transitive Dependency)", "BCNF (Super Key Condition)");
        }
        if (tLow.contains("photosynthesis") || scopeType.equalsIgnoreCase("stages") || scopeType.equalsIgnoreCase("phases")) {
            return List.of("Photosynthesis Stages", "Light-Dependent Reactions (Thylakoid Membrane)", "Calvin Cycle / Light-Independent Reactions (Stroma)");
        }
        if (tLow.contains("deadlock") || scopeType.equalsIgnoreCase("conditions")) {
            return List.of("Deadlock Conditions", "Mutual Exclusion", "Hold and Wait", "No Preemption", "Circular Wait");
        }
        if (tLow.contains("mitosis")) {
            return List.of("Mitosis Stages", "Prophase", "Metaphase", "Anaphase", "Telophase", "Cytokinesis");
        }
        if (tLow.contains("join") || scopeType.equalsIgnoreCase("types")) {
            return List.of("SQL Join Types", "INNER JOIN", "LEFT OUTER JOIN", "RIGHT OUTER JOIN", "FULL OUTER JOIN", "CROSS JOIN");
        }
        return List.of(scopeType + " of " + topic);
    }

    private UserIntent extractUserIntent(String lower) {
        if (lower.contains("compare") || lower.contains("difference") || lower.contains("vs") || lower.contains("versus") || lower.contains("differentiate")) {
            return UserIntent.COMPARE;
        }
        if (lower.startsWith("define") || lower.contains("definition of") || lower.contains("formal definition")) {
            return UserIntent.DEFINE;
        }
        if (lower.contains("derive") || lower.contains("derivation") || lower.contains("proof of") || lower.contains("mathematical proof")) {
            return UserIntent.DERIVE_FORMULATION;
        }
        if (lower.contains("types of") || lower.contains("classification of") || lower.contains("categories of") || lower.contains("and types") || lower.contains("join types") || lower.contains("scheduling algorithms")) {
            return UserIntent.CLASSIFY_TYPES;
        }
        if (lower.contains("how does") || lower.contains("working of") || lower.contains("process of") || lower.contains("mechanism of") || lower.contains("stages of") || lower.contains("phases of") || lower.contains("how it works")) {
            return UserIntent.PROCESS_MECHANISM;
        }
        if (lower.contains("algorithm trace") || lower.contains("trace of") || lower.contains("step by step execution") || lower.contains("dry run")) {
            return UserIntent.ALGORITHM_TRACE;
        }
        if (lower.contains("with example") || lower.contains("with an example") || lower.contains("worked example")) {
            return UserIntent.WORKED_EXAMPLE;
        }
        if (lower.contains("summary") || lower.contains("revision") || lower.contains("cheat sheet") || lower.contains("quick notes")) {
            return UserIntent.SUMMARIZE_REVISION;
        }
        return UserIntent.EXPLAIN;
    }

    private TopicCategory determineTopicCategory(String lower, DomainType domain, UserIntent intent) {
        if (intent == UserIntent.COMPARE) return TopicCategory.COMPARATIVE_ANALYSIS;
        if (domain == DomainType.THEORY_OF_COMPUTATION || lower.contains("automata") || lower.contains("grammar") || lower.contains("turing")) return TopicCategory.AUTOMATA_LANGUAGE;
        if (domain == DomainType.PHYSICS) return TopicCategory.PHYSICAL_LAW;
        if (domain == DomainType.BIOLOGY) return TopicCategory.BIOLOGICAL_PROCESS;
        if (domain == DomainType.CHEMISTRY) return TopicCategory.CHEMICAL_REACTION;
        if (domain == DomainType.MATHEMATICS || intent == UserIntent.DERIVE_FORMULATION) return TopicCategory.MATHEMATICAL_RELATIONSHIP;
        if (domain == DomainType.OPERATING_SYSTEMS) return TopicCategory.OPERATING_SYSTEM_MECHANIC;
        if (domain == DomainType.DBMS) return TopicCategory.DATABASE_SCHEMA;
        if (domain == DomainType.COMPUTER_NETWORKS) return TopicCategory.NETWORK_PROTOCOL;
        if (domain == DomainType.ALGORITHMS) return TopicCategory.ALGORITHM_PROCEDURE;
        if (domain == DomainType.DATA_STRUCTURES) return TopicCategory.DATA_STRUCTURE;
        if (domain == DomainType.PROGRAMMING || domain == DomainType.SOFTWARE_ENGINEERING) return TopicCategory.PROGRAMMING_PARADIGM;
        if (intent == UserIntent.PROCESS_MECHANISM) return TopicCategory.PROCESS_MECHANISM;
        return TopicCategory.THEORY_CONCEPT;
    }

    private DomainType classifyDomain(String lower) {
        // 1. Biology & Life Sciences
        if (lower.contains("photosynthesis") || lower.contains("respiration") || lower.contains("mitosis") || lower.contains("meiosis")
                || lower.contains("dna") || lower.contains("rna") || lower.contains("enzyme") || lower.contains("biology")
                || lower.contains("cell") || lower.contains("genetics") || lower.contains("ecology") || lower.contains("calvin")) {
            return DomainType.BIOLOGY;
        }

        // 2. Computer Networks
        if (lower.contains("topology") || lower.matches(".*\\b(osi|tcp|udp|ip|dns|http|https|arp|icmp|dhcp|nat|bgp|ospf|lan|wan)\\b.*")
                || lower.contains("handshake") || lower.contains("subnetting") || lower.contains("routing") || lower.contains("ethernet")
                || lower.contains("congestion control") || lower.contains("packet") || lower.contains("computer network")) {
            return DomainType.COMPUTER_NETWORKS;
        }

        // 2. Operating Systems
        if (lower.contains("cpu scheduling") || lower.contains("scheduling algorithm") || lower.contains("deadlock") || lower.contains("paging")
                || lower.contains("virtual memory") || lower.contains("semaphore") || lower.contains("mutex") || lower.contains("operating system")
                || lower.contains("round robin") || lower.contains("fcfs") || lower.contains("sjf") || lower.contains("page replacement")
                || lower.contains("banker") || lower.contains("fork") || lower.contains("ipc") || lower.contains("process synchronization")) {
            return DomainType.OPERATING_SYSTEMS;
        }

        // 3. DBMS
        if (lower.contains("normalization") || lower.matches(".*\\b(1nf|2nf|3nf|bcnf|4nf|dbms|sql|acid|rdbms|nosql|crud)\\b.*")
                || lower.contains("relational") || lower.contains("indexing") || lower.contains("transaction") || lower.contains("database")
                || lower.contains("candidate key") || lower.contains("functional dependenc") || lower.contains("join")) {
            return DomainType.DBMS;
        }

        // 4. Theory of Computation
        if (lower.contains("nfa") || lower.contains("dfa") || lower.contains("automata") || lower.contains("turing machine")
                || lower.contains("cfg") || lower.contains("pda") || lower.contains("chomsky") || lower.contains("regular expression")
                || lower.contains("context free") || lower.contains("theory of computation") || lower.contains("pumping lemma")) {
            return DomainType.THEORY_OF_COMPUTATION;
        }

        // 5. Data Structures
        if (lower.contains("linked list") || lower.contains("stack") || lower.contains("queue") || lower.contains("binary tree")
                || lower.contains("avl tree") || lower.contains("heap") || lower.contains("trie") || lower.contains("hash table")
                || lower.contains("graph adjacency") || lower.contains("data structure")) {
            return DomainType.DATA_STRUCTURES;
        }

        // 6. Algorithms
        if (lower.contains("binary search") || lower.contains("quick sort") || lower.contains("merge sort") || lower.contains("dijkstra")
                || lower.contains("bfs") || lower.contains("dfs") || lower.contains("dynamic programming") || lower.contains("greedy")
                || lower.contains("divide and conquer") || lower.contains("algorithm") || lower.contains("asymptotic") || lower.contains("big o")) {
            return DomainType.ALGORITHMS;
        }

        // 7. Programming & Software Engineering
        if (lower.contains("recursion") || lower.contains("polymorphism") || lower.contains("inheritance") || lower.contains("encapsulation")
                || lower.contains("oop") || lower.contains("object oriented") || lower.contains("design pattern") || lower.contains("singleton")
                || lower.contains("solid principles") || lower.contains("method overloading") || lower.contains("method overriding")
                || lower.contains("microservices") || lower.contains("software engineering") || lower.contains("uml")) {
            return DomainType.SOFTWARE_ENGINEERING;
        }

        // 8. Physics & Mechanics
        if (lower.contains("newton") || lower.contains("force") || lower.contains("kinematics") || lower.contains("thermodynamics")
                || lower.contains("optics") || lower.contains("gravity") || lower.contains("momentum") || lower.contains("physics")
                || lower.contains("quantum") || lower.contains("electromagnetism") || lower.contains("maxwell") || lower.contains("snell")) {
            return DomainType.PHYSICS;
        }

        // 9. Electronics
        if (lower.contains("ohm") || lower.contains("circuit") || lower.contains("diode") || lower.contains("transistor")
                || lower.contains("bjt") || lower.contains("mosfet") || lower.contains("logic gate") || lower.contains("op amp")
                || lower.contains("kirchhoff") || lower.contains("electronics") || lower.contains("semiconductor") || lower.contains("switch")) {
            return DomainType.ELECTRONICS;
        }

        // 10. Biology & Life Sciences
        if (lower.contains("photosynthesis") || lower.contains("respiration") || lower.contains("mitosis") || lower.contains("meiosis")
                || lower.contains("dna") || lower.contains("rna") || lower.contains("enzyme") || lower.contains("biology")
                || lower.contains("cell") || lower.contains("genetics") || lower.contains("ecology") || lower.contains("calvin")) {
            return DomainType.BIOLOGY;
        }

        // 11. Chemistry
        if (lower.contains("reaction") || lower.contains("organic chemistry") || lower.contains("periodic table")
                || lower.contains("equilibrium") || lower.contains("acid") || lower.contains("base") || lower.contains("chemistry")
                || lower.contains("catalyst") || lower.contains("mole") || lower.contains("stoichiometry") || lower.contains("redox")) {
            return DomainType.CHEMISTRY;
        }

        // 12. Mathematics
        if (lower.contains("quadratic") || lower.contains("fourier") || lower.contains("calculus") || lower.contains("derivative")
                || lower.contains("integral") || lower.contains("matrix") || lower.contains("probability") || lower.contains("eigenvalue")
                || lower.contains("mathematics") || lower.contains("differential") || lower.contains("laplace") || lower.contains("bayes")) {
            return DomainType.MATHEMATICS;
        }

        // 13. AI & ML
        if (lower.contains("gradient descent") || lower.contains("neural network") || lower.contains("backpropagation")
                || lower.contains("deep learning") || lower.contains("machine learning") || lower.contains("transformer")
                || lower.contains("reinforcement learning") || lower.contains("convolutional")) {
            return DomainType.AI_ML;
        }

        return DomainType.COMPUTER_NETWORKS; // Sensible default for engineering
    }

    private String determineSubdomain(DomainType domain, String lower) {
        switch (domain) {
            case COMPUTER_NETWORKS:
                if (lower.contains("topology")) return "Physical & Logical Network Topologies";
                if (lower.contains("osi") || lower.contains("layer")) return "7-Layer OSI Reference Architecture";
                if (lower.contains("tcp") || lower.contains("udp") || lower.contains("handshake")) return "Transport Layer & Protocol Dynamics";
                return "Computer Networks & Internetworking";
            case OPERATING_SYSTEMS:
                if (lower.contains("schedul") || lower.contains("gantt")) return "CPU Scheduling & Multiprogramming";
                if (lower.contains("deadlock")) return "Deadlock Characterization & Avoidance";
                if (lower.contains("memory") || lower.contains("paging")) return "Virtual Memory Management";
                return "Operating System Internals";
            case DBMS:
                if (lower.contains("normal")) return "Relational Normalization & Functional Dependencies";
                if (lower.contains("join")) return "Relational Algebra & SQL Operations";
                if (lower.contains("acid") || lower.contains("trans")) return "Transaction Management & Concurrency Control";
                return "Database Management Systems";
            case THEORY_OF_COMPUTATION:
                if (lower.contains("nfa") || lower.contains("dfa") || lower.contains("automata")) return "Finite Automata & Regular Languages";
                if (lower.contains("turing")) return "Turing Machines & Computability";
                return "Theory of Computation";
            case DATA_STRUCTURES:
                if (lower.contains("stack") || lower.contains("queue")) return "Linear Data Structures & Abstract Data Types";
                if (lower.contains("tree") || lower.contains("bst") || lower.contains("avl")) return "Hierarchical Tree Structures";
                return "Data Structures";
            case ALGORITHMS:
                if (lower.contains("search")) return "Searching Algorithms";
                if (lower.contains("sort")) return "Sorting & Divide-and-Conquer";
                return "Design & Analysis of Algorithms";
            case SOFTWARE_ENGINEERING:
                if (lower.contains("polymorph") || lower.contains("oop") || lower.contains("inherit")) return "Object-Oriented Programming Principles";
                if (lower.contains("recur")) return "Functional & Recursive Programming";
                return "Software Architecture & Design";
            case PHYSICS:
                if (lower.contains("newton") || lower.contains("force") || lower.contains("motion")) return "Classical Mechanics & Dynamics";
                if (lower.contains("thermo")) return "Thermodynamics";
                return "Physics";
            case ELECTRONICS:
                if (lower.contains("transistor") || lower.contains("switch") || lower.contains("bjt")) return "Semiconductor Devices & Transistor Switching";
                if (lower.contains("ohm") || lower.contains("circuit")) return "DC Circuit Analysis & Ohm's Law";
                return "Electronic Circuits & Systems";
            case BIOLOGY:
                if (lower.contains("photo")) return "Plant Physiology & Bioenergetics";
                if (lower.contains("mitosis") || lower.contains("meiosis") || lower.contains("cell")) return "Cell Biology & Cytology";
                return "Biological Sciences";
            case MATHEMATICS:
                if (lower.contains("quadratic")) return "Algebra & Polynomial Equations";
                if (lower.contains("calculus") || lower.contains("derivative")) return "Differential & Integral Calculus";
                return "Applied Mathematics";
            default:
                return domain.getDisplayName();
        }
    }

    public String extractCleanTopicTitle(String rawPrompt) {
        String clean = rawPrompt.replaceAll("(?i)^(explain|define|describe|what is|what are|difference between|compare|differentiate between|how does|process of|derive|prove that|write study notes on|give me notes on|notes on|notes for)\\s+", "")
                .replaceAll("(?i)\\s+(with\\b.*|for \\d+ marks?|in simple language|in detail|for b\\.tech|for exams?|in \\d+ pages?).*$", "")
                .replaceAll("[^a-zA-Z0-9\\s+\\-#./()]", "")
                .trim();

        if (clean.isEmpty()) return "Study Notes";

        String[] words = clean.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String w = words[i];
            if (!w.isEmpty()) {
                if (w.equalsIgnoreCase("tcp")) sb.append("TCP ");
                else if (w.equalsIgnoreCase("udp")) sb.append("UDP ");
                else if (w.equalsIgnoreCase("osi")) sb.append("OSI ");
                else if (w.equalsIgnoreCase("dbms")) sb.append("DBMS ");
                else if (w.equalsIgnoreCase("nfa")) sb.append("NFA ");
                else if (w.equalsIgnoreCase("dfa")) sb.append("DFA ");
                else if (w.equalsIgnoreCase("sql")) sb.append("SQL ");
                else if (w.equalsIgnoreCase("bjt")) sb.append("BJT ");
                else if (w.equalsIgnoreCase("cpu")) sb.append("CPU ");
                else if (w.equalsIgnoreCase("ip")) sb.append("IP ");
                else if (w.equalsIgnoreCase("vs")) sb.append("vs ");
                else if (w.equalsIgnoreCase("and") && i > 0) sb.append("and ");
                else if ((w.equalsIgnoreCase("of") || w.equalsIgnoreCase("in") || w.equalsIgnoreCase("for") || w.equalsIgnoreCase("the") || w.equalsIgnoreCase("a") || w.equalsIgnoreCase("an") || w.equalsIgnoreCase("as")) && i > 0) {
                    sb.append(w.toLowerCase()).append(" ");
                } else {
                    sb.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1)).append(" ");
                }
            }
        }
        return sb.toString().trim();
    }

    private String extractPrerequisite(String lower, DomainType domain) {
        if (lower.contains("topology")) return "Computer Networks Overview & Node-to-Node Links";
        if (lower.contains("osi")) return "Networking Fundamentals & Protocol Encapsulation";
        if (lower.contains("bcnf") || lower.contains("3nf") || lower.contains("normalization")) return "Functional Dependencies & Candidate Keys";
        if (lower.contains("binary search")) return "Sorted Array Precondition";
        if (lower.contains("dfa") || lower.contains("nfa")) return "Alphabets, Strings & State Transition Concepts";
        if (lower.contains("quadratic")) return "Basic Algebra & Polynomial Expansion";
        if (lower.contains("transistor")) return "P-N Junction & Semiconductor Biasing";
        if (lower.contains("mitosis")) return "Interphase & DNA Replication (S-Phase)";
        return null;
    }

    private String deriveAcademicAudience(DomainType domain, String lower) {
        switch (domain) {
            case COMPUTER_NETWORKS:
            case OPERATING_SYSTEMS:
            case DBMS:
            case SOFTWARE_ENGINEERING:
            case ALGORITHMS:
            case DATA_STRUCTURES:
                return "Computer Science & Engineering Students";
            case ELECTRONICS:
                return "Electronics & Electrical Engineering Students";
            case PHYSICS:
            case MATHEMATICS:
            case CHEMISTRY:
                return "Science & Engineering Undergraduates";
            case BIOLOGY:
                return "Life Sciences & Medical Students";
            default:
                return "University Undergraduates";
        }
    }

    private void configurePedagogicalRequirements(
            TopicUnderstandingResult result,
            String lower,
            DomainType domain,
            UserIntent intent,
            TopicCategory category) {

        Set<String> required = result.getRequiredBlocks();
        Set<String> excluded = result.getExplicitlyExcludedBlocks();

        // 1. Comparison Intent
        if (intent == UserIntent.COMPARE || result.isMultiEntityComparison()) {
            result.setRequiresComparisonTable(true);
            result.setRequiresDiagram(true);
            result.setRequiresFormula(domain == DomainType.PHYSICS || domain == DomainType.THEORY_OF_COMPUTATION || domain == DomainType.BIOLOGY || domain == DomainType.CHEMISTRY || domain == DomainType.MATHEMATICS);
            result.setRequiresAlgorithm(false);
            result.setRequiresPseudocode(false);
            result.setRequiresWorkedExample(true);
            result.setSuggestedDiagramType(deriveDiagramType(lower, domain));

            required.addAll(List.of("definitions", "comparison_table", "key_differences", "exam_takeaways"));
            excluded.addAll(List.of("algorithm", "pseudocode", "complexity", "advantages", "limitations"));
            return;
        }

        // 2. Classification / Types Intent
        if (intent == UserIntent.CLASSIFY_TYPES || lower.contains("types") || lower.contains("categories") || lower.contains("join") || lower.contains("topology")) {
            result.setRequiresComparisonTable(true);
            result.setRequiresDiagram(true);
            result.setRequiresFormula(domain == DomainType.PHYSICS || domain == DomainType.OPERATING_SYSTEMS || domain == DomainType.MATHEMATICS || domain == DomainType.BIOLOGY || domain == DomainType.CHEMISTRY);
            result.setRequiresAlgorithm(domain == DomainType.ALGORITHMS);
            result.setRequiresPseudocode(domain == DomainType.ALGORITHMS);
            result.setRequiresWorkedExample(true);
            result.setSuggestedDiagramType(deriveDiagramType(lower, domain));

            required.addAll(List.of("taxonomical_overview", "classification_table", "type_details", "worked_example"));
            excluded.addAll(List.of("advantages", "limitations"));
            return;
        }

        // 3. Pure Definition Intent
        if (intent == UserIntent.DEFINE) {
            result.setRequiresComparisonTable(false);
            result.setRequiresAlgorithm(false);
            result.setRequiresPseudocode(false);
            result.setRequiresWorkedExample(lower.contains("example"));
            result.setRequiresDiagram(true);
            result.setSuggestedDiagramType(deriveDiagramType(lower, domain));

            if (domain == DomainType.THEORY_OF_COMPUTATION) {
                result.setRequiresFormula(true); // 5-tuple
                if (result.getPrimarySubtopics().size() > 1 || lower.contains("and") || lower.contains("vs")) {
                    result.setRequiresComparisonTable(true);
                }
            } else if (lower.contains("osi") || domain == DomainType.BIOLOGY || domain == DomainType.PHYSICS || domain == DomainType.MATHEMATICS) {
                result.setRequiresFormula(true);
            }

            required.addAll(List.of("formal_definition", "foundations", "diagram", "key_properties"));
            excluded.addAll(List.of("algorithm", "pseudocode", "complexity", "advantages", "limitations", "code"));
            return;
        }

        // 4. Mathematical Derivation / Physical Law Intent
        if (intent == UserIntent.DERIVE_FORMULATION || domain == DomainType.PHYSICS || domain == DomainType.MATHEMATICS || category == TopicCategory.PHYSICAL_LAW) {
            result.setRequiresFormula(true);
            result.setRequiresDiagram(true);
            result.setRequiresWorkedExample(true);
            result.setRequiresAlgorithm(false);
            result.setRequiresPseudocode(false);
            result.setRequiresComparisonTable(false);
            result.setSuggestedDiagramType(deriveDiagramType(lower, domain));

            required.addAll(List.of("governing_laws", "mathematical_derivation", "diagram", "physical_meaning", "worked_application"));
            excluded.addAll(List.of("algorithm", "pseudocode", "complexity", "advantages", "limitations"));
            return;
        }

        // 5. Algorithm / Data Structure Intent
        if (domain == DomainType.ALGORITHMS || domain == DomainType.DATA_STRUCTURES || intent == UserIntent.ALGORITHM_TRACE) {
            result.setRequiresAlgorithm(true);
            result.setRequiresPseudocode(true);
            result.setRequiresWorkedExample(true);
            result.setRequiresDiagram(true);
            result.setRequiresFormula(true);
            result.setRequiresComparisonTable(false);
            result.setSuggestedDiagramType(deriveDiagramType(lower, domain));

            required.addAll(List.of("definition_preconditions", "algorithm_steps", "diagram_trace", "worked_example", "complexity_analysis"));
            excluded.addAll(List.of("advantages", "limitations"));
            return;
        }

        // 6. General Systems / Scientific / Domain Default
        result.setRequiresDiagram(true);
        result.setSuggestedDiagramType(deriveDiagramType(lower, domain));
        result.setRequiresWorkedExample(true);
        result.setRequiresFormula(domain == DomainType.ELECTRONICS || domain == DomainType.OPERATING_SYSTEMS || domain == DomainType.CHEMISTRY || domain == DomainType.BIOLOGY || domain == DomainType.PHYSICS || domain == DomainType.MATHEMATICS || domain == DomainType.AI_ML || lower.contains("osi") || lower.contains("photosynthesis") || lower.contains("gradient"));
        result.setRequiresComparisonTable(domain == DomainType.DBMS || domain == DomainType.OPERATING_SYSTEMS || lower.contains("join") || lower.contains("normal") || lower.contains("topology"));
        result.setRequiresAlgorithm(domain == DomainType.ALGORITHMS);
        result.setRequiresPseudocode(domain == DomainType.ALGORITHMS);

        if (lower.contains("example")) {
            result.setRequiresWorkedExample(true);
        }

        required.addAll(List.of("core_concept", "working_mechanism", "diagram", "worked_example", "exam_takeaways"));
        excluded.addAll(List.of("algorithm", "pseudocode", "complexity", "advantages", "limitations"));
    }

    private String deriveDiagramType(String lower, DomainType domain) {
        if (lower.contains("topology")) return "concept-map";
        if (domain == DomainType.THEORY_OF_COMPUTATION || lower.contains("automata") || lower.contains("nfa") || lower.contains("dfa")) return "automata-state-transition";
        if (domain == DomainType.OPERATING_SYSTEMS || lower.contains("scheduling") || lower.contains("gantt")) return "os-gantt-chart";
        if (lower.contains("join") || lower.contains("sql")) return "sql-join-venn";
        if (domain == DomainType.ELECTRONICS || lower.contains("ohm") || lower.contains("circuit") || lower.contains("transistor")) return "circuit-schematic";
        if (domain == DomainType.PHYSICS || lower.contains("newton") || lower.contains("force")) return "physics-diagram";
        if (domain == DomainType.BIOLOGY || lower.contains("photo") || lower.contains("cell") || lower.contains("mitosis")) return "science-reaction";
        if (domain == DomainType.DBMS || lower.contains("normal")) return "dbms-normalization";
        if (domain == DomainType.AI_ML || lower.contains("neural") || lower.contains("gradient")) return "neural-network";
        if (domain == DomainType.COMPUTER_NETWORKS || lower.contains("osi") || lower.contains("layer")) return "layer-stack";
        if (domain == DomainType.ALGORITHMS) {
            if (lower.contains("search")) return "binary-search-array";
            if (lower.contains("sort")) return "sorting-partition";
            if (lower.contains("graph") || lower.contains("dijkstra") || lower.contains("dfs")) return "graph-network";
            if (lower.contains("tree") || lower.contains("bfs")) return "tree-traversal";
        }
        return "concept-map";
    }

    private String derivePedagogicalGoal(String topic, UserIntent intent, DomainType domain) {
        switch (intent) {
            case DEFINE:
                return "Provide precise formal definition, theoretical foundations, and core architectural rules for " + topic + ".";
            case COMPARE:
                return "Differentiate core architectural parameters, mathematical rules, operational behavior, and trade-offs.";
            case DERIVE_FORMULATION:
                return "Establish fundamental principles, derive governing equations step-by-step, and state interpretations.";
            case CLASSIFY_TYPES:
                return "Deliver clear taxonomical classification, structural characteristics, trade-offs, and practical deployment.";
            case ALGORITHM_TRACE:
                return "Explain preconditions, step-by-step procedural execution, visual pointer trace, and asymptotic complexity.";
            case PROCESS_MECHANISM:
                return "Explain step-by-step physical/system mechanisms, internal state progression, and cause-effect interactions.";
            default:
                return "Deliver clear, high-yield academic study notes on " + topic + " with intuitive explanations and clean visual architecture.";
        }
    }
}
