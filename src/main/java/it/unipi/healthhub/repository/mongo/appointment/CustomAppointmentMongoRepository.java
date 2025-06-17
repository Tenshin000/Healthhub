package it.unipi.healthhub.repository.mongo.appointment;

import it.unipi.healthhub.model.mongo.Appointment;
import it.unipi.healthhub.model.mongo.Doctor;
import it.unipi.healthhub.model.mongo.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface CustomAppointmentMongoRepository {
    List<Appointment> findByDoctorIdAndDay(String doctorId, LocalDate day);
    void updateDoctorInfo(Doctor updatedDoctor);
    void updatePatientInfo(User updatedUser);
    Map<String, Integer> getVisitsCountByTypeForDoctor(String doctorId);
    Map<String, Double> getEarningsByYearForDoctor(String doctorId, Integer year);
    Map<String, Integer> getVisitsCountByDayForDoctorWeek(String doctorId, Integer week, Integer year);
    List<Appointment> findByPatientIdFromDate(String patientId, LocalDate date);
    List<Appointment> findByPatientIdBeforeDate(String patientId, LocalDate date);
    Integer findNewPatientsVisitedByDoctorInCurrentMonth(String doctorId, Integer year, Integer month);
    List<Appointment> findByDoctorIdAndWeek(String doctorId, Integer week, Integer year);
    boolean hasPastAppointment(String doctorId, String patientId);
    void updatePatientName(String id, String patientName);
    int getVisitsCountByDoctorAndPatient(String doctorId, String patientId);
}
