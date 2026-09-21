package com.visualnotes.ai.strategy;

import com.visualnotes.diagram.DiagramEngine;
import com.visualnotes.dto.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AiMlContentStrategy {

    private final DiagramEngine diagramEngine;

    public AiMlContentStrategy(DiagramEngine diagramEngine) {
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

        if (lower.contains("gradient descent")) {
            return generateGradientDescentContent(topic, overallPrompt, pageNumber, totalPages, style, audience, difficulty);
        } else {
            return generateGeneralAiMlContent(topic, overallPrompt, pageNumber, totalPages, style, audience, difficulty, focusArea);
        }
    }

    private PageContentDto generateGradientDescentContent(
            String topic,
            String overallPrompt,
            int pageNumber,
            int totalPages,
            String style,
            String audience,
            String difficulty) {

        DiagramDataDto diagram = diagramEngine.generateDiagram("Gradient Descent", "neural-network", overallPrompt);

        List<SectionDto> sections = new ArrayList<>();

        sections.add(SectionDto.builder()
                .heading("1. Mathematical Principle & Optimization Dynamics")
                .content("Gradient Descent is a first-order iterative optimization algorithm used to find the local minimum of a differentiable loss function L(θ):")
                .badge("Iterative Optimization")
                .bulletPoints(List.of(
                        "Gradient Vector ∇L(θ): Vector of partial derivatives pointing in the direction of steepest ASCENT.",
                        "Update Rule: Move parameter θ in the OPPOSITE direction of the gradient: θ := θ - η · ∇L(θ).",
                        "Learning Rate (η): Hyperparameter scaling the step size (too high causes divergence/oscillation; too low causes slow convergence)."
                ))
                .highlights(List.of("θ := θ - η · ∇L(θ)", "Steepest descent direction", "Learning rate tuning"))
                .build());

        sections.add(SectionDto.builder()
                .heading("2. Variants: Batch vs Stochastic (SGD) vs Mini-Batch")
                .content("Depending on the amount of training data used per gradient update step:")
                .badge("Algorithm Variants")
                .bulletPoints(List.of(
                        "Batch GD: Uses the ENTIRE training dataset per step (exact gradient, but memory intensive and slow for big data).",
                        "Stochastic GD (SGD): Uses 1 random sample per step (fast, noisy updates help escape local minima, but oscillates around minimum).",
                        "Mini-Batch GD: Uses small batches (e.g. 32, 64, 128 samples) - industry standard combining vectorization efficiency with SGD exploration."
                ))
                .highlights(List.of("Batch GD (N samples)", "SGD (1 sample)", "Mini-Batch GD (32-256 samples)"))
                .build());

        ExampleDto example = ExampleDto.builder()
                .title("1D Parameter Walkthrough")
                .scenario("Minimizing Quadratic Loss: L(θ) = θ², with Initial θ₀ = 4.0, Learning Rate η = 0.1")
                .stepByStep(List.of(
                        "Derivative: dL/dθ = 2θ.",
                        "Step 1: ∇L(θ₀) = 2(4.0) = 8.0 ⟹ θ₁ = 4.0 - 0.1(8.0) = 3.2.",
                        "Step 2: ∇L(θ₁) = 2(3.2) = 6.4 ⟹ θ₂ = 3.2 - 0.1(6.4) = 2.56.",
                        "Step 3: After k iterations, θ converges exponentially toward optimal minimum θ* = 0."
                ))
                .outputOrResult("Converged to global minimum θ* = 0 with zero gradient (∇L(0) = 0).")
                .takeaway("Gradient magnitude shrinks automatically as the parameter approaches a flat stationary minimum.")
                .build();

        FormulaDto formula = FormulaDto.builder()
                .title("Gradient Descent Parameter Update Equations")
                .expression("θ_{t+1} = θ_t - η · ∇_θ L(θ_t)  =  θ_t - η · (1/m) ∑_{i=1}^m ∇_θ ℓ(h_θ(x^{(i)}), y^{(i)})")
                .explanation("θ represents model weight parameters, η is the learning rate, L(θ) is the total loss function, and m is the batch size.")
                .variables(List.of(
                        FormulaVariableDto.builder().symbol("θ").description("Model parameters / weights vector").build(),
                        FormulaVariableDto.builder().symbol("η (eta)").description("Learning rate / step-size hyperparameter").build(),
                        FormulaVariableDto.builder().symbol("∇L(θ)").description("Gradient vector containing partial derivatives [∂L/∂θ₁, ∂L/∂θ₂, ...]").build()
                ))
                .build();

        return PageContentDto.builder()
                .documentTitle("Gradient Descent Optimization")
                .pageNumber(pageNumber)
                .totalPages(totalPages)
                .topicTitle("Gradient Descent Optimization")
                .topicSubtitle("Mathematical Formulation, Loss Landscapes & Mini-Batch Dynamics")
                .categoryBadge("Machine Learning & Deep Learning ★★★")
                .difficultyLevel(difficulty)
                .pagePartTitle(totalPages > 1 ? ("Part " + pageNumber + " of " + totalPages + ": Mathematical Formulation & Variants") : "Complete Machine Learning Notes")
                .definition("Gradient Descent is a first-order iterative mathematical optimization algorithm used to minimize differentiable objective and loss functions by taking steps proportional to the negative gradient.")
                .mainIdea("Follow the downward slope of the multi-dimensional loss surface step-by-step until reaching the lowest point (minimum error).")
                .simpleExplanation("Imagine standing blindfolded on a foggy mountain and wanting to reach the valley bottom. You feel the ground with your foot to find the steepest downhill direction, take a step, and repeat.")
                .sections(sections)
                .diagram(diagram)
                .example(example)
                .formula(formula)
                .advantages(List.of(
                        "Scales efficiently to millions of parameters (unlike matrix inversion O(n³) in Normal Equation).",
                        "Forms the universal training backbone for deep neural networks, transformers, and CNNs."
                ))
                .limitations(List.of(
                        "Can get trapped in suboptimal local minima or saddle points in non-convex loss surfaces.",
                        "Sensitive to learning rate choice (addressed by Adam, RMSprop, and Momentum optimizers)."
                ))
                .keyPoints(List.of(
                        KeyPointDto.builder().point("Gradient always points UPHILL; negative gradient points DOWNHILL.").starred(true).category("Direction").build(),
                        KeyPointDto.builder().point("Convex functions (like Linear Regression MSE) guarantee convergence to the global minimum.").starred(true).category("Convexity").build(),
                        KeyPointDto.builder().point("Adaptive optimizers (Adam) maintain running averages of first and second gradient moments.").starred(true).category("Modern ML").build()
                ))
                .examTips(List.of(
                        ExamTipDto.builder()
                                .tip("In exams, always draw the parabolic bowl loss surface with step vectors demonstrating learning rate behavior (overshooting vs undershooting).")
                                .commonMistake("Forgetting the negative sign in the update rule θ := θ - η∇L (a plus sign would cause Gradient Ascent!).")
                                .mnemonic("Slope Down: Subtract the Gradient (θ_new = θ_old - η · ∇Loss)")
                                .build()
                ))
                .quickTakeaways(List.of(
                        "Core Rule: θ := θ - η · ∇L(θ)",
                        "High η: Overshoots & Diverges | Low η: Crawls & Takes Forever",
                        "Standard Modern Practice: Mini-Batch SGD with Adam Optimizer"
                ))
                .continuesOnNextPage(pageNumber < totalPages)
                .isContinuation(pageNumber > 1)
                .layoutHint("handwritten-part1")
                .styleTheme(style)
                .build();
    }

    private PageContentDto generateGeneralAiMlContent(
            String topic,
            String overallPrompt,
            int pageNumber,
            int totalPages,
            String style,
            String audience,
            String difficulty,
            String focusArea) {

        DiagramDataDto diagram = diagramEngine.generateDiagram(topic, "neural-network", overallPrompt);

        List<SectionDto> sections = new ArrayList<>();
        sections.add(SectionDto.builder()
                .heading("1. Machine Learning Architecture & Loss Formulation")
                .content(topic + " forms a core methodology in statistical learning, artificial intelligence, and deep neural representations.")
                .badge("AI / ML Foundations")
                .bulletPoints(List.of(
                        "Objective Formulation: Minimizing empirical risk over training distributions",
                        "Model Parameterization: Weights, biases, activation functions, and embeddings",
                        "Generalization: Preventing overfitting via regularization (L1/L2, Dropout, Batch Normalization)"
                ))
                .highlights(List.of("Empirical Risk", "Generalization", "Regularization"))
                .build());

        return PageContentDto.builder()
                .documentTitle(topic)
                .pageNumber(pageNumber)
                .totalPages(totalPages)
                .topicTitle(topic)
                .topicSubtitle(focusArea != null ? focusArea : "Machine Learning Theory, Architectures & Optimization")
                .categoryBadge("AI & Data Science")
                .difficultyLevel(difficulty)
                .pagePartTitle(totalPages > 1 ? ("Part " + pageNumber + " of " + totalPages + ": Theoretical Foundations") : "Complete AI/ML Notes")
                .definition(topic + " is a foundational machine learning concept enabling autonomous pattern recognition, optimization, and decision making.")
                .mainIdea("Learn representations from data through continuous mathematical optimization and loss minimization.")
                .simpleExplanation("In AI and ML, " + topic + " teaches computers how to recognize patterns and make accurate predictions from data.")
                .sections(sections)
                .diagram(diagram)
                .keyPoints(List.of(
                        KeyPointDto.builder().point("Generalization on unseen test data is the true metric of model quality.").starred(true).category("Principle").build()
                ))
                .examTips(List.of(
                        ExamTipDto.builder().tip("Always state the loss function, write the parameter update rule, and specify inductive biases.").mnemonic("Data ➔ Architecture ➔ Loss ➔ Optimization").build()
                ))
                .continuesOnNextPage(pageNumber < totalPages)
                .isContinuation(pageNumber > 1)
                .layoutHint("handwritten-part1")
                .styleTheme(style)
                .build();
    }
}
