package com.visualnotes.ai.strategy;

import com.visualnotes.ai.domain.DomainType;
import com.visualnotes.diagram.DiagramEngine;
import com.visualnotes.dto.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OperatingSystemsContentStrategy {

    private final DiagramEngine diagramEngine;

    public OperatingSystemsContentStrategy(DiagramEngine diagramEngine) {
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

        if (lower.contains("scheduling") || lower.contains("fcfs") || lower.contains("sjf") || lower.contains("round robin") || lower.contains("cpu scheduling")) {
            return generateCpuSchedulingContent(topic, overallPrompt, pageNumber, totalPages, style, audience, difficulty);
        } else {
            return generateGeneralOsContent(topic, overallPrompt, pageNumber, totalPages, style, audience, difficulty);
        }
    }

    private PageContentDto generateCpuSchedulingContent(
            String topic,
            String overallPrompt,
            int pageNumber,
            int totalPages,
            String style,
            String audience,
            String difficulty) {

        DiagramDataDto diagram = diagramEngine.generateDiagram("CPU Scheduling Gantt Chart", "os-gantt-chart", overallPrompt);

        List<SectionDto> sections = new ArrayList<>();

        sections.add(SectionDto.builder()
                .heading("1. Core Scheduling Algorithms")
                .content("CPU scheduling determines which ready process in memory receives CPU execution time:")
                .badge("Algorithms")
                .bulletPoints(List.of(
                        "FCFS (First-Come, First-Served): Non-preemptive. Simple FIFO queue. Suffers from Convoy Effect (short processes wait behind long ones).",
                        "SJF (Shortest Job First): Optimal minimum average waiting time. Non-preemptive or Preemptive (SRTF - Shortest Remaining Time First).",
                        "Round Robin (RR): Preemptive. Each process gets a fixed Time Quantum (q). Ideal for time-sharing systems.",
                        "Priority Scheduling: CPU allocated to highest priority. Suffers from Starvation (solved via Aging)."
                ))
                .highlights(List.of("Convoy Effect in FCFS", "SJF is provably optimal for Avg WT", "Round Robin uses Time Quantum", "Aging prevents Starvation"))
                .build());

        // Comparison Table: CPU Scheduling Algorithms
        ComparisonTableDto table = ComparisonTableDto.builder()
                .title("Comparison of CPU Scheduling Algorithms")
                .headers(List.of("Algorithm", "Mode", "Optimization Metric", "Key Advantages", "Disadvantages / Traps"))
                .rows(List.of(
                        List.of("FCFS", "Non-preemptive", "FIFO Arrival", "Zero overhead, easy to implement", "Convoy Effect, high average waiting time"),
                        List.of("SJF / SRTF", "Both (Preemptive = SRTF)", "Burst Time", "Provably minimum average waiting time", "Requires predicting future CPU burst, Starvation"),
                        List.of("Round Robin (RR)", "Preemptive", "Time Quantum (q)", "Fair CPU share, excellent response time", "High context-switching overhead if q is too small"),
                        List.of("Priority", "Both", "Priority integer", "Critical tasks execute first", "Indefinite blocking / starvation of low-priority tasks")
                ))
                .conclusion("Design Choice: If Time Quantum 'q' in Round Robin is very large, RR becomes FCFS; if 'q' is extremely small, context-switch overhead dominates.")
                .build();

        // Scheduling Performance Formulas
        FormulaDto formula = FormulaDto.builder()
                .title("Evaluation Formulas")
                .expression("\\text{TAT} = \\text{Completion Time} - \\text{Arrival Time}, \\quad \\text{WT} = \\text{Turnaround Time} - \\text{Burst Time}")
                .explanation("Turnaround Time (TAT) = CT - AT. Waiting Time (WT) = TAT - BT. Response Time (RT) = Time of first CPU allocation - AT.")
                .build();

        // Worked Example
        ExampleDto example = ExampleDto.builder()
                .title("Numerical Trace: FCFS vs SJF")
                .scenario("Processes P1 (BT=6), P2 (BT=2), P3 (BT=8) all arriving at time 0.")
                .input("P1 (BT=6), P2 (BT=2), P3 (BT=8)")
                .stepByStep(List.of(
                        "FCFS Order: P1 (0..6) ➔ P2 (6..8) ➔ P3 (8..16). Waiting Times: P1=0, P2=6, P3=8. Avg WT = (0+6+8)/3 = 4.67 ms.",
                        "SJF Order: P2 (0..2) ➔ P1 (2..8) ➔ P3 (8..16). Waiting Times: P2=0, P1=2, P3=8. Avg WT = (0+2+8)/3 = 3.33 ms."
                ))
                .outputOrResult("SJF reduces average waiting time from 4.67 ms to 3.33 ms.")
                .takeaway("SJF strictly minimizes average waiting time by prioritizing shorter bursts.")
                .build();

        List<ExamTipDto> examTips = List.of(
                ExamTipDto.builder()
                        .tip("Exam Calculation Step: Always construct the Gantt Chart first. Mark 0 at start and add burst times sequentially before calculating CT, TAT, and WT.")
                        .commonMistake("Confusing Preemptive SJF (SRTF) with Non-preemptive SJF when processes arrive at different times.")
                        .mnemonic("TAT = CT - AT | WT = TAT - BT (Clean subtraction sequence)")
                        .build()
        );

        return PageContentDto.builder()
                .documentTitle("Operating Systems — CPU Scheduling")
                .pageNumber(pageNumber)
                .totalPages(totalPages)
                .topicTitle("Process & CPU Scheduling Algorithms")
                .topicSubtitle("FCFS, SJF, Round Robin, Priority & Performance Evaluation")
                .categoryBadge("Operating Systems")
                .difficultyLevel("Core University Standard")
                .domain(DomainType.OPERATING_SYSTEMS.name())
                .subdomain("Process Management & Concurrency")
                .definition("CPU Scheduling is the process of allocating the CPU core to ready processes in memory to maximize CPU utilization, throughput, and minimize response time.")
                .sections(sections)
                .comparisonTable(table)
                .formula(formula)
                .example(example)
                .diagram(diagram)
                .examTips(examTips)
                .keyPoints(List.of(
                        KeyPointDto.builder().point("SJF provides mathematically minimal average waiting time for any set of processes.").starred(true).category("SJF").build(),
                        KeyPointDto.builder().point("Round Robin performance heavily depends on Time Quantum: too large = FCFS, too small = excessive context switching.").starred(true).category("RR").build(),
                        KeyPointDto.builder().point("Aging (gradually increasing priority of waiting processes) prevents indefinite starvation in Priority Scheduling.").starred(true).category("Priority").build()
                ))
                .build();
    }

    private PageContentDto generateGeneralOsContent(
            String topic,
            String overallPrompt,
            int pageNumber,
            int totalPages,
            String style,
            String audience,
            String difficulty) {

        DiagramDataDto diagram = diagramEngine.generateDiagram(topic, "concept-map", overallPrompt);

        List<SectionDto> sections = new ArrayList<>();
        sections.add(SectionDto.builder()
                .heading("1. Operating System Mechanics")
                .content(topic + " coordinates hardware resources, manages concurrency, and enforces process isolation.")
                .badge("Kernel Architecture")
                .bulletPoints(List.of(
                        "Resource Management: Efficient CPU, memory, and I/O scheduling.",
                        "Synchronization & Safety: Mutexes, semaphores, and race condition prevention.",
                        "Hardware Abstraction: Clean virtual interfaces for physical system hardware."
                ))
                .build());

        return PageContentDto.builder()
                .documentTitle(topic)
                .pageNumber(pageNumber)
                .totalPages(totalPages)
                .topicTitle(topic)
                .categoryBadge("Operating Systems")
                .domain(DomainType.OPERATING_SYSTEMS.name())
                .definition(topic + " is a foundational system-level concept in modern multi-programmed operating systems.")
                .sections(sections)
                .diagram(diagram)
                .build();
    }
}
