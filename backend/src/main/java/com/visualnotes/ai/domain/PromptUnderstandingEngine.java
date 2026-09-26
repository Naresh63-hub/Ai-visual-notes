package com.visualnotes.ai.domain;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.*;

@Component
public class PromptUnderstandingEngine {

    public static class PromptAnalysisResult {
        private String topic;
        private DomainType domain;
        private String subdomain;
        private String audience;
        private String difficulty;
        private String detailLevel;
        private Integer estimatedMarks; // 2, 5, 10
        private List<String> requirements;
        private List<String> detectedTopics;
        private Integer requestedPageCount;
        private int suggestedPageCount;
        private List<Integer> quickPageOptions;
        private String feedback;

        public String getTopic() { return topic; }
        public void setTopic(String topic) { this.topic = topic; }
        public DomainType getDomain() { return domain; }
        public void setDomain(DomainType domain) { this.domain = domain; }
        public String getSubdomain() { return subdomain; }
        public void setSubdomain(String subdomain) { this.subdomain = subdomain; }
        public String getAudience() { return audience; }
        public void setAudience(String audience) { this.audience = audience; }
        public String getDifficulty() { return difficulty; }
        public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
        public String getDetailLevel() { return detailLevel; }
        public void setDetailLevel(String detailLevel) { this.detailLevel = detailLevel; }
        public Integer getEstimatedMarks() { return estimatedMarks; }
        public void setEstimatedMarks(Integer estimatedMarks) { this.estimatedMarks = estimatedMarks; }
        public List<String> getRequirements() { return requirements; }
        public void setRequirements(List<String> requirements) { this.requirements = requirements; }
        public List<String> getDetectedTopics() { return detectedTopics; }
        public void setDetectedTopics(List<String> detectedTopics) { this.detectedTopics = detectedTopics; }
        public Integer getRequestedPageCount() { return requestedPageCount; }
        public void setRequestedPageCount(Integer requestedPageCount) { this.requestedPageCount = requestedPageCount; }
        public int getSuggestedPageCount() { return suggestedPageCount; }
        public void setSuggestedPageCount(int suggestedPageCount) { this.suggestedPageCount = suggestedPageCount; }
        public List<Integer> getQuickPageOptions() { return quickPageOptions; }
        public void setQuickPageOptions(List<Integer> quickPageOptions) { this.quickPageOptions = quickPageOptions; }
        public String getFeedback() { return feedback; }
        public void setFeedback(String feedback) { this.feedback = feedback; }
    }

