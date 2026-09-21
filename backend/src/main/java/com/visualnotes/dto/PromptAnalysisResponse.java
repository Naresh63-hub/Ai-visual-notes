package com.visualnotes.dto;

import java.util.List;

public class PromptAnalysisResponse {
    private String rawPrompt;
    private String mainSubject;
    private String domain;
    private String subdomain;
    private List<String> requirements;
    private List<String> detectedTopics;
    private int topicCount;
    private String audience;
    private String difficulty;
    private String detailLevel; // Quick, Normal, Detailed, Very Detailed
    private String detectedStyle;
    private String pageMode; // AUTOMATIC, EXPLICIT, ONE_PAGE, ONE_PER_TOPIC
    private boolean requiresDiagram;
    private boolean requiresAlgorithm;
    private boolean requiresExample;
    private boolean isExamOriented;
    private Integer requestedPageCount;
    private Integer suggestedPageCount;
    private List<Integer> quickPageOptions;
    private String userPromptFeedback;

    public PromptAnalysisResponse() {}

    public PromptAnalysisResponse(String rawPrompt, String mainSubject, String domain, String subdomain,
                                  List<String> requirements, List<String> detectedTopics, int topicCount,
                                  String audience, String difficulty, String detailLevel, String detectedStyle,
                                  String pageMode, boolean requiresDiagram, boolean requiresAlgorithm, boolean requiresExample,
                                  boolean isExamOriented, Integer requestedPageCount, Integer suggestedPageCount,
                                  List<Integer> quickPageOptions, String userPromptFeedback) {
        this.rawPrompt = rawPrompt;
        this.mainSubject = mainSubject;
        this.domain = domain;
        this.subdomain = subdomain;
        this.requirements = requirements;
        this.detectedTopics = detectedTopics;
        this.topicCount = topicCount;
        this.audience = audience;
        this.difficulty = difficulty;
        this.detailLevel = detailLevel;
        this.detectedStyle = detectedStyle;
        this.pageMode = pageMode;
        this.requiresDiagram = requiresDiagram;
        this.requiresAlgorithm = requiresAlgorithm;
        this.requiresExample = requiresExample;
        this.isExamOriented = isExamOriented;
        this.requestedPageCount = requestedPageCount;
        this.suggestedPageCount = suggestedPageCount;
        this.quickPageOptions = quickPageOptions;
        this.userPromptFeedback = userPromptFeedback;
    }

