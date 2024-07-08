package it.unipi.healthhub.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "doctors")
public class Doctor extends User {
    private List<Service> services;
    private int endorsementCount;
    private List<Review> reviews;
    private List<Appointment> appointments;
    private List<Schedule> schedule;
    private List<String> calendarTemplates; // List of references (foreign keys) to CalendarTemplate
    private List<String> specializations;
    @Field("phone_numbers")
    private List<String> phoneNumbers;


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

    public List<Review> getReviews() {
        return reviews;
    }
    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }
    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    public List<Schedule> getSchedule() {
        return schedule;
    }
    public void setSchedule(List<Schedule> schedule) {
        this.schedule = schedule;
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

    public String toString() {
        return "User{" +
                ", username='" + getUsername() + '\'' +
                ", password='" + getPassword() + '\'' +
                ", age=" + getAge() + "}";
    }

}