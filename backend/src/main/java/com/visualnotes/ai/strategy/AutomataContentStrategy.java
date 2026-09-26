package com.visualnotes.ai.strategy;

import com.visualnotes.ai.domain.DomainType;
import com.visualnotes.diagram.DiagramEngine;
import com.visualnotes.dto.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AutomataContentStrategy {

    private final DiagramEngine diagramEngine;

    public AutomataContentStrategy(DiagramEngine diagramEngine) {
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

        if (lower.contains("nfa") || lower.contains("dfa") || lower.contains("finite automata") || lower.contains("automata")) {
            return generateNfaDfaContent(topic, overallPrompt, pageNumber, totalPages, style, audience, difficulty);
        } else {
            return generateGeneralAutomataContent(topic, overallPrompt, pageNumber, totalPages, style, audience, difficulty);
        }
    }

    private PageContentDto generateNfaDfaContent(
            String topic,
            String overallPrompt,
            int pageNumber,
            int totalPages,
            String style,
            String audience,
            String difficulty) {

        DiagramDataDto diagram = diagramEngine.generateDiagram("NFA vs DFA", "automata-state-transition", overallPrompt);

        List<SectionDto> sections = new ArrayList<>();

        // 1. Definition of DFA
        sections.add(SectionDto.builder()
                .heading("1. Deterministic Finite Automaton (DFA)")
                .content("A DFA is a 5-tuple M = (Q, Σ, δ, q₀, F) where for every state and input symbol, there is EXACTLY ONE next state:")
                .badge("Formal Definition")
                .bulletPoints(List.of(
                        "Transition Function: δ : Q × Σ ➔ Q (deterministic, exactly 1 transition per symbol).",
                        "Null Transitions: ε-transitions are strictly NOT allowed in DFA.",
                        "Execution: Follows a single deterministic computational path for any input string."
                ))
                .highlights(List.of("δ : Q × Σ ➔ Q", "No ε-moves", "Single path"))
                .build());

        // 2. Definition of NFA
        sections.add(SectionDto.builder()
                .heading("2. Non-Deterministic Finite Automaton (NFA)")
                .content("An NFA is a 5-tuple M = (Q, Σ, δ, q₀, F) where for a state and input symbol, there can be ZERO, ONE, or MULTIPLE next states:")
                .badge("Formal Definition")
                .bulletPoints(List.of(
                        "Transition Function: δ : Q × (Σ ∪ {ε}) ➔ 2^Q (maps to the power set of states).",
                        "Null Transitions: Can change state spontaneously on empty string (ε-move).",
                        "Acceptance: A string is accepted if AT LEAST ONE computational branch reaches an accept state in F."
                ))
                .highlights(List.of("δ : Q × (Σ ∪ {ε}) ➔ 2^Q", "Allows ε-moves", "Parallel branching"))
                .build());

        // Comparison Table: NFA vs DFA
        ComparisonTableDto table = ComparisonTableDto.builder()
                .title("Key Differences: DFA vs NFA")
                .headers(List.of("Feature / Parameter", "Deterministic (DFA)", "Non-Deterministic (NFA)"))
                .rows(List.of(
                        List.of("Transition Rule (δ)", "δ : Q × Σ ➔ Q (Unique)", "δ : Q × (Σ ∪ {ε}) ➔ 2^Q (Set of states)"),
                        List.of("ε (Empty) Moves", "Strictly Prohibited", "Allowed (changes state without input)"),
                        List.of("Multiple Choices", "No (single path always)", "Yes (explores multiple branches)"),
                        List.of("Implementation in HW/SW", "Easy and Fast to execute O(n)", "Harder (requires backtracking/subset construction)"),
                        List.of("Language Power", "Recognizes Regular Languages", "Recognizes Regular Languages (Equivalent power: 2^n states)")
                ))
                .conclusion("Equivalence Theorem: For every NFA, there exists an equivalent DFA recognizing the exact same regular language (via Subset Construction Algorithm).")
                .build();

        // Formula / Formal Tuple
        FormulaDto formula = FormulaDto.builder()
                .title("5-Tuple Formal Definition")
                .expression("M = (Q, \\Sigma, \\delta, q_0, F)")
                .explanation("Where Q = finite set of states, Σ = alphabet, δ = transition function, q₀ ∈ Q = start state, F ⊆ Q = set of final/accepting states.")
                .build();

        // Exam Tips
        List<ExamTipDto> examTips = List.of(
                ExamTipDto.builder()
                        .tip("Exam Rule: NFA and DFA have EQUAL computational power (both recognize Regular Languages). However, an NFA with 'n' states may require up to 2ⁿ states in its equivalent DFA.")
                        .commonMistake("Assuming NFA can recognize languages that DFA cannot (False: both recognize only regular languages).")
                        .mnemonic("DFA = 1 symbol ➔ 1 state | NFA = 1 symbol ➔ subset of states (2^Q)")
                        .build()
        );

        return PageContentDto.builder()
                .documentTitle("NFA and DFA — Formal Automata Theory")
                .pageNumber(pageNumber)
                .totalPages(totalPages)
                .topicTitle("Deterministic & Non-Deterministic Finite Automata (DFA vs NFA)")
                .topicSubtitle("Formal 5-Tuple Specification, State Transitions & Equivalence")
                .categoryBadge("Theory of Computation")
                .difficultyLevel("Intermediate / University Core")
                .domain(DomainType.THEORY_OF_COMPUTATION.name())
                .subdomain("Formal Languages & Automata Theory")
                .definition("Finite Automata are abstract mathematical state machines used to model computation and recognize Regular Languages specified by Regular Expressions.")
                .sections(sections)
                .comparisonTable(table)
                .formula(formula)
                .diagram(diagram)
                .examTips(examTips)
                .keyPoints(List.of(
                        KeyPointDto.builder().point("DFA transition function maps to a single state: δ(q, a) = p.").starred(true).category("DFA").build(),
                        KeyPointDto.builder().point("NFA transition function maps to a set of states: δ(q, a) ⊆ Q (power set 2^Q).").starred(true).category("NFA").build(),
                        KeyPointDto.builder().point("Every NFA can be converted to an equivalent DFA using the Subset Construction (Powerset) algorithm.").starred(true).category("Equivalence").build()
                ))
                .build();
    }

    private PageContentDto generateGeneralAutomataContent(
            String topic,
            String overallPrompt,
            int pageNumber,
            int totalPages,
            String style,
            String audience,
            String difficulty) {

        DiagramDataDto diagram = diagramEngine.generateDiagram(topic, "automata-state-transition", overallPrompt);

        List<SectionDto> sections = new ArrayList<>();
        sections.add(SectionDto.builder()
                .heading("1. Mathematical Formulation & Definition")
                .content(topic + " is formally defined by formal language theory rules and transition state graphs.")
                .badge("Formal Theory")
                .bulletPoints(List.of(
                        "Alphabet (Σ): Finite non-empty set of symbols.",
                        "States (Q): Internal states representing computational progress.",
                        "Acceptance: Input string w ∈ Σ* reaches an accepting state in F."
                ))
                .build());

        return PageContentDto.builder()
                .documentTitle(topic)
                .pageNumber(pageNumber)
                .totalPages(totalPages)
                .topicTitle(topic)
                .categoryBadge("Theory of Computation")
                .domain(DomainType.THEORY_OF_COMPUTATION.name())
                .definition(topic + " represents an essential state machine construct in theoretical computer science.")
                .sections(sections)
                .diagram(diagram)
                .build();
    }
}
