package it.unipi.healthhub.model.mongo;

public class Address {
    private String street;
    private String city;
    private String province;
    private String postalCode;
    private String country;

    // Constructor
    public Address(String street, String city, String province, String postalCode, String country){
        this.street = street;
        this.city = city;
        this.province = province;
        this.postalCode = postalCode;
        this.country = country;
    }

    // Getters and Setters
    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String toString() {
        return street + ", " + city + ", " + province + ", " + postalCode + ", " + country;
    }

    public String toShortString() {
        return street + ", " + city;
    }
}