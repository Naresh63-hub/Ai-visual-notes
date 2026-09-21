package com.visualnotes.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UpdatePageRequest {
    @NotNull(message = "Page number is required")
    private Integer pageNumber;
    
    @Size(max = 200, message = "Topic title cannot exceed 200 characters")
    private String topicTitle;

    @NotNull(message = "Page content cannot be null")
    private PageContentDto content;

    public UpdatePageRequest() {}

    public UpdatePageRequest(Integer pageNumber, String topicTitle, PageContentDto content) {
        this.pageNumber = pageNumber;
        this.topicTitle = topicTitle;
        this.content = content;
    }

    public Integer getPageNumber() { return pageNumber; }
    public void setPageNumber(Integer pageNumber) { this.pageNumber = pageNumber; }
    public String getTopicTitle() { return topicTitle; }
    public void setTopicTitle(String topicTitle) { this.topicTitle = topicTitle; }
    public PageContentDto getContent() { return content; }
    public void setContent(PageContentDto content) { this.content = content; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private Integer pageNumber;
        private String topicTitle;
        private PageContentDto content;

        public Builder pageNumber(Integer pageNumber) { this.pageNumber = pageNumber; return this; }
        public Builder topicTitle(String topicTitle) { this.topicTitle = topicTitle; return this; }
        public Builder content(PageContentDto content) { this.content = content; return this; }

        public UpdatePageRequest build() {
            return new UpdatePageRequest(pageNumber, topicTitle, content);
        }
    }
}
