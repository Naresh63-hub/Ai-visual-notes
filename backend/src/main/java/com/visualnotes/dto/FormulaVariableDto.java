package com.visualnotes.dto;

public class FormulaVariableDto {
    private String symbol;
    private String meaning;
    private String unit;

    public FormulaVariableDto() {}
    public FormulaVariableDto(String symbol, String meaning, String unit) {
        this.symbol = symbol;
        this.meaning = meaning;
        this.unit = unit;
    }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public String getMeaning() { return meaning; }
    public void setMeaning(String meaning) { this.meaning = meaning; }
    public String getDescription() { return meaning; }
    public void setDescription(String description) { this.meaning = description; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private String symbol;
        private String meaning;
        private String unit;
        public Builder symbol(String symbol) { this.symbol = symbol; return this; }
        public Builder meaning(String meaning) { this.meaning = meaning; return this; }
        public Builder description(String description) { this.meaning = description; return this; }
        public Builder unit(String unit) { this.unit = unit; return this; }
        public FormulaVariableDto build() { return new FormulaVariableDto(symbol, meaning, unit); }
    }
}
