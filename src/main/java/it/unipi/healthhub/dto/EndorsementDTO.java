package it.unipi.healthhub.dto;

public class EndorsementDTO {
    private Integer endorsementCount;
    private boolean hasEndorsed;


    public EndorsementDTO() {
    }

    public EndorsementDTO(Integer endorsementCount, boolean hasEndorsed) {
        this.endorsementCount = endorsementCount;
        this.hasEndorsed = hasEndorsed;
    }

    public Integer getEndorsementCount() {
        return endorsementCount;
    }

    public void setEndorsementCount(Integer endorsementCount) {
        this.endorsementCount = endorsementCount;
    }

    public boolean isHasEndorsed() {
        return hasEndorsed;
    }

    public void setHasEndorsed(boolean hasEndorsed) {
        this.hasEndorsed = hasEndorsed;
    }
}
