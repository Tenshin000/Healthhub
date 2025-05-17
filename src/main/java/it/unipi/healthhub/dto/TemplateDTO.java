package it.unipi.healthhub.dto;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public class TemplateDTO {
    private String id;
    private String name;
    private Map<String, List<SlotDTO>> slots;
    private Boolean isDefault;

    // Constructor
    public TemplateDTO() {
        // Empty constructor needed for JSON deserialization
    }
    public TemplateDTO(String id, String name, Map<String, List<SlotDTO>> slots) {
        this.id = id;
        this.name = name;
        this.slots = slots;
    }

    public TemplateDTO(String id, String name, Map<String, List<SlotDTO>> slots, Boolean isDefault) {
        this.id = id;
        this.name = name;
        this.slots = slots;
        this.isDefault = isDefault;
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

    public Map<String, List<SlotDTO>> getSlots() {
        return slots;
    }

    public void setSlotDTOs(Map<String, List<SlotDTO>> slots) {
        this.slots = slots;
    }

    public Boolean isDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    // Nested SlotDTO class for DTO
    public static class SlotDTO {
        private String start;
        private String end;

        // Constructor
        public SlotDTO() {}

        public SlotDTO(String start, String end) {
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
