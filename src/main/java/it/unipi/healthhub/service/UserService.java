package it.unipi.healthhub.service;

import it.unipi.healthhub.dto.AppointmentDTO;
import it.unipi.healthhub.dto.PatientContactsDTO;
import it.unipi.healthhub.dto.UserDetailsDTO;
import it.unipi.healthhub.model.mongo.Address;
import it.unipi.healthhub.model.mongo.Appointment;
import it.unipi.healthhub.model.mongo.Doctor;
import it.unipi.healthhub.model.mongo.User;
import it.unipi.healthhub.model.neo4j.UserDAO;
import it.unipi.healthhub.repository.mongo.AppointmentMongoRepository;
import it.unipi.healthhub.repository.mongo.DoctorMongoRepository;
import it.unipi.healthhub.repository.mongo.UserMongoRepository;
import it.unipi.healthhub.repository.neo4j.UserNeo4jRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

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

    public List<User> getAllUser(){
        return userMongoRepository.findAll();
    }

    public Optional<User> getUserById(String id){
        return userMongoRepository.findById(id);
    }

    @Transactional
    public User createUser(User user){
        User savedUser = userMongoRepository.save(user);
        UserDAO userDAO = new UserDAO(savedUser.getId(), savedUser.getName());
        userNeo4jRepository.save(userDAO);
        return savedUser;
    }

    @Transactional
    public User updateUser(String id, User user){
        Optional<User> userOptional = userMongoRepository.findById(id);
        if(userOptional.isPresent()){
            User userToUpdate = userOptional.get();
            // Update the user
            return userMongoRepository.save(userToUpdate);
        }
        return null;
    }

    public void deleteUser(String id){
        userMongoRepository.deleteById(id);
    }

    public User loginUser(String username, String password) {
        User user = userMongoRepository.findByUsername(username);

        if (user != null && user.getPassword().equals(password)) {
            System.out.println("User found");
            return user;
        }

        return null;
    }

    public boolean hasEndorsed(String patientId, String doctorId) {
        UserDAO userDAO = userNeo4jRepository.findById(patientId).orElseThrow(() -> new RuntimeException("User not found"));
        return userDAO.getEndorsedDoctors().stream().anyMatch(doctor -> doctor.getId().equals(doctorId));
    }

    @Transactional
    public UserDetailsDTO updateUserDetails(String patientId, UserDetailsDTO userDetails) {
        Optional<User> userOpt = userMongoRepository.findById(patientId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setName(userDetails.getFullName());
            user.setDob(userDetails.getBirthDate());
            user.setGender(userDetails.getGender());

            userNeo4jRepository.updateName(patientId, userDetails.getFullName());

            userMongoRepository.save(user); // Save updated doctor with the updated user details
            return userDetails;
        }
        return null;
    }

    public UserDetailsDTO getUserDetails(String patientId) {
        Optional<User> userOpt = userMongoRepository.findById(patientId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return new UserDetailsDTO(user.getName(), user.getDob(), user.getGender());
        }
        return null;
    }

    public PatientContactsDTO getUserContacts(String patientId) {
        Optional<User> userOpt = userMongoRepository.findById(patientId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if(user.getAddress() != null)
                return new PatientContactsDTO(user.getEmail(), user.getPersonalNumber(), user.getAddress().getStreet(), user.getAddress().getCity(), user.getAddress().getProvince(), user.getAddress().getPostalCode(), user.getAddress().getCountry());
            else
                return new PatientContactsDTO(user.getEmail(), user.getPersonalNumber());
        }
        return null;
    }

    @Transactional
    public PatientContactsDTO updateUserContacts(String patientId, PatientContactsDTO userContacts) {
        Optional<User> userOpt = userMongoRepository.findById(patientId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setEmail(userContacts.getEmail());
            user.setPersonalNumber(userContacts.getPhoneNumber());

            Address address = new Address(userContacts.getStreet(), userContacts.getCity(), userContacts.getProvince(), userContacts.getPostalCode(), userContacts.getCountry());

            user.setAddress(address);

            userMongoRepository.save(user); // Save updated doctor with the updated user details
            return new PatientContactsDTO(user.getEmail(), user.getPersonalNumber(), user.getAddress().getStreet(), user.getAddress().getCity(), user.getAddress().getProvince(), user.getAddress().getPostalCode(), user.getAddress().getCountry());
        }
        return null;
    }

    @Transactional
    public List<Appointment> getUpcomingAppointments(String patientId) {
        LocalDate currentDate = LocalDate.now();
        return appointmentMongoRepository.findByPatientIdFromDate(patientId, currentDate);
    }

    @Transactional
    public List<Appointment> getPastAppointments(String patientId) {
        LocalDate currentDate = LocalDate.now();
        return appointmentMongoRepository.findByPatientIdBeforeDate(patientId, currentDate);
    }

    @Transactional
    public boolean deleteAppointment(String appointmentId) {
        Optional<Appointment> appointmentOpt = appointmentMongoRepository.findById(appointmentId);
        if (appointmentOpt.isPresent()) {
            Appointment appointment = appointmentOpt.get();

            LocalDateTime dateTimeSlot = appointment.getDate();

            Integer year = dateTimeSlot.getYear();
            Integer week = dateTimeSlot.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
            String keyDay = dateTimeSlot.getDayOfWeek().toString().toLowerCase();
            String slotStart = dateTimeSlot.toLocalTime().toString();
            String doctorId = appointment.getDoctor().getId();

            boolean taken = doctorMongoRepository.checkScheduleSlot(doctorId, year, week, keyDay, slotStart);
            if(taken){
                appointmentMongoRepository.deleteById(appointmentId);
                doctorMongoRepository.freeScheduleSlot(doctorId, year, week, keyDay, slotStart);
                return true;
            }
        }
        return false;
    }

    @Transactional
    public List<Doctor> getEndorsedDoctors(String patientId) {
        UserDAO userDAO = userNeo4jRepository.findById(patientId).orElseThrow(() -> new RuntimeException("User not found"));
        List<Doctor> endorsedDoctors = new ArrayList<>();
        userDAO.getEndorsedDoctors().forEach(doctorDAO -> {
            Optional<Doctor> doctorOpt = doctorMongoRepository.findById(doctorDAO.getId());
            doctorOpt.ifPresent(endorsedDoctors::add);
        });
        return endorsedDoctors;
    }

    @Transactional
    public List<Doctor> getReviewedDoctors(String userId) {
        UserDAO userDAO = userNeo4jRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        List<Doctor> reviewedDoctors = new ArrayList<>();
        userDAO.getReviewedDoctors().forEach(doctorDAO -> {
            Optional<Doctor> doctorOpt = doctorMongoRepository.findById(doctorDAO.getId());
            doctorOpt.ifPresent(reviewedDoctors::add);
        });
        return reviewedDoctors;
    }
}
