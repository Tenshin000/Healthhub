package it.unipi.healthhub.model.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document(collection = "templates")
public class CalendarTemplate {
    @Id
    private String id;
    private String name;
    private Map<String, List<Slot>> slots;
    private boolean isDefault;

    // Constructor
    public CalendarTemplate() {}

    public CalendarTemplate(String id, String name, Map<String, List<Slot>> slots, boolean isDefault) {
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

    public Map<String, List<Slot>> getSlots() {
        return slots;
    }

    public void setSlots(Map<String, List<Slot>> slots) {
        this.slots = slots;
    }

    public boolean isDefault(){
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public String toString() {
        return "CalendarTemplate{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", slots=" + slots +
                ", default=" + isDefault +
                '}';
    }

}
