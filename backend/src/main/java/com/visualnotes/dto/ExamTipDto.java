package com.visualnotes.dto;

public class ExamTipDto {
    private String tip;
    private String commonMistake;
    private String mnemonic;

    public ExamTipDto() {}
    public ExamTipDto(String tip, String commonMistake, String mnemonic) {
        this.tip = tip;
        this.commonMistake = commonMistake;
        this.mnemonic = mnemonic;
    }

    public String getTip() { return tip; }
    public void setTip(String tip) { this.tip = tip; }
    public String getCommonMistake() { return commonMistake; }
    public void setCommonMistake(String commonMistake) { this.commonMistake = commonMistake; }
    public String getMnemonic() { return mnemonic; }
    public void setMnemonic(String mnemonic) { this.mnemonic = mnemonic; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private String tip;
        private String commonMistake;
        private String mnemonic;
        public Builder tip(String tip) { this.tip = tip; return this; }
        public Builder commonMistake(String commonMistake) { this.commonMistake = commonMistake; return this; }
        public Builder mnemonic(String mnemonic) { this.mnemonic = mnemonic; return this; }
        public ExamTipDto build() { return new ExamTipDto(tip, commonMistake, mnemonic); }
    }
}
