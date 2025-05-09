package it.unipi.healthhub.dto;

import it.unipi.healthhub.model.mongo.Address;
import it.unipi.healthhub.model.mongo.Doctor;

import java.util.ArrayList;
import java.util.List;

public class DoctorDTO {
    protected String name;
    protected List<String> specializations;
    protected Address address;

    public DoctorDTO() {
        specializations = new ArrayList<String>();
    }

    public DoctorDTO(String name, List<String> specializations, Address address) {
        this.name = name;
        this.specializations = specializations;
        this.address = address;
    }

    public DoctorDTO(Doctor doctor){
        this.name = doctor.getName();
        this.specializations = doctor.getSpecializations();
        this.address = doctor.getAddress();
    }

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
