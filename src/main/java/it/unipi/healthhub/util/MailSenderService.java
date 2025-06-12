package it.unipi.healthhub.util;

import it.unipi.healthhub.model.mongo.Appointment;

public interface MailSenderService{
    boolean sendDeletedAppointmentMailByDoctor(Appointment appointment);
    boolean sendDeletedAppointmentMailByPatient(Appointment appointment);
    boolean sendDeletedPastAppointmentMailByDoctor(Appointment appointment);
    boolean sendPasswordResetLink(String to, String resetLink);
}