    public PromptAnalysisResult analyze(String rawPrompt, String preferredStyle, Integer explicitPageCount) {
        PromptAnalysisResult result = new PromptAnalysisResult();
        String lower = rawPrompt.toLowerCase().trim();

        // 1. Detect Explicit Page Requests in Prompt if not provided
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

        // 2. Detect Marks & Exam Depth
        Integer marks = null;
        if (lower.contains("2 mark") || lower.contains("2-mark") || lower.contains("short answer")) {
            marks = 2;
        } else if (lower.contains("5 mark") || lower.contains("5-mark") || lower.contains("medium answer")) {
            marks = 5;
        } else if (lower.contains("10 mark") || lower.contains("10-mark") || lower.contains("long answer") || lower.contains("essay") || lower.contains("b.tech")) {
            marks = 10;
        }
        result.setEstimatedMarks(marks);

        // 3. Extract Requirements
        List<String> requirements = new ArrayList<>();
        if (lower.contains("vector") || lower.contains("physical vector") || lower.contains("vector equation")) requirements.add("physical vector equations");
        if (lower.contains("everyday example") || lower.contains("real-world example") || lower.contains("everyday examples") || lower.contains("example")) requirements.add("everyday examples");
        if (lower.contains("diagram") || lower.contains("labeled diagram") || lower.contains("figure")) requirements.add("domain-specific diagram");
        if (lower.contains("algorithm") || lower.contains("pseudocode") || lower.contains("code")) requirements.add("procedural algorithm");
        if (lower.contains("complexity") || lower.contains("time complexity") || lower.contains("space complexity")) requirements.add("complexity proof");
        if (lower.contains("derivation") || lower.contains("proof") || lower.contains("formula")) requirements.add("mathematical derivation");
        if (lower.contains("circuit") || lower.contains("schematic")) requirements.add("circuit schematic");
        result.setRequirements(requirements);

        // 4. Detect Domain & Subdomain
        DomainType domain = classifyDomain(lower);
        result.setDomain(domain);
        result.setSubdomain(determineSubdomain(domain, lower));

        // 5. Clean Normalized Topic Title
        String cleanTopic = extractNormalizedTopicTitle(rawPrompt, domain);
        result.setTopic(cleanTopic);

        // 6. Syllabus / Unit Expansion & Topic Extraction
        List<String> detectedTopics = decomposeTopics(rawPrompt, lower, domain, cleanTopic);
        result.setDetectedTopics(detectedTopics);

        if (detectedTopics.size() > 1 && (lower.contains("unit") || lower.contains("syllabus") || lower.contains("chapter"))) {
            cleanTopic = formatUnitTitle(rawPrompt);
            result.setTopic(cleanTopic);
        }

        // 7. Audience, Detail Level & Difficulty
        String audience = "B.Tech / College Students";
        if (lower.contains("high school") || lower.contains("beginner") || lower.contains("easy") || lower.contains("simple language")) {
            audience = "High School / Beginners";
        } else if (lower.contains("gate") || lower.contains("interview") || lower.contains("faang")) {
            audience = "Competitive Exam Aspirants";
        }
        result.setAudience(audience);

        String detailLevel = "Detailed";
        if (marks != null && marks <= 2) {
            detailLevel = "Quick";
        } else if (lower.contains("in-depth") || lower.contains("deep dive") || (marks != null && marks >= 10)) {
            detailLevel = "Very Detailed";
        } else if (lower.contains("quick") || lower.contains("brief") || lower.contains("summary")) {
            detailLevel = "Quick";
        }
        result.setDetailLevel(detailLevel);

        String difficulty = "Intermediate";
        if (lower.contains("beginner") || lower.contains("basic")) {
            difficulty = "Beginner";
        } else if (lower.contains("advanced") || lower.contains("hard") || lower.contains("gate")) {
            difficulty = "Advanced";
        }
        result.setDifficulty(difficulty);

        // 8. Page Count Estimation
        int topicCount = detectedTopics.size();
        int suggestedPages;

        if (pages != null && pages > 0) {
            suggestedPages = pages;
        } else if (topicCount == 1) {
            if ("Quick".equalsIgnoreCase(detailLevel)) {
                suggestedPages = 1;
            } else if ("Very Detailed".equalsIgnoreCase(detailLevel) || (marks != null && marks >= 10)) {
                suggestedPages = 2;
            } else {
                suggestedPages = 2;
            }
        } else if (topicCount <= 4) {
            suggestedPages = topicCount;
        } else if (topicCount <= 8) {
            suggestedPages = (int) Math.ceil(topicCount / 2.0);
        } else {
            suggestedPages = Math.min(topicCount, (int) Math.ceil(topicCount / 2.0));
        }
        result.setSuggestedPageCount(suggestedPages);

        List<Integer> quickOptions = new ArrayList<>();
        quickOptions.add(1);
        if (topicCount == 1) {
            quickOptions.add(2);
            quickOptions.add(3);
        } else {
            if (!quickOptions.contains(2)) quickOptions.add(2);
            if (topicCount >= 3 && !quickOptions.contains(topicCount)) quickOptions.add(topicCount);
        }
        Collections.sort(quickOptions);
        result.setQuickPageOptions(quickOptions);

        String feedback = "Identified " + domain.getDisplayName() + " with " + topicCount + " core topic(s). Exam-oriented " + suggestedPages + "-page notes planned.";
        result.setFeedback(feedback);

        return result;
    }

