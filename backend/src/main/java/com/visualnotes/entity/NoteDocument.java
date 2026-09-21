package com.visualnotes.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "note_documents", indexes = {
    @Index(name = "idx_doc_user", columnList = "user_id"),
    @Index(name = "idx_doc_created", columnList = "created_at")
})
public class NoteDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @Column(nullable = false, length = 300)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String originalPrompt;

    @Column(nullable = false)
    private Integer pageCount = 1;

    @Column(nullable = false, length = 50)
    private String style = "Handwritten";

    @Column(length = 100)
    private String audience;

    @Column(length = 50)
    private String difficulty;

    @Column(length = 50)
    private String status = "COMPLETED";

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("pageNumber ASC")
    private List<NotePage> pages = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public NoteDocument() {}

    public NoteDocument(Long id, User user, String title, String originalPrompt, Integer pageCount,
                        String style, String audience, String difficulty, String status,
                        List<NotePage> pages, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.user = user;
        this.title = title;
        this.originalPrompt = originalPrompt;
        this.pageCount = pageCount != null ? pageCount : 1;
        this.style = style != null ? style : "Handwritten";
        this.audience = audience;
        this.difficulty = difficulty;
        this.status = status != null ? status : "COMPLETED";
        this.pages = pages != null ? pages : new ArrayList<>();
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

    public void addPage(NotePage page) {
        pages.add(page);
        page.setDocument(this);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

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

    public List<NotePage> getPages() { return pages; }
    public void setPages(List<NotePage> pages) { this.pages = pages; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private User user;
        private String title;
        private String originalPrompt;
        private Integer pageCount = 1;
        private String style = "Handwritten";
        private String audience;
        private String difficulty;
        private String status = "COMPLETED";
        private List<NotePage> pages = new ArrayList<>();
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder user(User user) { this.user = user; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder originalPrompt(String originalPrompt) { this.originalPrompt = originalPrompt; return this; }
        public Builder pageCount(Integer pageCount) { this.pageCount = pageCount; return this; }
        public Builder style(String style) { this.style = style; return this; }
        public Builder audience(String audience) { this.audience = audience; return this; }
        public Builder difficulty(String difficulty) { this.difficulty = difficulty; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder pages(List<NotePage> pages) { this.pages = pages; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public NoteDocument build() {
            return new NoteDocument(id, user, title, originalPrompt, pageCount, style, audience, difficulty, status, pages, createdAt, updatedAt);
        }
    }
}
