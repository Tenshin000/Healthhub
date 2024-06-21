package it.unipi.healthhub.repository;

import it.unipi.healthhub.model.Appointment;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface AppointmentRepository extends MongoRepository<Appointment, String>{
    Appointment findByDoctorId(String doctorId);
    Appointment findByPatientId(String patientId);
    Appointment findByDoctorIdAndPatientId(String doctorId, String patientId);

}
