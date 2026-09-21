package com.visualnotes.ai.strategy;

import com.visualnotes.diagram.DiagramEngine;
import com.visualnotes.dto.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BiologyContentStrategy {

    private final DiagramEngine diagramEngine;

    public BiologyContentStrategy(DiagramEngine diagramEngine) {
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

        if (lower.contains("photosynthesis")) {
            return generatePhotosynthesisContent(topic, overallPrompt, pageNumber, totalPages, style, audience, difficulty);
        } else {
            return generateGeneralBiologyContent(topic, overallPrompt, pageNumber, totalPages, style, audience, difficulty, focusArea);
        }
    }

    private PageContentDto generatePhotosynthesisContent(
            String topic,
            String overallPrompt,
            int pageNumber,
            int totalPages,
            String style,
            String audience,
            String difficulty) {

        DiagramDataDto diagram = diagramEngine.generateDiagram("Photosynthesis", "science-reaction", overallPrompt);

        List<SectionDto> sections = new ArrayList<>();

        sections.add(SectionDto.builder()
                .heading("1. Two Interconnected Biochemical Stages")
                .content("Photosynthesis converts solar electromagnetic radiation into stored chemical energy inside plant chloroplasts through two complementary phases:")
                .badge("Chloroplast Physiology")
                .bulletPoints(List.of(
                        "Light-Dependent Reactions (Thylakoid Membrane): Photons excite chlorophyll electrons in PS II & PS I, photolyzing H₂O to release O₂ and synthesizing ATP + NADPH.",
                        "Light-Independent Reactions / Calvin Cycle (Stroma): Uses ATP and NADPH to fix atmospheric CO₂ via the enzyme RuBisCO into Glyceraldehyde 3-phosphate (G3P) ➔ Glucose.",
                        "Chlorophyll Pigments: Primary light absorption peaks in blue (~430 nm) and red (~660 nm) wavelengths, reflecting green light."
                ))
                .highlights(List.of("Thylakoid: Light Reactions", "Stroma: Calvin Cycle", "RuBisCO enzyme"))
                .build());

        sections.add(SectionDto.builder()
                .heading("2. Limiting Factors & Biological Significance")
                .content("The rate of photosynthesis obeys Blackman's Principle of Limiting Factors (light intensity, CO₂ concentration, and temperature).")
                .badge("Ecological Role")
                .bulletPoints(List.of(
                        "Primary Producers: Base of all terrestrial and aquatic food chains.",
                        "Atmospheric Regulation: Replenishes oxygen and sequesters carbon dioxide.",
                        "Stored Energy: Forms the origin of all fossil fuels and biomass."
                ))
                .highlights(List.of("Blackman's Limiting Factors", "Primary Food Web Source", "Oxygen Replenishment"))
                .build());

        ExampleDto example = ExampleDto.builder()
                .title("Step-by-Step Biochemical Pathway")
                .scenario("Energy Transformation Cycle in C3 Plants")
                .stepByStep(List.of(
                        "Step 1: Photons hit Photosystem II in thylakoids, splitting H₂O into 2H⁺ + 2e⁻ + ½O₂ (Photolysis).",
                        "Step 2: Electron transport chain pumps protons into thylakoid lumen, driving ATP Synthase to generate ATP.",
                        "Step 3: In the stroma, RuBisCO catalyzes 3 CO₂ + 3 RuBP ➔ 6 3-PGA, subsequently reduced by ATP/NADPH into glucose."
                ))
                .outputOrResult("Net Output: 1 molecule of Glucose (C₆H₁₂O₆) and 6 molecules of Oxygen (6O₂) per 6 CO₂ fixed.")
                .takeaway("Solar radiant energy is stably trapped as covalent bonds in hexose carbohydrates.")
                .build();

        FormulaDto formula = FormulaDto.builder()
                .title("Overall Balanced Photosynthesis Equation")
                .expression("6 CO₂  +  6 H₂O  +  Light Energy  ──[ Chlorophyll / Enzymes ]──▶  C₆H₁₂O₆  +  6 O₂")
                .explanation("6 Carbon Dioxide molecules + 6 Water molecules + Sunlight produce 1 Glucose molecule + 6 Oxygen gas molecules (ΔG°' = +2870 kJ/mol).")
                .variables(List.of(
                        FormulaVariableDto.builder().symbol("6 CO₂").description("Carbon dioxide absorbed from atmosphere via stomata").build(),
                        FormulaVariableDto.builder().symbol("6 H₂O").description("Water drawn from soil via xylem root system").build(),
                        FormulaVariableDto.builder().symbol("C₆H₁₂O₆").description("Glucose hexose sugar used for cellular respiration and cellulose synthesis").build(),
                        FormulaVariableDto.builder().symbol("6 O₂").description("Oxygen gas released into atmosphere as byproduct").build()
                ))
                .build();

        return PageContentDto.builder()
                .documentTitle("Photosynthesis")
                .pageNumber(pageNumber)
                .totalPages(totalPages)
                .topicTitle("Photosynthesis")
                .topicSubtitle("Biochemical Pathways, Chloroplast Ultrastructure & Calvin Cycle")
                .categoryBadge("Plant Physiology ★★★")
                .difficultyLevel(difficulty)
                .pagePartTitle(totalPages > 1 ? ("Part " + pageNumber + " of " + totalPages + ": Chemical Energetics & Organelles") : "Complete Plant Biology Study Notes")
                .definition("Photosynthesis is the endergonic biochemical process by which autotrophic green plants, algae, and cyanobacteria convert light energy into chemical energy stored in glucose molecules.")
                .mainIdea("Sunlight drives the endothermic reduction of carbon dioxide into high-energy carbohydrates, releasing oxygen as a byproduct.")
                .simpleExplanation("Plants act as solar-powered food factories: they drink water through roots, breathe in CO₂ through leaves, and use sunlight to make sugar (food) and oxygen.")
                .sections(sections)
                .diagram(diagram)
                .example(example)
                .formula(formula)
                .advantages(List.of(
                        "Generates virtually all breathable oxygen on planet Earth.",
                        "Provides the organic biomass and nutrition sustaining all heterotrophs."
                ))
                .limitations(List.of(
                        "Photorespiration in C3 plants under high heat/drought reduces efficiency (overcome by C4 and CAM adaptations).",
                        "Dependent on ambient light, water availability, and optimal temperature range (15°C - 35°C)."
                ))
                .keyPoints(List.of(
                        KeyPointDto.builder().point("Light reaction occurs in Thylakoids; Calvin cycle occurs in the Stroma.").starred(true).category("Location").build(),
                        KeyPointDto.builder().point("Oxygen released originates from WATER (H₂O photolysis), NOT from carbon dioxide.").starred(true).category("Exam Invariant").build(),
                        KeyPointDto.builder().point("RuBisCO is the most abundant enzyme on Earth, catalyzing carbon fixation.").starred(true).category("Enzyme").build()
                ))
                .examTips(List.of(
                        ExamTipDto.builder()
                                .tip("High-Yield Exam Favorite: Always highlight that O₂ gas comes from the photolysis of H₂O (proven by Ruben & Kamen isotopic ¹⁸O experiments), NOT CO₂!")
                                .commonMistake("Confusing the sites: Writing that the Calvin cycle happens in the thylakoid (it takes place in the fluid Stroma).")
                                .mnemonic("T-Light, S-Dark (Thylakoid = Light reaction, Stroma = Dark/Calvin cycle)")
                                .build()
                ))
                .quickTakeaways(List.of(
                        "Light Reaction: Thylakoid ➔ Light + H₂O ➔ ATP + NADPH + O₂",
                        "Calvin Cycle: Stroma ➔ CO₂ + ATP + NADPH ➔ Glucose (C₆H₁₂O₆)",
                        "Key Enzyme: RuBisCO fixes CO₂ onto 5-carbon RuBP"
                ))
                .continuesOnNextPage(pageNumber < totalPages)
                .isContinuation(pageNumber > 1)
                .layoutHint("handwritten-part1")
                .styleTheme(style)
                .build();
    }

    private PageContentDto generateGeneralBiologyContent(
            String topic,
            String overallPrompt,
            int pageNumber,
            int totalPages,
            String style,
            String audience,
            String difficulty,
            String focusArea) {

        DiagramDataDto diagram = diagramEngine.generateDiagram(topic, "science-reaction", overallPrompt);

        List<SectionDto> sections = new ArrayList<>();
        sections.add(SectionDto.builder()
                .heading("1. Biological Structure & Cellular Mechanisms")
                .content(topic + " plays a critical role in cellular metabolism, genetic regulation, and anatomical physiology.")
                .badge("Biological Principle")
                .bulletPoints(List.of(
                        "Cellular Localization: Specific organelle, membrane, or tissue site",
                        "Enzymatic Regulation: Catalysts controlling reaction rates and homeostasis",
                        "Evolutionary Role: Adaptation enabling organism survival and reproduction"
                ))
                .highlights(List.of("Homeostasis", "Cellular mechanisms", "Enzyme regulation"))
                .build());

        return PageContentDto.builder()
                .documentTitle(topic)
                .pageNumber(pageNumber)
                .totalPages(totalPages)
                .topicTitle(topic)
                .topicSubtitle(focusArea != null ? focusArea : "Cellular Mechanisms & Biological Systems")
                .categoryBadge("Biological Sciences")
                .difficultyLevel(difficulty)
                .pagePartTitle(totalPages > 1 ? ("Part " + pageNumber + " of " + totalPages + ": Conceptual Overview") : "Complete Biology Notes")
                .definition(topic + " is a foundational biological concept explaining living systems, metabolic processes, and molecular pathways.")
                .mainIdea("Biological systems maintain organized dynamic equilibrium through regulated biochemical pathways.")
                .simpleExplanation("In biology, " + topic + " describes how living organisms function, adapt, and sustain life.")
                .sections(sections)
                .diagram(diagram)
                .keyPoints(List.of(
                        KeyPointDto.builder().point("Structure directly determines biological function.").starred(true).category("Principle").build()
                ))
                .examTips(List.of(
                        ExamTipDto.builder().tip("Draw labeled biological diagrams, name specific organelles/enzymes, and state balanced equations.").mnemonic("Structure ➔ Function ➔ Regulation").build()
                ))
                .continuesOnNextPage(pageNumber < totalPages)
                .isContinuation(pageNumber > 1)
                .layoutHint("handwritten-part1")
                .styleTheme(style)
                .build();
    }
}
