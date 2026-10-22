package com.visualnotes.dto;

import java.util.List;

public class SectionDto {
    private String type; // e.g. definition, concept, classification, comparison, process, derivation, example, formula, procedure, characteristics, relationship, timeline, case, summary
    private String title;
    private String heading;
    private String content;
    private String badge;
    private List<String> bulletPoints;
    private List<String> highlights;

    public SectionDto() {}

    public SectionDto(String type, String title, String heading, String content, String badge, List<String> bulletPoints, List<String> highlights) {
        this.type = type;
        this.title = title != null ? title : heading;
        this.heading = heading != null ? heading : title;
        this.content = content;
        this.badge = badge;
        this.bulletPoints = bulletPoints;
        this.highlights = highlights;
    }

    public SectionDto(String heading, String content, String badge, List<String> bulletPoints, List<String> highlights) {
        this(null, heading, heading, content, badge, bulletPoints, highlights);
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTitle() { return title != null ? title : heading; }
    public void setTitle(String title) { this.title = title; if (this.heading == null) this.heading = title; }
    public String getHeading() { return heading != null ? heading : title; }
    public void setHeading(String heading) { this.heading = heading; if (this.title == null) this.title = heading; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getBadge() { return badge; }
    public void setBadge(String badge) { this.badge = badge; }
    public List<String> getBulletPoints() { return bulletPoints; }
    public void setBulletPoints(List<String> bulletPoints) { this.bulletPoints = bulletPoints; }
    public List<String> getHighlights() { return highlights; }
    public void setHighlights(List<String> highlights) { this.highlights = highlights; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private String type;
        private String title;
        private String heading;
        private String content;
        private String badge;
        private List<String> bulletPoints;
        private List<String> highlights;

        public Builder type(String type) { this.type = type; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder heading(String heading) { this.heading = heading; return this; }
        public Builder content(String content) { this.content = content; return this; }
        public Builder badge(String badge) { this.badge = badge; return this; }
        public Builder bulletPoints(List<String> bulletPoints) { this.bulletPoints = bulletPoints; return this; }
        public Builder highlights(List<String> highlights) { this.highlights = highlights; return this; }

        public SectionDto build() {
            return new SectionDto(type, title, heading, content, badge, bulletPoints, highlights);
        }
    }

    @Override
    public String toString() {
        return "SectionDto{" +
                "title='" + getTitle() + '\'' +
                ", content='" + content + '\'' +
                ", bulletPoints=" + bulletPoints +
                '}';
    }
}
