package it.unipi.healthhub.service;

import com.mongodb.client.model.Filters;
import it.unipi.healthhub.model.mongo.Address;
import it.unipi.healthhub.model.mongo.Appointment;
import it.unipi.healthhub.model.mongo.Doctor;
import it.unipi.healthhub.model.mongo.User;
import it.unipi.healthhub.repository.mongo.AppointmentMongoRepository;
import it.unipi.healthhub.util.FakeMailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
public class AppointmentService {
    @Autowired
    private AppointmentMongoRepository appointmentRepository;

    private String sanitizeForMongo(String input) {
        if (input == null)
            return null;
        // Remove any '$' or '.' that may create operator injection
        return input.replaceAll("[\\$]", "_");
    }

    // Generic helper to sanitize a field avoiding code repetition
    private void sanitizeField(Consumer<String> setter, Supplier<String> getter) {
        setter.accept(sanitizeForMongo(getter.get()));
    }

    private void sanitizeAppointmentMongo(Appointment appointment) {
        if (appointment == null)
            return;

        // Sanitize appointment fields
        sanitizeField(appointment::setVisitType, appointment::getVisitType);
        sanitizeField(appointment::setPatientNotes, appointment::getPatientNotes);

        // Sanitize doctor info
        Appointment.DoctorInfo doc = appointment.getDoctor();
        if (doc != null) {
            sanitizeField(doc::setName, doc::getName);
            sanitizeField(doc::setEmail, doc::getEmail);

            // Sanitize address
            Address address = doc.getAddress();
            if (address != null) {
                sanitizeField(address::setStreet, address::getStreet);
                sanitizeField(address::setCity, address::getCity);
                sanitizeField(address::setProvince, address::getProvince);
                sanitizeField(address::setCountry, address::getCountry);
                sanitizeField(address::setPostalCode, address::getPostalCode);
            }
        }

        // Sanitize patient info
        Appointment.PatientInfo patient = appointment.getPatient();
        if (patient != null) {
            sanitizeField(patient::setName, patient::getName);
            sanitizeField(patient::setEmail, patient::getEmail);
        }
    }

    public List<Appointment> getAllAppointment(){
        return appointmentRepository.findAll();
    }

    public Optional<Appointment> getAppointmentById(String id){
        return appointmentRepository.findById(id);
    }

    public Appointment createAppointment(Appointment appointment){
        sanitizeAppointmentMongo(appointment);
        return appointmentRepository.save(appointment);
    }

    public Appointment updateAppointment(String id, Appointment appointment){
        Optional<Appointment> appointmentOptional = appointmentRepository.findById(id);
        if(appointmentOptional.isPresent()){
            Appointment appointmentToUpdate = appointmentOptional.get();
            // Update the appointment
            sanitizeAppointmentMongo(appointmentToUpdate);
            return appointmentRepository.save(appointmentToUpdate);
        }
        return null;
    }

    @Async
    public void updateDoctorInfo(Doctor updatedDoctor){
        appointmentRepository.updateDoctorInfo(updatedDoctor);
    }

    @Async
    public void updatePatientInfo(User updatedUser) {
        appointmentRepository.updatePatientInfo(updatedUser);
    }

    public void deleteAppointment(String id){
        appointmentRepository.deleteById(id);
    }

    public List<Appointment> getAppointmentsForDay(String doctorId, LocalDate day) {
        return appointmentRepository.findByDoctorIdAndDay(doctorId, day);
    }
}
