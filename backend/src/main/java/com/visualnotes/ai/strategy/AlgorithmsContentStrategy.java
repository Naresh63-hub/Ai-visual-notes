package com.visualnotes.ai.strategy;

import com.visualnotes.diagram.DiagramEngine;
import com.visualnotes.dto.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AlgorithmsContentStrategy {

    private final DiagramEngine diagramEngine;

    public AlgorithmsContentStrategy(DiagramEngine diagramEngine) {
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
        boolean isMultiPage = (totalPages > 1);

        if (lower.contains("binary search")) {
            return generateBinarySearch(topic, pageNumber, totalPages, style, audience, difficulty, isMultiPage);
        } else if (lower.contains("quick sort")) {
            return generateQuickSort(topic, pageNumber, totalPages, style, audience, difficulty, isMultiPage);
        } else if (lower.contains("merge sort")) {
            return generateMergeSort(topic, pageNumber, totalPages, style, audience, difficulty, isMultiPage);
        } else {
            return generateGeneralAlgorithm(topic, overallPrompt, pageNumber, totalPages, style, audience, difficulty, focusArea, plannedDiagramType);
        }
    }

    private PageContentDto generateBinarySearch(String topic, int pageNum, int totalPages, String style, String audience, String difficulty, boolean isMultiPage) {
        DiagramDataDto diagram = diagramEngine.generateDiagram("Binary Search", "binary-search-array", "Binary Search");

        if (isMultiPage && totalPages >= 2) {
            if (pageNum == 1) {
                // PAGE 1: Concepts, Intuition & Diagram
                List<SectionDto> sections = new ArrayList<>();
                sections.add(SectionDto.builder()
                        .heading("1. Purpose & Preconditions")
                        .content("Binary Search quickly locates the position of a target key within a sequence.")
                        .badge("Crucial Precondition")
                        .bulletPoints(List.of(
                                "Array MUST be sorted in ascending (or monotonic) order before searching.",
                                "Requires O(1) random-access indexing (Arrays, Vectors).",
                                "Maintains search boundaries: Low (start) and High (end)."
                        ))
                        .highlights(List.of("Array MUST be sorted", "O(1) random access", "Low and High boundaries"))
                        .build());

                sections.add(SectionDto.builder()
                        .heading("2. Main Idea & Intuition (Divide & Conquer)")
                        .content("Instead of checking elements sequentially from left to right, compare the target key with the middle element:")
                        .badge("How It Works")
                        .bulletPoints(List.of(
                                "Compute Mid: mid = Low + (High - Low) / 2 (avoids integer overflow).",
                                "If Key == Arr[Mid] ➔ Target found immediately! Return Mid.",
                                "If Key < Arr[Mid] ➔ Target must lie in the Left half: High = Mid - 1.",
                                "If Key > Arr[Mid] ➔ Target must lie in the Right half: Low = Mid + 1.",
                                "Each comparison halves the remaining search space: n ➔ n/2 ➔ n/4 ➔ ... ➔ 1."
                        ))
                        .highlights(List.of("mid = Low + (High - Low)/2", "Halves search space each step"))
                        .build());

                return PageContentDto.builder()
                        .documentTitle("Binary Search")
                        .pageNumber(1)
                        .totalPages(totalPages)
                        .topicTitle("Binary Search")
                        .topicSubtitle("Divide & Conquer Monotonic Search Algorithm")
                        .categoryBadge("B.Tech / College Notes ★★★")
                        .difficultyLevel(difficulty)
                        .pagePartTitle("Part 1: Concept, Main Idea & Visual Working")
                        .definition("Binary Search is an optimal divide-and-conquer search algorithm for sorted arrays that repeatedly bisects the search interval until the target is located.")
                        .purpose("To locate an element in a sorted collection in O(log n) logarithmic time rather than O(n) linear scan.")
                        .mainIdea("Repeatedly eliminate half of the remaining array by comparing the target key against the middle element.")
                        .simpleExplanation("Open a dictionary at the middle. If your word comes earlier alphabetically, ignore the second half. Repeat on the remaining half until found.")
                        .sections(sections)
                        .diagram(diagram)
                        .keyPoints(List.of(
                                KeyPointDto.builder().point("Binary search ONLY works on sorted collections.").starred(true).category("Rule").build(),
                                KeyPointDto.builder().point("Each step cuts problem size in half (Logarithmic reduction).").starred(true).category("Principle").build()
                        ))
                        .quickTakeaways(List.of(
                                "Precondition: Pre-sorted Array",
                                "Search Mechanism: Midpoint comparison",
                                "Reduction: 50% search space eliminated per step"
                        ))
                        .continuesOnNextPage(true)
                        .isContinuation(false)
                        .layoutHint("handwritten-part1")
                        .styleTheme(style)
                        .build();
            } else {
                // PAGE 2: Algorithm, Trace & Complexity
                List<AlgorithmStepDto> algorithm = new ArrayList<>();
                algorithm.add(AlgorithmStepDto.builder().stepNumber(1).instruction("Initialize search boundaries").codeSnippet("low = 0, high = n - 1").build());
                algorithm.add(AlgorithmStepDto.builder().stepNumber(2).instruction("While low <= high, compute midpoint").codeSnippet("mid = low + (high - low) / 2").build());
                algorithm.add(AlgorithmStepDto.builder().stepNumber(3).instruction("If Arr[mid] == target, return index").codeSnippet("if (arr[mid] == key) return mid;").build());
                algorithm.add(AlgorithmStepDto.builder().stepNumber(4).instruction("If Arr[mid] < target, discard left half").codeSnippet("else if (arr[mid] < key) low = mid + 1;").build());
                algorithm.add(AlgorithmStepDto.builder().stepNumber(5).instruction("If Arr[mid] > target, discard right half").codeSnippet("else high = mid - 1;").build());
                algorithm.add(AlgorithmStepDto.builder().stepNumber(6).instruction("If low > high without match, key not present").codeSnippet("return -1; // Not found").build());

                String pseudocode =
                        "function binarySearch(arr: Array, target: Element) -> Integer:\n" +
                        "    low = 0\n" +
                        "    high = length(arr) - 1\n\n" +
                        "    while low <= high:\n" +
                        "        mid = low + (high - low) / 2    // Avoids integer overflow\n" +
                        "        if arr[mid] == target:\n" +
                        "            return mid                  // Target found!\n" +
                        "        else if arr[mid] < target:\n" +
                        "            low = mid + 1               // Search right half\n" +
                        "        else:\n" +
                        "            high = mid - 1              // Search left half\n\n" +
                        "    return -1                           // Element not present in array";

                ExampleDto example = ExampleDto.builder()
                        .title("Step-by-Step Execution Trace: Find Key = 15")
                        .scenario("Given Sorted Array A = [2, 5, 7, 11, 15, 18, 21], Target = 15")
                        .stepByStep(List.of(
                                "Pass 1: low = 0, high = 6 ➔ mid = 0 + (6-0)/2 = 3. Arr[3] = 11. Since 11 < 15, key is in right half: low = mid + 1 = 4.",
                                "Pass 2: low = 4, high = 6 ➔ mid = 4 + (6-4)/2 = 5. Arr[5] = 18. Since 18 > 15, key is in left sub-half: high = mid - 1 = 4.",
                                "Pass 3: low = 4, high = 4 ➔ mid = 4 + (4-4)/2 = 4. Arr[4] = 15. Arr[mid] == 15! MATCH FOUND."
                        ))
                        .outputOrResult("Target 15 found at Index = 4 in exactly 3 comparisons!")
                        .takeaway("Instead of 5 comparisons in linear search, binary search needed only 3 comparisons.")
                        .build();

                FormulaDto formula = FormulaDto.builder()
                        .title("Recurrence Relation & Mathematical Proof")
                        .expression("T(n) = T(n / 2) + O(1)   ⟹   By Master Theorem: T(n) = O(log₂ n)")
                        .explanation("At each step, the search space size n is divided by 2. The maximum number of iterations k satisfies: n / 2ᵏ = 1 ⟹ k = log₂ n.")
                        .variables(List.of(
                                FormulaVariableDto.builder().symbol("T(n)").description("Time required to search an array of size n").build(),
                                FormulaVariableDto.builder().symbol("T(n/2)").description("Time to search sub-array of halved size").build(),
                                FormulaVariableDto.builder().symbol("O(1)").description("Constant time comparison with middle element").build(),
                                FormulaVariableDto.builder().symbol("log₂ n").description("Base-2 logarithm representing recursion tree depth").build()
                        ))
                        .build();

                ComplexityDto complexity = ComplexityDto.builder()
                        .timeBest("O(1) — Target is exactly at the middle element on first check")
                        .timeAverage("O(log n) — Logarithmic time on general sorted distributions")
                        .timeWorst("O(log n) — Target at boundary or not present in array")
                        .space("O(1) Iterative / O(log n) Recursive call stack")
                        .build();

                return PageContentDto.builder()
                        .documentTitle("Binary Search")
                        .pageNumber(2)
                        .totalPages(totalPages)
                        .topicTitle("Binary Search")
                        .topicSubtitle("Algorithm, Pseudocode, Worked Trace & Complexity")
                        .categoryBadge("B.Tech / College Notes ★★★")
                        .difficultyLevel(difficulty)
                        .pagePartTitle("Part 2: Algorithm, Worked Example & Complexity Analysis")
                        .algorithm(algorithm)
                        .pseudocode(pseudocode)
                        .example(example)
                        .formula(formula)
                        .complexity(complexity)
                        .advantages(List.of(
                                "Extremely fast: For 1,000,000 elements, it takes maximum ~20 comparisons (log₂ 10⁶ ≈ 20).",
                                "O(1) constant auxiliary space memory consumption.",
                                "Universal foundation for binary search trees and range queries."
                        ))
                        .limitations(List.of(
                                "Requires the input collection to be pre-sorted.",
                                "Requires O(1) random-access data structures (inefficient for linked lists)."
                        ))
                        .keyPoints(List.of(
                                KeyPointDto.builder().point("Always compute mid = low + (high - low)/2 to prevent integer overflow bug.").starred(true).category("Best Practice").build(),
                                KeyPointDto.builder().point("Loop termination condition is low <= high (using low < high misses 1-element ranges).").starred(true).category("Loop Invariant").build()
                        ))
                        .examTips(List.of(
                                ExamTipDto.builder()
                                        .tip("★ High Probability Exam Question: State recurrence T(n) = T(n/2) + c, prove O(log n) using Master Theorem, and explain integer overflow bug prevention.")
                                        .commonMistake("Writing (low + high)/2 instead of low + (high - low)/2 which causes 32-bit integer overflow in Java/C++.")
                                        .mnemonic("Sorted ➔ Split ➔ Select ➔ Shift")
                                        .build()
                        ))
                        .quickTakeaways(List.of(
                                "Best: O(1) | Worst: O(log n) | Space: O(1)",
                                "Recurrence: T(n) = T(n/2) + 1",
                                "Safe Mid: low + (high - low)/2"
                        ))
                        .continuesOnNextPage(false)
                        .isContinuation(true)
                        .layoutHint("handwritten-part2")
                        .styleTheme(style)
                        .build();
            }
        }

        // Single page complete synthesis for Binary Search
        List<AlgorithmStepDto> algorithm = new ArrayList<>();
        algorithm.add(AlgorithmStepDto.builder().stepNumber(1).instruction("Set low = 0, high = n - 1").codeSnippet("low = 0; high = n - 1;").build());
        algorithm.add(AlgorithmStepDto.builder().stepNumber(2).instruction("While low <= high, compute mid = low + (high - low)/2").codeSnippet("int mid = low + (high - low) / 2;").build());
        algorithm.add(AlgorithmStepDto.builder().stepNumber(3).instruction("If arr[mid] == target, return mid; if arr[mid] < target, search right (low = mid + 1); else search left (high = mid - 1)").codeSnippet("if (arr[mid] < target) low = mid + 1; else high = mid - 1;").build());

        ComplexityDto complexity = ComplexityDto.builder()
                .timeBest("O(1)")
                .timeAverage("O(log n)")
                .timeWorst("O(log n)")
                .space("O(1)")
                .build();

        ExampleDto example = ExampleDto.builder()
                .title("Execution Trace: Key = 15")
                .scenario("Sorted Array A = [2, 5, 7, 11, 15, 18, 21]")
                .stepByStep(List.of(
                        "Pass 1: low=0, high=6 ➔ mid=3 (Arr[3]=11 < 15) ⟹ low=4",
                        "Pass 2: low=4, high=6 ➔ mid=5 (Arr[5]=18 > 15) ⟹ high=4",
                        "Pass 3: low=4, high=4 ➔ mid=4 (Arr[4]=15 == 15) ⟹ MATCH FOUND!"
                ))
                .outputOrResult("Target 15 found at Index = 4 in 3 iterations!")
                .build();

        return PageContentDto.builder()
                .documentTitle("Binary Search")
                .pageNumber(1)
                .totalPages(1)
                .topicTitle("Binary Search")
                .topicSubtitle("Divide & Conquer Monotonic Search Algorithm")
                .categoryBadge("Algorithms ★★★")
                .difficultyLevel(difficulty)
                .definition("Binary Search is an optimal divide-and-conquer search algorithm on pre-sorted arrays that halves the search space at each iteration.")
                .algorithm(algorithm)
                .complexity(complexity)
                .example(example)
                .diagram(diagram)
                .keyPoints(List.of(
                        KeyPointDto.builder().point("Precondition: Array MUST be sorted.").starred(true).build(),
                        KeyPointDto.builder().point("Prevents integer overflow by using low + (high - low)/2.").starred(true).build()
                ))
                .build();
    }

    private PageContentDto generateQuickSort(String topic, int pageNum, int totalPages, String style, String audience, String difficulty, boolean isMultiPage) {
        DiagramDataDto diagram = diagramEngine.generateDiagram("Quick Sort", "sorting-partition", "Quick Sort");

        List<AlgorithmStepDto> algorithm = new ArrayList<>();
        algorithm.add(AlgorithmStepDto.builder().stepNumber(1).instruction("Choose Pivot element (e.g. last, first, or median-of-three)").codeSnippet("pivot = arr[high]").build());
        algorithm.add(AlgorithmStepDto.builder().stepNumber(2).instruction("Partition array: elements < pivot to left, elements > pivot to right").codeSnippet("pIndex = partition(arr, low, high)").build());
        algorithm.add(AlgorithmStepDto.builder().stepNumber(3).instruction("Recursively sort left and right partitions").codeSnippet("quickSort(arr, low, pIndex - 1); quickSort(arr, pIndex + 1, high);").build());

        ComplexityDto complexity = ComplexityDto.builder()
                .timeBest("O(n log n)")
                .timeAverage("O(n log n)")
                .timeWorst("O(n²) on already sorted arrays with poor pivot")
                .space("O(log n) recursion call stack")
                .build();

        return PageContentDto.builder()
                .documentTitle("Quick Sort")
                .pageNumber(pageNum)
                .totalPages(totalPages)
                .topicTitle("Quick Sort")
                .topicSubtitle("Divide & Conquer In-Place Partitioning Algorithm")
                .categoryBadge("Sorting Algorithms ★★★")
                .difficultyLevel(difficulty)
                .pagePartTitle("Complete Quick Sort Breakdown & Partition Mechanics")
                .definition("Quick Sort is a highly efficient, comparison-based, in-place divide-and-conquer sorting algorithm that partitions an array around a chosen pivot element.")
                .mainIdea("Pick a pivot, partition the array so smaller elements go left and larger go right, and recurse.")
                .sections(List.of(
                        SectionDto.builder().heading("1. Lomuto & Hoare Partitioning").content("Reorganizes the array around the pivot in O(n) linear scan.").build()
                ))
                .diagram(diagram)
                .algorithm(algorithm)
                .complexity(complexity)
                .keyPoints(List.of(KeyPointDto.builder().point("Quick Sort is in-place and cache friendly.").starred(true).build()))
                .continuesOnNextPage(pageNum < totalPages)
                .isContinuation(pageNum > 1)
                .layoutHint("handwritten-part1")
                .styleTheme(style)
                .build();
    }

    private PageContentDto generateMergeSort(String topic, int pageNum, int totalPages, String style, String audience, String difficulty, boolean isMultiPage) {
        DiagramDataDto diagram = diagramEngine.generateDiagram("Merge Sort", "sorting-partition", "Merge Sort");

        ComplexityDto complexity = ComplexityDto.builder()
                .timeBest("O(n log n)")
                .timeAverage("O(n log n)")
                .timeWorst("O(n log n) Guaranteed")
                .space("O(n) auxiliary temporary array")
                .build();

        return PageContentDto.builder()
                .documentTitle("Merge Sort")
                .pageNumber(pageNum)
                .totalPages(totalPages)
                .topicTitle("Merge Sort")
                .topicSubtitle("Divide & Conquer Stable Sorting Algorithm")
                .categoryBadge("Sorting Algorithms ★★★")
                .difficultyLevel(difficulty)
                .pagePartTitle("Complete Merge Sort Breakdown & Recursion Tree")
                .definition("Merge Sort is an optimal, comparison-based, divide-and-conquer sorting algorithm that guarantees O(n log n) time complexity across all cases.")
                .mainIdea("Recursively divide the array into halves until single elements, then merge them in sorted order.")
                .diagram(diagram)
                .complexity(complexity)
                .keyPoints(List.of(KeyPointDto.builder().point("Merge Sort is ALWAYS O(n log n) and STABLE.").starred(true).build()))
                .continuesOnNextPage(pageNum < totalPages)
                .isContinuation(pageNum > 1)
                .layoutHint("handwritten-part1")
                .styleTheme(style)
                .build();
    }

    private PageContentDto generateGeneralAlgorithm(
            String topic,
            String overallPrompt,
            int pageNumber,
            int totalPages,
            String style,
            String audience,
            String difficulty,
            String focusArea,
            String plannedDiagramType) {

        DiagramDataDto diagram = diagramEngine.generateDiagram(topic, plannedDiagramType, overallPrompt);

        List<SectionDto> sections = new ArrayList<>();
        sections.add(SectionDto.builder()
                .heading("1. Algorithmic Strategy & Design Paradigm")
                .content(topic + " operates using structured algorithmic design paradigms (e.g. Divide & Conquer, Greedy, Dynamic Programming, or Traversal).")
                .badge("Algorithm Design")
                .bulletPoints(List.of(
                        "Input Preconditions: Valid structure and data range",
                        "Core Invariant: State maintained throughout loop/recursion",
                        "Termination Condition: Definite convergence to optimal result"
                ))
                .highlights(List.of("Algorithm invariant", "Optimal substructure", "Termination proof"))
                .build());

        List<AlgorithmStepDto> algorithm = new ArrayList<>();
        algorithm.add(AlgorithmStepDto.builder().stepNumber(1).instruction("Initialize data structures and pointers").codeSnippet("initialize();").build());
        algorithm.add(AlgorithmStepDto.builder().stepNumber(2).instruction("Execute core algorithmic loop/recursion").codeSnippet("executeStep();").build());
        algorithm.add(AlgorithmStepDto.builder().stepNumber(3).instruction("Return optimal result or computed state").codeSnippet("return result;").build());

        ComplexityDto complexity = ComplexityDto.builder()
                .timeBest("O(1) to O(log n)")
                .timeAverage("O(n) / O(n log n)")
                .timeWorst("O(n²)")
                .space("O(1) to O(n) auxiliary")
                .build();

        return PageContentDto.builder()
                .documentTitle(topic)
                .pageNumber(pageNumber)
                .totalPages(totalPages)
                .topicTitle(topic)
                .topicSubtitle(focusArea != null ? focusArea : "Algorithmic Analysis, Step-by-Step Working & Complexity")
                .categoryBadge("Algorithms & Data Structures")
                .difficultyLevel(difficulty)
                .pagePartTitle(totalPages > 1 ? ("Part " + pageNumber + " of " + totalPages + ": Algorithmic Mechanics") : "Complete Algorithm Notes")
                .definition(topic + " is an algorithmic procedure designed to solve computational tasks efficiently.")
                .mainIdea("Systematically process input structures to achieve correct results with optimal time and space complexity.")
                .simpleExplanation("In computer science, " + topic + " provides an efficient step-by-step recipe to solve computational problems.")
                .sections(sections)
                .diagram(diagram)
                .algorithm(algorithm)
                .complexity(complexity)
                .keyPoints(List.of(
                        KeyPointDto.builder().point("Understand time and space trade-offs before choosing this algorithm.").starred(true).category("Complexity").build()
                ))
                .examTips(List.of(
                        ExamTipDto.builder().tip("State time & space complexity clearly for Best, Average, and Worst cases with Big-O notation.").mnemonic("Algorithm ➔ Trace ➔ Complexity").build()
                ))
                .continuesOnNextPage(pageNumber < totalPages)
                .isContinuation(pageNumber > 1)
                .layoutHint("handwritten-part1")
                .styleTheme(style)
                .build();
    }
}
