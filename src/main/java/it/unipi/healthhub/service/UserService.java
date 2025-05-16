package it.unipi.healthhub.service;

import it.unipi.healthhub.dto.AppointmentDTO;
import it.unipi.healthhub.dto.PatientContactsDTO;
import it.unipi.healthhub.dto.UserDetailsDTO;
import it.unipi.healthhub.exception.UserNotFoundException;
import it.unipi.healthhub.model.mongo.Address;
import it.unipi.healthhub.model.mongo.Appointment;
import it.unipi.healthhub.model.mongo.Doctor;
import it.unipi.healthhub.model.mongo.User;
import it.unipi.healthhub.model.neo4j.DoctorDAO;
import it.unipi.healthhub.model.neo4j.UserDAO;
import it.unipi.healthhub.repository.mongo.AppointmentMongoRepository;
import it.unipi.healthhub.repository.mongo.DoctorMongoRepository;
import it.unipi.healthhub.repository.mongo.UserMongoRepository;
import it.unipi.healthhub.repository.neo4j.UserNeo4jRepository;
import it.unipi.healthhub.util.FakeMailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserMongoRepository userMongoRepository;
    @Autowired
    private UserNeo4jRepository userNeo4jRepository;
    @Autowired
    private AppointmentMongoRepository appointmentMongoRepository;
    @Autowired
    private DoctorMongoRepository doctorMongoRepository;
    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private FakeMailSender fakeMailSender;

    // --- Utilities for sanitization ---

    private String sanitizeForMongo(String input) {
        if (input == null) return null;
        return input.replaceAll("[\\$]", "_");
    }

    private void sanitizeFieldMongo(Consumer<String> setter, Supplier<String> getter) {
        setter.accept(sanitizeForMongo(getter.get()));
    }

    private void sanitizeUserMongo(User user) {
        if (user == null) return;
        sanitizeFieldMongo(user::setName, user::getName);
        sanitizeFieldMongo(user::setUsername, user::getUsername);
        sanitizeFieldMongo(user::setPassword, user::getPassword);
        sanitizeFieldMongo(user::setFiscalCode, user::getFiscalCode);
        sanitizeFieldMongo(user::setGender, user::getGender);
        sanitizeFieldMongo(user::setEmail, user::getEmail);
        sanitizeFieldMongo(user::setPersonalNumber, user::getPersonalNumber);
    }

    private String sanitizeForNeo4j(String input) {
        if (input == null) return null;
        return input.replaceAll("[\\$\\.]", "_");
    }

    private void sanitizeFieldNeo4j(Consumer<String> setter, Supplier<String> getter) {
        setter.accept(sanitizeForNeo4j(getter.get()));
    }

    private void sanitizeStringListNeo4j(List<String> list) {
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                list.set(i, sanitizeForNeo4j(list.get(i)));
            }
        }
    }

    private void sanitizeDoctorNeo4j(DoctorDAO doctor) {
        if (doctor == null) return;
        sanitizeFieldNeo4j(doctor::setId, doctor::getId);
        sanitizeFieldNeo4j(doctor::setName, doctor::getName);
        sanitizeStringListNeo4j(doctor.getSpecializations());
    }

    private void sanitizeUserNeo4j(UserDAO user) {
        if (user == null) return;
        sanitizeFieldNeo4j(user::setId, user::getId);
        sanitizeFieldNeo4j(user::setName, user::getName);

        if (user.getEndorsedDoctors() != null) {
            user.getEndorsedDoctors().forEach(this::sanitizeDoctorNeo4j);
        }

        if (user.getReviewedDoctors() != null) {
            user.getReviewedDoctors().forEach(this::sanitizeDoctorNeo4j);
        }
    }

    // --- User Management ---

    public List<User> getAllUser() {
        return userMongoRepository.findAll();
    }

    public Optional<User> getUserById(String id) {
        return userMongoRepository.findById(id);
    }

    @Transactional
    public User createUser(User user) {
        sanitizeUserMongo(user);
        if (userMongoRepository.findByUsername(user.getUsername()) != null ||
                userMongoRepository.findByEmail(user.getEmail()) != null ||
                doctorMongoRepository.findByUsername(user.getUsername()) != null ||
                doctorMongoRepository.findByEmail(user.getEmail()) != null) {
            return null;
        }

        User savedUser = userMongoRepository.save(user);
        UserDAO userDAO = new UserDAO(savedUser.getId(), savedUser.getName());
        sanitizeUserNeo4j(userDAO);
        userNeo4jRepository.save(userDAO);
        return savedUser;
    }

    @Transactional
    public User updateUser(String id, User userData) {
        if (!userMongoRepository.existsById(id)) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
        userData.setId(id);
        sanitizeUserMongo(userData);
        return userMongoRepository.save(userData);
    }

    public void deleteUser(String id) {;
        userMongoRepository.deleteById(id);
    }

    public User loginUser(String username, String password) {
        username = sanitizeForMongo(username);
        User user = userMongoRepository.findByUsername(username);
        if (user == null) user = userMongoRepository.findByEmail(username);
        return (user != null && user.getPassword().equals(password)) ? user : null;
    }

    public User findByEmail(String email) {
        email = sanitizeForMongo(email);
        return userMongoRepository.findByEmail(email);
    }

    public boolean changePassword(String id, String currentPassword, String newPassword) {
        Optional<User> userOpt = getUserById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (currentPassword.equals(user.getPassword())) {
                user.setPassword(newPassword);
                updateUser(user.getId(), user);
                return true;
            }
        }
        return false;
    }

    // --- User Details & Contacts ---

    @Transactional
    public UserDetailsDTO updateUserDetails(String patientId, UserDetailsDTO userDetails) {
        Optional<User> userOpt = userMongoRepository.findById(patientId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setName(userDetails.getFullName());
            user.setFiscalCode(userDetails.getFiscalCode());
            user.setDob(userDetails.getBirthDate());
            user.setGender(userDetails.getGender());

            userNeo4jRepository.updateName(patientId, userDetails.getFullName());

            sanitizeUserMongo(user);
            userMongoRepository.save(user);
            return userDetails;
        }
        return null;
    }

    public UserDetailsDTO getUserDetails(String patientId) {
        return userMongoRepository.findById(patientId)
                .map(user -> new UserDetailsDTO(user.getName(), user.getFiscalCode(), user.getDob(), user.getGender()))
                .orElse(null);
    }

    public PatientContactsDTO getUserContacts(String patientId) {
        return userMongoRepository.findById(patientId)
                .map(user -> user.getAddress() != null
                        ? new PatientContactsDTO(user.getEmail(), user.getPersonalNumber(),
                        user.getAddress().getStreet(), user.getAddress().getCity(),
                        user.getAddress().getProvince(), user.getAddress().getPostalCode(),
                        user.getAddress().getCountry())
                        : new PatientContactsDTO(user.getEmail(), user.getPersonalNumber()))
                .orElse(null);
    }

    @Transactional
    public PatientContactsDTO updateUserContacts(String patientId, PatientContactsDTO userContacts) {
        Optional<User> userOpt = userMongoRepository.findById(patientId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setEmail(userContacts.getEmail());
            user.setPersonalNumber(userContacts.getPhoneNumber());
            user.setAddress(new Address(userContacts.getStreet(), userContacts.getCity(),
                    userContacts.getProvince(), userContacts.getPostalCode(), userContacts.getCountry()));

            sanitizeUserMongo(user);
            userMongoRepository.save(user);

            return new PatientContactsDTO(user.getEmail(), user.getPersonalNumber(),
                    user.getAddress().getStreet(), user.getAddress().getCity(),
                    user.getAddress().getProvince(), user.getAddress().getPostalCode(),
                    user.getAddress().getCountry());
        }
        return null;
    }

    // --- Appointment Management ---

    @Transactional
    public List<Appointment> getUpcomingAppointments(String patientId) {
        return appointmentMongoRepository.findByPatientIdFromDate(patientId, LocalDate.now());
    }

    @Transactional
    public List<Appointment> getPastAppointments(String patientId) {
        return appointmentMongoRepository.findByPatientIdBeforeDate(patientId, LocalDate.now());
    }

    @Transactional
    public boolean cancelAppointment(String appointmentId) {
        return appointmentMongoRepository.findById(appointmentId)
                .map(this::deleteAppointment)
                .orElse(false);
    }

    private boolean deleteAppointment(Appointment appointment) {
        LocalDateTime dateTimeSlot = appointment.getDate();
        Integer year = dateTimeSlot.getYear();
        Integer week = dateTimeSlot.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
        String keyDay = dateTimeSlot.getDayOfWeek().toString().toLowerCase();
        String slotStart = dateTimeSlot.toLocalTime().toString();
        String doctorId = appointment.getDoctor().getId();

        if (doctorMongoRepository.checkScheduleSlot(doctorId, year, week, keyDay, slotStart)) {
            appointmentService.deleteAppointment(appointment.getId());
            doctorMongoRepository.freeScheduleSlot(doctorId, year, week, keyDay, slotStart);
            fakeMailSender.sendDeletedAppointmentMailByPatient(appointment);
            return true;
        }
        return false;
    }

    // --- Neo4j Relations & Recommendations ---

    public boolean hasEndorsed(String patientId, String doctorId) {
        UserDAO userDAO = userNeo4jRepository.findById(patientId).orElseThrow(UserNotFoundException::new);
        return userDAO.getEndorsedDoctors().stream().anyMatch(doctor -> doctor.getId().equals(doctorId));
    }

    @Transactional
    public List<Doctor> getEndorsedDoctors(String patientId) {
        UserDAO userDAO = userNeo4jRepository.findById(patientId).orElseThrow(UserNotFoundException::new);
        List<Doctor> endorsedDoctors = new ArrayList<>();
        userDAO.getEndorsedDoctors().forEach(doc -> doctorMongoRepository.findById(doc.getId()).ifPresent(endorsedDoctors::add));
        return endorsedDoctors;
    }

    @Transactional
    public List<Doctor> getReviewedDoctors(String userId) {
        UserDAO userDAO = userNeo4jRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        List<Doctor> reviewedDoctors = new ArrayList<>();
        userDAO.getReviewedDoctors().forEach(doc -> doctorMongoRepository.findById(doc.getId()).ifPresent(reviewedDoctors::add));
        return reviewedDoctors;
    }

    @Transactional
    public List<DoctorDAO> getRecommendedDoctors(String userId, int limit) {
        List<DoctorDAO> recommendedDoctors = userNeo4jRepository.recommendDoctorsForUser(userId, limit);

        if (recommendedDoctors.size() < limit) {
            int remaining = limit - recommendedDoctors.size();
            Set<String> alreadyAddedIds = recommendedDoctors.stream()
                    .map(DoctorDAO::getId)
                    .collect(Collectors.toSet());

            List<DoctorDAO> popularDoctors = userNeo4jRepository.recommendPopularDoctors(remaining * 2);
            if (!popularDoctors.isEmpty()) {
                for (DoctorDAO doctor : popularDoctors) {
                    if (!alreadyAddedIds.contains(doctor.getId())) {
                        recommendedDoctors.add(doctor);
                        if (recommendedDoctors.size() == limit)
                            break;
                    }
                }
            }
        }

        return recommendedDoctors;
    }

    // --- Mail Operations ---

    public boolean sendPasswordReset(String email, String link) {
        return fakeMailSender.sendPasswordResetLink(email, link);
    }
}
