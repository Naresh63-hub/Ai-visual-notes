package com.visualnotes.dto;

import java.util.List;

public class PageContentDto {
    private String documentTitle;
    private int pageNumber;
    private int totalPages;
    private String topicTitle;
    private String topicSubtitle;
    private String categoryBadge;
    private String difficultyLevel;
    private String domain; // e.g. PHYSICS, BIOLOGY, ALGORITHMS
    private String subdomain;
    private String pagePartTitle; // e.g. "Part 1: Concept & Working" or "Part 2: Algorithm & Complexity"
    
    private String definition;
    private String purpose;
    private String mainIdea;
    private String simpleExplanation;
    private List<SectionDto> sections;
    
    private List<AlgorithmStepDto> algorithm;
    private String pseudocode;
    
    private ExampleDto example;
    private FormulaDto formula;
    private ComplexityDto complexity;
    private DiagramDataDto diagram;
    private ComparisonTableDto comparisonTable;
    
    private List<String> advantages;
    private List<String> limitations;
    private List<KeyPointDto> keyPoints;
    private List<ExamTipDto> examTips;
    private List<String> quickTakeaways;
    
    private boolean continuesOnNextPage;
    private boolean isContinuation;
    private String layoutHint;
    private String styleTheme;

    public PageContentDto() {}

