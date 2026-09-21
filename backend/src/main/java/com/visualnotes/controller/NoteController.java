package com.visualnotes.controller;

import com.visualnotes.dto.*;
import com.visualnotes.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<PromptAnalysisResponse> analyzePrompt(@Valid @RequestBody AnalyzePromptRequest request) {
        return ResponseEntity.ok(noteService.analyzePrompt(request));
    }

    @PostMapping("/plan")
    public ResponseEntity<PagePlanDto> planPages(@Valid @RequestBody PlanPagesRequest request) {
        return ResponseEntity.ok(noteService.planPages(request));
    }

    @PostMapping("/generate")
    public ResponseEntity<NoteDocumentDto> generateNotes(@Valid @RequestBody GenerateNotesRequest request) {
        return ResponseEntity.ok(noteService.generateNotes(request));
    }

    @PostMapping("/{id}/regenerate-page")
    public ResponseEntity<NotePageDto> regeneratePage(
            @PathVariable Long id,
            @Valid @RequestBody RegeneratePageRequest request) {
        return ResponseEntity.ok(noteService.regenerateSinglePage(id, request));
    }

    @PutMapping("/{id}/update-page")
    public ResponseEntity<NotePageDto> updatePage(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePageRequest request) {
        return ResponseEntity.ok(noteService.updatePageContent(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteDocumentDto> getDocumentById(@PathVariable Long id) {
        return ResponseEntity.ok(noteService.getDocumentById(id));
    }

    @GetMapping
    public ResponseEntity<List<NoteDocumentDto>> getRecentDocuments(@RequestParam(required = false) String search) {
        return ResponseEntity.ok(noteService.getRecentDocuments(search));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteDocument(@PathVariable Long id) {
        noteService.deleteDocument(id);
        return ResponseEntity.ok(Map.of("message", "Document deleted successfully", "id", id));
    }

    @PatchMapping("/{id}/rename")
    public ResponseEntity<NoteDocumentDto> renameDocument(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String newTitle = body.get("title");
        if (newTitle == null || newTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be blank");
        }
        return ResponseEntity.ok(noteService.renameDocument(id, newTitle));
    }
}