    private DomainType classifyDomain(String lower) {
        if (lower.contains("newton") || lower.contains("force") || lower.contains("kinematics") || lower.contains("thermodynamics")
                || lower.contains("optics") || lower.contains("gravitation") || lower.contains("inertia") || lower.contains("physics")
                || lower.contains("momentum") || lower.contains("friction") || lower.contains("work energy")) {
            return DomainType.PHYSICS;
        }
        if (lower.contains("photosynthesis") || lower.contains("respiration") || lower.matches(".*\\b(dna|rna)\\b.*")
                || lower.contains("mitosis") || lower.contains("meiosis") || lower.contains("chloroplast") || lower.contains("biology")
                || lower.contains("cell division") || lower.contains("enzyme") || lower.contains("calvin cycle")) {
            return DomainType.BIOLOGY;
        }
        if (lower.contains("reaction") || lower.contains("organic chemistry") || lower.contains("periodic table")
                || lower.contains("chemical equilibrium") || lower.contains("acid base") || lower.contains("chemistry")) {
            return DomainType.CHEMISTRY;
        }
        if (lower.contains("bayes") || lower.contains("calculus") || lower.contains("derivative") || lower.contains("integral")
                || lower.contains("matrix") || lower.contains("fourier") || lower.contains("probability") || lower.contains("differential")
                || lower.contains("linear algebra") || lower.contains("eigenvalue")) {
            return DomainType.MATHEMATICS;
        }
        if (lower.contains("normalization") || lower.matches(".*\\b(1nf|2nf|3nf|bcnf|dbms|sql|acid)\\b.*")
                || lower.contains("relational") || lower.contains("er diagram")
                || lower.contains("indexing") || lower.contains("transaction")) {
            return DomainType.DBMS;
        }
        if (lower.contains("osi") || lower.matches(".*\\b(tcp|udp|ip|dns|http)\\b.*") || lower.contains("network")
                || lower.contains("router") || lower.contains("switch") || lower.contains("subnetting")
                || lower.contains("handshake") || lower.contains("routing")) {
            return DomainType.COMPUTER_NETWORKS;
        }
        if (lower.contains("nfa") || lower.contains("dfa") || lower.contains("automata")
                || lower.contains("turing machine") || lower.contains("regular expression") || lower.contains("cfg")
                || lower.contains("context free") || lower.contains("pda") || lower.contains("pushdown") || lower.contains("chomsky")
                || lower.contains("theory of computation") || lower.contains("toc")) {
            return DomainType.THEORY_OF_COMPUTATION;
        }
        if (lower.contains("process scheduling") || lower.contains("scheduling") || lower.contains("deadlock") || lower.contains("paging") || lower.contains("virtual memory")
                || lower.contains("semaphore") || lower.contains("operating system") || lower.contains("mutex") || lower.contains("thread")
                || lower.contains("fcfs") || lower.contains("sjf") || lower.contains("round robin") || lower.contains("cpu scheduling") || lower.contains("gantt")) {
            return DomainType.OPERATING_SYSTEMS;
        }
        if (lower.contains("ohm's law") || lower.contains("ohms law") || lower.contains("diode") || lower.contains("transistor") || lower.matches(".*\\b(bjt|fet|kcl|kvl)\\b.*")
                || lower.contains("logic gate") || lower.contains("op amp") || lower.contains("circuit") || lower.contains("kirchhoff")
                || lower.contains("electronics") || lower.contains("semiconductor")) {
            return DomainType.ELECTRONICS;
        }
        if (lower.contains("binary search") || lower.contains("quick sort") || lower.contains("merge sort") || lower.matches(".*\\b(daa|bfs|dfs)\\b.*")
                || lower.contains("breadth first") || lower.contains("depth first")
                || lower.contains("dijkstra") || lower.contains("dynamic programming")
                || lower.contains("greedy") || lower.contains("divide and conquer") || lower.contains("strassen") || lower.contains("asymptotic")
                || lower.contains("big o") || lower.contains("disjoint set") || lower.contains("union find") || lower.contains("algorithm")) {
            return DomainType.ALGORITHMS;
        }
        if (lower.contains("linked list") || lower.contains("stack") || lower.contains("queue") || lower.contains("binary tree")
                || lower.contains("avl tree") || lower.contains("red black") || lower.contains("heap") || lower.contains("trie")
                || lower.contains("hash table") || lower.contains("graph") || lower.contains("data structure")) {
            return DomainType.DATA_STRUCTURES;
        }
        if (lower.contains("gradient descent") || lower.contains("neural network") || lower.contains("backpropagation") || lower.contains("deep learning")
                || lower.contains("machine learning") || lower.contains("transformer") || lower.contains("attention") || lower.matches(".*\\b(svm|ai|ml)\\b.*")
                || lower.contains("linear regression") || lower.contains("logistic regression")) {
            return DomainType.AI_ML;
        }
        return DomainType.GENERAL_THEORY;
    }

