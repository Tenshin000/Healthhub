package it.unipi.healthhub.repository.appointment;

import it.unipi.healthhub.model.Appointment;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface CustomAppointmentRepository {
    List<Appointment> findByDoctorIdAndDay(String doctorId, LocalDate day);
    Map<String, Integer> getVisitsCountByTypeForDoctor(String doctorId);
    Map<String, Double> getEarningsByYearForDoctor(String doctorId, Integer year);
    Map<String, Integer> getVisitsCountByDayForDoctorWeek(String doctorId, Integer week, Integer year);
}
