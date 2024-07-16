package it.unipi.healthhub.service;

import it.unipi.healthhub.model.mongo.Appointment;
import it.unipi.healthhub.repository.mongo.AppointmentMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {
    @Autowired
    private AppointmentMongoRepository appointmentRepository;

    public List<Appointment> getAllAppointment(){
        return appointmentRepository.findAll();
    }

    public Optional<Appointment> getAppointmentById(String id){
        return appointmentRepository.findById(id);
    }

    public Appointment createAppointment(Appointment appointment){
        return appointmentRepository.save(appointment);
    }

    public Appointment updateAppointment(String id, Appointment appointment){
        Optional<Appointment> appointmentOptional = appointmentRepository.findById(id);
        if(appointmentOptional.isPresent()){
            Appointment appointmentToUpdate = appointmentOptional.get();
            // Update the appointment
            return appointmentRepository.save(appointmentToUpdate);
        }
        return null;
    }

    public void deleteAppointment(String id){
        appointmentRepository.deleteById(id);
    }

    public List<Appointment> getAppointmentsForDay(String doctorId, LocalDate day) {
        return appointmentRepository.findByDoctorIdAndDay(doctorId, day);
    }
}
