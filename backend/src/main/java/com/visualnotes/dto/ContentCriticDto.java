package com.visualnotes.dto;

import java.util.ArrayList;
import java.util.List;

public class ContentCriticDto {
    private boolean valid;
    private boolean answersRequest;
    private boolean scopeCovered;
    private boolean genericFiller;
    private List<String> irrelevantContent = new ArrayList<>();
    private List<String> missingConcepts = new ArrayList<>();
    private List<String> errors = new ArrayList<>();
    private List<String> repairInstructions = new ArrayList<>();

    public ContentCriticDto() {}

    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
    public boolean isAnswersRequest() { return answersRequest; }
    public void setAnswersRequest(boolean answersRequest) { this.answersRequest = answersRequest; }
    public boolean isScopeCovered() { return scopeCovered; }
    public void setScopeCovered(boolean scopeCovered) { this.scopeCovered = scopeCovered; }
    public boolean isGenericFiller() { return genericFiller; }
    public void setGenericFiller(boolean genericFiller) { this.genericFiller = genericFiller; }
    public List<String> getIrrelevantContent() { return irrelevantContent; }
    public void setIrrelevantContent(List<String> irrelevantContent) { this.irrelevantContent = irrelevantContent; }
    public List<String> getMissingConcepts() { return missingConcepts; }
    public void setMissingConcepts(List<String> missingConcepts) { this.missingConcepts = missingConcepts; }
    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }
    public List<String> getRepairInstructions() { return repairInstructions; }
    public void setRepairInstructions(List<String> repairInstructions) { this.repairInstructions = repairInstructions; }
}
