package it.unipi.healthhub.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ScheduleDTO {
    private LocalDate week;
    private Map<String, List<PrenotableSlotDTO>> slots;

    // Constructors
    public ScheduleDTO() {}

    public ScheduleDTO(LocalDate week, Map<String, List<PrenotableSlotDTO>> slots) {
        this.week = week;
        this.slots = slots;
    }

    // Getters and Setters
    public LocalDate getWeek() {
        return week;
    }

    public void setWeek(LocalDate week) {
        this.week = week;
    }

    public Map<String, List<PrenotableSlotDTO>> getSlots() {
        return slots;
    }

    public void setSlots(Map<String, List<PrenotableSlotDTO>> slots) {
        this.slots = slots;
    }

    // Nested PrenotableSlotDTO class for slot data
    public static class PrenotableSlotDTO {
        private String start;
        private String end;
        private boolean taken;

        // Constructors
        public PrenotableSlotDTO() {}

        public PrenotableSlotDTO(String start, String end, boolean taken) {
            this.start = start;
            this.end = end;
            this.taken = taken;
        }

        // Getters and Setters
        public String getStart() {
            return start;
        }

        public void setStart(String start) {
            this.start = start;
        }

        public String getEnd() {
            return end;
        }

        public void setEnd(String end) {
            this.end = end;
        }

        public boolean isTaken() {
            return taken;
        }

        public void setTaken(boolean taken) {
            this.taken = taken;
        }
    }
}
