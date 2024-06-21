package it.unipi.healthhub.service;

import it.unipi.healthhub.model.Doctor;
import it.unipi.healthhub.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {
    @Autowired
    private DoctorRepository doctorRepository;
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
}
