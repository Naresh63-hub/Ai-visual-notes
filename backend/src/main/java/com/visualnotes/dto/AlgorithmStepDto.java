package com.visualnotes.dto;

public class AlgorithmStepDto {
    private int stepNumber;
    private String instruction;
    private String codeSnippet;
    private String note;

    public AlgorithmStepDto() {}
    public AlgorithmStepDto(int stepNumber, String instruction, String codeSnippet, String note) {
        this.stepNumber = stepNumber;
        this.instruction = instruction;
        this.codeSnippet = codeSnippet;
        this.note = note;
    }

    public int getStepNumber() { return stepNumber; }
    public void setStepNumber(int stepNumber) { this.stepNumber = stepNumber; }
    public String getInstruction() { return instruction; }
    public void setInstruction(String instruction) { this.instruction = instruction; }
    public String getCodeSnippet() { return codeSnippet; }
    public void setCodeSnippet(String codeSnippet) { this.codeSnippet = codeSnippet; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private int stepNumber;
        private String instruction;
        private String codeSnippet;
        private String note;

        public Builder stepNumber(int stepNumber) { this.stepNumber = stepNumber; return this; }
        public Builder instruction(String instruction) { this.instruction = instruction; return this; }
        public Builder codeSnippet(String codeSnippet) { this.codeSnippet = codeSnippet; return this; }
        public Builder note(String note) { this.note = note; return this; }

        public AlgorithmStepDto build() {
            return new AlgorithmStepDto(stepNumber, instruction, codeSnippet, note);
        }
    }
}
