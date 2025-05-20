package it.unipi.healthhub.dto;

import javax.print.Doc;
import java.time.LocalDate;

public class DoctorDetailsDTO extends UserDetailsDTO{
    private String orderRegistrationNumber;
    private String email;

    public DoctorDetailsDTO(){ super(); }
    public DoctorDetailsDTO(String fullName, String fiscalCode, LocalDate birthDate, String gender, String orderRegistrationNumber, String email){
        super(fullName, fiscalCode, birthDate, gender);
        this.orderRegistrationNumber = orderRegistrationNumber;
        this.email = email;
    }

    public String getOrderRegistrationNumber(){
        return orderRegistrationNumber;
    }
    public void setOrderRegistrationNumber(String orderRegistrationNumber){
        this.orderRegistrationNumber = orderRegistrationNumber;
    }

    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email = email;
    }
}
