package com.visualnotes.diagram;

import com.visualnotes.dto.DiagramDataDto;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DiagramEngine {

    public DiagramDataDto generateDiagram(String topic, String specificType, String context) {
        String type = determineDiagramType(topic, specificType, context);
        String title = deriveDiagramTitle(topic, type);
        String caption = deriveDiagramCaption(topic, type);
        
        Map<String, Object> data = new HashMap<>();
        List<String> labels = new ArrayList<>();
        List<String> annotations = new ArrayList<>();
        String rawSvg = "";

        String lowerTopic = (topic + " " + context).toLowerCase();

        if (type.contains("binary-search") || lowerTopic.contains("binary search")) {
            type = "binary-search-array";
            title = "Binary Search Step-by-Step Array Trace (Target = 15)";
            caption = "Detailed trace over Array [2, 5, 7, 11, 15, 18, 21]. Pointers (Low, Mid, High) narrow search in 3 iterations.";
            data.put("target", 15);
            rawSvg = generateBinarySearchDetailedSvg();
        } else if (lowerTopic.contains("quick sort") || type.contains("quick-sort")) {
            type = "sorting-partition";
            title = "Quick Sort Pivot Partitioning & Pointer Movement";
            caption = "Partitioning array [40, 20, 60, 10, 30] with Pivot = 30. Left subarray < 30, Right subarray > 30.";
            rawSvg = generateQuickSortSvg();
        } else if (lowerTopic.contains("merge sort") || type.contains("merge-sort")) {
            type = "sorting-partition";
            title = "Merge Sort Divide & Conquer Recursion Tree";
            caption = "Divide array into halves until singletons, then merge sorted sub-lists back in O(n log n).";
            rawSvg = generateMergeSortSvg();
        } else if (lowerTopic.contains("bfs") || lowerTopic.contains("breadth first")) {
            type = "tree-traversal";
            title = "Breadth First Search (BFS) Level-Order Queue Trace";
            caption = "FIFO Queue exploration: Level 1 (A) -> Level 2 (B, C) -> Level 3 (D, E, F).";
            rawSvg = generateBfsSvg();
        } else if (lowerTopic.contains("dfs") || lowerTopic.contains("depth first")) {
            type = "graph-network";
            title = "Depth First Search (DFS) Stack & Backtracking Trace";
            caption = "Explores deepest unvisited node along current branch: A -> B -> D -> backtrack -> E -> backtrack -> C -> F.";
            rawSvg = generateDfsSvg();
        } else if (lowerTopic.contains("dijkstra") || lowerTopic.contains("shortest path")) {
            type = "graph-network";
            title = "Dijkstra's Single-Source Shortest Path Greedy Relaxation";
            caption = "Relax edges: dist[v] = min(dist[v], dist[u] + weight(u, v)) with Min-Priority Queue.";
            rawSvg = generateDijkstraSvg();
        } else if (lowerTopic.contains("osi") || lowerTopic.contains("tcp/ip") || lowerTopic.contains("network layer")) {
            type = "layer-stack";
            title = "OSI 7-Layer Model: Encapsulation & Protocol Data Units (PDU)";
            caption = "Data flows down from Layer 7 (Application) to Layer 1 (Physical) with protocol header encapsulation.";
            rawSvg = generateOsiSvg();
        } else if (lowerTopic.contains("normalization") || lowerTopic.contains("dbms") || lowerTopic.contains("database")) {
            type = "dbms-normalization";
            title = "DBMS Normalization Pipeline (UNF ➔ 1NF ➔ 2NF ➔ 3NF ➔ BCNF)";
            caption = "Eliminate Repeating Groups (1NF) -> Partial Dependencies (2NF) -> Transitive Dependencies (3NF).";
            rawSvg = generateNormalizationSvg();
        } else if (lowerTopic.contains("neural") || lowerTopic.contains("gradient descent") || lowerTopic.contains("machine learning") || lowerTopic.contains("deep learning")) {
            type = "neural-network";
            title = "Artificial Neural Network / Gradient Descent Optimization";
            caption = "Forward Propagation (Inputs * Weights + Bias -> Activation) & Backpropagation (Loss gradient update).";
            rawSvg = generateNeuralNetSvg();
        } else if (lowerTopic.contains("photosynthesis") || lowerTopic.contains("plant") || lowerTopic.contains("chloroplast")) {
            type = "science-reaction";
            title = "Photosynthesis Biochemical Reaction & Energy Flow";
            caption = "Light + 6CO₂ + 6H₂O  ──[Chlorophyll]──>  C₆H₁₂O₆ (Glucose) + 6O₂";
            rawSvg = generatePhotosynthesisSvg();
        } else if (lowerTopic.contains("newton") || lowerTopic.contains("force") || lowerTopic.contains("gravity") || lowerTopic.contains("physics")) {
            type = "physics-diagram";
            title = "Newton's 3 Laws of Motion Physical Mechanics";
            caption = "1st: Inertia (ΣF = 0) | 2nd: Force Law (F = m·a) | 3rd: Action-Reaction (F_AB = -F_BA)";
            rawSvg = generateNewtonSvg();
        } else if (lowerTopic.contains("handshake") || lowerTopic.contains("tcp")) {
            type = "process-steps";
            title = "TCP 3-Way Handshake Connection Protocol";
            caption = "Client sends SYN (seq=x) -> Server replies SYN-ACK (seq=y, ack=x+1) -> Client confirms ACK (ack=y+1).";
            rawSvg = generateTcpHandshakeSvg();
        } else {
            type = "concept-map";
            title = topic + " - Concept Architecture & Core Mechanics";
            caption = "Structural breakdown and functional interactions of " + topic + ".";
            rawSvg = generateGenericConceptMapSvg(topic);
        }

        return DiagramDataDto.builder()
                .type(type)
                .title(title)
                .caption(caption)
                .rawSvg(rawSvg)
                .data(data)
                .labels(labels)
                .annotations(annotations)
                .build();
    }

    private String determineDiagramType(String topic, String specificType, String context) {
        if (specificType != null && !specificType.isBlank()) {
            return specificType;
        }
        String combined = (topic + " " + context).toLowerCase();
        if (combined.contains("binary search") || combined.contains("array") || combined.contains("search")) return "binary-search-array";
        if (combined.contains("quick sort") || combined.contains("merge sort") || combined.contains("sort")) return "sorting-partition";
        if (combined.contains("bfs") || combined.contains("tree") || combined.contains("heap") || combined.contains("binary tree")) return "tree-traversal";
        if (combined.contains("dfs") || combined.contains("graph")) return "graph-network";
        if (combined.contains("dijkstra") || combined.contains("shortest path")) return "graph-network";
        if (combined.contains("osi") || combined.contains("layer") || combined.contains("tcp/ip")) return "layer-stack";
        if (combined.contains("dbms") || combined.contains("normalization") || combined.contains("sql") || combined.contains("relational")) return "dbms-normalization";
        if (combined.contains("neural") || combined.contains("machine learning") || combined.contains("gradient descent") || combined.contains("ai")) return "neural-network";
        if (combined.contains("photosynthesis") || combined.contains("biology") || combined.contains("cell")) return "science-reaction";
        if (combined.contains("newton") || combined.contains("physics") || combined.contains("force") || combined.contains("motion")) return "physics-diagram";
        if (combined.contains("tcp") || combined.contains("handshake") || combined.contains("protocol")) return "process-steps";
        return "concept-map";
    }

    private String deriveDiagramTitle(String topic, String type) {
        return topic + " - Visual Concept & Architecture";
    }

    private String deriveDiagramCaption(String topic, String type) {
        return "Visual representation illustrating core mechanism and state transitions of " + topic + ".";
    }

    // High-Resolution Educational SVG Generators

    private String generateBinarySearchDetailedSvg() {
        return "<svg viewBox=\"0 0 620 180\" xmlns=\"http://www.w3.org/2000/svg\" class=\"w-full h-auto\">" +
                "<rect width=\"620\" height=\"180\" rx=\"10\" fill=\"#fefce8\" stroke=\"#fef08a\" stroke-width=\"2\"/>" +
                // Target banner
                "<g transform=\"translate(20, 12)\">" +
                "  <rect x=\"0\" y=\"0\" width=\"580\" height=\"24\" rx=\"4\" fill=\"#fef08a\" stroke=\"#ca8a04\"/>" +
                "  <text x=\"290\" y=\"16\" font-family=\"'Patrick Hand', 'Caveat', cursive, sans-serif\" font-size=\"14\" font-weight=\"bold\" fill=\"#854d0e\" text-anchor=\"middle\">Goal: Search for Key = 15 in Sorted Array A[0...6]</text>" +
                "</g>" +
                // Iteration 1
                "<g transform=\"translate(30, 48)\">" +
                "  <text x=\"-10\" y=\"18\" font-family=\"sans-serif\" font-size=\"11\" font-weight=\"bold\" fill=\"#b91c1c\">Step 1:</text>" +
                "  <!-- Array Boxes -->" +
                "  <rect x=\"45\" y=\"0\" width=\"40\" height=\"28\" fill=\"#fee2e2\" stroke=\"#ef4444\" stroke-width=\"1.5\" rx=\"3\"/><text x=\"65\" y=\"18\" font-size=\"13\" font-weight=\"bold\" fill=\"#991b1b\" text-anchor=\"middle\">2</text>" +
                "  <rect x=\"90\" y=\"0\" width=\"40\" height=\"28\" fill=\"#fee2e2\" stroke=\"#ef4444\" stroke-width=\"1.5\" rx=\"3\"/><text x=\"110\" y=\"18\" font-size=\"13\" font-weight=\"bold\" fill=\"#991b1b\" text-anchor=\"middle\">5</text>" +
                "  <rect x=\"135\" y=\"0\" width=\"40\" height=\"28\" fill=\"#fee2e2\" stroke=\"#ef4444\" stroke-width=\"1.5\" rx=\"3\"/><text x=\"155\" y=\"18\" font-size=\"13\" font-weight=\"bold\" fill=\"#991b1b\" text-anchor=\"middle\">7</text>" +
                "  <rect x=\"180\" y=\"0\" width=\"40\" height=\"28\" fill=\"#fee2e2\" stroke=\"#b91c1c\" stroke-width=\"2.5\" rx=\"3\"/><text x=\"200\" y=\"18\" font-size=\"13\" font-weight=\"bold\" fill=\"#b91c1c\" text-anchor=\"middle\">11</text>" +
                "  <rect x=\"225\" y=\"0\" width=\"40\" height=\"28\" fill=\"#eff6ff\" stroke=\"#3b82f6\" stroke-width=\"1.5\" rx=\"3\"/><text x=\"245\" y=\"18\" font-size=\"13\" font-weight=\"bold\" fill=\"#1e40af\" text-anchor=\"middle\">15</text>" +
                "  <rect x=\"270\" y=\"0\" width=\"40\" height=\"28\" fill=\"#eff6ff\" stroke=\"#3b82f6\" stroke-width=\"1.5\" rx=\"3\"/><text x=\"290\" y=\"18\" font-size=\"13\" font-weight=\"bold\" fill=\"#1e40af\" text-anchor=\"middle\">18</text>" +
                "  <rect x=\"315\" y=\"0\" width=\"40\" height=\"28\" fill=\"#eff6ff\" stroke=\"#3b82f6\" stroke-width=\"1.5\" rx=\"3\"/><text x=\"335\" y=\"18\" font-size=\"13\" font-weight=\"bold\" fill=\"#1e40af\" text-anchor=\"middle\">21</text>" +
                "  <!-- Labels & Pointers -->" +
                "  <text x=\"65\" y=\"-4\" font-size=\"10\" font-family=\"monospace\" font-weight=\"bold\" fill=\"#047857\" text-anchor=\"middle\">L=0</text>" +
                "  <text x=\"200\" y=\"-4\" font-size=\"10\" font-family=\"monospace\" font-weight=\"bold\" fill=\"#b91c1c\" text-anchor=\"middle\">M=3</text>" +
                "  <text x=\"335\" y=\"-4\" font-size=\"10\" font-family=\"monospace\" font-weight=\"bold\" fill=\"#047857\" text-anchor=\"middle\">H=6</text>" +
                "  <text x=\"375\" y=\"18\" font-family=\"'Patrick Hand', cursive, sans-serif\" font-size=\"13\" fill=\"#1e293b\">A[3]=11 &lt; 15 ⟹ Search Right (Low = 4)</text>" +
                "</g>" +
                // Iteration 2
                "<g transform=\"translate(30, 95)\">" +
                "  <text x=\"-10\" y=\"18\" font-family=\"sans-serif\" font-size=\"11\" font-weight=\"bold\" fill=\"#b91c1c\">Step 2:</text>" +
                "  <rect x=\"45\" y=\"0\" width=\"175\" height=\"28\" fill=\"#f1f5f9\" stroke=\"#cbd5e1\" stroke-dasharray=\"3 2\" rx=\"3\"/><text x=\"132\" y=\"18\" font-size=\"11\" fill=\"#94a3b8\" text-anchor=\"middle\">[Discarded Left Half]</text>" +
                "  <rect x=\"225\" y=\"0\" width=\"40\" height=\"28\" fill=\"#eff6ff\" stroke=\"#3b82f6\" stroke-width=\"1.5\" rx=\"3\"/><text x=\"245\" y=\"18\" font-size=\"13\" font-weight=\"bold\" fill=\"#1e40af\" text-anchor=\"middle\">15</text>" +
                "  <rect x=\"270\" y=\"0\" width=\"40\" height=\"28\" fill=\"#fee2e2\" stroke=\"#b91c1c\" stroke-width=\"2.5\" rx=\"3\"/><text x=\"290\" y=\"18\" font-size=\"13\" font-weight=\"bold\" fill=\"#b91c1c\" text-anchor=\"middle\">18</text>" +
                "  <rect x=\"315\" y=\"0\" width=\"40\" height=\"28\" fill=\"#eff6ff\" stroke=\"#3b82f6\" stroke-width=\"1.5\" rx=\"3\"/><text x=\"335\" y=\"18\" font-size=\"13\" font-weight=\"bold\" fill=\"#1e40af\" text-anchor=\"middle\">21</text>" +
                "  <text x=\"245\" y=\"-4\" font-size=\"10\" font-family=\"monospace\" font-weight=\"bold\" fill=\"#047857\" text-anchor=\"middle\">L=4</text>" +
                "  <text x=\"290\" y=\"-4\" font-size=\"10\" font-family=\"monospace\" font-weight=\"bold\" fill=\"#b91c1c\" text-anchor=\"middle\">M=5</text>" +
                "  <text x=\"335\" y=\"-4\" font-size=\"10\" font-family=\"monospace\" font-weight=\"bold\" fill=\"#047857\" text-anchor=\"middle\">H=6</text>" +
                "  <text x=\"375\" y=\"18\" font-family=\"'Patrick Hand', cursive, sans-serif\" font-size=\"13\" fill=\"#1e293b\">A[5]=18 &gt; 15 ⟹ Search Left (High = 4)</text>" +
                "</g>" +
                // Iteration 3
                "<g transform=\"translate(30, 142)\">" +
                "  <text x=\"-10\" y=\"18\" font-family=\"sans-serif\" font-size=\"11\" font-weight=\"bold\" fill=\"#15803d\">Step 3:</text>" +
                "  <rect x=\"45\" y=\"0\" width=\"175\" height=\"28\" fill=\"#f1f5f9\" stroke=\"#cbd5e1\" stroke-dasharray=\"3 2\" rx=\"3\"/>" +
                "  <rect x=\"225\" y=\"0\" width=\"40\" height=\"28\" fill=\"#dcfce7\" stroke=\"#16a34a\" stroke-width=\"3\" rx=\"3\"/><text x=\"245\" y=\"19\" font-size=\"14\" font-weight=\"bold\" fill=\"#15803d\" text-anchor=\"middle\">15</text>" +
                "  <rect x=\"270\" y=\"0\" width=\"85\" height=\"28\" fill=\"#f1f5f9\" stroke=\"#cbd5e1\" stroke-dasharray=\"3 2\" rx=\"3\"/>" +
                "  <text x=\"245\" y=\"-4\" font-size=\"10\" font-family=\"monospace\" font-weight=\"bold\" fill=\"#15803d\" text-anchor=\"middle\">L=M=H=4</text>" +
                "  <text x=\"375\" y=\"18\" font-family=\"'Patrick Hand', cursive, sans-serif\" font-size=\"14\" font-weight=\"bold\" fill=\"#15803d\">★ MATCH FOUND at Index 4 (Found in 3 steps!)</text>" +
                "</g>" +
                "</svg>";
    }

    private String generateQuickSortSvg() {
        return "<svg viewBox=\"0 0 580 150\" xmlns=\"http://www.w3.org/2000/svg\" class=\"w-full h-auto\">" +
                "<rect width=\"580\" height=\"150\" rx=\"8\" fill=\"#fafaf9\" stroke=\"#e7e5e4\" stroke-width=\"1.5\"/>" +
                "<g transform=\"translate(160, 15)\">" +
                "  <rect x=\"0\" y=\"0\" width=\"240\" height=\"32\" rx=\"4\" fill=\"#f5f5f4\" stroke=\"#78716c\"/>" +
                "  <text x=\"30\" y=\"21\" font-size=\"13\" font-weight=\"bold\" text-anchor=\"middle\">40</text>" +
                "  <text x=\"75\" y=\"21\" font-size=\"13\" font-weight=\"bold\" text-anchor=\"middle\">20</text>" +
                "  <text x=\"120\" y=\"21\" font-size=\"13\" font-weight=\"bold\" text-anchor=\"middle\">60</text>" +
                "  <text x=\"165\" y=\"21\" font-size=\"13\" font-weight=\"bold\" text-anchor=\"middle\">10</text>" +
                "  <rect x=\"195\" y=\"0\" width=\"45\" height=\"32\" fill=\"#fef08a\" stroke=\"#ca8a04\" stroke-width=\"2\"/>" +
                "  <text x=\"217\" y=\"21\" font-size=\"13\" font-weight=\"bold\" fill=\"#854d0e\" text-anchor=\"middle\">30</text>" +
                "  <text x=\"217\" y=\"46\" font-size=\"10\" font-weight=\"bold\" fill=\"#ca8a04\" text-anchor=\"middle\">PIVOT</text>" +
                "</g>" +
                "<path d=\"M 230 62 L 150 90\" stroke=\"#0284c7\" stroke-width=\"2\" fill=\"none\" marker-end=\"url(#arrow)\"/>" +
                "<path d=\"M 350 62 L 430 90\" stroke=\"#0284c7\" stroke-width=\"2\" fill=\"none\"/>" +
                "<g transform=\"translate(60, 95)\">" +
                "  <rect x=\"40\" y=\"0\" width=\"110\" height=\"32\" rx=\"4\" fill=\"#e0f2fe\" stroke=\"#0284c7\" stroke-width=\"1.5\"/>" +
                "  <text x=\"95\" y=\"21\" font-size=\"12\" font-weight=\"bold\" fill=\"#0369a1\" text-anchor=\"middle\">[ 20 , 10 ] &lt; 30</text>" +
                "  <rect x=\"210\" y=\"0\" width=\"65\" height=\"32\" rx=\"4\" fill=\"#fef08a\" stroke=\"#ca8a04\" stroke-width=\"2\"/>" +
                "  <text x=\"242\" y=\"21\" font-size=\"13\" font-weight=\"bold\" fill=\"#854d0e\" text-anchor=\"middle\">[ 30 ]</text>" +
                "  <rect x=\"330\" y=\"0\" width=\"110\" height=\"32\" rx=\"4\" fill=\"#e0f2fe\" stroke=\"#0284c7\" stroke-width=\"1.5\"/>" +
                "  <text x=\"385\" y=\"21\" font-size=\"12\" font-weight=\"bold\" fill=\"#0369a1\" text-anchor=\"middle\">[ 40 , 60 ] &gt; 30</text>" +
                "</g>" +
                "<text x=\"290\" y=\"140\" font-size=\"11\" font-weight=\"bold\" fill=\"#854d0e\" text-anchor=\"middle\">In-Place Partition: Pivot 30 is placed at its final sorted position</text>" +
                "</svg>";
    }

    private String generateMergeSortSvg() {
        return "<svg viewBox=\"0 0 580 150\" xmlns=\"http://www.w3.org/2000/svg\" class=\"w-full h-auto\">" +
                "<rect width=\"580\" height=\"150\" rx=\"8\" fill=\"#f8fafc\" stroke=\"#e2e8f0\"/>" +
                "<g transform=\"translate(200, 10)\">" +
                "  <rect x=\"0\" y=\"0\" width=\"180\" height=\"26\" rx=\"4\" fill=\"#e2e8f0\" stroke=\"#64748b\"/>" +
                "  <text x=\"90\" y=\"18\" font-size=\"12\" font-weight=\"bold\" text-anchor=\"middle\">[ 38, 27, 43, 3 ]</text>" +
                "</g>" +
                "<path d=\"M 260 38 L 170 58\" stroke=\"#64748b\" stroke-width=\"1.5\" fill=\"none\"/>" +
                "<path d=\"M 320 38 L 410 58\" stroke=\"#64748b\" stroke-width=\"1.5\" fill=\"none\"/>" +
                "<g transform=\"translate(110, 60)\">" +
                "  <rect x=\"0\" y=\"0\" width=\"110\" height=\"24\" rx=\"4\" fill=\"#ede9fe\" stroke=\"#8b5cf6\"/>" +
                "  <text x=\"55\" y=\"16\" font-size=\"11\" font-weight=\"bold\" fill=\"#6d28d9\" text-anchor=\"middle\">[ 38, 27 ]</text>" +
                "  <rect x=\"250\" y=\"0\" width=\"110\" height=\"24\" rx=\"4\" fill=\"#ede9fe\" stroke=\"#8b5cf6\"/>" +
                "  <text x=\"305\" y=\"16\" font-size=\"11\" font-weight=\"bold\" fill=\"#6d28d9\" text-anchor=\"middle\">[ 43, 3 ]</text>" +
                "</g>" +
                "<path d=\"M 165 86 L 240 108\" stroke=\"#16a34a\" stroke-width=\"2\" fill=\"none\"/>" +
                "<path d=\"M 415 86 L 340 108\" stroke=\"#16a34a\" stroke-width=\"2\" fill=\"none\"/>" +
                "<g transform=\"translate(180, 110)\">" +
                "  <rect x=\"0\" y=\"0\" width=\"220\" height=\"28\" rx=\"4\" fill=\"#dcfce7\" stroke=\"#16a34a\" stroke-width=\"2\"/>" +
                "  <text x=\"110\" y=\"19\" font-size=\"12\" font-weight=\"bold\" fill=\"#15803d\" text-anchor=\"middle\">✓ Merged: [ 3, 27, 38, 43 ]</text>" +
                "</g>" +
                "</svg>";
    }

    private String generateBfsSvg() {
        return "<svg viewBox=\"0 0 580 145\" xmlns=\"http://www.w3.org/2000/svg\" class=\"w-full h-auto\">" +
                "<rect width=\"580\" height=\"145\" rx=\"8\" fill=\"#f8fafc\" stroke=\"#e2e8f0\"/>" +
                "<line x1=\"290\" y1=\"25\" x2=\"180\" y2=\"65\" stroke=\"#94a3b8\" stroke-width=\"2\"/>" +
                "<line x1=\"290\" y1=\"25\" x2=\"400\" y2=\"65\" stroke=\"#94a3b8\" stroke-width=\"2\"/>" +
                "<line x1=\"180\" y1=\"65\" x2=\"120\" y2=\"105\" stroke=\"#94a3b8\" stroke-width=\"2\"/>" +
                "<line x1=\"180\" y1=\"65\" x2=\"240\" y2=\"105\" stroke=\"#94a3b8\" stroke-width=\"2\"/>" +
                "<line x1=\"400\" y1=\"65\" x2=\"460\" y2=\"105\" stroke=\"#94a3b8\" stroke-width=\"2\"/>" +
                "<circle cx=\"290\" cy=\"25\" r=\"16\" fill=\"#3b82f6\" stroke=\"#1d4ed8\" stroke-width=\"2\"/><text x=\"290\" y=\"30\" font-size=\"12\" font-weight=\"bold\" fill=\"#fff\" text-anchor=\"middle\">A (1)</text>" +
                "<circle cx=\"180\" cy=\"65\" r=\"16\" fill=\"#60a5fa\" stroke=\"#2563eb\" stroke-width=\"2\"/><text x=\"180\" y=\"70\" font-size=\"12\" font-weight=\"bold\" fill=\"#fff\" text-anchor=\"middle\">B (2)</text>" +
                "<circle cx=\"400\" cy=\"65\" r=\"16\" fill=\"#60a5fa\" stroke=\"#2563eb\" stroke-width=\"2\"/><text x=\"400\" y=\"70\" font-size=\"12\" font-weight=\"bold\" fill=\"#fff\" text-anchor=\"middle\">C (3)</text>" +
                "<circle cx=\"120\" cy=\"105\" r=\"15\" fill=\"#93c5fd\" stroke=\"#3b82f6\" stroke-width=\"2\"/><text x=\"120\" y=\"110\" font-size=\"11\" font-weight=\"bold\" fill=\"#1e3a8a\" text-anchor=\"middle\">D (4)</text>" +
                "<circle cx=\"240\" cy=\"105\" r=\"15\" fill=\"#93c5fd\" stroke=\"#3b82f6\" stroke-width=\"2\"/><text x=\"240\" y=\"110\" font-size=\"11\" font-weight=\"bold\" fill=\"#1e3a8a\" text-anchor=\"middle\">E (5)</text>" +
                "<circle cx=\"460\" cy=\"105\" r=\"15\" fill=\"#93c5fd\" stroke=\"#3b82f6\" stroke-width=\"2\"/><text x=\"460\" y=\"110\" font-size=\"11\" font-weight=\"bold\" fill=\"#1e3a8a\" text-anchor=\"middle\">F (6)</text>" +
                "<text x=\"290\" y=\"136\" font-size=\"11\" font-weight=\"bold\" fill=\"#1e40af\" text-anchor=\"middle\">Level Order: Level 0 (A) ➔ Level 1 (B, C) ➔ Level 2 (D, E, F)</text>" +
                "</svg>";
    }

    private String generateDfsSvg() {
        return "<svg viewBox=\"0 0 580 140\" xmlns=\"http://www.w3.org/2000/svg\" class=\"w-full h-auto\">" +
                "<rect width=\"580\" height=\"140\" rx=\"8\" fill=\"#fefce8\" stroke=\"#fef08a\"/>" +
                "<line x1=\"290\" y1=\"20\" x2=\"180\" y2=\"60\" stroke=\"#eab308\" stroke-width=\"2\" stroke-dasharray=\"4 2\"/>" +
                "<line x1=\"180\" y1=\"60\" x2=\"120\" y2=\"100\" stroke=\"#ca8a04\" stroke-width=\"2\"/>" +
                "<line x1=\"180\" y1=\"60\" x2=\"240\" y2=\"100\" stroke=\"#ca8a04\" stroke-width=\"2\"/>" +
                "<line x1=\"290\" y1=\"20\" x2=\"400\" y2=\"60\" stroke=\"#a1a1aa\" stroke-width=\"1.5\"/>" +
                "<circle cx=\"290\" cy=\"20\" r=\"15\" fill=\"#eab308\" stroke=\"#a16207\" stroke-width=\"2\"/><text x=\"290\" y=\"24\" font-size=\"11\" font-weight=\"bold\" fill=\"#fff\" text-anchor=\"middle\">A (1)</text>" +
                "<circle cx=\"180\" cy=\"60\" r=\"15\" fill=\"#ca8a04\" stroke=\"#854d0e\" stroke-width=\"2\"/><text x=\"180\" y=\"64\" font-size=\"11\" font-weight=\"bold\" fill=\"#fff\" text-anchor=\"middle\">B (2)</text>" +
                "<circle cx=\"120\" cy=\"100\" r=\"15\" fill=\"#a16207\" stroke=\"#713f12\" stroke-width=\"2\"/><text x=\"120\" y=\"104\" font-size=\"11\" font-weight=\"bold\" fill=\"#fff\" text-anchor=\"middle\">D (3)</text>" +
                "<circle cx=\"240\" cy=\"100\" r=\"15\" fill=\"#ca8a04\" stroke=\"#854d0e\" stroke-width=\"2\"/><text x=\"240\" y=\"104\" font-size=\"11\" font-weight=\"bold\" fill=\"#fff\" text-anchor=\"middle\">E (4)</text>" +
                "<circle cx=\"400\" cy=\"60\" r=\"15\" fill=\"#d4d4d8\" stroke=\"#71717a\" stroke-width=\"1.5\"/><text x=\"400\" y=\"64\" font-size=\"11\" font-weight=\"bold\" fill=\"#27272a\" text-anchor=\"middle\">C (5)</text>" +
                "<text x=\"380\" y=\"110\" font-size=\"11\" font-weight=\"bold\" fill=\"#a16207\">Traversal Order: A ➔ B ➔ D ➔ E ➔ C</text>" +
                "</svg>";
    }

    private String generateDijkstraSvg() {
        return "<svg viewBox=\"0 0 580 140\" xmlns=\"http://www.w3.org/2000/svg\" class=\"w-full h-auto\">" +
                "<rect width=\"580\" height=\"140\" rx=\"8\" fill=\"#f0fdf4\" stroke=\"#bbf7d0\"/>" +
                "<line x1=\"120\" y1=\"60\" x2=\"240\" y2=\"25\" stroke=\"#16a34a\" stroke-width=\"2.5\"/>" +
                "<line x1=\"120\" y1=\"60\" x2=\"240\" y2=\"95\" stroke=\"#16a34a\" stroke-width=\"2.5\"/>" +
                "<line x1=\"240\" y1=\"25\" x2=\"360\" y2=\"25\" stroke=\"#16a34a\" stroke-width=\"2.5\"/>" +
                "<line x1=\"240\" y1=\"95\" x2=\"360\" y2=\"95\" stroke=\"#94a3b8\" stroke-width=\"1.5\" stroke-dasharray=\"3 2\"/>" +
                "<line x1=\"360\" y1=\"25\" x2=\"480\" y2=\"60\" stroke=\"#16a34a\" stroke-width=\"2.5\"/>" +
                "<circle cx=\"120\" cy=\"60\" r=\"16\" fill=\"#15803d\" stroke=\"#166534\" stroke-width=\"2\"/><text x=\"120\" y=\"64\" font-size=\"11\" font-weight=\"bold\" fill=\"#fff\" text-anchor=\"middle\">S (0)</text>" +
                "<circle cx=\"240\" cy=\"25\" r=\"16\" fill=\"#22c55e\" stroke=\"#15803d\" stroke-width=\"2\"/><text x=\"240\" y=\"29\" font-size=\"11\" font-weight=\"bold\" fill=\"#fff\" text-anchor=\"middle\">A (4)</text>" +
                "<circle cx=\"240\" cy=\"95\" r=\"16\" fill=\"#86efac\" stroke=\"#15803d\" stroke-width=\"2\"/><text x=\"240\" y=\"99\" font-size=\"11\" font-weight=\"bold\" fill=\"#14532d\" text-anchor=\"middle\">B (2)</text>" +
                "<circle cx=\"360\" cy=\"25\" r=\"16\" fill=\"#22c55e\" stroke=\"#15803d\" stroke-width=\"2\"/><text x=\"360\" y=\"29\" font-size=\"11\" font-weight=\"bold\" fill=\"#fff\" text-anchor=\"middle\">C (7)</text>" +
                "<circle cx=\"480\" cy=\"60\" r=\"16\" fill=\"#15803d\" stroke=\"#166534\" stroke-width=\"2\"/><text x=\"480\" y=\"64\" font-size=\"11\" font-weight=\"bold\" fill=\"#fff\" text-anchor=\"middle\">T (9)</text>" +
                "<text x=\"290\" y=\"128\" font-size=\"11\" font-weight=\"bold\" fill=\"#166534\" text-anchor=\"middle\">Shortest Path S ➔ A ➔ C ➔ T (Total Weight = 9)</text>" +
                "</svg>";
    }

    private String generateOsiSvg() {
        return "<svg viewBox=\"0 0 580 165\" xmlns=\"http://www.w3.org/2000/svg\" class=\"w-full h-auto\">" +
                "<rect width=\"580\" height=\"165\" rx=\"8\" fill=\"#f0fdf4\" stroke=\"#bbf7d0\"/>" +
                "<g transform=\"translate(25, 10)\">" +
                "  <rect x=\"0\" y=\"0\" width=\"530\" height=\"18\" rx=\"3\" fill=\"#dbeafe\" stroke=\"#3b82f6\"/><text x=\"10\" y=\"13\" font-size=\"10\" font-weight=\"bold\" fill=\"#1e40af\">7. Application Layer (HTTP, DNS, SMTP) ➔ PDU: Data (User Interface)</text>" +
                "  <rect x=\"0\" y=\"21\" width=\"530\" height=\"18\" rx=\"3\" fill=\"#e0e7ff\" stroke=\"#6366f1\"/><text x=\"10\" y=\"34\" font-size=\"10\" font-weight=\"bold\" fill=\"#3730a3\">6. Presentation Layer (SSL/TLS, Encryption, Compression) ➔ PDU: Data</text>" +
                "  <rect x=\"0\" y=\"42\" width=\"530\" height=\"18\" rx=\"3\" fill=\"#ede9fe\" stroke=\"#8b5cf6\"/><text x=\"10\" y=\"55\" font-size=\"10\" font-weight=\"bold\" fill=\"#5b21b6\">5. Session Layer (Sockets, RPC, Dialog Management) ➔ PDU: Data</text>" +
                "  <rect x=\"0\" y=\"63\" width=\"530\" height=\"18\" rx=\"3\" fill=\"#fae8ff\" stroke=\"#d946ef\"/><text x=\"10\" y=\"76\" font-size=\"10\" font-weight=\"bold\" fill=\"#86198f\">4. Transport Layer (TCP, UDP, Flow/Error Control) ➔ PDU: Segments</text>" +
                "  <rect x=\"0\" y=\"84\" width=\"530\" height=\"18\" rx=\"3\" fill=\"#fee2e2\" stroke=\"#ef4444\"/><text x=\"10\" y=\"97\" font-size=\"10\" font-weight=\"bold\" fill=\"#991b1b\">3. Network Layer (IPv4/IPv6, Routers, Logical Addressing) ➔ PDU: Packets</text>" +
                "  <rect x=\"0\" y=\"105\" width=\"530\" height=\"18\" rx=\"3\" fill=\"#ffedd5\" stroke=\"#f97316\"/><text x=\"10\" y=\"118\" font-size=\"10\" font-weight=\"bold\" fill=\"#9a3412\">2. Data Link Layer (Ethernet, MAC, Switch Framing) ➔ PDU: Frames</text>" +
                "  <rect x=\"0\" y=\"126\" width=\"530\" height=\"18\" rx=\"3\" fill=\"#fef3c7\" stroke=\"#f59e0b\"/><text x=\"10\" y=\"139\" font-size=\"10\" font-weight=\"bold\" fill=\"#92400e\">1. Physical Layer (Cables, Signals, Bitstream Transmission) ➔ PDU: Bits</text>" +
                "</g>" +
                "</svg>";
    }

    private String generateNormalizationSvg() {
        return "<svg viewBox=\"0 0 580 135\" xmlns=\"http://www.w3.org/2000/svg\" class=\"w-full h-auto\">" +
                "<rect width=\"580\" height=\"135\" rx=\"8\" fill=\"#fdf4ff\" stroke=\"#f0abfc\"/>" +
                "<g transform=\"translate(25, 20)\">" +
                "  <rect x=\"0\" y=\"0\" width=\"105\" height=\"55\" rx=\"5\" fill=\"#fff\" stroke=\"#d946ef\" stroke-width=\"1.5\"/>" +
                "  <text x=\"52\" y=\"22\" font-size=\"12\" font-weight=\"bold\" fill=\"#a21caf\" text-anchor=\"middle\">1NF</text>" +
                "  <text x=\"52\" y=\"42\" font-size=\"9\" fill=\"#701a75\" text-anchor=\"middle\">Atomic Values</text>" +

                "  <path d=\"M 108 27 L 137 27\" stroke=\"#a21caf\" stroke-width=\"2\" marker-end=\"url(#arrow)\"/>" +

                "  <rect x=\"140\" y=\"0\" width=\"105\" height=\"55\" rx=\"5\" fill=\"#fff\" stroke=\"#d946ef\" stroke-width=\"1.5\"/>" +
                "  <text x=\"192\" y=\"22\" font-size=\"12\" font-weight=\"bold\" fill=\"#a21caf\" text-anchor=\"middle\">2NF</text>" +
                "  <text x=\"192\" y=\"42\" font-size=\"9\" fill=\"#701a75\" text-anchor=\"middle\">No Partial Dep</text>" +

                "  <path d=\"M 248 27 L 277 27\" stroke=\"#a21caf\" stroke-width=\"2\"/>" +

                "  <rect x=\"280\" y=\"0\" width=\"105\" height=\"55\" rx=\"5\" fill=\"#fff\" stroke=\"#d946ef\" stroke-width=\"1.5\"/>" +
                "  <text x=\"332\" y=\"22\" font-size=\"12\" font-weight=\"bold\" fill=\"#a21caf\" text-anchor=\"middle\">3NF</text>" +
                "  <text x=\"332\" y=\"42\" font-size=\"9\" fill=\"#701a75\" text-anchor=\"middle\">No Transitive Dep</text>" +

                "  <path d=\"M 388 27 L 417 27\" stroke=\"#a21caf\" stroke-width=\"2\"/>" +

                "  <rect x=\"420\" y=\"0\" width=\"105\" height=\"55\" rx=\"5\" fill=\"#fae8ff\" stroke=\"#86198f\" stroke-width=\"2\"/>" +
                "  <text x=\"472\" y=\"22\" font-size=\"12\" font-weight=\"bold\" fill=\"#701a75\" text-anchor=\"middle\">BCNF</text>" +
                "  <text x=\"472\" y=\"42\" font-size=\"9\" fill=\"#581c87\" text-anchor=\"middle\">Strict Super Key</text>" +
                "</g>" +
                "<text x=\"290\" y=\"110\" font-size=\"11\" font-weight=\"bold\" fill=\"#86198f\" text-anchor=\"middle\">Reduces Data Redundancy &amp; Prevents Insertion / Deletion / Update Anomalies</text>" +
                "</svg>";
    }

    private String generateNeuralNetSvg() {
        return "<svg viewBox=\"0 0 580 135\" xmlns=\"http://www.w3.org/2000/svg\" class=\"w-full h-auto\">" +
                "<rect width=\"580\" height=\"135\" rx=\"8\" fill=\"#f8fafc\" stroke=\"#cbd5e1\"/>" +
                "<line x1=\"130\" y1=\"35\" x2=\"290\" y2=\"25\" stroke=\"#cbd5e1\" stroke-width=\"1.5\"/>" +
                "<line x1=\"130\" y1=\"35\" x2=\"290\" y2=\"65\" stroke=\"#cbd5e1\" stroke-width=\"1.5\"/>" +
                "<line x1=\"130\" y1=\"35\" x2=\"290\" y2=\"105\" stroke=\"#cbd5e1\" stroke-width=\"1.5\"/>" +
                "<line x1=\"130\" y1=\"95\" x2=\"290\" y2=\"25\" stroke=\"#cbd5e1\" stroke-width=\"1.5\"/>" +
                "<line x1=\"130\" y1=\"95\" x2=\"290\" y2=\"65\" stroke=\"#cbd5e1\" stroke-width=\"1.5\"/>" +
                "<line x1=\"130\" y1=\"95\" x2=\"290\" y2=\"105\" stroke=\"#cbd5e1\" stroke-width=\"1.5\"/>" +
                "<line x1=\"290\" y1=\"25\" x2=\"450\" y2=\"65\" stroke=\"#93c5fd\" stroke-width=\"2\"/>" +
                "<line x1=\"290\" y1=\"65\" x2=\"450\" y2=\"65\" stroke=\"#93c5fd\" stroke-width=\"2\"/>" +
                "<line x1=\"290\" y1=\"105\" x2=\"450\" y2=\"65\" stroke=\"#93c5fd\" stroke-width=\"2\"/>" +
                "<circle cx=\"130\" cy=\"35\" r=\"14\" fill=\"#dbeafe\" stroke=\"#2563eb\" stroke-width=\"2\"/><text x=\"130\" y=\"39\" font-size=\"10\" font-weight=\"bold\" text-anchor=\"middle\">x₁</text>" +
                "<circle cx=\"130\" cy=\"95\" r=\"14\" fill=\"#dbeafe\" stroke=\"#2563eb\" stroke-width=\"2\"/><text x=\"130\" y=\"99\" font-size=\"10\" font-weight=\"bold\" text-anchor=\"middle\">x₂</text>" +
                "<circle cx=\"290\" cy=\"25\" r=\"14\" fill=\"#ede9fe\" stroke=\"#7c3aed\" stroke-width=\"2\"/><text x=\"290\" y=\"29\" font-size=\"10\" font-weight=\"bold\" text-anchor=\"middle\">h₁</text>" +
                "<circle cx=\"290\" cy=\"65\" r=\"14\" fill=\"#ede9fe\" stroke=\"#7c3aed\" stroke-width=\"2\"/><text x=\"290\" y=\"69\" font-size=\"10\" font-weight=\"bold\" text-anchor=\"middle\">h₂</text>" +
                "<circle cx=\"290\" cy=\"105\" r=\"14\" fill=\"#ede9fe\" stroke=\"#7c3aed\" stroke-width=\"2\"/><text x=\"290\" y=\"109\" font-size=\"10\" font-weight=\"bold\" text-anchor=\"middle\">h₃</text>" +
                "<circle cx=\"450\" cy=\"65\" r=\"16\" fill=\"#dcfce7\" stroke=\"#16a34a\" stroke-width=\"2\"/><text x=\"450\" y=\"69\" font-size=\"11\" font-weight=\"bold\" fill=\"#15803d\" text-anchor=\"middle\">ŷ</text>" +
                "</svg>";
    }

    private String generatePhotosynthesisSvg() {
        return "<svg viewBox=\"0 0 580 135\" xmlns=\"http://www.w3.org/2000/svg\" class=\"w-full h-auto\">" +
                "<rect width=\"580\" height=\"135\" rx=\"8\" fill=\"#ecfdf5\" stroke=\"#a7f3d0\"/>" +
                "<g transform=\"translate(35, 20)\">" +
                "  <rect x=\"0\" y=\"10\" width=\"140\" height=\"60\" rx=\"6\" fill=\"#fef9c3\" stroke=\"#ca8a04\" stroke-width=\"1.5\"/>" +
                "  <text x=\"70\" y=\"32\" font-size=\"11\" font-weight=\"bold\" fill=\"#854d0e\" text-anchor=\"middle\">INPUTS</text>" +
                "  <text x=\"70\" y=\"48\" font-size=\"10\" fill=\"#713f12\" text-anchor=\"middle\">Sunlight (Photons)</text>" +
                "  <text x=\"70\" y=\"62\" font-size=\"10\" fill=\"#713f12\" text-anchor=\"middle\">6 CO₂ + 6 H₂O</text>" +

                "  <path d=\"M 145 40 L 185 40\" stroke=\"#059669\" stroke-width=\"3\"/>" +

                "  <rect x=\"190\" y=\"0\" width=\"135\" height=\"80\" rx=\"6\" fill=\"#d1fae5\" stroke=\"#059669\" stroke-width=\"2\"/>" +
                "  <text x=\"257\" y=\"25\" font-size=\"11\" font-weight=\"bold\" fill=\"#065f46\" text-anchor=\"middle\">CHLOROPLAST</text>" +
                "  <text x=\"257\" y=\"45\" font-size=\"9\" fill=\"#047857\" text-anchor=\"middle\">Thylakoid (Light Rxn)</text>" +
                "  <text x=\"257\" y=\"65\" font-size=\"9\" fill=\"#047857\" text-anchor=\"middle\">Stroma (Calvin Cycle)</text>" +

                "  <path d=\"M 330 40 L 370 40\" stroke=\"#059669\" stroke-width=\"3\"/>" +

                "  <rect x=\"375\" y=\"10\" width=\"135\" height=\"60\" rx=\"6\" fill=\"#e0f2fe\" stroke=\"#0284c7\" stroke-width=\"1.5\"/>" +
                "  <text x=\"442\" y=\"32\" font-size=\"11\" font-weight=\"bold\" fill=\"#0369a1\" text-anchor=\"middle\">OUTPUTS</text>" +
                "  <text x=\"442\" y=\"48\" font-size=\"10\" fill=\"#075985\" text-anchor=\"middle\">Glucose (C₆H₁₂O₆)</text>" +
                "  <text x=\"442\" y=\"62\" font-size=\"10\" fill=\"#075985\" text-anchor=\"middle\">Oxygen (6 O₂)</text>" +
                "</g>" +
                "<text x=\"290\" y=\"120\" font-size=\"11\" font-weight=\"bold\" fill=\"#065f46\" text-anchor=\"middle\">6CO₂ + 6H₂O + Light Energy ──▶ C₆H₁₂O₆ + 6O₂</text>" +
                "</svg>";
    }

    private String generateNewtonSvg() {
        return "<svg viewBox=\"0 0 580 125\" xmlns=\"http://www.w3.org/2000/svg\" class=\"w-full h-auto\">" +
                "<rect width=\"580\" height=\"125\" rx=\"8\" fill=\"#fff7ed\" stroke=\"#ffedd5\"/>" +
                "<g transform=\"translate(25, 15)\">" +
                "  <rect x=\"0\" y=\"0\" width=\"160\" height=\"65\" rx=\"5\" fill=\"#fff\" stroke=\"#ea580c\" stroke-width=\"1.5\"/>" +
                "  <text x=\"80\" y=\"22\" font-size=\"11\" font-weight=\"bold\" fill=\"#c2410c\" text-anchor=\"middle\">1st: Law of Inertia</text>" +
                "  <text x=\"80\" y=\"42\" font-size=\"12\" font-family=\"monospace\" font-weight=\"bold\" fill=\"#9a3412\" text-anchor=\"middle\">Σ F = 0 ⟹ v = const</text>" +
                "  <text x=\"80\" y=\"56\" font-size=\"9\" fill=\"#7c2d12\" text-anchor=\"middle\">Resists change in state</text>" +

                "  <rect x=\"185\" y=\"0\" width=\"160\" height=\"65\" rx=\"5\" fill=\"#fff\" stroke=\"#ea580c\" stroke-width=\"1.5\"/>" +
                "  <text x=\"265\" y=\"22\" font-size=\"11\" font-weight=\"bold\" fill=\"#c2410c\" text-anchor=\"middle\">2nd: Force Law</text>" +
                "  <text x=\"265\" y=\"42\" font-size=\"13\" font-family=\"monospace\" font-weight=\"bold\" fill=\"#9a3412\" text-anchor=\"middle\">F = m · a</text>" +
                "  <text x=\"265\" y=\"56\" font-size=\"9\" fill=\"#7c2d12\" text-anchor=\"middle\">Acceleration ∝ Force</text>" +

                "  <rect x=\"370\" y=\"0\" width=\"160\" height=\"65\" rx=\"5\" fill=\"#fff\" stroke=\"#ea580c\" stroke-width=\"1.5\"/>" +
                "  <text x=\"450\" y=\"22\" font-size=\"11\" font-weight=\"bold\" fill=\"#c2410c\" text-anchor=\"middle\">3rd: Action-Reaction</text>" +
                "  <text x=\"450\" y=\"42\" font-size=\"12\" font-family=\"monospace\" font-weight=\"bold\" fill=\"#9a3412\" text-anchor=\"middle\">F_AB = - F_BA</text>" +
                "  <text x=\"450\" y=\"56\" font-size=\"9\" fill=\"#7c2d12\" text-anchor=\"middle\">Equal &amp; Opposite Pair</text>" +
                "</g>" +
                "<text x=\"290\" y=\"108\" font-size=\"11\" font-weight=\"bold\" fill=\"#9a3412\" text-anchor=\"middle\">Foundational Pillars of Classical Dynamics</text>" +
                "</svg>";
    }

    private String generateTcpHandshakeSvg() {
        return "<svg viewBox=\"0 0 580 140\" xmlns=\"http://www.w3.org/2000/svg\" class=\"w-full h-auto\">" +
                "<rect width=\"580\" height=\"140\" rx=\"8\" fill=\"#f0fdfa\" stroke=\"#ccfbf1\"/>" +
                "<line x1=\"140\" y1=\"20\" x2=\"140\" y2=\"120\" stroke=\"#0d9488\" stroke-width=\"2\"/>" +
                "<line x1=\"440\" y1=\"20\" x2=\"440\" y2=\"120\" stroke=\"#0d9488\" stroke-width=\"2\"/>" +
                "<text x=\"140\" y=\"16\" font-size=\"12\" font-weight=\"bold\" fill=\"#115e59\" text-anchor=\"middle\">CLIENT</text>" +
                "<text x=\"440\" y=\"16\" font-size=\"12\" font-weight=\"bold\" fill=\"#115e59\" text-anchor=\"middle\">SERVER</text>" +
                "<line x1=\"140\" y1=\"40\" x2=\"430\" y2=\"60\" stroke=\"#0f766e\" stroke-width=\"2\"/>" +
                "<text x=\"290\" y=\"45\" font-size=\"10\" font-weight=\"bold\" fill=\"#0f766e\" text-anchor=\"middle\">1. SYN (seq = x)</text>" +
                "<line x1=\"440\" y1=\"65\" x2=\"150\" y2=\"85\" stroke=\"#0f766e\" stroke-width=\"2\"/>" +
                "<text x=\"290\" y=\"72\" font-size=\"10\" font-weight=\"bold\" fill=\"#0f766e\" text-anchor=\"middle\">2. SYN-ACK (seq = y, ack = x+1)</text>" +
                "<line x1=\"140\" y1=\"90\" x2=\"430\" y2=\"110\" stroke=\"#16a34a\" stroke-width=\"2\"/>" +
                "<text x=\"290\" y=\"98\" font-size=\"10\" font-weight=\"bold\" fill=\"#15803d\" text-anchor=\"middle\">3. ACK (ack = y+1) ➔ [ESTABLISHED]</text>" +
                "</svg>";
    }

    private String generateGenericConceptMapSvg(String topic) {
        return "<svg viewBox=\"0 0 580 135\" xmlns=\"http://www.w3.org/2000/svg\" class=\"w-full h-auto\">" +
                "<rect width=\"580\" height=\"135\" rx=\"8\" fill=\"#f8fafc\" stroke=\"#e2e8f0\"/>" +
                "<rect x=\"220\" y=\"48\" width=\"140\" height=\"40\" rx=\"8\" fill=\"#3b82f6\" stroke=\"#1d4ed8\" stroke-width=\"2\"/>" +
                "<text x=\"290\" y=\"73\" font-size=\"12\" font-weight=\"bold\" fill=\"#fff\" text-anchor=\"middle\">" + escapeXml(topic) + "</text>" +

                "<line x1=\"220\" y1=\"58\" x2=\"140\" y2=\"28\" stroke=\"#94a3b8\" stroke-width=\"1.5\"/>" +
                "<rect x=\"20\" y=\"12\" width=\"120\" height=\"30\" rx=\"5\" fill=\"#eff6ff\" stroke=\"#3b82f6\"/>" +
                "<text x=\"80\" y=\"32\" font-size=\"10\" font-weight=\"bold\" fill=\"#1e40af\" text-anchor=\"middle\">1. Foundations &amp; Rules</text>" +

                "<line x1=\"360\" y1=\"58\" x2=\"440\" y2=\"28\" stroke=\"#94a3b8\" stroke-width=\"1.5\"/>" +
                "<rect x=\"440\" y=\"12\" width=\"120\" height=\"30\" rx=\"5\" fill=\"#f0fdf4\" stroke=\"#16a34a\"/>" +
                "<text x=\"500\" y=\"32\" font-size=\"10\" font-weight=\"bold\" fill=\"#166534\" text-anchor=\"middle\">2. Core Mechanics</text>" +

                "<line x1=\"220\" y1=\"78\" x2=\"140\" y2=\"108\" stroke=\"#94a3b8\" stroke-width=\"1.5\"/>" +
                "<rect x=\"20\" y=\"92\" width=\"120\" height=\"30\" rx=\"5\" fill=\"#fefce8\" stroke=\"#ca8a04\"/>" +
                "<text x=\"80\" y=\"112\" font-size=\"10\" font-weight=\"bold\" fill=\"#854d0e\" text-anchor=\"middle\">3. Worked Examples</text>" +

                "<line x1=\"360\" y1=\"78\" x2=\"440\" y2=\"108\" stroke=\"#94a3b8\" stroke-width=\"1.5\"/>" +
                "<rect x=\"440\" y=\"92\" width=\"120\" height=\"30\" rx=\"5\" fill=\"#faf5ff\" stroke=\"#a855f7\"/>" +
                "<text x=\"500\" y=\"112\" font-size=\"10\" font-weight=\"bold\" fill=\"#6b21a8\" text-anchor=\"middle\">4. Complexity &amp; Exams</text>" +
                "</svg>";
    }

    private String escapeXml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
