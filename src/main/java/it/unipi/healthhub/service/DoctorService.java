package it.unipi.healthhub.service;

import it.unipi.healthhub.dto.*;
import it.unipi.healthhub.exception.DoctorNotFoundException;
import it.unipi.healthhub.exception.ScheduleAlreadyExistsException;
import it.unipi.healthhub.model.mongo.*;
import it.unipi.healthhub.model.neo4j.DoctorDAO;
import it.unipi.healthhub.projection.DoctorMongoProjection;
import it.unipi.healthhub.projection.DoctorNeo4jProjection;
import it.unipi.healthhub.repository.mongo.AppointmentMongoRepository;
import it.unipi.healthhub.repository.mongo.DoctorMongoRepository;
import it.unipi.healthhub.repository.mongo.TemplateMongoRepository;
import it.unipi.healthhub.repository.mongo.UserMongoRepository;
import it.unipi.healthhub.repository.neo4j.DoctorNeo4jRepository;
import it.unipi.healthhub.repository.neo4j.UserNeo4jRepository;
import it.unipi.healthhub.util.FakeMailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.WeekFields;
import java.util.*;

@Service
public class DoctorService {
    @Autowired
    private DoctorMongoRepository doctorMongoRepository;

    @Autowired
    private DoctorNeo4jRepository doctorNeo4jRepository;

    @Autowired
    private TemplateMongoRepository templateRepository;

    @Autowired
    private UserMongoRepository userMongoRepository;

    @Autowired
    private UserNeo4jRepository userNeo4jRepository;


    @Autowired
    private AppointmentMongoRepository appointmentRepository;
    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private FakeMailSender fakeMailSender;

    public List<DoctorMongoProjection> searchDoctorsMongo(String query) {
        if (query != null && !query.isEmpty()) {
            return doctorMongoRepository.searchDoctors(query);
        } else {
            // return doctorMongoRepository.findAll();
            return null;
        }
    }

    public List<DoctorNeo4jProjection> searchDoctorsNeo4j(String patientId, String query) {
        if(query != null && !query.isEmpty()) {
            return doctorNeo4jRepository.findConnectedDoctorsBySteps(patientId, query);
        } else {
            return null;
        }
    }

    public List<Doctor> getAllDoctor(){
        return doctorMongoRepository.findAll();
    }

    public Optional<Doctor> getDoctorById(String id){
        return doctorMongoRepository.findById(id);
    }

    @Transactional
    public Doctor createDoctor(Doctor doctor){
        it.unipi.healthhub.model.mongo.Service service = new it.unipi.healthhub.model.mongo.Service("Standard Visit", 0);
        newService(doctor, service);
        Doctor newDoc = doctorMongoRepository.save(doctor);
        DoctorDAO doctorDAO = new DoctorDAO(newDoc.getId(), newDoc.getName(), newDoc.getSpecializations());
        doctorNeo4jRepository.save(doctorDAO);
        return newDoc;
    }

    public Doctor updateDoctor(String id, Doctor doctor){
        Optional<Doctor> doctorOptional = doctorMongoRepository.findById(id);
        if(doctorOptional.isPresent()){
            Doctor doctorToUpdate = doctorOptional.get();
            // Update the doctor
            return doctorMongoRepository.save(doctorToUpdate);
        }
        return null;
    }

    @Transactional
    public void deleteDoctor(String id){
        doctorNeo4jRepository.deleteDoctor(id);
        doctorMongoRepository.deleteById(id);
    }

    public List<it.unipi.healthhub.model.mongo.Service> getServices(String doctorId) {
        Optional<Doctor> doctorOpt = doctorMongoRepository.findById(doctorId);
        if(doctorOpt.isPresent()){
            Doctor doctor = doctorOpt.get();
            return doctor.getServices();
        }
        return null;
    }

