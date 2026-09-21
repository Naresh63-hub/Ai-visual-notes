package com.visualnotes.dto;

import java.util.List;

public class SectionDto {
    private String heading;
    private String content;
    private String badge;
    private List<String> bulletPoints;
    private List<String> highlights;

    public SectionDto() {}
    public SectionDto(String heading, String content, String badge, List<String> bulletPoints, List<String> highlights) {
        this.heading = heading;
        this.content = content;
        this.badge = badge;
        this.bulletPoints = bulletPoints;
        this.highlights = highlights;
    }

    public String getHeading() { return heading; }
    public void setHeading(String heading) { this.heading = heading; }
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
        private String heading;
        private String content;
        private String badge;
        private List<String> bulletPoints;
        private List<String> highlights;

        public Builder heading(String heading) { this.heading = heading; return this; }
        public Builder content(String content) { this.content = content; return this; }
        public Builder badge(String badge) { this.badge = badge; return this; }
        public Builder bulletPoints(List<String> bulletPoints) { this.bulletPoints = bulletPoints; return this; }
        public Builder highlights(List<String> highlights) { this.highlights = highlights; return this; }

        public SectionDto build() {
            return new SectionDto(heading, content, badge, bulletPoints, highlights);
        }
    }
}
