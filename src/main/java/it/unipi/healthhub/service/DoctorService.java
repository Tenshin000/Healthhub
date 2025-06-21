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
import it.unipi.healthhub.util.DateUtil;
import it.unipi.healthhub.service.AppointmentService;
import it.unipi.healthhub.util.FakeMailSender;
import it.unipi.healthhub.util.ScheduleConverter;
import it.unipi.healthhub.util.TemplateConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;
import java.util.function.Consumer;
import java.util.function.Supplier;


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

    private String sanitizeForMongo(String input) {
        if (input == null)
            return null;
        // Remove any '$' that may create operator injection
        return input.replaceAll("[\\$]", "_");
    }

    private void sanitizeFieldMongo(Consumer<String> setter, Supplier<String> getter) {
        setter.accept(sanitizeForMongo(getter.get()));
    }

    private List<String> sanitizeStringListMongo(List<String> list) {
        if (list == null) {
            return null;
        }
        List<String> mutableList = new ArrayList<>(list);
        for (int i = 0; i < mutableList.size(); i++) {
            mutableList.set(i, sanitizeForMongo(mutableList.get(i)));
        }
        return mutableList;
    }

    private void sanitizeAddress(Address address) {
        if (address != null) {
            sanitizeFieldMongo(address::setStreet, address::getStreet);
            sanitizeFieldMongo(address::setCity, address::getCity);
            sanitizeFieldMongo(address::setProvince, address::getProvince);
            sanitizeFieldMongo(address::setCountry, address::getCountry);
            sanitizeFieldMongo(address::setPostalCode, address::getPostalCode);
        }
    }

    private void sanitizeReview(Review review) {
        if (review == null)
            return;
        sanitizeFieldMongo(review::setName, review::getName);
        sanitizeFieldMongo(review::setText, review::getText);
        sanitizeFieldMongo(review::setPatientId, review::getPatientId);
    }

    private void sanitizeService(it.unipi.healthhub.model.mongo.Service service) {
        if (service == null)
            return;
        sanitizeFieldMongo(service::setService, service::getService);
    }

    private void sanitizeTemplate(CalendarTemplate template) {
        if(template == null)
            return;

        // Sanitize id and name
        sanitizeFieldMongo(template::setId, template::getId);
    }

    // Sanitize User fields
    private void sanitizeUserMongo(User user) {
        if (user == null)
            return;
        sanitizeFieldMongo(user::setName, user::getName);
        sanitizeFieldMongo(user::setUsername, user::getUsername);
        sanitizeFieldMongo(user::setPassword, user::getPassword);
        sanitizeFieldMongo(user::setFiscalCode, user::getFiscalCode);
        sanitizeFieldMongo(user::setGender, user::getGender);
        sanitizeFieldMongo(user::setEmail, user::getEmail);
        sanitizeFieldMongo(user::setPersonalNumber, user::getPersonalNumber);
    }

    // Sanitize Doctor (including User fields)
    private void sanitizeDoctorMongo(Doctor doctor) {
        if (doctor == null)
            return;

        // Sanitize User inherited fields
        sanitizeUserMongo(doctor);

        // Sanitize Doctor specific fields
        sanitizeFieldMongo(doctor::setOrderRegistrationNumber, doctor::getOrderRegistrationNumber);

        sanitizeStringListMongo(doctor.getCalendarTemplates());
        sanitizeStringListMongo(doctor.getSpecializations());
        sanitizeStringListMongo(doctor.getPhoneNumbers());

        // Sanitize each review
        if(doctor.getReviews() != null){
            for(Review review : doctor.getReviews()){
                sanitizeReview(review);
            }
        }

        // Sanitize each service
        if(doctor.getServices() != null) {
            for(it.unipi.healthhub.model.mongo.Service service : doctor.getServices()){
                sanitizeService(service);
            }
        }
    }

    // Remove any '$' or '.' that may create operator injection in Cypher parameters
    private String sanitizeForNeo4j(String input) {
        if (input == null)
            return null;
        return input.replaceAll("[\\$\\.]", "_");
    }

    // Generic helper to sanitize a String field
    private void sanitizeFieldNeo4j(Consumer<String> setter, Supplier<String> getter) {
        setter.accept(sanitizeForNeo4j(getter.get()));
    }

    // Helper to sanitize String lists
    private void sanitizeStringListNeo4j(List<String> list) {
        if (list == null)
            return;
        list = list.stream()
                .map(this::sanitizeForNeo4j)
                .toList();
    }


    // Sanitize DoctorDAO (nodo Doctor)
    private void sanitizeDoctorNeo4j(DoctorDAO doctor) {
        if (doctor == null)
            return;

        // Sanitize id and name
        sanitizeFieldNeo4j(doctor::setId, doctor::getId);
        sanitizeFieldNeo4j(doctor::setName, doctor::getName);

        // Sanitize specializations list
        sanitizeStringListNeo4j(doctor.getSpecializations());
    }

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
        sanitizeDoctorMongo(doctor);

        User controlUser = userMongoRepository.findByUsername(doctor.getUsername());
        if(controlUser != null){
            return null;
        }

        controlUser = userMongoRepository.findByEmail(doctor.getEmail());
        if(controlUser != null){
            return null;
        }

        Doctor controlDoctor = doctorMongoRepository.findByUsername(doctor.getUsername());
        if(controlDoctor != null){
            return null;
        }

        controlDoctor = doctorMongoRepository.findByEmail(doctor.getEmail());
        if(controlDoctor != null){
            return null;
        }

        it.unipi.healthhub.model.mongo.Service service = new it.unipi.healthhub.model.mongo.Service("Standard Visit", 0);
        newService(doctor, service);
        sanitizeDoctorMongo(doctor);
        Doctor newDoc = doctorMongoRepository.save(doctor);
        DoctorDAO doctorDAO = new DoctorDAO(newDoc.getId(), newDoc.getName(), newDoc.getSpecializations());

        sanitizeDoctorNeo4j(doctorDAO);
        doctorNeo4jRepository.save(doctorDAO);
        return newDoc;
    }

    @Transactional
    public Doctor updateDoctor(String id, Doctor doctorData){
        Optional<Doctor> optOldDoctor = getDoctorById(id);
        if(optOldDoctor.isPresent()){
            Doctor oldDoctor = optOldDoctor.get();
            doctorData.setId(id);
            sanitizeDoctorMongo(doctorData);
            Doctor updatedDoctor = doctorMongoRepository.save(doctorData);

            boolean nameChanged = !oldDoctor.getName().equals(updatedDoctor.getName());
            boolean emailChanged = !oldDoctor.getEmail().equals(updatedDoctor.getEmail());
            boolean specializationsChanged =
                    !new HashSet<>(Optional.ofNullable(oldDoctor.getSpecializations()).orElse(List.of()))
                            .equals(new HashSet<>(Optional.ofNullable(updatedDoctor.getSpecializations()).orElse(List.of())));

            if(nameChanged || emailChanged)
                appointmentService.updateDoctorInfo(updatedDoctor);

            if(nameChanged || specializationsChanged){
                DoctorDAO doctorDAO = new DoctorDAO(updatedDoctor.getId(), updatedDoctor.getName(), updatedDoctor.getSpecializations());
                doctorNeo4jRepository.save(doctorDAO);
            }
            
            return updatedDoctor;
        }
        else
            throw new DoctorNotFoundException("Doctor not found with id: " + id);
    }

    @Transactional
    public void deleteDoctor(String id){
        doctorNeo4jRepository.deleteDoctor(id);
        doctorMongoRepository.deleteById(id);
    }

    public Doctor findByEmail(String email){
        return doctorMongoRepository.findByEmail(email);
    }

    public boolean changePassword(String id, String currentPassword, String newPassword){
        Optional<Doctor> doctorOpt = getDoctorById(id);
        if(doctorOpt.isPresent()){
            Doctor doctor = doctorOpt.get();
            if(currentPassword.equals(doctor.getPassword())){
                doctor.setPassword(newPassword);
                updateDoctor(doctor.getId(), doctor);
                return true;
            }
        }

        return false;
    }

    public List<it.unipi.healthhub.model.mongo.Service> getServices(String doctorId) {
        Optional<Doctor> doctorOpt = doctorMongoRepository.findById(doctorId);
        if(doctorOpt.isPresent()){
            Doctor doctor = doctorOpt.get();
            return doctor.getServices();
        }
        return null;
    }

    public it.unipi.healthhub.model.mongo.Service getService(String doctorId, int index) {
        Optional<Doctor> doctorOpt = doctorMongoRepository.findById(doctorId);
        if(doctorOpt.isPresent()){
            Doctor doctor = doctorOpt.get();
            it.unipi.healthhub.model.mongo.Service service = null;
            try{
                service = doctor.getServices().get(index);
            }
            catch(IndexOutOfBoundsException ioobe){
                return null;
            }

            return service;
        }
        return null;
    }

    
    public Integer addService(String doctorId, it.unipi.healthhub.model.mongo.Service service) {
        Optional<Doctor> doctorOpt = doctorMongoRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();

            int newIndex = newService(doctor, service);

            // Add service to doctor's list
            sanitizeDoctorMongo(doctor);
            updateDoctor(doctor.getId(), doctor); // Save updated doctor with the new service
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
                sanitizeDoctorMongo(doctor);
                updateDoctor(doctor.getId(), doctor); // Save the updated doctor with the updated service
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
                updateDoctor(doctor.getId(), doctor); // Save updated doctor without the removed service
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

        // Extract appointment date components
        Integer year = dateTimeSlot.getYear();
        Integer week = dateTimeSlot.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
        String keyDay = dateTimeSlot.getDayOfWeek().toString().toLowerCase();
        String slotStart = dateTimeSlot.toLocalTime().toString();

        // Get current year and week
        LocalDateTime now = LocalDateTime.now();
        Integer currentYear = now.getYear();
        Integer currentWeek = now.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());

        // The function tries to update the slot's taken parameter in the schedule
        boolean taken = doctorMongoRepository.checkScheduleSlot(appointment.getDoctor().getId(), year, week, keyDay, slotStart);
        if(taken){
            appointmentService.deleteAppointment(appointment.getId());
            doctorMongoRepository.freeScheduleSlot(appointment.getDoctor().getId(), year, week, keyDay, slotStart);
            fakeMailSender.sendDeletedAppointmentMailByDoctor(appointment);
            return true;
        }

        if((year < currentYear) || (year.equals(currentYear) && week < currentWeek)){
            appointmentService.deleteAppointment(appointment.getId());
            fakeMailSender.sendDeletedPastAppointmentMailByDoctor(appointment);
            return true;
        }

        return false;
    }

    public boolean sendPasswordReset(String email, String link){
        return fakeMailSender.sendPasswordResetLink(email, link);
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
            Doctor doctor = doctorOpt.get();

            if (doctor.getCalendarTemplates() == null) {
                doctor.setCalendarTemplates(new ArrayList<>());
            }
            List<String> templateIds = doctor.getCalendarTemplates();

            // Prevent duplicate names among this doctor's templates
            if (templateRepository.existsByNameIgnoreCaseAndIdIn(template.getName(), templateIds)) {
                return null;
            }

            sanitizeTemplate(template);
            CalendarTemplate newTemplate = templateRepository.save(template);

            doctor.getCalendarTemplates().add(newTemplate.getId());
            updateDoctor(doctor.getId(), doctor);

            return newTemplate;
        }
        return null;
    }

    @Transactional
    public CalendarTemplate updateTemplate(String doctorId, CalendarTemplate updatedTemplate) {
        Optional<Doctor> doctorOpt = doctorMongoRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            List<String> templateIds = doctor.getCalendarTemplates() != null
                    ? doctor.getCalendarTemplates()
                    : Collections.emptyList();

            Optional<CalendarTemplate> existingOpt = templateRepository.findById(updatedTemplate.getId());
            if (existingOpt.isPresent()) {
                CalendarTemplate existing = existingOpt.get();
                sanitizeTemplate(updatedTemplate);
                existing.setName(updatedTemplate.getName());
                existing.setSlots(updatedTemplate.getSlots());
                existing.setDefault(updatedTemplate.isDefault());

                return templateRepository.save(existing);
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
            updateDoctor(doctor.getId(), doctor); // Save updated doctor without the removed template
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
                        template.setDefault(id.equals(templateId));
                        templateRepository.save(template);
                    }
                }
                return true;
            }
        }
        return false;
    }

    public CalendarTemplate getDefaultTemplate(String doctorId) {
        Optional<Doctor> doctorOpt = doctorMongoRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            List<String> templateIds = doctor.getCalendarTemplates();
            for (String id : templateIds) {
                Optional<CalendarTemplate> templateOpt = templateRepository.findById(id);
                if (templateOpt.isPresent()) {
                    CalendarTemplate template = templateOpt.get();
                    if (template.isDefault()) {
                        return template;
                    }
                }
            }
        }
        return null;
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
            updateDoctor(doctor.getId(), doctor); // Save updated doctor with the new appointment
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
            updateDoctor(doctor.getId(), doctor); // Save updated doctor with the updated calendar
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
                    updateDoctor(doctor.getId(), doctor); // Save updated doctor without the removed calendar
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
            sanitizeDoctorMongo(doctor);
            updateDoctor(doctor.getId(), doctor); // Save updated doctor with the new review

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
            updateDoctor(doctor.getId(), doctor); // Save updated doctor without the removed review
            return true;
        }
        return false;
    }

    public Doctor loginDoctor(String username, String password){
        Doctor doctor = doctorMongoRepository.findByUsername(username);

        if(doctor == null)
            doctor = doctorMongoRepository.findByEmail(username);

        if(doctor != null && doctor.getPassword().equals(password)){
            return doctor;
        }

        return null;
    }

    public Address updateAddress(String doctorId, Address address) {
        Optional<Doctor> doctorOpt = doctorMongoRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            doctor.setAddress(address); // Update address
            sanitizeDoctorMongo(doctor);
            updateDoctor(doctor.getId(), doctor); // Save updated doctor with the updated address
            return address;
        }
        return null;
    }

    @Transactional
    public DoctorDetailsDTO updateDoctorDetails(String doctorId, DoctorDetailsDTO doctorDetails) {
        Optional<Doctor> doctorOpt = doctorMongoRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            doctor.setName(doctorDetails.getFullName());
            doctor.setOrderRegistrationNumber(doctorDetails.getOrderRegistrationNumber());
            doctor.setEmail(doctorDetails.getEmail());
            doctor.setFiscalCode(doctorDetails.getFiscalCode());
            doctor.setDob(doctorDetails.getBirthDate());
            doctor.setGender(doctorDetails.getGender());

            doctorNeo4jRepository.updateName(doctorId, sanitizeForNeo4j(doctorDetails.getFullName()));

            sanitizeDoctorMongo(doctor);
            updateDoctor(doctor.getId(), doctor); // Save updated doctor with the updated user details
            return doctorDetails;
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

            sanitizeDoctorMongo(doctor);
            updateDoctor(doctor.getId(), doctor);
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
                updateDoctor(doctor.getId(), doctor);
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
                sanitizeDoctorMongo(doctor);
                updateDoctor(doctor.getId(), doctor);
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
                updateDoctor(doctor.getId(), doctor);
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
        updateDoctor(doc.getId(), doc);
    }

    @Transactional
    public void unendorse(String doctorId, String patientId) {
        userNeo4jRepository.unendorse(patientId, doctorId);

        Doctor doc = doctorMongoRepository.findById(doctorId).orElseThrow(DoctorNotFoundException::new);
        doc.setEndorsementCount(doc.getEndorsementCount() - 1);
        updateDoctor(doc.getId(), doc);
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

    public void cleanOldSchedules(LocalDate date) {
        Date currentDate = DateUtil.convertToDate(date);
        doctorMongoRepository.cleanOldSchedules(currentDate);
    }

    public void cleanOldSchedules() {
        LocalDate currentDate = LocalDate.now();
        cleanOldSchedules(currentDate);
    }

    public void setupNewSchedules() {
        List<Doctor> doctors = doctorMongoRepository.findDoctorsMissingSchedulesInNext4Weeks();
        LocalDate start = LocalDate.now();
        List<LocalDate> allMondays = DateUtil.getNext4Mondays(start);

        long startTime = System.nanoTime(); // Tempo iniziale
        long lastLogTime = startTime;
        int found = doctors.size();
        System.out.println("Found " + found + " doctors with missing schedules.");
        int processed = 0;
        for (Doctor doctor : doctors) {
            setupDoctorSchedules(doctor, allMondays);
            processed++;
            if (processed % 100 == 0 || processed == found) {
                long now = System.nanoTime();
                long totalElapsedMs = (now - startTime) / 1_000_000;
                long intervalElapsedMs = (now - lastLogTime) / 1_000_000;
                int percent = (int) ((processed / (double) found) * 100);
                System.out.println("Processed " + processed + " doctors. [" + percent + "%] - Elapsed: " + totalElapsedMs + " ms (+" + intervalElapsedMs + " ms)");
                lastLogTime = now;
            }
        }

        long endTime = System.nanoTime();
        long durationMs = (endTime - startTime) / 1_000_000;
        System.out.println("setupNewSchedules completed in " + durationMs + " ms.");
    }

    private void setupDoctorSchedules(Doctor doctor, List<LocalDate> allMondays) {
        CalendarTemplate defaultTemplate = getDefaultTemplate(doctor.getId());
        if (defaultTemplate == null) {
            throw new IllegalStateException("Default template not found for doctor with ID: " + doctor.getId());
        }
        List<Date> foundSchedules = doctorMongoRepository.findSchedulesWithinNext4Weeks(doctor.getId());

        Set<LocalDate> foundDates = foundSchedules.stream()
                .map(DateUtil::convertToLocalDate)
                .collect(Collectors.toSet());

        List<LocalDate> missingMondays = allMondays.stream()
                .filter(d -> !foundDates.contains(d))
                .toList();

        for (LocalDate monday : missingMondays) {
            Schedule schedule = ScheduleConverter.buildScheduleForMonday(monday, defaultTemplate);
            addSchedule(doctor.getId(), schedule);
        }
    }
}