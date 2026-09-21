package com.visualnotes.ai.strategy;

import com.visualnotes.diagram.DiagramEngine;
import com.visualnotes.dto.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class NetworkingContentStrategy {

    private final DiagramEngine diagramEngine;

    public NetworkingContentStrategy(DiagramEngine diagramEngine) {
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

        if (lower.contains("osi") || lower.contains("layer")) {
            return generateOsiContent(topic, overallPrompt, pageNumber, totalPages, style, audience, difficulty);
        } else if (lower.contains("handshake") || lower.contains("tcp")) {
            return generateTcpHandshakeContent(topic, overallPrompt, pageNumber, totalPages, style, audience, difficulty);
        } else {
            return generateGeneralNetworkingContent(topic, overallPrompt, pageNumber, totalPages, style, audience, difficulty, focusArea);
        }
    }

    private PageContentDto generateOsiContent(
            String topic,
            String overallPrompt,
            int pageNumber,
            int totalPages,
            String style,
            String audience,
            String difficulty) {

        DiagramDataDto diagram = diagramEngine.generateDiagram("OSI Model", "layer-stack", overallPrompt);

        List<SectionDto> sections = new ArrayList<>();
        sections.add(SectionDto.builder()
                .heading("1. Architectural Purpose & Encapsulation Stack")
                .content("The Open Systems Interconnection (OSI) reference model is an ISO standard 7-layer architectural framework for telecommunications:")
                .badge("7-Layer Reference Stack")
                .bulletPoints(List.of(
                        "Layer 7 (Application): User-facing network protocols (HTTP, HTTPS, DNS, SMTP, SSH).",
                        "Layer 6 (Presentation): Syntax translation, data encryption/decryption (TLS/SSL), and compression.",
                        "Layer 5 (Session): Manages authentication, session checkpoints, and full/half-duplex dialogue.",
                        "Layer 4 (Transport): End-to-end reliable transmission, port addressing (TCP 3-way handshake, UDP), flow & error control.",
                        "Layer 3 (Network): Logical IP addressing, packet routing across subnetworks (Routers, ICMP, OSPF, BGP).",
                        "Layer 2 (Data Link): Physical MAC addressing, frame framing, error detection (Switches, Ethernet IEEE 802.3).",
                        "Layer 1 (Physical): Raw binary bitstream transmission over electrical, optical, or radio media (Cables, Hubs, Repeaters)."
                ))
                .highlights(List.of("Encapsulation down the stack", "Decapsulation up the stack", "PDU at each tier"))
                .build());

        FormulaDto formula = FormulaDto.builder()
                .title("Protocol Data Unit (PDU) Across OSI Layers")
                .expression("L7-L5: Data / Message  ➔  L4: Segment  ➔  L3: Packet  ➔  L2: Frame  ➔  L1: Bits")
                .explanation("Each layer prepends its protocol header (and trailer at L2) during transmission.")
                .build();

        return PageContentDto.builder()
                .documentTitle("OSI 7-Layer Reference Model")
                .pageNumber(pageNumber)
                .totalPages(totalPages)
                .topicTitle("OSI Reference Model")
                .topicSubtitle("7-Layer Protocol Stack, Encapsulation & Protocol Data Units")
                .categoryBadge("Computer Networks ★★★")
                .difficultyLevel(difficulty)
                .pagePartTitle(totalPages > 1 ? ("Part " + pageNumber + " of " + totalPages + ": 7 Layers & Encapsulation") : "Complete Networking Notes")
                .definition("The Open Systems Interconnection (OSI) model is a conceptual 7-layer framework standardizing network communication protocols.")
                .mainIdea("Modularize network functions so software applications can communicate independently of underlying physical network hardware.")
                .sections(sections)
                .diagram(diagram)
                .formula(formula)
                .advantages(List.of("Standardizes hardware & software vendor compatibility", "Modular debugging at individual layer tiers", "Separates application code from network transmission"))
                .limitations(List.of("Theoretical reference model; TCP/IP 4-layer suite is more widely used in production", "Protocol header encapsulation introduces packet overhead"))
                .keyPoints(List.of(
                        KeyPointDto.builder().point("Router works at Layer 3 (Network); Switch works at Layer 2 (Data Link); Hub works at Layer 1 (Physical).").starred(true).category("Exam Favorite").build(),
                        KeyPointDto.builder().point("Transport layer provides End-to-End communication (TCP/UDP ports).").starred(true).category("Core Role").build()
                ))
                .examTips(List.of(
                        ExamTipDto.builder()
                                .tip("In university and certification exams, always list all 7 layers in exact numerical order with their corresponding PDUs.")
                                .commonMistake("Confusing the direction: Layer 7 is Application (top) down to Layer 1 Physical (bottom).")
                                .mnemonic("All People Seem To Need Data Processing (L7: Application, L6: Presentation, L5: Session, L4: Transport, L3: Network, L2: Data Link, L1: Physical)")
                                .build()
                ))
                .layoutHint("handwritten-part1")
                .styleTheme(style)
                .build();
    }

    private PageContentDto generateTcpHandshakeContent(
            String topic,
            String overallPrompt,
            int pageNumber,
            int totalPages,
            String style,
            String audience,
            String difficulty) {

        DiagramDataDto diagram = diagramEngine.generateDiagram("TCP 3-Way Handshake", "process-steps", overallPrompt);

        List<SectionDto> sections = new ArrayList<>();
        sections.add(SectionDto.builder()
                .heading("1. Connection Establishment Protocol (SYN ➔ SYN-ACK ➔ ACK)")
                .content("TCP uses a 3-way handshake to synchronize sequence numbers and establish a reliable full-duplex byte stream connection:")
                .badge("Connection Lifecycle")
                .bulletPoints(List.of(
                        "Step 1 (SYN): Client picks initial sequence number (ISN = x) and sends [SYN, seq = x] to Server. Client enters SYN-SENT state.",
                        "Step 2 (SYN-ACK): Server acknowledges client seq (ack = x + 1), chooses its own ISN (seq = y), and sends [SYN+ACK, seq = y, ack = x + 1]. Server enters SYN-RCVD state.",
                        "Step 3 (ACK): Client acknowledges server seq (ack = y + 1) with [ACK, seq = x + 1, ack = y + 1]. Connection is now ESTABLISHED."
                ))
                .highlights(List.of("SYN (seq=x)", "SYN-ACK (seq=y, ack=x+1)", "ACK (seq=x+1, ack=y+1)"))
                .build());

        FormulaDto formula = FormulaDto.builder()
                .title("TCP Handshake State Transition Sequence")
                .expression("Client (CLOSED ➔ SYN-SENT ➔ ESTABLISHED)  ⟷  Server (LISTEN ➔ SYN-RCVD ➔ ESTABLISHED)")
                .explanation("Guarantees both endpoints agree on sequence numbers and window buffer sizes before data transmission.")
                .build();

        return PageContentDto.builder()
                .documentTitle("TCP 3-Way Handshake")
                .pageNumber(pageNumber)
                .totalPages(totalPages)
                .topicTitle("TCP 3-Way Handshake")
                .topicSubtitle("Reliable Connection Establishment & Sequence Synchronization")
                .categoryBadge("Transport Layer ★★★")
                .difficultyLevel(difficulty)
                .pagePartTitle("Transport Layer Protocol Mechanics")
                .definition("The TCP 3-Way Handshake is a mechanism used in computer networks to establish a reliable, full-duplex TCP socket connection between a client and server before exchanging application data.")
                .mainIdea("Synchronize sequence numbers and acknowledge buffers bidirectionally to ensure no phantom packets are accepted.")
                .simpleExplanation("Like a phone call: 1. 'Can you hear me?' 2. 'Yes, I can hear you, can you hear me?' 3. 'Yes, I hear you too, let's talk.'")
                .sections(sections)
                .diagram(diagram)
                .formula(formula)
                .keyPoints(List.of(
                        KeyPointDto.builder().point("SYN packets consume 1 sequence number; pure ACKs carrying no payload consume 0 sequence numbers.").starred(true).category("Rule").build()
                ))
                .examTips(List.of(
                        ExamTipDto.builder().tip("Always draw the vertical timeline diagram with SYN, SYN-ACK, and ACK arrows showing exact sequence and acknowledgement numbers.").mnemonic("SYN ➔ SYN-ACK ➔ ACK").build()
                ))
                .continuesOnNextPage(pageNumber < totalPages)
                .isContinuation(pageNumber > 1)
                .layoutHint("handwritten-part1")
                .styleTheme(style)
                .build();
    }

    private PageContentDto generateGeneralNetworkingContent(
            String topic,
            String overallPrompt,
            int pageNumber,
            int totalPages,
            String style,
            String audience,
            String difficulty,
            String focusArea) {

        DiagramDataDto diagram = diagramEngine.generateDiagram(topic, "layer-stack", overallPrompt);

        List<SectionDto> sections = new ArrayList<>();
        sections.add(SectionDto.builder()
                .heading("1. Network Architecture & Protocol Operation")
                .content(topic + " defines standard communication rules, packet formats, and routing behaviors across computer networks.")
                .badge("Networking Principles")
                .bulletPoints(List.of(
                        "Protocol Specification: Header formats, flags, and payload boundaries",
                        "Addressing & Routing: IP routing, port multiplexing, and subnetting",
                        "Reliability & Congestion: Flow control, retransmission timers, and sliding windows"
                ))
                .highlights(List.of("Protocol stack", "Packet headers", "End-to-end delivery"))
                .build());

        return PageContentDto.builder()
                .documentTitle(topic)
                .pageNumber(pageNumber)
                .totalPages(totalPages)
                .topicTitle(topic)
                .topicSubtitle(focusArea != null ? focusArea : "Computer Networking Protocols & Architecture")
                .categoryBadge("Computer Networks")
                .difficultyLevel(difficulty)
                .pagePartTitle(totalPages > 1 ? ("Part " + pageNumber + " of " + totalPages + ": Protocol Overview") : "Complete Networking Notes")
                .definition(topic + " is an essential computer networking concept governing digital communication across interconnected computing systems.")
                .mainIdea("Ensure reliable, interoperable, and secure data transmission across distributed network topologies.")
                .simpleExplanation("In networking, " + topic + " describes how devices connect and transmit data across the internet.")
                .sections(sections)
                .diagram(diagram)
                .keyPoints(List.of(
                        KeyPointDto.builder().point("Network protocols provide standardized abstraction across diverse hardware.").starred(true).category("Principle").build()
                ))
                .examTips(List.of(
                        ExamTipDto.builder().tip("Always mention which OSI/TCP-IP layer the protocol operates on and state its port number.").mnemonic("Layer ➔ PDU ➔ Port ➔ Purpose").build()
                ))
                .continuesOnNextPage(pageNumber < totalPages)
                .isContinuation(pageNumber > 1)
                .layoutHint("handwritten-part1")
                .styleTheme(style)
                .build();
    }
}
