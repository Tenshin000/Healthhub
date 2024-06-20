package it.unipi.healthhub.repository;

import it.unipi.healthhub.model.Doctor;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DoctorRepository extends MongoRepository<Doctor, String> {
    Doctor findByUsername(String username);
    Doctor findById(ObjectId id);
    List<Doctor> findBySpecializations(String specialization);
    List<Doctor> findByServices(String service);
    List<Doctor> findByPhoneNumbers(String phoneNumber);
    List<Doctor> findByEndorsementCount(int endorsementCount);
    List<Doctor> findByCalendarTemplates(String calendarTemplate);
    List<Doctor> findBySchedule(String schedule);
    List<Doctor> findByAppointments(String appointment);
    List<Doctor> findByReviews(String review);
    List<Doctor> findByServicesAndSpecializations(String service, String specialization);
    List<Doctor> findByServicesAndSpecializationsAndPhoneNumbers(String service, String specialization, String phoneNumber);
    List<Doctor> findByServicesAndSpecializationsAndPhoneNumbersAndEndorsementCount(String service, String specialization, String phoneNumber, int endorsementCount);
    List<Doctor> findByServicesAndSpecializationsAndPhoneNumbersAndEndorsementCountAndCalendarTemplates(String service, String specialization, String phoneNumber, int endorsementCount, String calendarTemplate);
    List<Doctor> findByServicesAndSpecializationsAndPhoneNumbersAndEndorsementCountAndCalendarTemplatesAndSchedule(String service, String specialization, String phoneNumber, int endorsementCount, String calendarTemplate, String schedule);
    List<Doctor> findByServicesAndSpecializationsAndPhoneNumbersAndEndorsementCountAndCalendarTemplatesAndScheduleAndAppointments(String service, String specialization, String phoneNumber, int endorsementCount, String calendarTemplate, String schedule, String appointment);
    List<Doctor> findByServicesAndSpecializationsAndPhoneNumbersAndEndorsementCountAndCalendarTemplatesAndScheduleAndAppointmentsAndReviews(String service, String specialization, String phoneNumber, int endorsementCount, String calendarTemplate, String schedule, String appointment, String review);
    List<Doctor> findByServicesAndSpecializationsAndPhoneNumbersAndEndorsementCountAndCalendarTemplatesAndScheduleAndAppointmentsAndReviewsAndName(String service, String specialization, String phoneNumber, int endorsementCount, String calendarTemplate, String schedule, String appointment, String review, String name);
    List<Doctor> findByServicesAndSpecializationsAndPhoneNumbersAndEndorsementCountAndCalendarTemplatesAndScheduleAndAppointmentsAndReviewsAndNameAndGender(String service, String specialization, String phoneNumber, int endorsementCount, String calendarTemplate, String schedule, String appointment, String review, String name);
}
