package it.unipi.healthhub.service;

import it.unipi.healthhub.model.Doctor;
import it.unipi.healthhub.model.User;
import it.unipi.healthhub.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {
    @Autowired
    private DoctorRepository doctorRepository;

    public List<Doctor> searchDoctors(String query) {
        if (query != null && !query.isEmpty()) {
            return doctorRepository.findByNameContainingOrSpecializationsContainingOrAddressContaining(query, query, query);
        } else {
            return doctorRepository.findAll();
        }
    }
    public List<Doctor> getAllDoctor(){
        return doctorRepository.findAll();
    }

    public Optional<Doctor> getDoctorById(String id){
        return doctorRepository.findById(id);
    }

    public Doctor createDoctor(Doctor doctor){
        return doctorRepository.save(doctor);
    }

    public Doctor updateDoctor(String id, Doctor doctor){
        Optional<Doctor> doctorOptional = doctorRepository.findById(id);
        if(doctorOptional.isPresent()){
            Doctor doctorToUpdate = doctorOptional.get();
            // Update the doctor
            return doctorRepository.save(doctorToUpdate);
        }
        return null;
    }

    public void deleteDoctor(String id){
        doctorRepository.deleteById(id);
    }

    public Doctor loginDoctor(String username, String password) {
        Doctor doctor = doctorRepository.findByUsername(username);
        System.out.println(doctor);
        System.out.println(username);

        if (doctor != null && doctor.getPassword().equals(password)) {
            return doctor;
        }

        return null;
    }
}