    public Integer addService(String doctorId, it.unipi.healthhub.model.mongo.Service service) {
        Optional<Doctor> doctorOpt = doctorMongoRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();

            int newIndex = newService(doctor, service);

            // Add service to doctor's list
            doctorMongoRepository.save(doctor); // Save updated doctor with the new service
            return newIndex;
        }
        return null;
    }

    private Integer newService(Doctor doctor, it.unipi.healthhub.model.mongo.Service service){
        if(doctor.getServices() == null){
            doctor.setServices(new ArrayList<>());
        }

        int newIndex = doctor.getServices().size();
        doctor.getServices().add(service);
        return newIndex;
    }

    public List<it.unipi.healthhub.model.mongo.Service> getMyServices(String doctorId) {
        Optional<Doctor> doctorOpt = doctorMongoRepository.findById(doctorId);
        if(doctorOpt.isPresent()){
            Doctor doctor = doctorOpt.get();
            return doctor.getServices();
        }
        return null;
    }

    public boolean updateService(String doctorId, Integer index, it.unipi.healthhub.model.mongo.Service service) {
        Optional<Doctor> doctorOpt = doctorMongoRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            List<it.unipi.healthhub.model.mongo.Service> services = doctor.getServices();

            if (index >= 0 && index < services.size()) {
                services.set(index, service); // Update the service
                doctorMongoRepository.save(doctor); // Save the updated doctor with the updated service
                return true;
            }
        }
        return false;
    }

    public boolean deleteService(String doctorId, Integer index) {
        Optional<Doctor> doctorOpt = doctorMongoRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            List<it.unipi.healthhub.model.mongo.Service> services = doctor.getServices();

            if (index >= 0 && index < services.size()) {
                services.remove(index.intValue()); // Remove service
                doctorMongoRepository.save(doctor); // Save updated doctor without the removed service
                return true; // Indicates that the removal was successful
            }
        }
        return false; // Indicates that the removal did not occur (e.g. invalid index or doctor not found)
    }

    public List<Appointment> getAppointments(String doctorId) {
        Optional<Doctor> doctorOpt = doctorMongoRepository.findById(doctorId);
        if(doctorOpt.isPresent()){
            return null;
        }
        return null;
    }

    @Transactional
    public boolean bookAnAppointment(String doctorId, AppointmentDTO appointmentDto, String patientId) {
        Optional<Doctor> doctorOpt = doctorMongoRepository.findById(doctorId);
        Optional<User> patientOpt = userMongoRepository.findById(patientId);
        if (doctorOpt.isPresent() && patientOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            User patient = patientOpt.get();

            Integer year = appointmentDto.getDate().getYear();
            Integer week = appointmentDto.getDate().get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
            String keyDay = appointmentDto.getDate().getDayOfWeek().toString().toLowerCase();
            String slotStart = appointmentDto.getSlot();


            // The function tries to update the slot's taken parameter in the schedule
            // If the slot is already occupied, it returns false
            // If it manages to book the appointment, it returns true
            boolean taken = doctorMongoRepository.checkScheduleSlot(doctorId, year, week, keyDay, slotStart);
            if(!taken){
                Appointment appointment = createAppointment(appointmentDto, patient, doctor);
                if (appointment == null) {
                    return false;
                }
                appointmentRepository.save(appointment); // Save appointment
                doctorMongoRepository.bookScheduleSlot(doctorId, year, week, keyDay, slotStart);
                return true;
            }

        }
        return false;
    }

    private static Appointment createAppointment(AppointmentDTO appointmentDto, User patient, Doctor doctor) {
        Appointment appointment = new Appointment();

        LocalTime timeSlot = LocalTime.parse(appointmentDto.getSlot());
        LocalDateTime appointmentDateTime = appointmentDto.getDate().atTime(timeSlot);

        appointment.setDate(appointmentDateTime);
        appointment.setDoctor(new Appointment.DoctorInfo(doctor.getId(), doctor.getName(), doctor.getAddress(), doctor.getEmail()));
        appointment.setPatient(new Appointment.PatientInfo(patient.getId(), patient.getName(), patient.getEmail(), patient.getGender()));
        appointment.setVisitType(appointmentDto.getService());
        List<it.unipi.healthhub.model.mongo.Service> services = doctor.getServices();
        if (services == null) {
            return null;
        }
        for (it.unipi.healthhub.model.mongo.Service service : services) {
            if (service.getService().equals(appointmentDto.getService())) {
                appointment.setPrice(service.getPrice());
                break;
            }
        }
        appointment.setPatientNotes(appointmentDto.getPatientNotes());
        return appointment;
    }

    @Transactional
    public boolean cancelAnAppointment(String doctorId, String appointmentId){
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);

        if(appointmentOpt.isPresent() && appointmentOpt.get().getDoctor().getId().equals(doctorId)){
            Appointment appointment = appointmentOpt.get();
            return deleteAppointment(appointment);
        }
        return false;
    }

    private boolean deleteAppointment(Appointment appointment){
        LocalDateTime dateTimeSlot = appointment.getDate();

        Integer year = dateTimeSlot.getYear();
        Integer week = dateTimeSlot.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
        String keyDay = dateTimeSlot.getDayOfWeek().toString().toLowerCase();
        String slotStart = dateTimeSlot.toLocalTime().toString();

        // The function tries to update the slot's taken parameter in the schedule
        // If the slot is occupied return false
        boolean taken = doctorMongoRepository.checkScheduleSlot(appointment.getDoctor().getId(), year, week, keyDay, slotStart);
        if(taken){
            appointmentService.deleteAppointment(appointment.getId());
            doctorMongoRepository.freeScheduleSlot(appointment.getDoctor().getId(), year, week, keyDay, slotStart);
            fakeMailSender.sendDeletedAppointmentMailByDoctor(appointment);
            return true;
        }

        return false;
    }

    public List<CalendarTemplate> getTemplates(String doctorId) {
        Optional<Doctor> doctorOpt = doctorMongoRepository.findById(doctorId);
        if(doctorOpt.isPresent()){
            Doctor doctor = doctorOpt.get();
            List<String> ids = doctor.getCalendarTemplates(); // List of references (foreign keys) to Calendar Template
            List<CalendarTemplate> templates = new ArrayList<>();
            for (String id : ids) {
                Optional<CalendarTemplate> templateOpt = templateRepository.findById(id);
                templateOpt.ifPresent(templates::add);
            }
            return templates;
        }
        return null;
    }

    @Transactional
    public CalendarTemplate addTemplate(String doctorId, CalendarTemplate template) {
        Optional<Doctor> doctorOpt = doctorMongoRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            CalendarTemplate newTemplate = templateRepository.save(template); // Save template
            Doctor doctor = doctorOpt.get();

            if (doctor.getCalendarTemplates() == null) {
                doctor.setCalendarTemplates(new ArrayList<>());
            }

            doctor.getCalendarTemplates().add(newTemplate.getId());
            doctorMongoRepository.save(doctor); // Save updated doctor with the new appointment
            return newTemplate;
        }
        return null;
    }

    @Transactional
    public CalendarTemplate updateTemplate(String doctorId, CalendarTemplate updatedTemplate) {
        Optional<Doctor> doctorOpt = doctorMongoRepository.findById(doctorId);
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
        Optional<Doctor> doctorOpt = doctorMongoRepository.findById(doctorId);
        if(doctorOpt.isPresent()){
            Doctor doctor = doctorOpt.get();
            List<String> ids = doctor.getCalendarTemplates(); // List of references (foreign keys) to Calendar Template
            List<CalendarTemplate> templates = new ArrayList<>();
            for (String id : ids) {
                Optional<CalendarTemplate> templateOpt = templateRepository.findById(id);
                templateOpt.ifPresent(templates::add);
            }
            return templates;
        }
        return null;
    }

    @Transactional
    public boolean deleteTemplate(String doctorId, String templateId) {
        Optional<Doctor> doctorOpt = doctorMongoRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            List<String> templateIds = doctor.getCalendarTemplates();
            templateRepository.deleteById(templateId); // Delete template
            templateIds.remove(templateId); // Remove template
            doctorMongoRepository.save(doctor); // Save updated doctor without the removed template
            return true;
        }
        return false;
    }

    @Transactional
    public boolean setDefaultTemplate(String doctorId, String templateId) {
        Optional<Doctor> doctorOpt = doctorMongoRepository.findById(doctorId);
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
        Optional<Doctor> doctorOpt = doctorMongoRepository.findById(doctorId);
        if(doctorOpt.isPresent()){
            Doctor doctor = doctorOpt.get();
            List<Schedule> schedules = doctor.getSchedules();
            if (schedules == null) {
                System.out.println("Schedules not found");
                return null;
            }
            // Schedule typically is a 4 weeks schedule
            // Passed schedule are removed at the end of the week
            for (Schedule schedule : schedules) {
                WeekFields weekFields = WeekFields.of(Locale.getDefault());
                // Again we need to add 1 to the week number because of the issue with the time zones
                Integer scheduleWeek = schedule.getWeek().get(weekFields.weekOfWeekBasedYear());
                Integer scheduleYear = schedule.getWeek().getYear();

                if (scheduleYear.equals(year) && scheduleWeek.equals(week)) {
                    return Pair.of(schedule, schedules.indexOf(schedule));
                }
            }
            System.out.println("Schedule not found in list");
        }
        return null;
    }

    public List<Schedule> getSchedules(String doctorId) {
        Optional<Doctor> doctorOpt = doctorMongoRepository.findById(doctorId);
        if(doctorOpt.isPresent()){
            Doctor doctor = doctorOpt.get();
            return doctor.getSchedules();
        }
        return null;
    }

    public Schedule addSchedule(String doctorId, Schedule calendar) throws ScheduleAlreadyExistsException{
        Optional<Doctor> doctorOpt = doctorMongoRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();

            if (doctor.getSchedules() == null) {
                doctor.setSchedules(new ArrayList<>());
            }

            boolean alreadyExists = doctor.getSchedules().stream()
                    .anyMatch(s -> s.getWeek().equals(calendar.getWeek()));

            if(alreadyExists)
                throw new ScheduleAlreadyExistsException("A schedule for this week already exists.");

            doctor.getSchedules().add(calendar);
            doctorMongoRepository.save(doctor); // Save updated doctor with the new appointment
            return calendar;
        }
        return null;
    }

    public Schedule updateSchedule(String doctorId, Integer scheduleIndex, Schedule calendar) {
        Optional<Doctor> doctorOpt = doctorMongoRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            List<Schedule> calendars = doctor.getSchedules();
            calendars.set(scheduleIndex, calendar); // Update calendar
            doctorMongoRepository.save(doctor); // Save updated doctor with the updated calendar
            return calendar;
        }
        return null;
    }

    public boolean deleteCalendar(String doctorId, LocalDate calendarDate) {
        Optional<Doctor> doctorOpt = doctorMongoRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            List<Schedule> calendars = doctor.getSchedules();
            for (int i = 0; i < calendars.size(); i++) {
                if (calendars.get(i).getWeek().equals(calendarDate)) {
                    // Use ISO week definition (week starts on Monday, week 1 is the one with the first Thursday)
                    WeekFields weekFields = WeekFields.ISO;
                    int week = calendarDate.get(weekFields.weekOfWeekBasedYear());
                    int year = calendarDate.get(weekFields.weekBasedYear());

                    List<Appointment> appointments = appointmentRepository.findByDoctorIdAndWeek(doctorId, week, year);
                    for (Appointment appointment : appointments)
                        deleteAppointment(appointment);

                    calendars.remove(i); // Remove calendar
                    doctorMongoRepository.save(doctor); // Save updated doctor without the removed calendar
                    return true;
                }
            }
        }
        return false;
    }


    public List<Review> getReviews(String doctorId) {
        Optional<Doctor> doctorOpt = doctorMongoRepository.findById(doctorId);
        if(doctorOpt.isPresent()){
            Doctor doctor = doctorOpt.get();
            return doctor.getReviews();
        }
        return null;
    }

    @Transactional
    public Review addReview(String doctorId, String userId, ReviewDTO review) {
        Optional<Doctor> doctorOpt = doctorMongoRepository.findById(doctorId);
        if (doctorOpt.isPresent() && appointmentRepository.hasPastAppointment(doctorId, userId)) {
            Doctor doctor = doctorOpt.get();

            if (doctor.getReviews() == null) {
                doctor.setReviews(new ArrayList<>());
            }

            Review modelReview = new Review();
            modelReview.setName(review.getName());
            modelReview.setText(review.getText());
            modelReview.setDate(review.getDate());
            modelReview.setPatientId(userId);

            doctor.getReviews().add(modelReview); // Add review to doctor's list
            doctorMongoRepository.save(doctor); // Save updated doctor with the new review

            userNeo4jRepository.review(userId, doctorId);

            return modelReview;
        }
        return null;
    }

    @Transactional
    public boolean deleteReview(String doctorId, Integer reviewIndex) {
        Optional<Doctor> doctorOpt = doctorMongoRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            List<Review> reviews = doctor.getReviews();

            Review review = reviews.get(reviewIndex);
            userNeo4jRepository.unreview(review.getPatientId(), doctorId);

            reviews.remove(reviewIndex.intValue()); // Remove review
            doctorMongoRepository.save(doctor); // Save updated doctor without the removed review
            return true;
        }
        return false;
    }

    public Doctor loginDoctor(String username, String password) {
        Doctor doctor = doctorMongoRepository.findByUsername(username);

        if (doctor != null && doctor.getPassword().equals(password)) {
            return doctor;
        }

        return null;
    }

    public Address updateAddress(String doctorId, Address address) {
        Optional<Doctor> doctorOpt = doctorMongoRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            doctor.setAddress(address); // Update address
            doctorMongoRepository.save(doctor); // Save updated doctor with the updated address
            return address;
        }
        return null;
    }

    @Transactional
    public UserDetailsDTO updateUserDetails(String doctorId, UserDetailsDTO userDetails) {
        Optional<Doctor> doctorOpt = doctorMongoRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            doctor.setName(userDetails.getFullName());
            doctor.setFiscalCode(userDetails.getFiscalCode());
            doctor.setDob(userDetails.getBirthDate());
            doctor.setGender(userDetails.getGender());

            doctorNeo4jRepository.updateName(doctorId, userDetails.getFullName());

            doctorMongoRepository.save(doctor); // Save updated doctor with the updated user details
            return userDetails;
        }
        return null;
    }

    public Integer addPhoneNumber(String doctorId, String number) {
        Optional<Doctor> doctorOpt = doctorMongoRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();

            if (doctor.getPhoneNumbers() == null) {
                doctor.setPhoneNumbers(new ArrayList<>());
            }

            int newIndex = doctor.getPhoneNumbers().size();
            doctor.getPhoneNumbers().add(number);
            doctorMongoRepository.save(doctor);
            return newIndex;
        }
        return null;
    }

    public List<String> getMyPhoneNumbers(String doctorId) {
        Optional<Doctor> doctorOpt = doctorMongoRepository.findById(doctorId);
        if(doctorOpt.isPresent()){
            Doctor doctor = doctorOpt.get();
            return doctor.getPhoneNumbers();
        }
        return null;
    }

    public boolean removePhoneNumber(String doctorId, Integer index) {
        Optional<Doctor> doctorOpt = doctorMongoRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            List<String> phoneNumbers = doctor.getPhoneNumbers();
            if (index >= 0 && index < phoneNumbers.size()) {
                phoneNumbers.remove(index.intValue());
                doctorMongoRepository.save(doctor);
                return true;
            }
        }
        return false;
    }

    @Transactional
    public Integer addSpecialization(String doctorId, String specialization) {
        Optional<Doctor> doctorOpt = doctorMongoRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();

            if (doctor.getSpecializations() == null) {
                doctor.setSpecializations(new ArrayList<>());
            }

            List<String> specializations = doctor.getSpecializations();
            if (!specializations.contains(specialization)) {
                specializations.add(specialization);
                doctorMongoRepository.save(doctor);
                doctorNeo4jRepository.addSpecialization(doctorId, specialization);
                return specializations.size() - 1;
            }
        }
        return null;
    }


    @Transactional
    public boolean removeSpecialization(String doctorId, Integer index) {
        Optional<Doctor> doctorOpt = doctorMongoRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            List<String> specializations = doctor.getSpecializations();
            if (index >= 0 && index < specializations.size()) {
                doctorNeo4jRepository.removeSpecialization(doctorId, specializations.get(index));
                specializations.remove(index.intValue());
                doctorMongoRepository.save(doctor);
                return true;
            }
        }
        return false;
    }

    public List<SpecializationDTO> getSpecializations(String doctorId) {
        Optional<Doctor> doctorOpt = doctorMongoRepository.findById(doctorId);
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
        Optional<Doctor> doctorOpt = doctorMongoRepository.findById(doctorId);
        if(doctorOpt.isPresent()){
            Doctor doctor = doctorOpt.get();
            return doctor.getEndorsementCount();
        }
        return null;
    }

    @Transactional
    public void endorse(String doctorId, String patientId) {
        userNeo4jRepository.endorse(patientId, doctorId);

        Doctor doc = doctorMongoRepository.findById(doctorId).orElseThrow(DoctorNotFoundException::new);
        doc.setEndorsementCount(doc.getEndorsementCount() + 1);
        doctorMongoRepository.save(doc);
    }

    @Transactional
    public void unendorse(String doctorId, String patientId) {
        userNeo4jRepository.unendorse(patientId, doctorId);

        Doctor doc = doctorMongoRepository.findById(doctorId).orElseThrow(DoctorNotFoundException::new);
        doc.setEndorsementCount(doc.getEndorsementCount() - 1);
        doctorMongoRepository.save(doc);
    }

    public Map<String, Integer> getVisitsAnalytics(String doctorId) {
        return appointmentRepository.getVisitsCountByTypeForDoctor(doctorId);
    }

    public Map<String, Double> getEarningsAnalytics(String doctorId, Integer year) {
        return appointmentRepository.getEarningsByYearForDoctor(doctorId, year);
    }

    public Map<String, Integer> getVisitsAnalyticsWeek(String doctorId, Integer week, Integer year) {
        // Returns the distribution of the visits by day for the given week
        return appointmentRepository.getVisitsCountByDayForDoctorWeek(doctorId, week, year);
    }

    public Integer getAnalyticsNewPatientsByMonth(String doctorId, int year, int month){
        // Returns the count of patients who were visited for the first time in the month and year indicated
        return appointmentRepository.findNewPatientsVisitedByDoctorInCurrentMonth(doctorId, year, month);
    }
}