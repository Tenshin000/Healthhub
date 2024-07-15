package it.unipi.healthhub.dao.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Document(collection = "users")
public class User {
    @Id
    protected String id;

    protected String username;
    //private String fiscalCode;
    protected String name;
    protected String password;
    protected int age;
    protected LocalDate dob;
    protected String gender;
//  private List<String> phoneNumbers;
    protected String email;
    protected Address address;
    protected List<String> endorsedDoctors;


//  public String getFiscalCode() { return fiscalCode; }
//  public void setFiscalCode(String fiscalCode) { this.fiscalCode = fiscalCode; }

    public String getId() {
        return id;
    }

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

    public int getAge() {
        return age;
    }
    public void setAge(int age) {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    //public static User parseDocument(){}

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }
    public LocalDate getDob() {
        return dob;
    }

    public String toString() {
        return "User{" +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", age=" + age + "}";
    }

    public List<String> getEndorsedDoctors() {
        return endorsedDoctors;
    }

    public void setEndorsedDoctors(List<String> endorsedDoctors) {
        this.endorsedDoctors = endorsedDoctors;
    }
}

