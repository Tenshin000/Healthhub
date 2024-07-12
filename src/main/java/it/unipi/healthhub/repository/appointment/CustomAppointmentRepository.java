package it.unipi.healthhub.repository.appointment;

import it.unipi.healthhub.model.Appointment;

import java.time.LocalDate;
import java.util.List;

public interface CustomAppointmentRepository {
    public List<Appointment> findByDoctorIdAndDay(String doctorId, LocalDate day);
}
