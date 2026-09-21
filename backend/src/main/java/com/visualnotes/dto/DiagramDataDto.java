package com.visualnotes.dto;

import java.util.List;
import java.util.Map;

public class DiagramDataDto {
    private String type;
    private String title;
    private String caption;
    private String rawSvg;
    private Map<String, Object> data;
    private List<String> labels;
    private List<String> annotations;

    public DiagramDataDto() {}
    public DiagramDataDto(String type, String title, String caption, String rawSvg,
                          Map<String, Object> data, List<String> labels, List<String> annotations) {
        this.type = type;
        this.title = title;
        this.caption = caption;
        this.rawSvg = rawSvg;
        this.data = data;
        this.labels = labels;
        this.annotations = annotations;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCaption() { return caption; }
    public void setCaption(String caption) { this.caption = caption; }
    public String getRawSvg() { return rawSvg; }
    public void setRawSvg(String rawSvg) { this.rawSvg = rawSvg; }
    public Map<String, Object> getData() { return data; }
    public void setData(Map<String, Object> data) { this.data = data; }
    public List<String> getLabels() { return labels; }
    public void setLabels(List<String> labels) { this.labels = labels; }
    public List<String> getAnnotations() { return annotations; }
    public void setAnnotations(List<String> annotations) { this.annotations = annotations; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private String type;
        private String title;
        private String caption;
        private String rawSvg;
        private Map<String, Object> data;
        private List<String> labels;
        private List<String> annotations;

        public Builder type(String type) { this.type = type; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder caption(String caption) { this.caption = caption; return this; }
        public Builder rawSvg(String rawSvg) { this.rawSvg = rawSvg; return this; }
        public Builder data(Map<String, Object> data) { this.data = data; return this; }
        public Builder labels(List<String> labels) { this.labels = labels; return this; }
        public Builder annotations(List<String> annotations) { this.annotations = annotations; return this; }

        public DiagramDataDto build() {
            return new DiagramDataDto(type, title, caption, rawSvg, data, labels, annotations);
        }
    }
}
