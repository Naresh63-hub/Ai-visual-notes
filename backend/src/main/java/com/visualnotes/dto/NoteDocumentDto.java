package com.visualnotes.dto;

import java.time.LocalDateTime;
import java.util.List;

public class NoteDocumentDto {
    private Long id;
    private Long userId;
    private String title;
    private String originalPrompt;
    private Integer pageCount;
    private String style;
    private String audience;
    private String difficulty;
    private String status;
    private List<NotePageDto> pages;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public NoteDocumentDto() {}

    public NoteDocumentDto(Long id, Long userId, String title, String originalPrompt, Integer pageCount,
                           String style, String audience, String difficulty, String status,
                           List<NotePageDto> pages, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.originalPrompt = originalPrompt;
        this.pageCount = pageCount;
        this.style = style;
        this.audience = audience;
        this.difficulty = difficulty;
        this.status = status;
        this.pages = pages;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getOriginalPrompt() { return originalPrompt; }
    public void setOriginalPrompt(String originalPrompt) { this.originalPrompt = originalPrompt; }
    public Integer getPageCount() { return pageCount; }
    public void setPageCount(Integer pageCount) { this.pageCount = pageCount; }
    public String getStyle() { return style; }
    public void setStyle(String style) { this.style = style; }
    public String getAudience() { return audience; }
    public void setAudience(String audience) { this.audience = audience; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<NotePageDto> getPages() { return pages; }
    public void setPages(List<NotePageDto> pages) { this.pages = pages; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private Long id;
        private Long userId;
        private String title;
        private String originalPrompt;
        private Integer pageCount;
        private String style;
        private String audience;
        private String difficulty;
        private String status;
        private List<NotePageDto> pages;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder userId(Long userId) { this.userId = userId; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder originalPrompt(String originalPrompt) { this.originalPrompt = originalPrompt; return this; }
        public Builder pageCount(Integer pageCount) { this.pageCount = pageCount; return this; }
        public Builder style(String style) { this.style = style; return this; }
        public Builder audience(String audience) { this.audience = audience; return this; }
        public Builder difficulty(String difficulty) { this.difficulty = difficulty; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder pages(List<NotePageDto> pages) { this.pages = pages; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public NoteDocumentDto build() {
            return new NoteDocumentDto(id, userId, title, originalPrompt, pageCount, style, audience, difficulty, status, pages, createdAt, updatedAt);
        }
    }
}
