package com.visualnotes.dto;

public class KeyPointDto {
    private String point;
    private boolean starred;
    private String category;

    public KeyPointDto() {}
    public KeyPointDto(String point, boolean starred, String category) {
        this.point = point;
        this.starred = starred;
        this.category = category;
    }

    public String getPoint() { return point; }
    public void setPoint(String point) { this.point = point; }
    public boolean isStarred() { return starred; }
    public void setStarred(boolean starred) { this.starred = starred; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private String point;
        private boolean starred;
        private String category;
        public Builder point(String point) { this.point = point; return this; }
        public Builder starred(boolean starred) { this.starred = starred; return this; }
        public Builder category(String category) { this.category = category; return this; }
        public KeyPointDto build() { return new KeyPointDto(point, starred, category); }
    }
}
