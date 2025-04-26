package it.unipi.healthhub.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentDTO {
    private LocalDate date;
    private String slot;
    private String service;
    private String patientNotes;

    // Constructor with no arguments
    public AppointmentDTO() {}

    // Constructor with arguments
    public AppointmentDTO(LocalDate date, String slot, String service, String patientNotes) {
        this.date = date;
        this.slot = slot;
        this.service = service;
        this.patientNotes = patientNotes;
    }

    // Getters and Setters
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getSlot() {
        return slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getPatientNotes() {
        return patientNotes;
    }

    public void setPatientNotes(String patientNotes) {
        this.patientNotes = patientNotes;
    }
}
