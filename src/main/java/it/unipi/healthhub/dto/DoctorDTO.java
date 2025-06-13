package it.unipi.healthhub.dto;

import it.unipi.healthhub.model.mongo.Address;
import it.unipi.healthhub.model.mongo.Doctor;
import it.unipi.healthhub.model.neo4j.DoctorDAO;

import java.util.ArrayList;
import java.util.List;

public class DoctorDTO {
    protected String id;
    protected String name;
    protected List<String> specializations;
    protected Address address;

    public DoctorDTO() {
        specializations = new ArrayList<String>();
    }

    public DoctorDTO(String id, String name, List<String> specializations, Address address) {
        this.id = id;
        this.name = name;
        this.specializations = specializations;
        this.address = address;
    }

    public DoctorDTO(Doctor doctor){
        this.id = doctor.getId();
        this.name = doctor.getName();
        this.specializations = doctor.getSpecializations();
        this.address = doctor.getAddress();
    }

    public DoctorDTO(DoctorDAO doctorDAO) {
        this.id = doctorDAO.getId();
        this.name = doctorDAO.getName();
        this.specializations = doctorDAO.getSpecializations();
        this.address = null; // Address is not part of DoctorDAO, so we set it to null
    }

    public String getId() {
        return id;
    }
    public void setId(String id) { this.id = id; }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public List<String> getSpecializations() {
        return specializations;
    }
    public void setSpecializations(List<String> specializations) {
        this.specializations = specializations;
    }

    public Address getAddress() {
        return address;
    }
    public void setAddress(Address address) {
        this.address = address;
    }
}
