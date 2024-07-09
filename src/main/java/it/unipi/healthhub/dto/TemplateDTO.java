package it.unipi.healthhub.dto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TemplateDTO {
    private String id;
    private String name;
    private Map<String, List<Slot>> slots;

    // Constructor
    public TemplateDTO(String id, String name, Map<String, List<Slot>> slots) {
        this.id = id;
        this.name = name;
        this.slots = slots;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, List<Slot>> getSlots() {
        return slots;
    }

    public void setSlots(Map<String, List<Slot>> slots) {
        this.slots = slots;
    }

    // Nested Slot class for DTO
    public static class Slot {
        private String start;
        private String end;

        // Constructor
        public Slot() {}

        public Slot(String start, String end) {
            this.start = start;
            this.end = end;
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
    }
}