    public PageContentDto(String documentTitle, int pageNumber, int totalPages, String topicTitle, String topicSubtitle,
                          String categoryBadge, String difficultyLevel, String domain, String subdomain,
                          String pagePartTitle, String definition, String purpose, String mainIdea,
                          String simpleExplanation, List<SectionDto> sections, List<AlgorithmStepDto> algorithm,
                          String pseudocode, ExampleDto example, FormulaDto formula, ComplexityDto complexity,
                          DiagramDataDto diagram, List<String> advantages, List<String> limitations,
                          List<KeyPointDto> keyPoints, List<ExamTipDto> examTips, List<String> quickTakeaways,
                          boolean continuesOnNextPage, boolean isContinuation, String layoutHint, String styleTheme) {
        this.documentTitle = documentTitle;
        this.pageNumber = pageNumber;
        this.totalPages = totalPages;
        this.topicTitle = topicTitle;
        this.topicSubtitle = topicSubtitle;
        this.categoryBadge = categoryBadge;
        this.difficultyLevel = difficultyLevel;
        this.domain = domain;
        this.subdomain = subdomain;
        this.pagePartTitle = pagePartTitle;
        this.definition = definition;
        this.purpose = purpose;
        this.mainIdea = mainIdea;
        this.simpleExplanation = simpleExplanation;
        this.sections = sections;
        this.algorithm = algorithm;
        this.pseudocode = pseudocode;
        this.example = example;
        this.formula = formula;
        this.complexity = complexity;
        this.diagram = diagram;
        this.advantages = advantages;
        this.limitations = limitations;
        this.keyPoints = keyPoints;
        this.examTips = examTips;
        this.quickTakeaways = quickTakeaways;
        this.continuesOnNextPage = continuesOnNextPage;
        this.isContinuation = isContinuation;
        this.layoutHint = layoutHint;
        this.styleTheme = styleTheme;
    }

    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }
    public String getSubdomain() { return subdomain; }
    public void setSubdomain(String subdomain) { this.subdomain = subdomain; }

    public String getDocumentTitle() { return documentTitle; }
    public void setDocumentTitle(String documentTitle) { this.documentTitle = documentTitle; }
    public int getPageNumber() { return pageNumber; }
    public void setPageNumber(int pageNumber) { this.pageNumber = pageNumber; }
    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    public String getTopicTitle() { return topicTitle; }
    public void setTopicTitle(String topicTitle) { this.topicTitle = topicTitle; }
    public String getTopicSubtitle() { return topicSubtitle; }
    public void setTopicSubtitle(String topicSubtitle) { this.topicSubtitle = topicSubtitle; }
    public String getCategoryBadge() { return categoryBadge; }
    public void setCategoryBadge(String categoryBadge) { this.categoryBadge = categoryBadge; }
    public String getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(String difficultyLevel) { this.difficultyLevel = difficultyLevel; }
    public String getPagePartTitle() { return pagePartTitle; }
    public void setPagePartTitle(String pagePartTitle) { this.pagePartTitle = pagePartTitle; }
    public String getDefinition() { return definition; }
    public void setDefinition(String definition) { this.definition = definition; }
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public String getMainIdea() { return mainIdea; }
    public void setMainIdea(String mainIdea) { this.mainIdea = mainIdea; }
    public String getSimpleExplanation() { return simpleExplanation; }
    public void setSimpleExplanation(String simpleExplanation) { this.simpleExplanation = simpleExplanation; }
    public List<SectionDto> getSections() { return sections; }
    public void setSections(List<SectionDto> sections) { this.sections = sections; }
    public List<AlgorithmStepDto> getAlgorithm() { return algorithm; }
    public void setAlgorithm(List<AlgorithmStepDto> algorithm) { this.algorithm = algorithm; }
    public String getPseudocode() { return pseudocode; }
    public void setPseudocode(String pseudocode) { this.pseudocode = pseudocode; }
    public ExampleDto getExample() { return example; }
    public void setExample(ExampleDto example) { this.example = example; }
    public FormulaDto getFormula() { return formula; }
    public void setFormula(FormulaDto formula) { this.formula = formula; }
    public ComplexityDto getComplexity() { return complexity; }
    public void setComplexity(ComplexityDto complexity) { this.complexity = complexity; }
    public DiagramDataDto getDiagram() { return diagram; }
    public void setDiagram(DiagramDataDto diagram) { this.diagram = diagram; }
    public ComparisonTableDto getComparisonTable() { return comparisonTable; }
    public void setComparisonTable(ComparisonTableDto comparisonTable) { this.comparisonTable = comparisonTable; }
    public List<String> getAdvantages() { return advantages; }
    public void setAdvantages(List<String> advantages) { this.advantages = advantages; }
    public List<String> getLimitations() { return limitations; }
    public void setLimitations(List<String> limitations) { this.limitations = limitations; }
    public List<KeyPointDto> getKeyPoints() { return keyPoints; }
    public void setKeyPoints(List<KeyPointDto> keyPoints) { this.keyPoints = keyPoints; }
    public List<ExamTipDto> getExamTips() { return examTips; }
    public void setExamTips(List<ExamTipDto> examTips) { this.examTips = examTips; }
    public List<String> getQuickTakeaways() { return quickTakeaways; }
    public void setQuickTakeaways(List<String> quickTakeaways) { this.quickTakeaways = quickTakeaways; }
    public boolean isContinuesOnNextPage() { return continuesOnNextPage; }
    public boolean getContinuesOnNextPage() { return continuesOnNextPage; }
    public void setContinuesOnNextPage(boolean continuesOnNextPage) { this.continuesOnNextPage = continuesOnNextPage; }
    public boolean isContinuation() { return isContinuation; }
    public boolean getIsContinuation() { return isContinuation; }
    public void setContinuation(boolean continuation) { isContinuation = continuation; }
    public void setIsContinuation(boolean continuation) { isContinuation = continuation; }
    public String getLayoutHint() { return layoutHint; }
    public void setLayoutHint(String layoutHint) { this.layoutHint = layoutHint; }
    public String getStyleTheme() { return styleTheme; }
    public void setStyleTheme(String styleTheme) { this.styleTheme = styleTheme; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private String documentTitle;
        private int pageNumber;
        private int totalPages;
        private String topicTitle;
        private String topicSubtitle;
        private String categoryBadge;
        private String difficultyLevel;
        private String domain;
        private String subdomain;
        private String pagePartTitle;
        private String definition;
        private String purpose;
        private String mainIdea;
        private String simpleExplanation;
        private List<SectionDto> sections;
        private List<AlgorithmStepDto> algorithm;
        private String pseudocode;
        private ExampleDto example;
        private FormulaDto formula;
        private ComplexityDto complexity;
        private DiagramDataDto diagram;
        private ComparisonTableDto comparisonTable;
        private List<String> advantages;
        private List<String> limitations;
        private List<KeyPointDto> keyPoints;
        private List<ExamTipDto> examTips;
        private List<String> quickTakeaways;
        private boolean continuesOnNextPage;
        private boolean isContinuation;
        private String layoutHint;
        private String styleTheme;

        public Builder documentTitle(String documentTitle) { this.documentTitle = documentTitle; return this; }
        public Builder pageNumber(int pageNumber) { this.pageNumber = pageNumber; return this; }
        public Builder totalPages(int totalPages) { this.totalPages = totalPages; return this; }
        public Builder topicTitle(String topicTitle) { this.topicTitle = topicTitle; return this; }
        public Builder topicSubtitle(String topicSubtitle) { this.topicSubtitle = topicSubtitle; return this; }
        public Builder categoryBadge(String categoryBadge) { this.categoryBadge = categoryBadge; return this; }
        public Builder difficultyLevel(String difficultyLevel) { this.difficultyLevel = difficultyLevel; return this; }
        public Builder domain(String domain) { this.domain = domain; return this; }
        public Builder subdomain(String subdomain) { this.subdomain = subdomain; return this; }
        public Builder pagePartTitle(String pagePartTitle) { this.pagePartTitle = pagePartTitle; return this; }
        public Builder definition(String definition) { this.definition = definition; return this; }
        public Builder purpose(String purpose) { this.purpose = purpose; return this; }
        public Builder mainIdea(String mainIdea) { this.mainIdea = mainIdea; return this; }
        public Builder simpleExplanation(String simpleExplanation) { this.simpleExplanation = simpleExplanation; return this; }
        public Builder sections(List<SectionDto> sections) { this.sections = sections; return this; }
        public Builder algorithm(List<AlgorithmStepDto> algorithm) { this.algorithm = algorithm; return this; }
        public Builder pseudocode(String pseudocode) { this.pseudocode = pseudocode; return this; }
        public Builder example(ExampleDto example) { this.example = example; return this; }
        public Builder formula(FormulaDto formula) { this.formula = formula; return this; }
        public Builder complexity(ComplexityDto complexity) { this.complexity = complexity; return this; }
        public Builder diagram(DiagramDataDto diagram) { this.diagram = diagram; return this; }
        public Builder comparisonTable(ComparisonTableDto comparisonTable) { this.comparisonTable = comparisonTable; return this; }
        public Builder advantages(List<String> advantages) { this.advantages = advantages; return this; }
        public Builder limitations(List<String> limitations) { this.limitations = limitations; return this; }
        public Builder keyPoints(List<KeyPointDto> keyPoints) { this.keyPoints = keyPoints; return this; }
        public Builder examTips(List<ExamTipDto> examTips) { this.examTips = examTips; return this; }
        public Builder quickTakeaways(List<String> quickTakeaways) { this.quickTakeaways = quickTakeaways; return this; }
        public Builder continuesOnNextPage(boolean continuesOnNextPage) { this.continuesOnNextPage = continuesOnNextPage; return this; }
        public Builder isContinuation(boolean isContinuation) { this.isContinuation = isContinuation; return this; }
        public Builder layoutHint(String layoutHint) { this.layoutHint = layoutHint; return this; }
        public Builder styleTheme(String styleTheme) { this.styleTheme = styleTheme; return this; }

        public PageContentDto build() {
            PageContentDto dto = new PageContentDto(documentTitle, pageNumber, totalPages, topicTitle, topicSubtitle, categoryBadge,
                    difficultyLevel, domain, subdomain, pagePartTitle, definition, purpose, mainIdea, simpleExplanation,
                    sections, algorithm, pseudocode, example, formula, complexity, diagram, advantages, limitations,
                    keyPoints, examTips, quickTakeaways, continuesOnNextPage, isContinuation, layoutHint, styleTheme);
            dto.setComparisonTable(comparisonTable);
            return dto;
        }
    }
}
