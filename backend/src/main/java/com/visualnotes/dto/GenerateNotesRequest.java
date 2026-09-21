package com.visualnotes.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class GenerateNotesRequest {
    @NotBlank(message = "Prompt cannot be empty")
    @Size(max = 2000, message = "Prompt cannot exceed 2000 characters")
    private String prompt;
    
    @Min(value = 1, message = "Page count must be at least 1")
    @Max(value = 10, message = "Page count cannot exceed 10")
    private Integer pageCount;

    private String style = "Handwritten";
    private String audience;
    private String difficulty;
    private String detailLevel;
    private Boolean includeDiagrams = true;
    private PagePlanDto customPlan;

    public GenerateNotesRequest() {}

    public GenerateNotesRequest(String prompt, Integer pageCount, String style, String audience,
                                String difficulty, String detailLevel, Boolean includeDiagrams, PagePlanDto customPlan) {
        this.prompt = prompt;
        this.pageCount = pageCount;
        this.style = style != null ? style : "Handwritten";
        this.audience = audience;
        this.difficulty = difficulty;
        this.detailLevel = detailLevel;
        this.includeDiagrams = includeDiagrams != null ? includeDiagrams : true;
        this.customPlan = customPlan;
    }

    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }
    public Integer getPageCount() { return pageCount; }
    public void setPageCount(Integer pageCount) { this.pageCount = pageCount; }
    public String getStyle() { return style; }
    public void setStyle(String style) { this.style = style; }
    public String getAudience() { return audience; }
    public void setAudience(String audience) { this.audience = audience; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public String getDetailLevel() { return detailLevel; }
    public void setDetailLevel(String detailLevel) { this.detailLevel = detailLevel; }
    public Boolean getIncludeDiagrams() { return includeDiagrams; }
    public void setIncludeDiagrams(Boolean includeDiagrams) { this.includeDiagrams = includeDiagrams; }
    public PagePlanDto getCustomPlan() { return customPlan; }
    public void setCustomPlan(PagePlanDto customPlan) { this.customPlan = customPlan; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private String prompt;
        private Integer pageCount;
        private String style = "Handwritten";
        private String audience;
        private String difficulty;
        private String detailLevel;
        private Boolean includeDiagrams = true;
        private PagePlanDto customPlan;

        public Builder prompt(String prompt) { this.prompt = prompt; return this; }
        public Builder pageCount(Integer pageCount) { this.pageCount = pageCount; return this; }
        public Builder style(String style) { this.style = style; return this; }
        public Builder audience(String audience) { this.audience = audience; return this; }
        public Builder difficulty(String difficulty) { this.difficulty = difficulty; return this; }
        public Builder detailLevel(String detailLevel) { this.detailLevel = detailLevel; return this; }
        public Builder includeDiagrams(Boolean includeDiagrams) { this.includeDiagrams = includeDiagrams; return this; }
        public Builder customPlan(PagePlanDto customPlan) { this.customPlan = customPlan; return this; }

        public GenerateNotesRequest build() {
            return new GenerateNotesRequest(prompt, pageCount, style, audience, difficulty, detailLevel, includeDiagrams, customPlan);
        }
    }
}
