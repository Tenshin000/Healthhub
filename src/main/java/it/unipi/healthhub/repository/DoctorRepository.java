package it.unipi.healthhub.repository;

import it.unipi.healthhub.model.Doctor;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DoctorRepository extends MongoRepository<Doctor, String> {
    Doctor findByUsername(String username);
    Doctor findById(ObjectId id);
    List<Doctor> findBySpecializations(String specialization);
    List<Doctor> findByServices(String service);
    List<Doctor> findByPhoneNumbers(String phoneNumber);
    List<Doctor> findByEndorsementCount(int endorsementCount);
    List<Doctor> findByNameContainingOrSpecializationsContainingOrAddressContaining(String query, String query1, String query2);
}
