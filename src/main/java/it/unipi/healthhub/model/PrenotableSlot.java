package it.unipi.healthhub.model;

public class PrenotableSlot extends Slot{
    private boolean taken;

    public boolean isTaken() {
        return taken;
    }

    public void setTaken(boolean taken) {
        this.taken = taken;
    }
}