    private String determineSubdomain(DomainType domain, String lower) {
        switch (domain) {
            case PHYSICS:
                if (lower.contains("newton") || lower.contains("force") || lower.contains("motion")) return "Classical Mechanics & Dynamics";
                if (lower.contains("thermo")) return "Thermodynamics & Heat";
                if (lower.contains("optics")) return "Wave Optics & Ray Optics";
                if (lower.contains("electromag") || lower.contains("ohm")) return "Electromagnetism";
                return "General Physics";
            case BIOLOGY:
                if (lower.contains("photo") || lower.contains("plant")) return "Plant Physiology & Photosynthesis";
                if (lower.contains("cell") || lower.contains("mitosis")) return "Cell Biology & Cytology";
                if (lower.contains("dna") || lower.contains("gene")) return "Molecular Genetics";
                return "Life Sciences";
            case DBMS:
                if (lower.contains("normal")) return "Relational Normalization & Functional Dependencies";
                if (lower.contains("trans") || lower.contains("acid")) return "Transaction Management & Concurrency Control";
                return "Database Systems";
            case COMPUTER_NETWORKS:
                if (lower.contains("osi") || lower.contains("layer")) return "OSI 7-Layer Reference Model";
                if (lower.contains("tcp") || lower.contains("handshake")) return "Transport Layer Protocols";
                return "Networking Protocols";
            case ALGORITHMS:
                if (lower.contains("search")) return "Searching Algorithms";
                if (lower.contains("sort")) return "Sorting & Divide-and-Conquer";
                if (lower.contains("graph") || lower.contains("dijkstra") || lower.contains("bfs") || lower.contains("dfs")) return "Graph Algorithms";
                return "Design & Analysis of Algorithms (DAA)";
            default:
                return domain.getDisplayName();
        }
    }

    private List<String> decomposeTopics(String rawPrompt, String lower, DomainType domain, String cleanTopic) {
        // Single topic fast return
        if (lower.contains("newton") || lower.contains("binary search") || lower.contains("photosynthesis")
                || lower.contains("osi") || lower.contains("normalization") || lower.contains("gradient descent")
                || lower.contains("ohm's law") || lower.contains("ohms law")) {
            return List.of(cleanTopic);
        }

        // Unit 1 DAA Syllabus Decomposition special handling
        if (lower.contains("daa unit 1") || (lower.contains("daa") && lower.contains("unit 1"))) {
            return List.of(
                    "Algorithm & Pseudocode Foundations",
                    "Time & Space Complexity",
                    "Asymptotic Notations (Big-O, Omega, Theta, Little-o)",
                    "Disjoint Sets (Union & Find Operations)",
                    "Divide and Conquer Strategy",
                    "Binary Search",
                    "Quick Sort",
                    "Merge Sort",
                    "Strassen's Matrix Multiplication"
            );
        }

        List<String> topics = new ArrayList<>();

        // Multi-line input parsing
        String[] lines = rawPrompt.split("\\r?\\n");
        if (lines.length > 1) {
            for (String line : lines) {
                String clean = cleanIndividualTopic(line);
                if (clean.length() >= 2 && !isStopPhrase(clean)) {
                    topics.add(clean);
                }
            }
            if (topics.size() > 1) return topics;
        }

        // Colon separated lists e.g. "DAA Unit 1: Binary Search, Quick Sort, Merge Sort"
        if (rawPrompt.contains(":")) {
            String afterColon = rawPrompt.substring(rawPrompt.indexOf(":") + 1).trim();
            String[] parts = afterColon.split("[,;]+");
            for (String p : parts) {
                String clean = cleanIndividualTopic(p);
                if (clean.length() >= 2 && !isStopPhrase(clean)) {
                    topics.add(clean);
                }
            }
            if (topics.size() > 1) return topics;
        }

        // Comma / vs separated topics
        String stripped = rawPrompt.replaceAll("(?i)\\b(explain|create notes for|make handwritten notes for|give me notes for|make study notes for|these|topics|in \\d+ pages?|in (one|1) page|with diagrams?|for b\\.tech|for exams?|in simple language|in detail|for \\d+ marks?|with algorithm|examples?|step-by-step working|time complexity|space complexity|complexity|proof|diagrams?|algorithm|pseudocode|formula)\\b", " ").trim();
        String[] parts = stripped.split("[,;]+|\\bvs\\b|\\bversus\\b");

        for (String part : parts) {
            String clean = cleanIndividualTopic(part);
            if (clean.length() >= 2 && !isStopPhrase(clean)) {
                topics.add(clean);
            }
        }

        List<String> unique = new ArrayList<>();
        for (String t : topics) {
            if (!unique.contains(t) && !isStopPhrase(t)) unique.add(t);
        }

        return unique.isEmpty() ? List.of(cleanTopic) : unique;
    }

