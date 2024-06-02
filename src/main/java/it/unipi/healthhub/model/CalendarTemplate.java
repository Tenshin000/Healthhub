package it.unipi.healthhub.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "template")
public class CalendarTemplate {
    @Id
    private String id;
    private List<Slot> slotList;
    private boolean active;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }

    public List<Slot> getSlotList() {
        return slotList;
    }
    public void setSlotList(List<Slot> slotList) {
        this.slotList = slotList;
    }
}
