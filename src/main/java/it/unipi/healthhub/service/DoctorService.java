package it.unipi.healthhub.service;

import it.unipi.healthhub.model.*;
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

    public List<it.unipi.healthhub.model.Service> getServices(String doctorId) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if(doctorOpt.isPresent()){
            Doctor doctor = doctorOpt.get();
            return doctor.getServices();
        }
        return null;
    }

    public it.unipi.healthhub.model.Service addService(String doctorId, it.unipi.healthhub.model.Service service) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            doctor.getServices().add(service); // Add service to doctor's list
            doctorRepository.save(doctor); // Save updated doctor with the new service
            return service;
        }
        return null;
    }

    public it.unipi.healthhub.model.Service updateService(String doctorId, Integer serviceId, it.unipi.healthhub.model.Service service) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            List<it.unipi.healthhub.model.Service> services = doctor.getServices();
            services.set(serviceId, service); // Update service
            doctorRepository.save(doctor); // Save updated doctor with the updated service
            return service;
        }
        return null;
    }

    public void deleteService(String doctorId, Integer serviceId) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            List<it.unipi.healthhub.model.Service> services = doctor.getServices();
            services.remove(serviceId.intValue()); // Remove service
            doctorRepository.save(doctor); // Save updated doctor without the removed service
        }
    }

    public List<Appointment> getAppointments(String doctorId) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if(doctorOpt.isPresent()){
            Doctor doctor = doctorOpt.get();
            return doctor.getAppointments();
        }
        return null;
    }

    public Appointment addAppointment(String doctorId, Appointment appointment) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            doctor.getAppointments().add(appointment); // Add appointment to doctor's list
            doctorRepository.save(doctor); // Save updated doctor with the new appointment
            return appointment;
        }
        return null;
    }

    public void deleteAppointment(String doctorId, String appointmentId) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            List<Appointment> appointments = doctor.getAppointments();
            appointments.removeIf(appointment -> appointment.getId().equals(appointmentId)); // Remove appointment
            doctorRepository.save(doctor); // Save updated doctor without the removed appointment
        }
    }

    public List<CalendarTemplate> getTemplates(String doctorId) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if(doctorOpt.isPresent()){
            Doctor doctor = doctorOpt.get();
            doctor.getCalendarTemplates(); // lista di riferimenti (chiavi esterne) a CalendarTemplate
        }
        return null;
    }

    public CalendarTemplate addTemplate(String doctorId, CalendarTemplate template) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            String key = ""; //= templateRepository.save(template); // Save template
            doctor.getCalendarTemplates().add(key);
            doctorRepository.save(doctor); // Save updated doctor with the new appointment
            return template;
        }
        return null;
    }

    public CalendarTemplate updateTemplate(String doctorId, Integer templateId, CalendarTemplate template) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            //List<String> templates = doctor.getCalendarTemplates();
            //templates.set(templates.indexOf(templateId), template); // Update template
            //doctorRepository.save(doctor); // Save updated doctor with the updated template
            return template;
        }
        return null;
    }

    public void deleteTemplate(String doctorId, String templateId) {

    }

    public List<Schedule> getCalendars(String doctorId) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if(doctorOpt.isPresent()){
            Doctor doctor = doctorOpt.get();
            return doctor.getSchedule();
        }
        return null;
    }

    public Schedule addCalendar(String doctorId, Schedule calendar) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            doctor.getSchedule().add(calendar);
            doctorRepository.save(doctor); // Save updated doctor with the new appointment
            return calendar;
        }
        return null;
    }

    public Schedule updateCalendar(String doctorId, Integer calendarId, Schedule calendar) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            List<Schedule> calendars = doctor.getSchedule();
            calendars.set(calendarId, calendar); // Update calendar
            doctorRepository.save(doctor); // Save updated doctor with the updated calendar
            return calendar;
        }
        return null;
    }

    public void deleteCalendar(String doctorId, Integer calendarId) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            List<Schedule> calendars = doctor.getSchedule();
            calendars.remove(calendarId.intValue()); // Remove calendar
            doctorRepository.save(doctor); // Save updated doctor without the removed calendar
        }
    }

    public List<Review> getReviews(String doctorId) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if(doctorOpt.isPresent()){
            Doctor doctor = doctorOpt.get();
            return doctor.getReviews();
        }
        return null;
    }

    public Review addReview(String doctorId, Review review) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            doctor.getReviews().add(review); // Add review to doctor's list
            doctorRepository.save(doctor); // Save updated doctor with the new review
            return review;
        }
        return null;
    }

    public void deleteReview(String doctorId, Integer reviewId) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            List<Review> reviews = doctor.getReviews();
            reviews.remove(reviewId.intValue()); // Remove review
            doctorRepository.save(doctor); // Save updated doctor without the removed review
        }
    }
}
