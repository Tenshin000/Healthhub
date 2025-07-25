package it.unipi.healthhub.model.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "appointments")
public class Appointment {
    @Id
    private String id;
    private LocalDateTime date;
    private DoctorInfo doctor;
    private PatientInfo patient;
    private String visitType;
    private String patientNotes;
    private double price;

    // Constructors
    public Appointment() {}

    public Appointment(String id, LocalDateTime appointmentDateTime, DoctorInfo doctorInfo, PatientInfo patientInfo, String visitType, String patientNotes) {
        this.id = id;
        this.date = appointmentDateTime;
        this.doctor = doctorInfo;
        this.patient = patientInfo;
        this.visitType = visitType;
        this.patientNotes = patientNotes;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public DoctorInfo getDoctor() {
        return doctor;
    }

    public void setDoctor(DoctorInfo doctor) {
        this.doctor = doctor;
    }

    public PatientInfo getPatient() {
        return patient;
    }

    public void setPatient(PatientInfo patient) {
        this.patient = patient;
    }

    public String getVisitType() {
        return visitType;
    }

    public void setVisitType(String visitType) {
        this.visitType = visitType;
    }

    public String getPatientNotes() {
        return patientNotes;
    }

    public void setPatientNotes(String patientNotes) {
        this.patientNotes = patientNotes;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    // Inner class DoctorInfo
    public static class DoctorInfo {
        private String id;
        private String name;
        private Address address;
        private String email;

        public DoctorInfo() {}

        public DoctorInfo(String doctorId, String doctorName, Address address, String email) {
            this.id = doctorId;
            this.name = doctorName;
            this.address = address;
            this.email = email;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    // Inner class PatientInfo
    public static class PatientInfo {
        private String id;
        private String name;
        private String email;
        private String gender;

        public PatientInfo() {}

        public PatientInfo(String patientId, String patientName, String email, String gender) {
            this.id = patientId;
            this.name = patientName;
            this.email = email;
            this.gender = gender;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }
    }
}
