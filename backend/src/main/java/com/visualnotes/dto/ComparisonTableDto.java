package com.visualnotes.dto;

import java.util.List;

public class ComparisonTableDto {
    private String title;
    private List<String> headers;
    private List<List<String>> rows;
    private String conclusion;

    public ComparisonTableDto() {}

    public ComparisonTableDto(String title, List<String> headers, List<List<String>> rows, String conclusion) {
        this.title = title;
        this.headers = headers;
        this.rows = rows;
        this.conclusion = conclusion;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public List<String> getHeaders() { return headers; }
    public void setHeaders(List<String> headers) { this.headers = headers; }
    public List<List<String>> getRows() { return rows; }
    public void setRows(List<List<String>> rows) { this.rows = rows; }
    public String getConclusion() { return conclusion; }
    public void setConclusion(String conclusion) { this.conclusion = conclusion; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String title;
        private List<String> headers;
        private List<List<String>> rows;
        private String conclusion;

        public Builder title(String title) { this.title = title; return this; }
        public Builder headers(List<String> headers) { this.headers = headers; return this; }
        public Builder rows(List<List<String>> rows) { this.rows = rows; return this; }
        public Builder conclusion(String conclusion) { this.conclusion = conclusion; return this; }

        public ComparisonTableDto build() {
            return new ComparisonTableDto(title, headers, rows, conclusion);
        }
    }
}
