package com.visualnotes.dto;

import java.util.List;

public class ExampleDto {
    private String title;
    private String scenario;
    private String input;
    private List<String> stepByStep;
    private String outputOrResult;
    private String takeaway;

    public ExampleDto() {}
    public ExampleDto(String title, String scenario, String input, List<String> stepByStep, String outputOrResult, String takeaway) {
        this.title = title;
        this.scenario = scenario;
        this.input = input;
        this.stepByStep = stepByStep;
        this.outputOrResult = outputOrResult;
        this.takeaway = takeaway;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getScenario() { return scenario; }
    public void setScenario(String scenario) { this.scenario = scenario; }
    public String getInput() { return input; }
    public void setInput(String input) { this.input = input; }
    public List<String> getStepByStep() { return stepByStep; }
    public void setStepByStep(List<String> stepByStep) { this.stepByStep = stepByStep; }
    public String getOutputOrResult() { return outputOrResult; }
    public void setOutputOrResult(String outputOrResult) { this.outputOrResult = outputOrResult; }
    public String getTakeaway() { return takeaway; }
    public void setTakeaway(String takeaway) { this.takeaway = takeaway; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private String title;
        private String scenario;
        private String input;
        private List<String> stepByStep;
        private String outputOrResult;
        private String takeaway;

        public Builder title(String title) { this.title = title; return this; }
        public Builder scenario(String scenario) { this.scenario = scenario; return this; }
        public Builder input(String input) { this.input = input; return this; }
        public Builder stepByStep(List<String> stepByStep) { this.stepByStep = stepByStep; return this; }
        public Builder outputOrResult(String outputOrResult) { this.outputOrResult = outputOrResult; return this; }
        public Builder takeaway(String takeaway) { this.takeaway = takeaway; return this; }

        public ExampleDto build() {
            return new ExampleDto(title, scenario, input, stepByStep, outputOrResult, takeaway);
        }
    }
}
