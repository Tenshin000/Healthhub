package it.unipi.healthhub.dto;

import javax.print.Doc;
import java.time.LocalDate;

public class DoctorDetailsDTO extends UserDetailsDTO{
    private String orderRegistrationNumber;

    public DoctorDetailsDTO(){ super(); }
    public DoctorDetailsDTO(String fullName, String fiscalCode, LocalDate birthDate, String gender, String orderRegistrationNumber){
        super(fullName, fiscalCode, birthDate, gender);
        this.orderRegistrationNumber = orderRegistrationNumber;
    }

    public String getOrderRegistrationNumber(){
        return orderRegistrationNumber;
    }
    public void setOrderRegistrationNumber(String orderRegistrationNumber){
        this.orderRegistrationNumber = orderRegistrationNumber;
    }
}
