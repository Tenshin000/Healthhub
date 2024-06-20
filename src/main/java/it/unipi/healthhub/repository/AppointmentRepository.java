package it.unipi.healthhub.repository;

import it.unipi.healthhub.model.Appointment;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface AppointmentRepository extends MongoRepository<Appointment, String>{
    Appointment findByDoctorId(String doctorId);
    Appointment findByPatientId(String patientId);
    Appointment findByDoctorIdAndPatientId(String doctorId, String patientId);
    Appointment findByDoctorIdAndPatientIdAndTime(String doctorId, String patientId, String time);
    Appointment findByDoctorIdAndPatientIdAndTimeAndStatus(String doctorId, String patientId, String time, String status);
    Appointment findByDoctorIdAndPatientIdAndTimeAndStatusAndType(String doctorId, String patientId, String time, String status, String type);
    Appointment findByDoctorIdAndPatientIdAndTimeAndStatusAndTypeAndReason(String doctorId, String patientId, String time, String status, String type, String reason);
    Appointment findByDoctorIdAndPatientIdAndTimeAndStatusAndTypeAndReasonAndNotes(String doctorId, String patientId, String time, String status, String type, String reason, String notes);
    Appointment findByDoctorIdAndPatientIdAndTimeAndStatusAndTypeAndReasonAndNotesAndPrescription(String doctorId, String patientId, String time, String status, String type, String reason, String notes, String prescription);
    Appointment findByDoctorIdAndPatientIdAndTimeAndStatusAndTypeAndReasonAndNotesAndPrescriptionAndRating(String doctorId, String patientId, String time, String status, String type, String reason, String notes, String prescription, int rating);
    Appointment findByDoctorIdAndPatientIdAndTimeAndStatusAndTypeAndReasonAndNotesAndPrescriptionAndRatingAndReview(String doctorId, String patientId, String time, String status, String type, String reason, String notes, String prescription, int rating, String review);
    Appointment findByDoctorIdAndPatientIdAndTimeAndStatusAndTypeAndReasonAndNotesAndPrescriptionAndRatingAndReviewAndFeedback(String doctorId, String patientId, String time, String status, String type, String reason, String notes, String prescription, int rating, String review, String feedback);
    Appointment findByDoctorIdAndPatientIdAndTimeAndStatusAndTypeAndReasonAndNotesAndPrescriptionAndRatingAndReviewAndFeedbackAndFeedbackTime(String doctorId, String patientId, String time, String status, String type, String reason, String notes, String prescription, int rating, String review, String feedback, String feedbackTime);

}
