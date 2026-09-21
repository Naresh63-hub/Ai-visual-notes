package com.visualnotes.dto;

import java.util.List;

public class FormulaDto {
    private String title;
    private String expression;
    private String explanation;
    private List<FormulaVariableDto> variables;
    private String applicationExample;

    public FormulaDto() {}
    public FormulaDto(String title, String expression, String explanation, List<FormulaVariableDto> variables, String applicationExample) {
        this.title = title;
        this.expression = expression;
        this.explanation = explanation;
        this.variables = variables;
        this.applicationExample = applicationExample;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getExpression() { return expression; }
    public void setExpression(String expression) { this.expression = expression; }
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
    public List<FormulaVariableDto> getVariables() { return variables; }
    public void setVariables(List<FormulaVariableDto> variables) { this.variables = variables; }
    public String getApplicationExample() { return applicationExample; }
    public void setApplicationExample(String applicationExample) { this.applicationExample = applicationExample; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private String title;
        private String expression;
        private String explanation;
        private List<FormulaVariableDto> variables;
        private String applicationExample;

        public Builder title(String title) { this.title = title; return this; }
        public Builder expression(String expression) { this.expression = expression; return this; }
        public Builder explanation(String explanation) { this.explanation = explanation; return this; }
        public Builder variables(List<FormulaVariableDto> variables) { this.variables = variables; return this; }
        public Builder applicationExample(String applicationExample) { this.applicationExample = applicationExample; return this; }

        public FormulaDto build() {
            return new FormulaDto(title, expression, explanation, variables, applicationExample);
        }
    }
}
