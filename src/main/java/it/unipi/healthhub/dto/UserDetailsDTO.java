package it.unipi.healthhub.dto;

import it.unipi.healthhub.model.mongo.Address;

import java.time.LocalDate;

public class UserDetailsDTO {
    private String fullName;
    private String fiscalCode;
    private LocalDate birthDate;
    private String gender;
    private String personalNumber;
    private String email;
    private Address address;

    // Constructors, getters and setters

    public UserDetailsDTO() {
    }

    public UserDetailsDTO(String fullName, String fiscalCode, LocalDate birthDate, String gender) {
        this.fullName = fullName;
        this.fiscalCode = fiscalCode;
        this.birthDate = birthDate;
        this.gender = gender;
    }

    public UserDetailsDTO(String fullName, String fiscalCode, LocalDate birthDate, String gender, String personalNumber, String email, Address address) {
        this.fullName = fullName;
        this.fiscalCode = fiscalCode;
        this.birthDate = birthDate;
        this.gender = gender;
        this.personalNumber = personalNumber;
        this.email = email;
        this.address = address;
    }

    // Getter e Setter
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFiscalCode(){ return this.fiscalCode; }
    public void setFiscalCode(String fiscalCode){ this.fiscalCode = fiscalCode; }

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

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public Address getAddress() {
        return address;
    }
    public void setAddress(Address address) {
        this.address = address;
    }

    public String getPersonalNumber() {
        return personalNumber;
    }
    public void setPersonalNumber(String personalNumber) {
        this.personalNumber = personalNumber;
    }
}

