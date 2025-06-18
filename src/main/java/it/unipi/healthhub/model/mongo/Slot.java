package it.unipi.healthhub.model.mongo;

public class Slot {
    protected String start;
    protected String end;

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

    public String toString() {
        return "Slot{" +
                "start='" + start + '\'' +
                ", end='" + end + '\'' +
                '}';
    }
}
