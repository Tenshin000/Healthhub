package it.unipi.healthhub.dto;

public class SpecializationDTO {
    private String specialization;
    private Integer index;

    public SpecializationDTO() {
        // Costruttore vuoto necessario per la deserializzazione JSON
    }

    public SpecializationDTO(String specialization, Integer index) {
        this.specialization = specialization;
        this.index = index;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setName(String specialization) {
        this.specialization = specialization;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}

