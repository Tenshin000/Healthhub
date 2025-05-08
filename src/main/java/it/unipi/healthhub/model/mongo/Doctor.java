package it.unipi.healthhub.model.mongo;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "doctors")
public class Doctor extends User {
    protected String orderRegistrationNumber;
    private List<Service> services;
    private int endorsementCount;
    private int reviewCount;
    private List<Review> reviews;
    private List<Schedule> schedules;
    private List<String> calendarTemplates;
    private List<String> specializations;
    private List<String> phoneNumbers;

    public Doctor() {
        super();
        services = new ArrayList<>();
        reviews = new ArrayList<>();
        schedules = new ArrayList<>();
        calendarTemplates = new ArrayList<>();
        specializations = new ArrayList<>();
        phoneNumbers = new ArrayList<>();
    }

    public String getOrderRegistrationNumber() { return orderRegistrationNumber; }
    public void setOrderRegistrationNumber(String orderRegistrationNumber) { this.orderRegistrationNumber = orderRegistrationNumber; }

    public List<Service> getServices() {
        return services;
    }
    public void setServices(List<Service> services) {
        this.services = services;
    }

    public int getEndorsementCount() {
        return endorsementCount;
    }
    public void setEndorsementCount(int endorsementCount) {
        this.endorsementCount = endorsementCount;
    }

    public int getReviewCountCount() {
        return reviewCount;
    }
    public void setReviewCountCount(int reviewCount) { this.reviewCount = reviewCount; }

    public List<Review> getReviews() {
        return reviews;
    }
    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }
    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
    }

    public List<String> getCalendarTemplates() {
        return calendarTemplates;
    }
    public void setCalendarTemplates(List<String> calendarTemplates) {
        this.calendarTemplates = calendarTemplates;
    }

    public List<String> getSpecializations() {
        return specializations;
    }
    public void setSpecializations(List<String> specializations) {
        this.specializations = specializations;
    }

    public List<String> getPhoneNumbers() {
        return phoneNumbers;
    }
    public void setPhoneNumbers(List<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public String getId(){
        return super.getId();
    }

    public String toString() {
        return "User{" +
                ", username='" + getUsername() + '\'' +
                ", password='" + getPassword() + '\'' +
                ", dob=" + dob + "}";
    }
}