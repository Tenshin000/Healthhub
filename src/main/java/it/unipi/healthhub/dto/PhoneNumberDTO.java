package it.unipi.healthhub.dto;

public class PhoneNumberDTO {
    private String phoneNumber;
    private Integer index;

    public PhoneNumberDTO(String phoneNumber, Integer index) {
        this.phoneNumber = phoneNumber;
        this.index = index;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}
