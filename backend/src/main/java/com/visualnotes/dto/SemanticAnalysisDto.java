package com.visualnotes.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SemanticAnalysisDto {
    private String originalRequest;
    private String mainTopic;
    private String intent; // EXPLAIN, COMPARE, DEFINE, CLASSIFY_TYPES, PROCESS_MECHANISM, DERIVE_FORMULATION, WORKED_EXAMPLE
    private String scope;
    private String relationship;
    private String domain;
    private String subdomain;
    @JsonProperty("multiEntityComparison")
    @JsonAlias({"isMultiEntityComparison", "multiEntityComparison"})
    private boolean isMultiEntityComparison;
    private List<String> targetEntities = new ArrayList<>();
    private List<String> requiredConcepts = new ArrayList<>();
    private List<String> excludedConcepts = new ArrayList<>();
    private boolean requiresDiagram;
    private String suggestedDiagramType;
    private boolean requiresFormula;
    private boolean requiresComparisonTable;
    private boolean requiresAlgorithm;
    private boolean requiresWorkedExample;
    private int suggestedPageCount = 1;

    public SemanticAnalysisDto() {}

    public String getOriginalRequest() { return originalRequest; }
    public void setOriginalRequest(String originalRequest) { this.originalRequest = originalRequest; }
    public String getMainTopic() { return mainTopic; }
    public void setMainTopic(String mainTopic) { this.mainTopic = mainTopic; }
    public String getIntent() { return intent; }
    public void setIntent(String intent) { this.intent = intent; }
    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }
    public String getRelationship() { return relationship; }
    public void setRelationship(String relationship) { this.relationship = relationship; }
    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }
    public String getSubdomain() { return subdomain; }
    public void setSubdomain(String subdomain) { this.subdomain = subdomain; }
    public boolean isMultiEntityComparison() { return isMultiEntityComparison; }
    public void setMultiEntityComparison(boolean multiEntityComparison) { isMultiEntityComparison = multiEntityComparison; }
    public List<String> getTargetEntities() { return targetEntities; }
    public void setTargetEntities(List<String> targetEntities) { this.targetEntities = targetEntities; }
    public List<String> getRequiredConcepts() { return requiredConcepts; }
    public void setRequiredConcepts(List<String> requiredConcepts) { this.requiredConcepts = requiredConcepts; }
    public List<String> getExcludedConcepts() { return excludedConcepts; }
    public void setExcludedConcepts(List<String> excludedConcepts) { this.excludedConcepts = excludedConcepts; }
    public boolean isRequiresDiagram() { return requiresDiagram; }
    public void setRequiresDiagram(boolean requiresDiagram) { this.requiresDiagram = requiresDiagram; }
    public String getSuggestedDiagramType() { return suggestedDiagramType; }
    public void setSuggestedDiagramType(String suggestedDiagramType) { this.suggestedDiagramType = suggestedDiagramType; }
    public boolean isRequiresFormula() { return requiresFormula; }
    public void setRequiresFormula(boolean requiresFormula) { this.requiresFormula = requiresFormula; }
    public boolean isRequiresComparisonTable() { return requiresComparisonTable; }
    public void setRequiresComparisonTable(boolean requiresComparisonTable) { this.requiresComparisonTable = requiresComparisonTable; }
    public boolean isRequiresAlgorithm() { return requiresAlgorithm; }
    public void setRequiresAlgorithm(boolean requiresAlgorithm) { this.requiresAlgorithm = requiresAlgorithm; }
    public boolean isRequiresWorkedExample() { return requiresWorkedExample; }
    public void setRequiresWorkedExample(boolean requiresWorkedExample) { this.requiresWorkedExample = requiresWorkedExample; }
    public int getSuggestedPageCount() { return suggestedPageCount; }
    public void setSuggestedPageCount(int suggestedPageCount) { this.suggestedPageCount = suggestedPageCount; }
}
