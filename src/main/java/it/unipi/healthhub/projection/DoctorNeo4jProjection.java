package it.unipi.healthhub.projection;

import it.unipi.healthhub.model.neo4j.DoctorDAO;

public class DoctorNeo4jProjection {
    private DoctorDAO doctor;
    private long score;

    public DoctorDAO getDoctor(){ return doctor; }
    public long getScore(){ return score; };
}
