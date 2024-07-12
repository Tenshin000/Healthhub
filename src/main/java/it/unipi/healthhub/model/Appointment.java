package it.unipi.healthhub.model;

import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

public class Appointment {
    @Id
    private String id;
    private LocalDateTime appointmentDateTime;
    private DoctorInfo doctorInfo;
    private PatientInfo patientInfo;
    private String visitType;
    private String patientNotes;
    private double price;

    // Costruttori
    public Appointment() {}

    public Appointment(String id, LocalDateTime appointmentDateTime, DoctorInfo doctorInfo, PatientInfo patientInfo, String visitType, String patientNotes) {
        this.id = id;
        this.appointmentDateTime = appointmentDateTime;
        this.doctorInfo = doctorInfo;
        this.patientInfo = patientInfo;
        this.visitType = visitType;
        this.patientNotes = patientNotes;
    }

    // Getter e Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getAppointmentDateTime() {
        return appointmentDateTime;
    }

    public void setAppointmentDateTime(LocalDateTime appointmentDateTime) {
        this.appointmentDateTime = appointmentDateTime;
    }

    public DoctorInfo getDoctorInfo() {
        return doctorInfo;
    }

    public void setDoctorInfo(DoctorInfo doctorInfo) {
        this.doctorInfo = doctorInfo;
    }

    public PatientInfo getPatientInfo() {
        return patientInfo;
    }

    public void setPatientInfo(PatientInfo patientInfo) {
        this.patientInfo = patientInfo;
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
        private String doctorId;
        private String doctorName;

        public DoctorInfo() {}

        public DoctorInfo(String doctorId, String doctorName) {
            this.doctorId = doctorId;
            this.doctorName = doctorName;
        }

        public String getDoctorId() {
            return doctorId;
        }

        public void setDoctorId(String doctorId) {
            this.doctorId = doctorId;
        }

        public String getDoctorName() {
            return doctorName;
        }

        public void setDoctorName(String doctorName) {
            this.doctorName = doctorName;
        }
    }

    // Inner class PatientInfo
    public static class PatientInfo {
        private String patientId;
        private String patientName;

        public PatientInfo() {}

        public PatientInfo(String patientId, String patientName) {
            this.patientId = patientId;
            this.patientName = patientName;
        }

        public String getPatientId() {
            return patientId;
        }

        public void setPatientId(String patientId) {
            this.patientId = patientId;
        }

        public String getPatientName() {
            return patientName;
        }

        public void setPatientName(String patientName) {
            this.patientName = patientName;
        }
    }
}
