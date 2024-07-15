package it.unipi.healthhub.dao.mongo;

public class PrenotableSlot extends Slot{
    private boolean taken;

    public PrenotableSlot() {
        super();
    }

    public PrenotableSlot(String start, String end, boolean taken) {
        super(start, end);
        this.taken = taken;
    }

    public boolean isTaken() {
        return taken;
    }

    public void setTaken(boolean taken) {
        this.taken = taken;
    }
}
