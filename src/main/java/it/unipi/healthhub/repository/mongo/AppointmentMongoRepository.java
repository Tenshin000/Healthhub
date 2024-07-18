package it.unipi.healthhub.repository.mongo;

import it.unipi.healthhub.model.mongo.Appointment;
import it.unipi.healthhub.repository.mongo.appointment.CustomAppointmentMongoRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;


public interface AppointmentMongoRepository extends MongoRepository<Appointment, String>, CustomAppointmentMongoRepository {
}
