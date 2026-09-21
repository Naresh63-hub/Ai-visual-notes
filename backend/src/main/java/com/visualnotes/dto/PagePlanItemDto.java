package com.visualnotes.dto;

import java.util.List;

public class PagePlanItemDto {
    private int pageNumber;
    private String pageTitle;
    private List<String> topics;
    private String focusArea;
    private String plannedDiagramType;
    private String estimatedDensity;
    private String keyConceptsSummary;

    public PagePlanItemDto() {}
    public PagePlanItemDto(int pageNumber, String pageTitle, List<String> topics, String focusArea,
                           String plannedDiagramType, String estimatedDensity, String keyConceptsSummary) {
        this.pageNumber = pageNumber;
        this.pageTitle = pageTitle;
        this.topics = topics;
        this.focusArea = focusArea;
        this.plannedDiagramType = plannedDiagramType;
        this.estimatedDensity = estimatedDensity;
        this.keyConceptsSummary = keyConceptsSummary;
    }

    public int getPageNumber() { return pageNumber; }
    public void setPageNumber(int pageNumber) { this.pageNumber = pageNumber; }
    public String getPageTitle() { return pageTitle; }
    public void setPageTitle(String pageTitle) { this.pageTitle = pageTitle; }
    public List<String> getTopics() { return topics; }
    public void setTopics(List<String> topics) { this.topics = topics; }
    public String getFocusArea() { return focusArea; }
    public void setFocusArea(String focusArea) { this.focusArea = focusArea; }
    public String getPlannedDiagramType() { return plannedDiagramType; }
    public void setPlannedDiagramType(String plannedDiagramType) { this.plannedDiagramType = plannedDiagramType; }
    public String getEstimatedDensity() { return estimatedDensity; }
    public void setEstimatedDensity(String estimatedDensity) { this.estimatedDensity = estimatedDensity; }
    public String getKeyConceptsSummary() { return keyConceptsSummary; }
    public void setKeyConceptsSummary(String keyConceptsSummary) { this.keyConceptsSummary = keyConceptsSummary; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private int pageNumber;
        private String pageTitle;
        private List<String> topics;
        private String focusArea;
        private String plannedDiagramType;
        private String estimatedDensity;
        private String keyConceptsSummary;

        public Builder pageNumber(int pageNumber) { this.pageNumber = pageNumber; return this; }
        public Builder pageTitle(String pageTitle) { this.pageTitle = pageTitle; return this; }
        public Builder topics(List<String> topics) { this.topics = topics; return this; }
        public Builder focusArea(String focusArea) { this.focusArea = focusArea; return this; }
        public Builder plannedDiagramType(String plannedDiagramType) { this.plannedDiagramType = plannedDiagramType; return this; }
        public Builder estimatedDensity(String estimatedDensity) { this.estimatedDensity = estimatedDensity; return this; }
        public Builder keyConceptsSummary(String keyConceptsSummary) { this.keyConceptsSummary = keyConceptsSummary; return this; }

        public PagePlanItemDto build() {
            return new PagePlanItemDto(pageNumber, pageTitle, topics, focusArea, plannedDiagramType, estimatedDensity, keyConceptsSummary);
        }
    }
}
