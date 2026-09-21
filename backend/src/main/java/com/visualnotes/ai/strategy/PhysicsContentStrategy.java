package com.visualnotes.ai.strategy;

import com.visualnotes.diagram.DiagramEngine;
import com.visualnotes.dto.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PhysicsContentStrategy {

    private final DiagramEngine diagramEngine;

    public PhysicsContentStrategy(DiagramEngine diagramEngine) {
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

        if (lower.contains("newton") || lower.contains("laws of motion") || lower.contains("law of motion")) {
            return generateNewtonLawsContent(topic, overallPrompt, pageNumber, totalPages, style, audience, difficulty);
        } else if (lower.contains("ohm") || lower.contains("current") || lower.contains("voltage") || lower.contains("resistance")) {
            return generateOhmsLawContent(topic, overallPrompt, pageNumber, totalPages, style, audience, difficulty);
        } else {
            return generateGeneralPhysicsContent(topic, overallPrompt, pageNumber, totalPages, style, audience, difficulty, focusArea);
        }
    }

    private PageContentDto generateNewtonLawsContent(
            String topic,
            String overallPrompt,
            int pageNumber,
            int totalPages,
            String style,
            String audience,
            String difficulty) {

        DiagramDataDto diagram = diagramEngine.generateDiagram("Newton's 3 Laws of Motion", "physics-diagram", overallPrompt);

        List<SectionDto> sections = new ArrayList<>();

        if (totalPages == 1 || pageNumber == 1) {
            // Law 1: Inertia
            sections.add(SectionDto.builder()
                    .heading("1. First Law of Motion (Law of Inertia)")
                    .content("An object remains at rest or in uniform motion along a straight line unless acted upon by a non-zero external resultant force.")
                    .badge("Inertia & Equilibrium")
                    .bulletPoints(List.of(
                            "Physical Vector Equation: Σ F⃗ = 0 ⟹ v⃗ = constant, a⃗ = 0",
                            "Inertia is directly proportional to mass (m): heavier objects possess greater resistance to velocity changes.",
                            "Defines Inertial Reference Frames (frames moving with constant velocity where Newton's laws hold without pseudo-forces)."
                    ))
                    .highlights(List.of("Σ F⃗ = 0 ⟹ v⃗ = const", "Inertia ∝ Mass", "Inertial Reference Frame"))
                    .build());

            // Law 2: Force & Momentum
            sections.add(SectionDto.builder()
                    .heading("2. Second Law of Motion (Fundamental Force Law)")
                    .content("The rate of change of linear momentum (p⃗) is directly proportional to the applied net external force and occurs in the direction of the force.")
                    .badge("Vector Dynamics")
                    .bulletPoints(List.of(
                            "Vector Formulation: F⃗_net = dp⃗/dt = d(m·v⃗)/dt",
                            "Constant Mass System: F⃗_net = m · a⃗ (where a⃗ = d²r⃗/dt²)",
                            "Orthogonal Component Form: Σ F_x = m·a_x, Σ F_y = m·a_y, Σ F_z = m·a_z",
                            "SI Unit of Force: 1 Newton (N) = 1 kg·m/s² = 10⁵ dynes (CGS)"
                    ))
                    .highlights(List.of("F⃗_net = m·a⃗", "Σ F_x = m·a_x", "1 N = 1 kg·m/s²"))
                    .build());

            // Law 3: Action-Reaction
            sections.add(SectionDto.builder()
                    .heading("3. Third Law of Motion (Action-Reaction Pairs)")
                    .content("To every action, there is always an equal and opposite reaction. Forces ALWAYS occur in matched pairs acting on TWO DIFFERENT interacting bodies.")
                    .badge("Force Interactions")
                    .bulletPoints(List.of(
                            "Vector Symmetry: F⃗_AB = - F⃗_BA (Force exerted by body B on body A is equal in magnitude and opposite in direction to force by body A on body B).",
                            "Crucial Principle: Action and reaction forces NEVER act on the same body; therefore, they NEVER cancel each other out.",
                            "Simultaneous Occurrence: No time lag exists between action and reaction."
                    ))
                    .highlights(List.of("F⃗_AB = - F⃗_BA", "Acts on TWO DIFFERENT bodies", "Never cancel each other"))
                    .build());
        }

        // Everyday / Real-World Physical Examples
        ExampleDto example = ExampleDto.builder()
                .title("Everyday Physical Applications & Observations")
                .scenario("Real-World Demonstrations of Newton's Laws in Daily Life")
                .stepByStep(List.of(
                        "1st Law (Inertia): When a moving bus brakes abruptly, standing passengers lurch forward because their upper bodies maintain forward inertia.",
                        "2nd Law (Force = ma): Pushing an empty supermarket cart produces large acceleration; pushing a heavily loaded cart with the same force produces much smaller acceleration (a = F/m).",
                        "3rd Law (Action-Reaction): Rocket propulsion pushes hot exhaust gases downward (Action: F_rocket-on-gas); the expanding gas pushes the rocket upward into space (Reaction: F_gas-on-rocket)."
                ))
                .outputOrResult("Newtonian mechanics accurately predicts motion for macroscopic bodies at non-relativistic velocities (v << c).")
                .takeaway("Every mechanical interaction obeys conservation of momentum derived from Newton's 3rd Law.")
                .build();

        // Formula / Derivation Box with Vector Equations
        FormulaDto formula = FormulaDto.builder()
                .title("Complete Physical Vector Equations & SI Units")
                .expression("1st Law: Σ F⃗ = 0  ⟹  v⃗ = const  |  2nd Law: F⃗_net = m·a⃗ = dp⃗/dt  |  3rd Law: F⃗_AB = - F⃗_BA")
                .explanation("F⃗ represents vector force (Newtons, N), m is scalar mass (kg), a⃗ is vector acceleration (m/s²), and p⃗ = m·v⃗ is linear momentum (kg·m/s).")
                .variables(List.of(
                        FormulaVariableDto.builder().symbol("F⃗_net").description("Net resultant external vector force acting on the body (N = kg·m/s²)").build(),
                        FormulaVariableDto.builder().symbol("m").description("Inertial mass of the object (kg)").build(),
                        FormulaVariableDto.builder().symbol("a⃗").description("Linear vector acceleration (m/s²)").build(),
                        FormulaVariableDto.builder().symbol("p⃗").description("Linear momentum vector: p⃗ = m·v⃗ (kg·m/s)").build()
                ))
                .build();

        return PageContentDto.builder()
                .documentTitle("Newton's 3 Laws of Motion")
                .pageNumber(pageNumber)
                .totalPages(totalPages)
                .topicTitle("Newton's 3 Laws of Motion")
                .topicSubtitle("Classical Dynamics, Vector Equations & Everyday Physical Examples")
                .categoryBadge("Classical Mechanics ★★★")
                .difficultyLevel(difficulty)
                .pagePartTitle(totalPages > 1 ? ("Part " + pageNumber + " of " + totalPages + ": Laws, Vectors & Applications") : "Complete Classical Physics Study Notes")
                .definition("Newton's Laws of Motion are three fundamental physical laws that establish the mathematical relationship between the forces acting on a body and its resulting motion.")
                .mainIdea("Force causes acceleration (change in momentum), not velocity; in the absence of net external force, velocity remains constant.")
                .simpleExplanation("1st Law: Things keep doing what they are doing. 2nd Law: Heavier things need more push to speed up (F = ma). 3rd Law: Push anything, and it pushes back equally hard in reverse.")
                .sections(sections)
                .diagram(diagram)
                .example(example)
                .formula(formula)
                .advantages(List.of(
                        "Provides the complete mathematical foundation for engineering, orbital mechanics, and kinematics.",
                        "Enables precise trajectory calculation for satellites, vehicles, and structures.",
                        "Directly implies Conservation of Linear Momentum in isolated systems."
                ))
                .limitations(List.of(
                        "Breaks down at speeds approaching the speed of light (Special Relativity applies: v ≈ c).",
                        "Inapplicable at atomic and subatomic quantum scales (Quantum Mechanics applies)."
                ))
                .keyPoints(List.of(
                        KeyPointDto.builder().point("Mass is a measure of an object's inertia (resistance to acceleration).").starred(true).category("Law 1").build(),
                        KeyPointDto.builder().point("F = ma is a vector equation: F_x = m·a_x and F_y = m·a_y are resolved independently.").starred(true).category("Law 2").build(),
                        KeyPointDto.builder().point("Action-Reaction pairs act on TWO DIFFERENT bodies, so they never cancel in a Free Body Diagram.").starred(true).category("Law 3").build()
                ))
                .examTips(List.of(
                        ExamTipDto.builder()
                                .tip("High-Yield Exam Rule: Always draw a clean Free Body Diagram (FBD) with vector arrows before setting up Σ F_x = m·a_x and Σ F_y = m·a_y.")
                                .commonMistake("Claiming Normal force (N) and Gravity (W = mg) are action-reaction pairs. Both act on the SAME body; action-reaction pairs must act on DIFFERENT bodies!")
                                .mnemonic("1: Inertia (ΣF=0) ➔ 2: Formula (F=ma) ➔ 3: Dual Forces (F_AB = -F_BA)")
                                .build()
                ))
                .quickTakeaways(List.of(
                        "1st Law: Inertia (No net force = Constant velocity)",
                        "2nd Law: Dynamics (Net Force = Mass × Acceleration)",
                        "3rd Law: Interaction (Equal magnitude, Opposite direction, Different bodies)"
                ))
                .continuesOnNextPage(pageNumber < totalPages)
                .isContinuation(pageNumber > 1)
                .layoutHint("handwritten-part1")
                .styleTheme(style)
                .build();
    }

    private PageContentDto generateOhmsLawContent(
            String topic,
            String overallPrompt,
            int pageNumber,
            int totalPages,
            String style,
            String audience,
            String difficulty) {

        DiagramDataDto diagram = diagramEngine.generateDiagram("Ohm's Law", "circuit-diagram", overallPrompt);

        List<SectionDto> sections = new ArrayList<>();
        sections.add(SectionDto.builder()
                .heading("1. Physical Principle & Linear Conduction")
                .content("At constant temperature, the electric current flowing through a metallic conductor is directly proportional to the potential difference applied across its ends.")
                .badge("Electrodynamics")
                .bulletPoints(List.of(
                        "Mathematical Law: V ∝ I  ⟹  V = I · R",
                        "Resistance (R): Ratio of voltage to current, measured in Ohms (Ω = V/A).",
                        "Ohmic Conductors: Materials having linear I-V characteristic passing through the origin (copper, aluminium)."
                ))
                .highlights(List.of("V = I · R", "Constant temperature requirement", "Linear I-V curve"))
                .build());

        FormulaDto formula = FormulaDto.builder()
                .title("Ohm's Law & Electrical Power Relations")
                .expression("V = I · R  |  I = V / R  |  R = ρ · (L / A)  |  P = V · I = I² · R = V² / R")
                .explanation("V is potential difference (Volts), I is current (Amperes), R is resistance (Ohms), and P is electrical power (Watts).")
                .build();

        return PageContentDto.builder()
                .documentTitle("Ohm's Law")
                .pageNumber(pageNumber)
                .totalPages(totalPages)
                .topicTitle("Ohm's Law")
                .topicSubtitle("Voltage, Current, Resistance & Electrical Power Relations")
                .categoryBadge("Electrodynamics ★★★")
                .difficultyLevel(difficulty)
                .pagePartTitle("Fundamental Electrical Circuit Principles")
                .definition("Ohm's Law states that the current passing through a conductor between two points is directly proportional to the voltage across the two points, provided physical conditions (temperature, strain) remain constant.")
                .mainIdea("Voltage acts as electrical pressure, pushing electric charge (current) through a resistive material.")
                .simpleExplanation("Think of electricity like water flowing in a pipe. Voltage is the water pressure, Current is the water flow rate, and Resistance is the pipe narrowing.")
                .sections(sections)
                .diagram(diagram)
                .formula(formula)
                .advantages(List.of("Fundamental law for circuit analysis (mesh, nodal, Kirchhoff's laws)", "Allows sizing resistors, power supplies, and safety fuses"))
                .limitations(List.of("Non-ohmic devices (diodes, transistors, vacuum tubes) do not follow V = IR linearly", "Temperature variations alter material resistivity (ρ(T) = ρ₀[1 + αΔT])"))
                .keyPoints(List.of(
                        KeyPointDto.builder().point("Valid strictly for Ohmic conductors at constant temperature.").starred(true).category("Condition").build(),
                        KeyPointDto.builder().point("Power dissipated as Joule heating: P = I²R.").starred(true).category("Power").build()
                ))
                .examTips(List.of(
                        ExamTipDto.builder()
                                .tip("Plot the V-I slope: Slope = ΔV / ΔI = R. For an Ohmic conductor, it is a straight line through origin.")
                                .commonMistake("Applying Ohm's law to semiconductor diodes without dynamic resistance analysis.")
                                .mnemonic("V = I × R (Vir: Voltage = Current × Resistance)")
                                .build()
                ))
                .continuesOnNextPage(pageNumber < totalPages)
                .isContinuation(pageNumber > 1)
                .layoutHint("handwritten-part1")
                .styleTheme(style)
                .build();
    }

    private PageContentDto generateGeneralPhysicsContent(
            String topic,
            String overallPrompt,
            int pageNumber,
            int totalPages,
            String style,
            String audience,
            String difficulty,
            String focusArea) {

        DiagramDataDto diagram = diagramEngine.generateDiagram(topic, "physics-diagram", overallPrompt);

        List<SectionDto> sections = new ArrayList<>();
        sections.add(SectionDto.builder()
                .heading("1. Physical Foundations & Principles")
                .content(topic + " describes fundamental physical interactions governed by conservation laws and mathematical equations.")
                .badge("Physical Principles")
                .bulletPoints(List.of(
                        "Governing Mechanics: Conservation of Energy, Momentum, or Charge",
                        "Physical Variables: Vectors (Magnitude & Direction) vs Scalars",
                        "Boundary Conditions: Constraints imposed by the physical system"
                ))
                .highlights(List.of("Conservation laws", "Vector relationships", "SI Units"))
                .build());

        FormulaDto formula = FormulaDto.builder()
                .title("Governing Equations & SI Dimensional Units")
                .expression("State equation representing the physical laws of " + topic)
                .explanation("Relates fundamental observable physical quantities in standard SI units.")
                .build();

        return PageContentDto.builder()
                .documentTitle(topic)
                .pageNumber(pageNumber)
                .totalPages(totalPages)
                .topicTitle(topic)
                .topicSubtitle(focusArea != null ? focusArea : "Physical Principles, Vector Dynamics & Real-World Applications")
                .categoryBadge("Physics Study Notes")
                .difficultyLevel(difficulty)
                .pagePartTitle(totalPages > 1 ? ("Part " + pageNumber + " of " + totalPages + ": Conceptual Foundations") : "Complete Physics Notes")
                .definition(topic + " is a core physical concept establishing fundamental relationships between observable quantities, forces, and energy transformations.")
                .mainIdea("Physical systems evolve according to deterministic governing laws subject to initial conditions and conservation constraints.")
                .simpleExplanation("In physics, " + topic + " explains how forces, energy, and matter interact in our physical world.")
                .sections(sections)
                .diagram(diagram)
                .formula(formula)
                .keyPoints(List.of(
                        KeyPointDto.builder().point("Always verify SI units and vector coordinate axes before solving.").starred(true).category("Methodology").build()
                ))
                .examTips(List.of(
                        ExamTipDto.builder().tip("Always state governing physical laws, draw labeled vector diagrams, and include SI units with every numerical answer.").mnemonic("Law ➔ Diagram ➔ Formula ➔ Units").build()
                ))
                .continuesOnNextPage(pageNumber < totalPages)
                .isContinuation(pageNumber > 1)
                .layoutHint("handwritten-part1")
                .styleTheme(style)
                .build();
    }
}
