package com.visualnotes.dto;

import jakarta.validation.constraints.NotNull;

public class RegeneratePageRequest {
    @NotNull(message = "Page number is required")
    private Integer pageNumber;
    
    private String instruction;
    private String customModifier;
    private String style;

    public RegeneratePageRequest() {}

    public RegeneratePageRequest(Integer pageNumber, String instruction, String customModifier, String style) {
        this.pageNumber = pageNumber;
        this.instruction = instruction;
        this.customModifier = customModifier;
        this.style = style;
    }

    public Integer getPageNumber() { return pageNumber; }
    public void setPageNumber(Integer pageNumber) { this.pageNumber = pageNumber; }
    public String getInstruction() { return instruction; }
    public void setInstruction(String instruction) { this.instruction = instruction; }
    public String getCustomModifier() { return customModifier; }
    public void setCustomModifier(String customModifier) { this.customModifier = customModifier; }
    public String getStyle() { return style; }
    public void setStyle(String style) { this.style = style; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private Integer pageNumber;
        private String instruction;
        private String customModifier;
        private String style;

        public Builder pageNumber(Integer pageNumber) { this.pageNumber = pageNumber; return this; }
        public Builder instruction(String instruction) { this.instruction = instruction; return this; }
        public Builder customModifier(String customModifier) { this.customModifier = customModifier; return this; }
        public Builder style(String style) { this.style = style; return this; }

        public RegeneratePageRequest build() {
            return new RegeneratePageRequest(pageNumber, instruction, customModifier, style);
        }
    }
}
