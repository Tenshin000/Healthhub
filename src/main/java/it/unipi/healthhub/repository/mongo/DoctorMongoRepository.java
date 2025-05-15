package it.unipi.healthhub.repository.mongo;

import it.unipi.healthhub.model.mongo.Doctor;

import it.unipi.healthhub.repository.mongo.doctor.CustomDoctorMongoRepository;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DoctorMongoRepository extends MongoRepository<Doctor, String>, CustomDoctorMongoRepository {
    Doctor findByUsername(String username);
    Doctor findByEmail(String email);
    List<Doctor> findBySpecializations(String specialization);
    List<Doctor> findByServices(String service);
    List<Doctor> findByPhoneNumbers(String phoneNumber);
    List<Doctor> findByEndorsementCount(int endorsementCount);
}
