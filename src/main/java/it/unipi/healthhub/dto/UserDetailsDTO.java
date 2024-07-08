package it.unipi.healthhub.dto;

import java.time.LocalDate;

public class UserDetailsDTO {
    private String fullName;
    private LocalDate birthDate;
    private String gender;

    // Costruttori, getter e setter

    public UserDetailsDTO() {
    }

    public UserDetailsDTO(String fullName, LocalDate birthDate, String gender) {
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.gender = gender;
    }

    // Getter e Setter
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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
}

