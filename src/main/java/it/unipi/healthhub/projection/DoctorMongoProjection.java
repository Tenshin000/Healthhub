package it.unipi.healthhub.projection;

import it.unipi.healthhub.model.mongo.Doctor;

public class DoctorMongoProjection {
    private Doctor doctor;
    private long score;

    public Doctor getDoctor(){ return doctor; }
    public long getScore(){ return score; };

    public void setScore(long score){ this.score = score; }
}
