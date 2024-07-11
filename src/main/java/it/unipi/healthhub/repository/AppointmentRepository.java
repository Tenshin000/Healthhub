package it.unipi.healthhub.repository;

import it.unipi.healthhub.model.Appointment;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface AppointmentRepository extends MongoRepository<Appointment, String>{

}
