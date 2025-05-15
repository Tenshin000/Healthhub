package it.unipi.healthhub.dto;

import java.time.LocalDate;

/**
 * DTO class representing patient's contact and personal information.
 */
public class PatientContactsDTO {
    private String name;
    private String email;
    private String fiscalCode;
    private LocalDate birthDate;
    private String gender;
    private String phoneNumber;

    public PatientContactsDTO(){}

    public PatientContactsDTO(String email, String phoneNumber){
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public PatientContactsDTO(String name, String email, String fiscalCode, LocalDate birthDate, String gender, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.fiscalCode = fiscalCode;
        this.birthDate = birthDate;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFiscalCode() {
        return fiscalCode;
    }

    public void setFiscalCode(String fiscalCode) {
        this.fiscalCode = fiscalCode;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
