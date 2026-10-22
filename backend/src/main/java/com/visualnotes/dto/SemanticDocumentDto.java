package com.visualnotes.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SemanticDocumentDto {
    private String topicTitle;
    private String topicSubtitle;
    private String categoryBadge;
    private String domain;
    private String subdomain;
    private String difficultyLevel;
    private String definition;
    private String simpleExplanation;
    private String purpose;
    private List<SectionDto> sections = new ArrayList<>();
    private ComparisonTableDto comparisonTable;
    private FormulaDto formula;
    private List<AlgorithmStepDto> algorithm;
    private String pseudocode;
    private ExampleDto example;
    private ComplexityDto complexity;
    private DiagramDataDto diagram;
    private List<KeyPointDto> keyPoints = new ArrayList<>();
    private List<ExamTipDto> examTips = new ArrayList<>();

    public SemanticDocumentDto() {}

    public String getTopicTitle() { return topicTitle; }
    public void setTopicTitle(String topicTitle) { this.topicTitle = topicTitle; }
    public String getTopicSubtitle() { return topicSubtitle; }
    public void setTopicSubtitle(String topicSubtitle) { this.topicSubtitle = topicSubtitle; }
    public String getCategoryBadge() { return categoryBadge; }
    public void setCategoryBadge(String categoryBadge) { this.categoryBadge = categoryBadge; }
    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }
    public String getSubdomain() { return subdomain; }
    public void setSubdomain(String subdomain) { this.subdomain = subdomain; }
    public String getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(String difficultyLevel) { this.difficultyLevel = difficultyLevel; }
    public String getDefinition() { return definition; }
    public void setDefinition(String definition) { this.definition = definition; }
    public String getSimpleExplanation() { return simpleExplanation; }
    public void setSimpleExplanation(String simpleExplanation) { this.simpleExplanation = simpleExplanation; }
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public List<SectionDto> getSections() { return sections; }
    public void setSections(List<SectionDto> sections) { this.sections = sections; }
    public ComparisonTableDto getComparisonTable() { return comparisonTable; }
    public void setComparisonTable(ComparisonTableDto comparisonTable) { this.comparisonTable = comparisonTable; }
    public FormulaDto getFormula() { return formula; }
    public void setFormula(FormulaDto formula) { this.formula = formula; }
    public List<AlgorithmStepDto> getAlgorithm() { return algorithm; }
    public void setAlgorithm(List<AlgorithmStepDto> algorithm) { this.algorithm = algorithm; }
    public String getPseudocode() { return pseudocode; }
    public void setPseudocode(String pseudocode) { this.pseudocode = pseudocode; }
    public ExampleDto getExample() { return example; }
    public void setExample(ExampleDto example) { this.example = example; }
    public ComplexityDto getComplexity() { return complexity; }
    public void setComplexity(ComplexityDto complexity) { this.complexity = complexity; }
    public DiagramDataDto getDiagram() { return diagram; }
    public void setDiagram(DiagramDataDto diagram) { this.diagram = diagram; }
    public List<KeyPointDto> getKeyPoints() { return keyPoints; }
    public void setKeyPoints(List<KeyPointDto> keyPoints) { this.keyPoints = keyPoints; }
    public List<ExamTipDto> getExamTips() { return examTips; }
    public void setExamTips(List<ExamTipDto> examTips) { this.examTips = examTips; }
}
