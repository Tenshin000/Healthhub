package it.unipi.healthhub.dao.mongo;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class Schedule {
    private LocalDate week;
    private Map<String, List<PrenotableSlot>> slots;

    // Getters and Setters
    public LocalDate getWeek() {
        return week;
    }

    public void setWeek(LocalDate week) {
        this.week = week;
    }

    public Map<String, List<PrenotableSlot>> getSlots() {
        return slots;
    }

    public void setSlots(Map<String, List<PrenotableSlot>> slots) {
        this.slots = slots;
    }
}
