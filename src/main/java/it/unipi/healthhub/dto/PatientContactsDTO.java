package it.unipi.healthhub.dto;

public class PatientContactsDTO {
    private String email;
    private String phoneNumber;
    private String street;
    private String city;
    private String province;
    private String postalCode;
    private String country;

    public PatientContactsDTO(){}

    public PatientContactsDTO(String email, String phoneNumber){
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public PatientContactsDTO(String email, String phoneNumber, String street, String city, String province, String postalCode, String country){
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.street = street;
        this.city = city;
        this.province = province;
        this.postalCode = postalCode;
        this.country = country;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
}
