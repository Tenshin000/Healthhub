package it.unipi.healthhub.repository.mongo.doctor;

import it.unipi.healthhub.model.mongo.Doctor;
import it.unipi.healthhub.projection.DoctorMongoProjection;
import org.bson.Document;

import java.util.Date;
import java.util.List;

public interface CustomDoctorMongoRepository {
    boolean checkScheduleSlot(String doctorId, Integer year, Integer week, String keyDay, String slotStart);
    void bookScheduleSlot(String doctorId, Integer year, Integer week, String keyDay, String slotStart);
    void freeScheduleSlot(String doctorId, Integer year, Integer week, String keyDay, String slotStart);
    public List<DoctorMongoProjection> searchDoctors(String text);
    void cleanOldSchedules(Date currentDate);
    List<Doctor> findDoctorsMissingSchedulesInNext4Weeks();
    List<Date> findSchedulesWithinNext4Weeks(String doctorId);
    void updateReviewName(String id, String patientId, String patientName);
}
