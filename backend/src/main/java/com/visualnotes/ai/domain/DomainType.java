package com.visualnotes.ai.domain;

public enum DomainType {
    ALGORITHMS("Algorithms & Complexity"),
    DATA_STRUCTURES("Data Structures"),
    PROGRAMMING("Programming & Software Development"),
    DBMS("Database Management Systems"),
    OPERATING_SYSTEMS("Operating Systems"),
    COMPUTER_NETWORKS("Computer Networks & Protocols"),
    AI_ML("Artificial Intelligence & Machine Learning"),
    MATHEMATICS("Mathematics & Statistics"),
    PHYSICS("Physics & Mechanics"),
    CHEMISTRY("Chemistry & Chemical Engineering"),
    BIOLOGY("Biology & Life Sciences"),
    ELECTRONICS("Electronics & Digital Logic"),
    GENERAL_THEORY("General Academic Theory");

    private final String displayName;

    DomainType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
