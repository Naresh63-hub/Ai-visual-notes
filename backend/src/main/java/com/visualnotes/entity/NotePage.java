package com.visualnotes.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "note_pages", indexes = {
    @Index(name = "idx_page_doc", columnList = "document_id"),
    @Index(name = "idx_page_num", columnList = "document_id, pageNumber")
})
public class NotePage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private NoteDocument document;

    @Column(nullable = false)
    private Integer pageNumber;

    @Column(nullable = false, length = 200)
    private String topicTitle;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String contentJson;

    @Column(length = 50)
    private String layoutType = "standard";

    @Column(length = 50)
    private String diagramType;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public NotePage() {}

    public NotePage(Long id, NoteDocument document, Integer pageNumber, String topicTitle, String contentJson,
                    String layoutType, String diagramType, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.document = document;
        this.pageNumber = pageNumber;
        this.topicTitle = topicTitle;
        this.contentJson = contentJson;
        this.layoutType = layoutType != null ? layoutType : "standard";
        this.diagramType = diagramType;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public NoteDocument getDocument() { return document; }
    public void setDocument(NoteDocument document) { this.document = document; }

    public Integer getPageNumber() { return pageNumber; }
    public void setPageNumber(Integer pageNumber) { this.pageNumber = pageNumber; }

    public String getTopicTitle() { return topicTitle; }
    public void setTopicTitle(String topicTitle) { this.topicTitle = topicTitle; }

    public String getContentJson() { return contentJson; }
    public void setContentJson(String contentJson) { this.contentJson = contentJson; }

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
        private NoteDocument document;
        private Integer pageNumber;
        private String topicTitle;
        private String contentJson;
        private String layoutType = "standard";
        private String diagramType;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder document(NoteDocument document) { this.document = document; return this; }
        public Builder pageNumber(Integer pageNumber) { this.pageNumber = pageNumber; return this; }
        public Builder topicTitle(String topicTitle) { this.topicTitle = topicTitle; return this; }
        public Builder contentJson(String contentJson) { this.contentJson = contentJson; return this; }
        public Builder layoutType(String layoutType) { this.layoutType = layoutType; return this; }
        public Builder diagramType(String diagramType) { this.diagramType = diagramType; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public NotePage build() {
            return new NotePage(id, document, pageNumber, topicTitle, contentJson, layoutType, diagramType, createdAt, updatedAt);
        }
    }
}
