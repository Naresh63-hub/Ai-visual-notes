package com.visualnotes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AnalyzePromptRequest {
    @NotBlank(message = "Prompt cannot be empty")
    @Size(max = 2000, message = "Prompt cannot exceed 2000 characters")
    private String prompt;

    private String preferredStyle;
    private Integer requestedPageCount;

    public AnalyzePromptRequest() {}
    public AnalyzePromptRequest(String prompt, String preferredStyle, Integer requestedPageCount) {
        this.prompt = prompt;
        this.preferredStyle = preferredStyle;
        this.requestedPageCount = requestedPageCount;
    }

    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }
    public String getPreferredStyle() { return preferredStyle; }
    public void setPreferredStyle(String preferredStyle) { this.preferredStyle = preferredStyle; }
    public Integer getRequestedPageCount() { return requestedPageCount; }
    public void setRequestedPageCount(Integer requestedPageCount) { this.requestedPageCount = requestedPageCount; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private String prompt;
        private String preferredStyle;
        private Integer requestedPageCount;
        public Builder prompt(String prompt) { this.prompt = prompt; return this; }
        public Builder preferredStyle(String preferredStyle) { this.preferredStyle = preferredStyle; return this; }
        public Builder requestedPageCount(Integer requestedPageCount) { this.requestedPageCount = requestedPageCount; return this; }
        public AnalyzePromptRequest build() { return new AnalyzePromptRequest(prompt, preferredStyle, requestedPageCount); }
    }
}
