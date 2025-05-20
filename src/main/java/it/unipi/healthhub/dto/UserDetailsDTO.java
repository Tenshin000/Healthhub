
package it.unipi.healthhub.dto;

import java.time.LocalDate;

public class UserDetailsDTO {
    private String fullName;
    private String fiscalCode;
    private LocalDate birthDate;
    private String gender;

    // Constructors
    public UserDetailsDTO() {
    }

    public UserDetailsDTO(String fullName, String fiscalCode, LocalDate birthDate, String gender) {
        this.fullName = fullName;
        this.fiscalCode = fiscalCode;
        this.birthDate = birthDate;
        this.gender = gender;
    }

    // Getters and Setters
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
}
