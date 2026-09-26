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
        GENERAL_ACADEMIC
    }

    public static class TopicUnderstandingResult {
        private String rawPrompt;
        private String normalizedTopic;
        private UserIntent intent;
        private TopicCategory category;
        private DomainType domain;
        private String subdomain;
        private List<String> primarySubtopics;
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
        private Set<String> explicitlyExcludedBlocks;
        private Set<String> requiredBlocks;
        private String pedagogicalGoal;

        public String getRawPrompt() { return rawPrompt; }
        public void setRawPrompt(String rawPrompt) { this.rawPrompt = rawPrompt; }
        public String getNormalizedTopic() { return normalizedTopic; }
        public void setNormalizedTopic(String normalizedTopic) { this.normalizedTopic = normalizedTopic; }
        public UserIntent getIntent() { return intent; }
        public void setIntent(UserIntent intent) { this.intent = intent; }
        public TopicCategory getCategory() { return category; }
        public void setCategory(TopicCategory category) { this.category = category; }
        public DomainType getDomain() { return domain; }
        public void setDomain(DomainType domain) { this.domain = domain; }
        public String getSubdomain() { return subdomain; }
        public void setSubdomain(String subdomain) { this.subdomain = subdomain; }
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

        // 5. Clean Normalized Topic Title
        String normalizedTopic = extractCleanTopicTitle(rawPrompt);
        result.setNormalizedTopic(normalizedTopic);

        // 6. Primary Subtopics & Prerequisites
        result.setPrimarySubtopics(extractSubtopics(rawPrompt, lower, normalizedTopic));
        result.setPrerequisiteConcept(extractPrerequisite(lower, domain));

        // 7. Audience and Difficulty
        result.setAcademicAudience("College / University Students");
        result.setDifficulty(lower.contains("advanced") || lower.contains("gate") ? "Advanced" : "Standard Academic");

        // 8. Dynamic Component Inclusions & Exclusions
        Set<String> required = new HashSet<>();
        Set<String> excluded = new HashSet<>();

        configurePedagogicalRequirements(result, lower, domain, intent, category, required, excluded);

        result.setRequiredBlocks(required);
        result.setExplicitlyExcludedBlocks(excluded);
        result.setPedagogicalGoal(derivePedagogicalGoal(normalizedTopic, intent, domain));

        return result;
    }

    private UserIntent extractUserIntent(String lower) {
        if (lower.contains("compare") || lower.contains("difference") || lower.contains("vs") || lower.contains("versus") || lower.contains("differentiate")) {
            return UserIntent.COMPARE;
        }
        if (lower.startsWith("define") || lower.contains("definition of") || lower.contains("what is") || lower.contains("formal definition")) {
            return UserIntent.DEFINE;
        }
        if (lower.contains("derive") || lower.contains("derivation") || lower.contains("proof of") || lower.contains("mathematical proof")) {
            return UserIntent.DERIVE_FORMULATION;
        }
        if (lower.contains("types of") || lower.contains("classification of") || lower.contains("categories of") || lower.contains("join types") || lower.contains("scheduling algorithms")) {
            return UserIntent.CLASSIFY_TYPES;
        }
        if (lower.contains("how does") || lower.contains("working of") || lower.contains("process of") || lower.contains("mechanism of") || lower.contains("how it works")) {
            return UserIntent.PROCESS_MECHANISM;
        }
        if (lower.contains("algorithm trace") || lower.contains("trace of") || lower.contains("step by step execution") || lower.contains("dry run")) {
            return UserIntent.ALGORITHM_TRACE;
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
        if (intent == UserIntent.PROCESS_MECHANISM) return TopicCategory.PROCESS_MECHANISM;
        return TopicCategory.THEORY_CONCEPT;
    }

    private DomainType classifyDomain(String lower) {
        if (lower.contains("newton") || lower.contains("force") || lower.contains("kinematics") || lower.contains("thermodynamics")
                || lower.contains("optics") || lower.contains("gravity") || lower.contains("momentum") || lower.contains("physics")
                || lower.contains("quantum") || lower.contains("electromagnetism") || lower.contains("maxwell")) {
            return DomainType.PHYSICS;
        }
        if (lower.contains("photosynthesis") || lower.contains("respiration") || lower.contains("mitosis") || lower.contains("meiosis")
                || lower.contains("dna") || lower.contains("rna") || lower.contains("enzyme") || lower.contains("biology")
                || lower.contains("cell") || lower.contains("genetics") || lower.contains("ecology")) {
            return DomainType.BIOLOGY;
        }
        if (lower.contains("reaction") || lower.contains("organic chemistry") || lower.contains("periodic table")
                || lower.contains("equilibrium") || lower.contains("acid") || lower.contains("base") || lower.contains("chemistry")
                || lower.contains("catalyst") || lower.contains("mole") || lower.contains("stoichiometry")) {
            return DomainType.CHEMISTRY;
        }
        if (lower.contains("fourier") || lower.contains("calculus") || lower.contains("derivative") || lower.contains("integral")
                || lower.contains("matrix") || lower.contains("probability") || lower.contains("eigenvalue") || lower.contains("mathematics")
                || lower.contains("differential") || lower.contains("laplace") || lower.contains("bayes")) {
            return DomainType.MATHEMATICS;
        }
        if (lower.contains("normalization") || lower.matches(".*\\b(1nf|2nf|3nf|bcnf|dbms|sql|acid|rdbms|nosql)\\b.*")
                || lower.contains("relational") || lower.contains("indexing") || lower.contains("transaction") || lower.contains("database")) {
            return DomainType.DBMS;
        }
        if (lower.contains("nfa") || lower.contains("dfa") || lower.contains("automata") || lower.contains("turing machine")
                || lower.contains("cfg") || lower.contains("pda") || lower.contains("chomsky") || lower.contains("regular expression")
                || lower.contains("context free") || lower.contains("theory of computation") || lower.contains("pumping lemma")) {
            return DomainType.THEORY_OF_COMPUTATION;
        }
        if (lower.contains("cpu scheduling") || lower.contains("scheduling algorithm") || lower.contains("deadlock") || lower.contains("paging")
                || lower.contains("virtual memory") || lower.contains("semaphore") || lower.contains("mutex") || lower.contains("operating system")
                || lower.contains("round robin") || lower.contains("fcfs") || lower.contains("sjf") || lower.contains("page replacement")) {
            return DomainType.OPERATING_SYSTEMS;
        }
        if (lower.contains("osi") || lower.matches(".*\\b(tcp|udp|ip|dns|http|https|arp|icmp)\\b.*") || lower.contains("handshake")
                || lower.contains("subnetting") || lower.contains("routing") || lower.contains("computer network")) {
            return DomainType.COMPUTER_NETWORKS;
        }
        if (lower.contains("ohm") || lower.contains("circuit") || lower.contains("diode") || lower.contains("transistor")
                || lower.contains("bjt") || lower.contains("mosfet") || lower.contains("logic gate") || lower.contains("op amp")
                || lower.contains("kirchhoff") || lower.contains("electronics") || lower.contains("semiconductor")) {
            return DomainType.ELECTRONICS;
        }
        if (lower.contains("binary search") || lower.contains("quick sort") || lower.contains("merge sort") || lower.contains("dijkstra")
                || lower.contains("bfs") || lower.contains("dfs") || lower.contains("dynamic programming") || lower.contains("greedy")
                || lower.contains("divide and conquer") || lower.contains("algorithm") || lower.contains("asymptotic") || lower.contains("big o")) {
            return DomainType.ALGORITHMS;
        }
        if (lower.contains("linked list") || lower.contains("stack") || lower.contains("queue") || lower.contains("binary tree")
                || lower.contains("avl tree") || lower.contains("heap") || lower.contains("trie") || lower.contains("hash table") || lower.contains("graph")) {
            return DomainType.DATA_STRUCTURES;
        }
        if (lower.contains("gradient descent") || lower.contains("neural network") || lower.contains("backpropagation")
                || lower.contains("deep learning") || lower.contains("machine learning") || lower.contains("transformer")
                || lower.contains("reinforcement learning") || lower.contains("convolutional")) {
            return DomainType.AI_ML;
        }
        if (lower.contains("design pattern") || lower.contains("solid principles") || lower.contains("agile")
                || lower.contains("microservices") || lower.contains("software engineering") || lower.contains("uml")) {
            return DomainType.SOFTWARE_ENGINEERING;
        }
        return DomainType.GENERAL_THEORY;
    }

    private String determineSubdomain(DomainType domain, String lower) {
        switch (domain) {
            case PHYSICS:
                if (lower.contains("newton") || lower.contains("force") || lower.contains("motion")) return "Classical Mechanics & Dynamics";
                if (lower.contains("thermo")) return "Thermodynamics";
                if (lower.contains("optics")) return "Optics & Light";
                if (lower.contains("electromag") || lower.contains("ohm")) return "Electromagnetism & Circuits";
                return "Physics";
            case BIOLOGY:
                if (lower.contains("photo") || lower.contains("plant")) return "Plant Physiology & Photosynthesis";
                if (lower.contains("cell") || lower.contains("mitosis")) return "Cell Biology";
                if (lower.contains("dna") || lower.contains("gene")) return "Molecular Genetics";
                return "Biological Sciences";
            case DBMS:
                if (lower.contains("normal")) return "Relational Normalization & Functional Dependencies";
                if (lower.contains("acid") || lower.contains("trans")) return "Transaction & Concurrency Control";
                if (lower.contains("sql") || lower.contains("join")) return "Relational Algebra & SQL";
                return "Database Management Systems";
            case THEORY_OF_COMPUTATION:
                if (lower.contains("nfa") || lower.contains("dfa") || lower.contains("automata")) return "Finite Automata & Formal Languages";
                if (lower.contains("turing")) return "Turing Machines & Computability";
                if (lower.contains("cfg") || lower.contains("pda")) return "Context-Free Grammars & Pushdown Automata";
                return "Theory of Computation";
            case OPERATING_SYSTEMS:
                if (lower.contains("schedul") || lower.contains("gantt")) return "CPU Scheduling & Process Synchronization";
                if (lower.contains("deadlock")) return "Deadlock Management & Avoidance";
                if (lower.contains("memory") || lower.contains("paging")) return "Virtual Memory & Paging";
                return "Operating Systems";
            case COMPUTER_NETWORKS:
                if (lower.contains("osi") || lower.contains("layer")) return "OSI 7-Layer Reference Architecture";
                if (lower.contains("tcp") || lower.contains("handshake")) return "Transport Layer Protocols";
                if (lower.contains("ip") || lower.contains("routing")) return "Network Layer & Routing";
                return "Computer Networks";
            case ALGORITHMS:
                if (lower.contains("search")) return "Searching Algorithms";
                if (lower.contains("sort")) return "Sorting & Divide-and-Conquer";
                if (lower.contains("graph") || lower.contains("dijkstra") || lower.contains("bfs") || lower.contains("dfs")) return "Graph Algorithms";
                return "Design & Analysis of Algorithms";
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
        
        // Capitalize title
        String[] words = clean.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            if (!w.isEmpty()) {
                sb.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1)).append(" ");
            }
        }
        return sb.toString().trim();
    }

    private List<String> extractSubtopics(String rawPrompt, String lower, String normalizedTopic) {
        if (rawPrompt.contains(" and ") || rawPrompt.contains(" vs ") || rawPrompt.contains(" versus ") || rawPrompt.contains(",")) {
            String[] parts = normalizedTopic.split("(?i)\\s+(?:and|vs|versus|,)\\s+");
            List<String> list = new ArrayList<>();
            for (String p : parts) {
                String trimmed = p.trim();
                if (trimmed.length() > 1 && !list.contains(trimmed)) list.add(trimmed);
            }
            if (list.size() > 1) return list;
        }
        return List.of(normalizedTopic);
    }

    private String extractPrerequisite(String lower, DomainType domain) {
        if (lower.contains("bcnf") || lower.contains("3nf")) return "Functional Dependencies & Candidate Keys";
        if (lower.contains("binary search")) return "Sorted Array Precondition";
        if (lower.contains("quick sort") || lower.contains("merge sort")) return "Divide and Conquer Strategy";
        if (lower.contains("dfa") || lower.contains("nfa")) return "Alphabets, Strings & State Transition Concepts";
        if (lower.contains("dijkstra")) return "Weighted Graphs & Non-negative Edge Weights";
        return null;
    }

    private void configurePedagogicalRequirements(
            TopicUnderstandingResult result,
            String lower,
            DomainType domain,
            UserIntent intent,
            TopicCategory category,
            Set<String> required,
            Set<String> excluded) {

        // 1. Comparison Intent
        if (intent == UserIntent.COMPARE) {
            result.setRequiresComparisonTable(true);
            result.setRequiresDiagram(true);
            result.setRequiresFormula(false);
            result.setRequiresAlgorithm(false);
            result.setRequiresPseudocode(false);
            result.setRequiresWorkedExample(true);
            result.setSuggestedDiagramType(deriveDiagramType(lower, domain));

            required.addAll(List.of("definitions", "comparison_table", "key_differences", "exam_takeaways"));
            excluded.addAll(List.of("algorithm", "pseudocode", "complexity", "advantages", "limitations"));
            return;
        }

        // 2. Classification / Types Intent
        if (intent == UserIntent.CLASSIFY_TYPES || lower.contains("types") || lower.contains("categories") || lower.contains("join")) {
            result.setRequiresComparisonTable(true);
            result.setRequiresDiagram(true);
            result.setRequiresFormula(domain == DomainType.PHYSICS || domain == DomainType.OPERATING_SYSTEMS || domain == DomainType.MATHEMATICS);
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
            result.setRequiresWorkedExample(false);
            result.setRequiresDiagram(true);
            result.setSuggestedDiagramType(deriveDiagramType(lower, domain));

            if (domain == DomainType.THEORY_OF_COMPUTATION) {
                result.setRequiresFormula(true); // 5-tuple
                if (result.getPrimarySubtopics().size() > 1 || lower.contains("and") || lower.contains("vs")) {
                    result.setRequiresComparisonTable(true);
                }
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
        result.setRequiresFormula(domain == DomainType.ELECTRONICS || domain == DomainType.OPERATING_SYSTEMS || domain == DomainType.CHEMISTRY || domain == DomainType.BIOLOGY || domain == DomainType.PHYSICS);
        result.setRequiresComparisonTable(domain == DomainType.DBMS || domain == DomainType.OPERATING_SYSTEMS || lower.contains("join") || lower.contains("normal"));
        result.setRequiresAlgorithm(domain == DomainType.ALGORITHMS || domain == DomainType.DATA_STRUCTURES);
        result.setRequiresPseudocode(domain == DomainType.ALGORITHMS || domain == DomainType.DATA_STRUCTURES);

        required.addAll(List.of("core_concept", "working_mechanism", "diagram", "worked_example", "exam_takeaways"));
        excluded.addAll(List.of("algorithm", "pseudocode", "complexity", "advantages", "limitations"));
    }

    private String deriveDiagramType(String lower, DomainType domain) {
        if (domain == DomainType.THEORY_OF_COMPUTATION || lower.contains("automata") || lower.contains("nfa") || lower.contains("dfa")) return "automata-state-transition";
        if (domain == DomainType.OPERATING_SYSTEMS || lower.contains("scheduling") || lower.contains("gantt")) return "os-gantt-chart";
        if (lower.contains("join") || lower.contains("sql")) return "sql-join-venn";
        if (domain == DomainType.ELECTRONICS || lower.contains("ohm") || lower.contains("circuit")) return "circuit-schematic";
        if (domain == DomainType.PHYSICS || lower.contains("newton") || lower.contains("force")) return "physics-diagram";
        if (domain == DomainType.BIOLOGY || lower.contains("photo") || lower.contains("cell")) return "science-reaction";
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
                return "Provide precise formal definition, core mathematical/theoretical foundations, and clear visual state model for " + topic + ".";
            case COMPARE:
                return "Differentiate core architectural parameters, mathematical rules, operational behavior, and equivalence trade-offs.";
            case DERIVE_FORMULATION:
                return "Establish fundamental principles, derive governing equations step-by-step, and state physical/mathematical interpretations.";
            case ALGORITHM_TRACE:
                return "Explain preconditions, step-by-step procedural execution, visual pointer/array trace, and asymptotic complexity boundaries.";
            case PROCESS_MECHANISM:
                return "Explain step-by-step physical/system mechanisms, internal state progression, and cause-effect interactions.";
            default:
                return "Deliver clear, high-yield academic study notes on " + topic + " with intuitive explanations and clean visual architecture.";
        }
    }
}
