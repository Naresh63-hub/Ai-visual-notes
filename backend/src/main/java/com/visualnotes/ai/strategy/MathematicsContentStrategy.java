package com.visualnotes.ai.strategy;

import com.visualnotes.diagram.DiagramEngine;
import com.visualnotes.dto.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MathematicsContentStrategy {

    private final DiagramEngine diagramEngine;

    public MathematicsContentStrategy(DiagramEngine diagramEngine) {
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

        if (lower.contains("bayes") || lower.contains("probability")) {
            return generateBayesContent(topic, overallPrompt, pageNumber, totalPages, style, audience, difficulty);
        } else {
            return generateGeneralMathContent(topic, overallPrompt, pageNumber, totalPages, style, audience, difficulty, focusArea);
        }
    }

    private PageContentDto generateBayesContent(
            String topic,
            String overallPrompt,
            int pageNumber,
            int totalPages,
            String style,
            String audience,
            String difficulty) {

        DiagramDataDto diagram = diagramEngine.generateDiagram("Bayes' Theorem", "formula-math", overallPrompt);

        List<SectionDto> sections = new ArrayList<>();
        sections.add(SectionDto.builder()
                .heading("1. Mathematical Principle & Conditional Probability")
                .content("Bayes' Theorem describes the probability of an event based on prior knowledge of conditions related to the event:")
                .badge("Probability Theory")
                .bulletPoints(List.of(
                        "Posterior Probability P(A|B): Updated probability of hypothesis A given observed evidence B.",
                        "Likelihood P(B|A): Probability of observing evidence B given hypothesis A is true.",
                        "Prior Probability P(A): Initial degree of belief before observing evidence.",
                        "Marginal Likelihood / Evidence P(B): Total probability of evidence across all hypotheses: P(B) = ∑ P(B|A_i) · P(A_i)."
                ))
                .highlights(List.of("P(A|B) = [P(B|A) · P(A)] / P(B)", "Prior to Posterior Update", "Law of Total Probability"))
                .build());

        ExampleDto example = ExampleDto.builder()
                .title("Medical Diagnostic Test Walkthrough")
                .scenario("Disease Prevalence P(D) = 1%, Test Sensitivity P(+|D) = 99%, False Positive Rate P(+|¬D) = 5%")
                .stepByStep(List.of(
                        "Prior: P(D) = 0.01, P(¬D) = 0.99.",
                        "Total Evidence P(+): P(+|D)·P(D) + P(+|¬D)·P(¬D) = (0.99 × 0.01) + (0.05 × 0.99) = 0.0099 + 0.0495 = 0.0594.",
                        "Posterior P(D|+): [0.99 × 0.01] / 0.0594 = 0.0099 / 0.0594 ≈ 16.67%."
                ))
                .outputOrResult("Despite a 99% accurate test, a positive test result only gives a ~16.7% true probability of having the rare disease.")
                .takeaway("Prior probability (base rate) heavily dominates posterior outcomes when the event is rare.")
                .build();

        FormulaDto formula = FormulaDto.builder()
                .title("Bayes' Theorem Formula & Total Probability Expansion")
                .expression("P(A|B) = [ P(B|A) · P(A) ] / P(B)  =  [ P(B|A) · P(A) ] / [ P(B|A)·P(A) + P(B|¬A)·P(¬A) ]")
                .explanation("Calculates posterior conditional probability by multiplying prior beliefs by the likelihood of observed evidence.")
                .build();

        return PageContentDto.builder()
                .documentTitle("Bayes' Theorem")
                .pageNumber(pageNumber)
                .totalPages(totalPages)
                .topicTitle("Bayes' Theorem")
                .topicSubtitle("Conditional Probability, Prior/Posterior Inference & Diagnostic Applications")
                .categoryBadge("Probability & Statistics ★★★")
                .difficultyLevel(difficulty)
                .pagePartTitle(totalPages > 1 ? ("Part " + pageNumber + " of " + totalPages + ": Conditional Probability") : "Complete Mathematical Notes")
                .definition("Bayes' Theorem is a fundamental theorem in probability calculus that calculates the conditional probability of an event given prior odds and new empirical evidence.")
                .mainIdea("Update existing beliefs (Prior) in light of newly observed data (Evidence) to reach an updated belief (Posterior).")
                .simpleExplanation("If you see wet grass, Bayes' theorem lets you calculate whether it rained versus someone using a sprinkler, taking into account how often it rains in your town.")
                .sections(sections)
                .diagram(diagram)
                .example(example)
                .formula(formula)
                .advantages(List.of("Provides formal mathematical framework for Bayesian statistical inference", "Powers spam filters, medical diagnostics, and Bayesian neural networks"))
                .limitations(List.of("Subjective choice of prior distribution can bias results if data is sparse", "Computing evidence denominator P(B) is intractable in high dimensions (solved via MCMC)"))
                .keyPoints(List.of(
                        KeyPointDto.builder().point("Base Rate Fallacy: Ignoring prior P(A) leads to severe miscalculations in medical and legal tests.").starred(true).category("Pitfall").build(),
                        KeyPointDto.builder().point("Posterior ∝ Likelihood × Prior.").starred(true).category("Proportionality").build()
                ))
                .examTips(List.of(
                        ExamTipDto.builder()
                                .tip("High-Yield Exam Check: Always write out the expansion of P(B) in the denominator using the Law of Total Probability before plugging in values.")
                                .commonMistake("Confusing P(A|B) with P(B|A). They are rarely equal!")
                                .mnemonic("Posterior = (Likelihood × Prior) / Evidence")
                                .build()
                ))
                .continuesOnNextPage(pageNumber < totalPages)
                .isContinuation(pageNumber > 1)
                .layoutHint("handwritten-part1")
                .styleTheme(style)
                .build();
    }

    private PageContentDto generateGeneralMathContent(
            String topic,
            String overallPrompt,
            int pageNumber,
            int totalPages,
            String style,
            String audience,
            String difficulty,
            String focusArea) {

        DiagramDataDto diagram = diagramEngine.generateDiagram(topic, "formula-math", overallPrompt);

        List<SectionDto> sections = new ArrayList<>();
        sections.add(SectionDto.builder()
                .heading("1. Mathematical Definition & Formal Theorem")
                .content(topic + " establishes rigorous mathematical relationships, algebraic properties, and analytical transformations.")
                .badge("Mathematical Foundations")
                .bulletPoints(List.of(
                        "Axiomatic Foundations: Definitions, domain constraints, and range bounds",
                        "Analytical Properties: Linearity, continuity, symmetry, and differentiability",
                        "Transformation Space: Mapping between vector spaces or geometric coordinates"
                ))
                .highlights(List.of("Axiomatic definition", "Analytical properties", "Exact derivation"))
                .build());

        FormulaDto formula = FormulaDto.builder()
                .title("Formal Mathematical Equation")
                .expression("Analytical formulation and identities defining " + topic)
                .explanation("Rigorous mathematical representation with defined variable domains.")
                .build();

        return PageContentDto.builder()
                .documentTitle(topic)
                .pageNumber(pageNumber)
                .totalPages(totalPages)
                .topicTitle(topic)
                .topicSubtitle(focusArea != null ? focusArea : "Analytical Formulations & Mathematical Proofs")
                .categoryBadge("Pure & Applied Mathematics")
                .difficultyLevel(difficulty)
                .pagePartTitle(totalPages > 1 ? ("Part " + pageNumber + " of " + totalPages + ": Conceptual Foundations") : "Complete Mathematics Notes")
                .definition(topic + " is a formal mathematical concept characterized by exact axioms, logical derivations, and computational applications.")
                .mainIdea("Understand the core theorems, governing identities, and algebraic relationships.")
                .simpleExplanation("In mathematics, " + topic + " provides exact equations and formulas to model and solve quantitative problems.")
                .sections(sections)
                .diagram(diagram)
                .formula(formula)
                .keyPoints(List.of(
                        KeyPointDto.builder().point("Always specify the domain and boundary conditions under which the theorem holds.").starred(true).category("Condition").build()
                ))
                .examTips(List.of(
                        ExamTipDto.builder().tip("State formal definitions clearly, write step-by-step algebraic derivations, and box final numerical answers.").mnemonic("Theorem ➔ Derivation ➔ Worked Example").build()
                ))
                .continuesOnNextPage(pageNumber < totalPages)
                .isContinuation(pageNumber > 1)
                .layoutHint("handwritten-part1")
                .styleTheme(style)
                .build();
    }
}
