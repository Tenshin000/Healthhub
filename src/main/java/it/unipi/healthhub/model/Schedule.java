package it.unipi.healthhub.model;

import java.util.List;

public class Schedule {
    private String week;
    private List<PrenotableSlot> slotList;

    public String getWeek() {
        return week;
    }
    public void setWeek(String week) {
        this.week = week;
    }

    public List<PrenotableSlot> getSlotList() {
        return slotList;
    }
    public void setSlotList(List<PrenotableSlot> slotList) {
        this.slotList = slotList;
    }
}
