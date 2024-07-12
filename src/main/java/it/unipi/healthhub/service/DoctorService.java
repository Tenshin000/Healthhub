package it.unipi.healthhub.service;

import it.unipi.healthhub.dto.AppointmentDTO;
import it.unipi.healthhub.dto.ReviewDTO;
import it.unipi.healthhub.dto.SpecializationDTO;
import it.unipi.healthhub.dto.UserDetailsDTO;
import it.unipi.healthhub.model.*;
import it.unipi.healthhub.repository.AppointmentRepository;
import it.unipi.healthhub.repository.DoctorRepository;
import it.unipi.healthhub.repository.TemplateRepository;
import it.unipi.healthhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.WeekFields;
import java.util.*;

@Service
public class DoctorService {
    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

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
            return null;
        }
        return null;
    }

    public boolean bookAnAppointment(String doctorId, AppointmentDTO appointmentDto, String patientId) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        Optional<User> patientOpt = userRepository.findById(patientId);
        if (doctorOpt.isPresent() && patientOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            User patient = patientOpt.get();

            // check the slot in the schedule
            Pair<Schedule, Integer> response = getSchedule(
                    doctorId,
                    appointmentDto.getDate().getYear(),
                    appointmentDto.getDate().get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear())
            );
            if(response == null){
                return false;
            }
            Schedule schedule = response.getFirst();

            String keyDay = appointmentDto.getDate().getDayOfWeek().toString().toLowerCase();
            if(!schedule.getSlots().containsKey(keyDay)){
                return false;
            }

            List<PrenotableSlot> slots = schedule.getSlots().get(keyDay);
            boolean slotFound = false;
            for (PrenotableSlot slot : slots) {
                if (slot.getStart().equals(appointmentDto.getSlot())) {
                    slotFound = true;
                    if(slot.isTaken()){
                        return false;
                    }
                    slot.setTaken(true);
                    break;
                }
            }
            if (!slotFound) {
                return false;
            }

            updateSchedule(doctorId, response.getSecond(), schedule);

            Appointment appointment = createAppointment(appointmentDto, patient, doctor);

            appointmentRepository.save(appointment); // Save appointment

            return true;
        }
        return false;
    }

    private static Appointment createAppointment(AppointmentDTO appointmentDto, User patient, Doctor doctor) {
        Appointment appointment = new Appointment();

        LocalTime timeSlot = LocalTime.parse(appointmentDto.getSlot());
        LocalDateTime appointmentDateTime = appointmentDto.getDate().atTime(timeSlot);

        appointment.setAppointmentDateTime(appointmentDateTime);
        appointment.setDoctorInfo(new Appointment.DoctorInfo(doctor.getId(), doctor.getName()));
        appointment.setPatientInfo(new Appointment.PatientInfo(patient.getId(), patient.getName()));
        appointment.setVisitType(appointmentDto.getService());
        appointment.setPatientNotes(appointmentDto.getPatientNotes());
        return appointment;
    }

    public boolean deleteAppointment(String doctorId, String appointmentId) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
        if (doctorOpt.isPresent() && appointmentOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            Appointment appointment = appointmentOpt.get();
            LocalDateTime dateTimeSlot = appointment.getAppointmentDateTime();
            Pair<Schedule, Integer> response = getSchedule(doctorId,
                    dateTimeSlot.getYear(),
                    dateTimeSlot.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()));
            if(response == null){
                return false;
            }
            Schedule schedule = response.getFirst();
            String keyDay = dateTimeSlot.getDayOfWeek().toString().toLowerCase();
            if(!schedule.getSlots().containsKey(keyDay)){
                return false;
            }
            List<PrenotableSlot> slots = schedule.getSlots().get(keyDay);
            boolean slotFound = false;
            for (PrenotableSlot slot : slots) {
                if (slot.getStart().equals(dateTimeSlot.toLocalTime().toString())) {
                    slotFound = true;
                    slot.setTaken(false);
                    break;
                }
            }

            // Mi interessa che sia stato trovato?
            // Non ne sono sicuro, problemi di inconsistenza
            if (!slotFound) {
                return false;
            }

            updateSchedule(doctorId, response.getSecond(), schedule);
            appointmentRepository.deleteById(appointmentId);

            return true;

        }
        return false;

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

    public Pair<Schedule, Integer> getSchedule(String doctorId, Integer year, Integer week) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if(doctorOpt.isPresent()){
            Doctor doctor = doctorOpt.get();
            List<Schedule> schedules = doctor.getSchedule();
            if (schedules == null) {
                return null;
            }
            for (Schedule schedule : schedules) {
                WeekFields weekFields = WeekFields.of(Locale.getDefault());
                // Again we need to add 1 to the week number because of the issue with the time zones
                Integer scheduleWeek = schedule.getWeek().get(weekFields.weekOfWeekBasedYear()) + 1;
                Integer scheduleYear = schedule.getWeek().getYear();

                if (scheduleYear.equals(year) && scheduleWeek.equals(week)) {
                    return Pair.of(schedule, schedules.indexOf(schedule));
                }
            }
        }
        return null;
    }

    public List<Schedule> getSchedules(String doctorId) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if(doctorOpt.isPresent()){
            Doctor doctor = doctorOpt.get();
            return doctor.getSchedule();
        }
        return null;
    }

    public Schedule addSchedule(String doctorId, Schedule calendar) {
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

    public Schedule updateSchedule(String doctorId, Integer scheduleIndex, Schedule calendar) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            List<Schedule> calendars = doctor.getSchedule();
            calendars.set(scheduleIndex, calendar); // Update calendar
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

    public Integer getEndorsements(String doctorId) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if(doctorOpt.isPresent()){
            Doctor doctor = doctorOpt.get();
            return doctor.getEndorsementCount();
        }
        return null;
    }

    public boolean toggleEndorsement(String doctorId, String patientId) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        Optional<User> patientOpt = userRepository.findById(patientId);
        if (doctorOpt.isPresent() && patientOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            User patient = patientOpt.get();

            if(patient.getEndorsedDoctors() == null){
                patient.setEndorsedDoctors(new ArrayList<>());
            }

            if (!patient.getEndorsedDoctors().contains(doctorId)) {
                patient.getEndorsedDoctors().add(doctorId);
                userRepository.save(patient);
                doctor.setEndorsementCount(doctor.getEndorsementCount() + 1);
                doctorRepository.save(doctor);
                return true;
            }
            else {
                patient.getEndorsedDoctors().remove(doctorId);
                userRepository.save(patient);
                doctor.setEndorsementCount(doctor.getEndorsementCount() - 1);
                doctorRepository.save(doctor);
                return false;
            }
        }
        return false;
    }

    public Map<String, Integer> getVisitsAnalytics(String doctorId) {
        return appointmentRepository.getVisitsCountByTypeForDoctor(doctorId);
    }
}