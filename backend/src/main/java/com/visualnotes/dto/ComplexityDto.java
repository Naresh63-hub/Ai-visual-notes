package com.visualnotes.dto;

public class ComplexityDto {
    private String timeBest;
    private String timeAverage;
    private String timeWorst;
    private String space;
    private String timeComplexitySummary;
    private String spaceComplexitySummary;
    private String explanation;

    public ComplexityDto() {}
    public ComplexityDto(String timeBest, String timeAverage, String timeWorst, String space,
                         String timeComplexitySummary, String spaceComplexitySummary, String explanation) {
        this.timeBest = timeBest;
        this.timeAverage = timeAverage;
        this.timeWorst = timeWorst;
        this.space = space;
        this.timeComplexitySummary = timeComplexitySummary;
        this.spaceComplexitySummary = spaceComplexitySummary;
        this.explanation = explanation;
    }

    public String getTimeBest() { return timeBest; }
    public void setTimeBest(String timeBest) { this.timeBest = timeBest; }
    public String getTimeAverage() { return timeAverage; }
    public void setTimeAverage(String timeAverage) { this.timeAverage = timeAverage; }
    public String getTimeWorst() { return timeWorst; }
    public void setTimeWorst(String timeWorst) { this.timeWorst = timeWorst; }
    public String getSpace() { return space; }
    public void setSpace(String space) { this.space = space; }
    public String getTimeComplexitySummary() { return timeComplexitySummary; }
    public void setTimeComplexitySummary(String timeComplexitySummary) { this.timeComplexitySummary = timeComplexitySummary; }
    public String getSpaceComplexitySummary() { return spaceComplexitySummary; }
    public void setSpaceComplexitySummary(String spaceComplexitySummary) { this.spaceComplexitySummary = spaceComplexitySummary; }
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private String timeBest;
        private String timeAverage;
        private String timeWorst;
        private String space;
        private String timeComplexitySummary;
        private String spaceComplexitySummary;
        private String explanation;

        public Builder timeBest(String timeBest) { this.timeBest = timeBest; return this; }
        public Builder timeAverage(String timeAverage) { this.timeAverage = timeAverage; return this; }
        public Builder timeWorst(String timeWorst) { this.timeWorst = timeWorst; return this; }
        public Builder space(String space) { this.space = space; return this; }
        public Builder timeComplexitySummary(String timeComplexitySummary) { this.timeComplexitySummary = timeComplexitySummary; return this; }
        public Builder spaceComplexitySummary(String spaceComplexitySummary) { this.spaceComplexitySummary = spaceComplexitySummary; return this; }
        public Builder explanation(String explanation) { this.explanation = explanation; return this; }

        public ComplexityDto build() {
            return new ComplexityDto(timeBest, timeAverage, timeWorst, space, timeComplexitySummary, spaceComplexitySummary, explanation);
        }
    }
}