    public String getRawPrompt() { return rawPrompt; }
    public void setRawPrompt(String rawPrompt) { this.rawPrompt = rawPrompt; }
    public String getMainSubject() { return mainSubject; }
    public void setMainSubject(String mainSubject) { this.mainSubject = mainSubject; }
    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }
    public String getSubdomain() { return subdomain; }
    public void setSubdomain(String subdomain) { this.subdomain = subdomain; }
    public List<String> getRequirements() { return requirements; }
    public void setRequirements(List<String> requirements) { this.requirements = requirements; }
    public List<String> getDetectedTopics() { return detectedTopics; }
    public void setDetectedTopics(List<String> detectedTopics) { this.detectedTopics = detectedTopics; }
    public int getTopicCount() { return topicCount; }
    public void setTopicCount(int topicCount) { this.topicCount = topicCount; }
    public String getAudience() { return audience; }
    public void setAudience(String audience) { this.audience = audience; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public String getDetailLevel() { return detailLevel; }
    public void setDetailLevel(String detailLevel) { this.detailLevel = detailLevel; }
    public String getDetectedStyle() { return detectedStyle; }
    public void setDetectedStyle(String detectedStyle) { this.detectedStyle = detectedStyle; }
    public String getPageMode() { return pageMode; }
    public void setPageMode(String pageMode) { this.pageMode = pageMode; }
    public boolean isRequiresDiagram() { return requiresDiagram; }
    public void setRequiresDiagram(boolean requiresDiagram) { this.requiresDiagram = requiresDiagram; }
    public boolean isRequiresAlgorithm() { return requiresAlgorithm; }
    public void setRequiresAlgorithm(boolean requiresAlgorithm) { this.requiresAlgorithm = requiresAlgorithm; }
    public boolean isRequiresExample() { return requiresExample; }
    public void setRequiresExample(boolean requiresExample) { this.requiresExample = requiresExample; }
    public boolean isExamOriented() { return isExamOriented; }
    public void setExamOriented(boolean examOriented) { isExamOriented = examOriented; }
    public Integer getRequestedPageCount() { return requestedPageCount; }
    public void setRequestedPageCount(Integer requestedPageCount) { this.requestedPageCount = requestedPageCount; }
    public Integer getSuggestedPageCount() { return suggestedPageCount; }
    public void setSuggestedPageCount(Integer suggestedPageCount) { this.suggestedPageCount = suggestedPageCount; }
    public List<Integer> getQuickPageOptions() { return quickPageOptions; }
    public void setQuickPageOptions(List<Integer> quickPageOptions) { this.quickPageOptions = quickPageOptions; }
    public String getUserPromptFeedback() { return userPromptFeedback; }
    public void setUserPromptFeedback(String userPromptFeedback) { this.userPromptFeedback = userPromptFeedback; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String rawPrompt;
        private String mainSubject;
        private String domain;
        private String subdomain;
        private List<String> requirements;
        private List<String> detectedTopics;
        private int topicCount;
        private String audience;
        private String difficulty;
        private String detailLevel;
        private String detectedStyle;
        private String pageMode;
        private boolean requiresDiagram;
        private boolean requiresAlgorithm;
        private boolean requiresExample;
        private boolean isExamOriented;
        private Integer requestedPageCount;
        private Integer suggestedPageCount;
        private List<Integer> quickPageOptions;
        private String userPromptFeedback;

        public Builder rawPrompt(String rawPrompt) { this.rawPrompt = rawPrompt; return this; }
        public Builder mainSubject(String mainSubject) { this.mainSubject = mainSubject; return this; }
        public Builder domain(String domain) { this.domain = domain; return this; }
        public Builder subdomain(String subdomain) { this.subdomain = subdomain; return this; }
        public Builder requirements(List<String> requirements) { this.requirements = requirements; return this; }
        public Builder detectedTopics(List<String> detectedTopics) { this.detectedTopics = detectedTopics; return this; }
        public Builder topicCount(int topicCount) { this.topicCount = topicCount; return this; }
        public Builder audience(String audience) { this.audience = audience; return this; }
        public Builder difficulty(String difficulty) { this.difficulty = difficulty; return this; }
        public Builder detailLevel(String detailLevel) { this.detailLevel = detailLevel; return this; }
        public Builder detectedStyle(String detectedStyle) { this.detectedStyle = detectedStyle; return this; }
        public Builder pageMode(String pageMode) { this.pageMode = pageMode; return this; }
        public Builder requiresDiagram(boolean requiresDiagram) { this.requiresDiagram = requiresDiagram; return this; }
        public Builder requiresAlgorithm(boolean requiresAlgorithm) { this.requiresAlgorithm = requiresAlgorithm; return this; }
        public Builder requiresExample(boolean requiresExample) { this.requiresExample = requiresExample; return this; }
        public Builder isExamOriented(boolean isExamOriented) { this.isExamOriented = isExamOriented; return this; }
        public Builder requestedPageCount(Integer requestedPageCount) { this.requestedPageCount = requestedPageCount; return this; }
        public Builder suggestedPageCount(Integer suggestedPageCount) { this.suggestedPageCount = suggestedPageCount; return this; }
        public Builder quickPageOptions(List<Integer> quickPageOptions) { this.quickPageOptions = quickPageOptions; return this; }
        public Builder userPromptFeedback(String userPromptFeedback) { this.userPromptFeedback = userPromptFeedback; return this; }

        public PromptAnalysisResponse build() {
            return new PromptAnalysisResponse(rawPrompt, mainSubject, domain, subdomain, requirements, detectedTopics, topicCount, audience, difficulty,
                    detailLevel, detectedStyle, pageMode, requiresDiagram, requiresAlgorithm, requiresExample, isExamOriented,
                    requestedPageCount, suggestedPageCount, quickPageOptions, userPromptFeedback);
        }
    }
}
