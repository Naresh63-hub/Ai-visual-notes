package com.visualnotes.dto;

import java.util.List;

public class PagePlanDto {
    private String documentTitle;
    private int totalPages;
    private String overallSummary;
    private String style;
    private String audience;
    private String difficulty;
    private List<PagePlanItemDto> pages;

    public PagePlanDto() {}
    public PagePlanDto(String documentTitle, int totalPages, String overallSummary, String style,
                       String audience, String difficulty, List<PagePlanItemDto> pages) {
        this.documentTitle = documentTitle;
        this.totalPages = totalPages;
        this.overallSummary = overallSummary;
        this.style = style;
        this.audience = audience;
        this.difficulty = difficulty;
        this.pages = pages;
    }

    public String getDocumentTitle() { return documentTitle; }
    public void setDocumentTitle(String documentTitle) { this.documentTitle = documentTitle; }
    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    public String getOverallSummary() { return overallSummary; }
    public void setOverallSummary(String overallSummary) { this.overallSummary = overallSummary; }
    public String getStyle() { return style; }
    public void setStyle(String style) { this.style = style; }
    public String getAudience() { return audience; }
    public void setAudience(String audience) { this.audience = audience; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public List<PagePlanItemDto> getPages() { return pages; }
    public void setPages(List<PagePlanItemDto> pages) { this.pages = pages; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private String documentTitle;
        private int totalPages;
        private String overallSummary;
        private String style;
        private String audience;
        private String difficulty;
        private List<PagePlanItemDto> pages;

        public Builder documentTitle(String documentTitle) { this.documentTitle = documentTitle; return this; }
        public Builder totalPages(int totalPages) { this.totalPages = totalPages; return this; }
        public Builder overallSummary(String overallSummary) { this.overallSummary = overallSummary; return this; }
        public Builder style(String style) { this.style = style; return this; }
        public Builder audience(String audience) { this.audience = audience; return this; }
        public Builder difficulty(String difficulty) { this.difficulty = difficulty; return this; }
        public Builder pages(List<PagePlanItemDto> pages) { this.pages = pages; return this; }

        public PagePlanDto build() {
            return new PagePlanDto(documentTitle, totalPages, overallSummary, style, audience, difficulty, pages);
        }
    }
}
