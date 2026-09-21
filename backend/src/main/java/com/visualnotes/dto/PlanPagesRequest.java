package com.visualnotes.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PlanPagesRequest {

    @NotBlank(message = "Prompt cannot be empty")
    @Size(max = 2000, message = "Prompt cannot exceed 2000 characters")
    private String prompt;

    @Min(value = 1, message = "Page count must be at least 1")
    @Max(value = 10, message = "Page count cannot exceed 10")
    private Integer pageCount;

    private String audience;
    private String style = "Handwritten";
    private String difficulty;

    public PlanPagesRequest() {}

    public PlanPagesRequest(String prompt, Integer pageCount, String audience, String style, String difficulty) {
        this.prompt = prompt;
        this.pageCount = pageCount;
        this.audience = audience;
        this.style = style != null ? style : "Handwritten";
        this.difficulty = difficulty;
    }

    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }
    public Integer getPageCount() { return pageCount; }
    public void setPageCount(Integer pageCount) { this.pageCount = pageCount; }
    public String getAudience() { return audience; }
    public void setAudience(String audience) { this.audience = audience; }
    public String getStyle() { return style; }
    public void setStyle(String style) { this.style = style; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String prompt;
        private Integer pageCount;
        private String audience;
        private String style = "Handwritten";
        private String difficulty;

        public Builder prompt(String prompt) { this.prompt = prompt; return this; }
        public Builder pageCount(Integer pageCount) { this.pageCount = pageCount; return this; }
        public Builder audience(String audience) { this.audience = audience; return this; }
        public Builder style(String style) { this.style = style; return this; }
        public Builder difficulty(String difficulty) { this.difficulty = difficulty; return this; }

        public PlanPagesRequest build() {
            return new PlanPagesRequest(prompt, pageCount, audience, style, difficulty);
        }
    }
}
