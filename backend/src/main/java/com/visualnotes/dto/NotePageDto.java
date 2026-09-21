package com.visualnotes.dto;

import java.time.LocalDateTime;

public class NotePageDto {
    private Long id;
    private Integer pageNumber;
    private String topicTitle;
    private PageContentDto content;
    private String layoutType;
    private String diagramType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public NotePageDto() {}

    public NotePageDto(Long id, Integer pageNumber, String topicTitle, PageContentDto content,
                       String layoutType, String diagramType, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.pageNumber = pageNumber;
        this.topicTitle = topicTitle;
        this.content = content;
        this.layoutType = layoutType;
        this.diagramType = diagramType;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getPageNumber() { return pageNumber; }
    public void setPageNumber(Integer pageNumber) { this.pageNumber = pageNumber; }
    public String getTopicTitle() { return topicTitle; }
    public void setTopicTitle(String topicTitle) { this.topicTitle = topicTitle; }
    public PageContentDto getContent() { return content; }
    public void setContent(PageContentDto content) { this.content = content; }
    public String getLayoutType() { return layoutType; }
    public void setLayoutType(String layoutType) { this.layoutType = layoutType; }
    public String getDiagramType() { return diagramType; }
    public void setDiagramType(String diagramType) { this.diagramType = diagramType; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private Long id;
        private Integer pageNumber;
        private String topicTitle;
        private PageContentDto content;
        private String layoutType;
        private String diagramType;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder pageNumber(Integer pageNumber) { this.pageNumber = pageNumber; return this; }
        public Builder topicTitle(String topicTitle) { this.topicTitle = topicTitle; return this; }
        public Builder content(PageContentDto content) { this.content = content; return this; }
        public Builder layoutType(String layoutType) { this.layoutType = layoutType; return this; }
        public Builder diagramType(String diagramType) { this.diagramType = diagramType; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public NotePageDto build() {
            return new NotePageDto(id, pageNumber, topicTitle, content, layoutType, diagramType, createdAt, updatedAt);
        }
    }
}
