package it.unipi.healthhub.dao.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document(collection = "template")
public class CalendarTemplate {
    @Id
    private String id;
    private String name;
    private Map<String, List<Slot>> slots;
    private boolean active;

    // Constructor
    public CalendarTemplate() {}

    public CalendarTemplate(String id, String name, Map<String, List<Slot>> slots, boolean active) {
        this.id = id;
        this.name = name;
        this.slots = slots;
        this.active = active;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String toString() {
        return "CalendarTemplate{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", slots=" + slots +
                ", active=" + active +
                '}';
    }

}