    private String extractNormalizedTopicTitle(String rawPrompt, DomainType domain) {
        String lower = rawPrompt.toLowerCase();
        if (lower.contains("newton")) return "Newton's 3 Laws of Motion";
        if (lower.contains("binary search")) return "Binary Search";
        if (lower.contains("quick sort")) return "Quick Sort";
        if (lower.contains("merge sort")) return "Merge Sort";
        if (lower.contains("photosynthesis")) return "Photosynthesis";
        if (lower.contains("osi")) return "OSI 7-Layer Reference Model";
        if (lower.contains("normalization")) return "DBMS Normalization (1NF to BCNF)";
        if (lower.contains("gradient descent")) return "Gradient Descent Optimization";
        if (lower.contains("ohm")) return "Ohm's Law";
        if (lower.contains("bfs") || lower.contains("breadth first")) return "Breadth First Search (BFS)";
        if (lower.contains("dfs") || lower.contains("depth first")) return "Depth First Search (DFS)";
        if (lower.contains("dijkstra")) return "Dijkstra's Algorithm";

        String clean = rawPrompt.replaceAll("(?i)^(explain|create|give me|make|notes on|notes for|study notes on|study notes for)\\s+", "")
                .replaceAll("(?i)\\s+(with physical vector equations|with vector equations|everyday examples|with everyday examples|and everyday examples|with labeled diagram|with diagrams?|for \\d+ marks?|in simple language|in detail|for b\\.tech|for exams?).*$", "")
                .replaceAll("[^a-zA-Z0-9\\s+\\-#./()]", "")
                .trim();

        return clean.isEmpty() ? domain.getDisplayName() + " Study Notes" : clean;
    }

    private String formatUnitTitle(String prompt) {
        Matcher m = Pattern.compile("(?i)([a-zA-Z]+)\\s*(unit\\s*\\d+)", Pattern.CASE_INSENSITIVE).matcher(prompt);
        if (m.find()) {
            return m.group(1).toUpperCase() + " " + capitalizeWords(m.group(2));
        }
        return "Comprehensive Study Syllabus";
    }

    private String cleanIndividualTopic(String str) {
        if (str == null) return "";
        return str.replaceAll("^[0-9]+[.\\-)]\\s*", "")
                .replaceAll("(?i)^(explain|give me|make|create|write|notes on|notes for|about|study)\\s+", "")
                .replaceAll("(?i)\\s+(with physical vector equations|with vector equations|everyday examples|with everyday examples|and everyday examples|with labeled diagram|with diagrams?|in detail|simply|for exams?|for b\\.tech|algorithm|and complexity|with algorithm|examples?|and diagram|step-by-step working|time complexity|space complexity|complexity|for \\d+ marks?).*$", "")
                .replaceAll("(?i)^(with|and|for|in|about|to|of|the|a|an)\\s+", "")
                .replaceAll("(?i)\\s+(with|and|for|in|about|to|of)$", "")
                .replaceAll("[^a-zA-Z0-9\\s+\\-#./()]", "")
                .trim();
    }

    private boolean isStopPhrase(String s) {
        String l = s.toLowerCase().trim();
        return l.equals("pages") || l.equals("page") || l.equals("diagram") || l.equals("diagrams")
                || l.equals("example") || l.equals("examples") || l.equals("notes") || l.equals("algorithm")
                || l.equals("complexity") || l.equals("time complexity") || l.equals("space complexity")
                || l.equals("step-by-step working") || l.equals("step by step") || l.equals("with")
                || l.equals("and") || l.equals("daa unit 1") || l.length() < 2;
    }

    private String capitalizeWords(String str) {
        String[] words = str.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            if (!w.isEmpty()) {
                sb.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1).toLowerCase()).append(" ");
            }
        }
        return sb.toString().trim();
    }
}
