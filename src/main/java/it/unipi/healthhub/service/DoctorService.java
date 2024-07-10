package it.unipi.healthhub.service;

import it.unipi.healthhub.dto.ReviewDTO;
import it.unipi.healthhub.dto.SpecializationDTO;
import it.unipi.healthhub.dto.UserDetailsDTO;
import it.unipi.healthhub.model.*;
import it.unipi.healthhub.repository.DoctorRepository;
import it.unipi.healthhub.repository.TemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {
    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private TemplateRepository templateRepository;

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

    public Integer addService(String doctorId, it.unipi.healthhub.model.Service service) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();

            if (doctor.getServices() == null) {
                doctor.setServices(new ArrayList<>());
            }

            int newIndex = doctor.getServices().size();
            doctor.getServices().add(service); // Add service to doctor's list
            doctorRepository.save(doctor); // Save updated doctor with the new service
            return newIndex;
        }
        return null;
    }

    public List<it.unipi.healthhub.model.Service> getMyServices(String doctorId) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if(doctorOpt.isPresent()){
            Doctor doctor = doctorOpt.get();
            return doctor.getServices();
        }
        return null;
    }

    public boolean updateService(String doctorId, Integer index, it.unipi.healthhub.model.Service service) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            List<it.unipi.healthhub.model.Service> services = doctor.getServices();

            if (index >= 0 && index < services.size()) {
                services.set(index, service); // Aggiorna il servizio
                doctorRepository.save(doctor); // Salva il dottore aggiornato con il servizio aggiornato
                return true;
            }
        }
        return false;
    }


    public boolean deleteService(String doctorId, Integer index) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            List<it.unipi.healthhub.model.Service> services = doctor.getServices();

            if (index >= 0 && index < services.size()) {
                services.remove(index.intValue()); // Remove service
                doctorRepository.save(doctor); // Save updated doctor without the removed service
                return true; // Indica che la rimozione è avvenuta con successo
            }
        }
        return false; // Indica che la rimozione non è avvenuta (es. indice non valido o dottore non trovato)
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
            List<String> ids = doctor.getCalendarTemplates(); // lista di riferimenti (chiavi esterne) a CalendarTemplate
            List<CalendarTemplate> templates = new ArrayList<>();
            for (String id : ids) {
                Optional<CalendarTemplate> templateOpt = templateRepository.findById(id);
                templateOpt.ifPresent(templates::add);
            }
            return templates;
        }
        return null;
    }

    public CalendarTemplate addTemplate(String doctorId, CalendarTemplate template) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            CalendarTemplate newTemplate = templateRepository.save(template); // Save template
            Doctor doctor = doctorOpt.get();

            if (doctor.getCalendarTemplates() == null) {
                doctor.setCalendarTemplates(new ArrayList<>());
            }

            doctor.getCalendarTemplates().add(newTemplate.getId());
            doctorRepository.save(doctor); // Save updated doctor with the new appointment
            return newTemplate;
        }
        return null;
    }

    public CalendarTemplate updateTemplate(String doctorId, CalendarTemplate updatedTemplate) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            Optional<CalendarTemplate> existingTemplateOpt = templateRepository.findById(updatedTemplate.getId());
            if (existingTemplateOpt.isPresent()) {
                CalendarTemplate existingTemplate = existingTemplateOpt.get();
                existingTemplate.setName(updatedTemplate.getName());
                existingTemplate.setSlots(updatedTemplate.getSlots());
                existingTemplate.setActive(updatedTemplate.isActive());

                return templateRepository.save(existingTemplate);
            }
        }
        return null;
    }

    public List<CalendarTemplate> getMyTemplates(String doctorId) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if(doctorOpt.isPresent()){
            Doctor doctor = doctorOpt.get();
            List<String> ids = doctor.getCalendarTemplates(); // lista di riferimenti (chiavi esterne) a CalendarTemplate
            List<CalendarTemplate> templates = new ArrayList<>();
            for (String id : ids) {
                Optional<CalendarTemplate> templateOpt = templateRepository.findById(id);
                templateOpt.ifPresent(templates::add);
            }
            return templates;
        }
        return null;
    }

    public boolean deleteTemplate(String doctorId, String templateId) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            List<String> templateIds = doctor.getCalendarTemplates();
            templateRepository.deleteById(templateId); // Delete template
            templateIds.remove(templateId); // Remove template
            doctorRepository.save(doctor); // Save updated doctor without the removed template
            return true;
        }
        return false;
    }

    public boolean setDefaultTemplate(String doctorId, String templateId) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            List<String> templateIds = doctor.getCalendarTemplates();
            if (templateIds.contains(templateId)) {
                for (String id : templateIds) {
                    Optional<CalendarTemplate> templateOpt = templateRepository.findById(id);
                    if (templateOpt.isPresent()) {
                        CalendarTemplate template = templateOpt.get();
                        template.setActive(id.equals(templateId));
                        templateRepository.save(template);
                    }
                }
                return true;
            }
        }
        return false;
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

            if (doctor.getSchedule() == null) {
                doctor.setSchedule(new ArrayList<>());
            }

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

    public boolean deleteCalendar(String doctorId, LocalDate calendarDate) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            List<Schedule> calendars = doctor.getSchedule();
            for (int i = 0; i < calendars.size(); i++) {
                System.out.println(calendars.get(i).getWeek());
                System.out.println(calendarDate);
                if (calendars.get(i).getWeek().equals(calendarDate)) {
                    calendars.remove(i); // Remove calendar
                    doctorRepository.save(doctor); // Save updated doctor without the removed calendar
                    return true;
                }
            }
        }
        return false;
    }

    public List<Review> getReviews(String doctorId) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if(doctorOpt.isPresent()){
            Doctor doctor = doctorOpt.get();
            return doctor.getReviews();
        }
        return null;
    }

    public Review addReview(String doctorId, ReviewDTO review) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();

            if (doctor.getReviews() == null) {
                doctor.setReviews(new ArrayList<>());
            }

            Review modelReview = new Review();
            modelReview.setName(review.getName());
            modelReview.setText(review.getText());
            modelReview.setDate(review.getDate());

            doctor.getReviews().add(modelReview); // Add review to doctor's list
            doctorRepository.save(doctor); // Save updated doctor with the new review
            return modelReview;
        }
        return null;
    }

    public boolean deleteReview(String doctorId, Integer reviewIndex) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            List<Review> reviews = doctor.getReviews();
            reviews.remove(reviewIndex.intValue()); // Remove review
            doctorRepository.save(doctor); // Save updated doctor without the removed review
            return true;
        }
        return false;
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

    public Address updateAddress(String doctorId, Address address) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            doctor.setAddress(address); // Update address
            doctorRepository.save(doctor); // Save updated doctor with the updated address
            return address;
        }
        return null;
    }


    public UserDetailsDTO updateUserDetails(String doctorId, UserDetailsDTO userDetails) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            doctor.setName(userDetails.getFullName());
            doctor.setDob(userDetails.getBirthDate());
            doctor.setGender(userDetails.getGender());
            doctorRepository.save(doctor); // Save updated doctor with the updated user details
            return userDetails;
        }
        return null;
    }

    public Integer addPhoneNumber(String doctorId, String number) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();

            if (doctor.getPhoneNumbers() == null) {
                doctor.setPhoneNumbers(new ArrayList<>());
            }

            int newIndex = doctor.getPhoneNumbers().size(); // Ottieni l'indice del nuovo numero di telefono
            doctor.getPhoneNumbers().add(number); // Aggiunge il numero di telefono alla lista del dottore
            doctorRepository.save(doctor); // Salva il dottore aggiornato con il nuovo numero di telefono
            return newIndex;
        }
        return null;
    }

    public List<String> getMyPhoneNumbers(String doctorId) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if(doctorOpt.isPresent()){
            Doctor doctor = doctorOpt.get();
            return doctor.getPhoneNumbers();
        }
        return null;
    }

    public boolean removePhoneNumber(String doctorId, Integer index) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            List<String> phoneNumbers = doctor.getPhoneNumbers();
            if (index >= 0 && index < phoneNumbers.size()) {
                phoneNumbers.remove(index.intValue());
                doctorRepository.save(doctor); // Salva il dottore aggiornato senza il numero di telefono rimosso
                return true;
            }
        }
        return false;
    }

    public Integer addSpecialization(String doctorId, String specialization) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();

            if (doctor.getSpecializations() == null) {
                doctor.setSpecializations(new ArrayList<>());
            }

            int newIndex = doctor.getSpecializations().size(); // Ottieni l'indice del nuovo numero di telefono
            doctor.getSpecializations().add(specialization); // Aggiunge il numero di telefono alla lista del dottore
            doctorRepository.save(doctor); // Salva il dottore aggiornato con il nuovo numero di telefono
            return newIndex;
        }
        return null;
    }


    public boolean removeSpecialization(String doctorId, Integer index) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            List<String> specializations = doctor.getSpecializations();
            if (index >= 0 && index < specializations.size()) {
                specializations.remove(index.intValue());
                doctorRepository.save(doctor);
                return true;
            }
        }
        return false;
    }

    public List<SpecializationDTO> getSpecializations(String doctorId) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if(doctorOpt.isPresent()){
            Doctor doctor = doctorOpt.get();
            List<String> specializations = doctor.getSpecializations();
            List<SpecializationDTO> specializationDTOs = new ArrayList<>();
            for (int i = 0; i < specializations.size(); i++) {
                specializationDTOs.add(new SpecializationDTO(specializations.get(i), i));
            }
            return specializationDTOs;
        }
        return null;
    }
}