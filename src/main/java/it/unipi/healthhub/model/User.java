package it.unipi.healthhub.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "users")
public class User {
    @Id
    private String id;
    
    private String username;
//  private String fiscalCode;
    private String name;
//  private String passwordHash;
    private int age;
    private String gender;
//  private List<String> phoneNumbers;
    private String email;
    private Address address;


//  public String getFiscalCode() { return fiscalCode; }
//  public void setFiscalCode(String fiscalCode) { this.fiscalCode = fiscalCode; }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

//  public String getPasswordHash() {return passwordHash;}
//  public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public int getBirthDate() {
        return age;
    }
    public void setBirthDate(int age) {
        this.age = age;
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

    //public static User parseDocument(){}
}

