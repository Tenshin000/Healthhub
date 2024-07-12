package it.unipi.healthhub.repository.appointment;

import it.unipi.healthhub.model.Appointment;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface CustomAppointmentRepository {
    public List<Appointment> findByDoctorIdAndDay(String doctorId, LocalDate day);
    public Map<String, Integer> getVisitsCountByTypeForDoctor(String doctorId);

}
