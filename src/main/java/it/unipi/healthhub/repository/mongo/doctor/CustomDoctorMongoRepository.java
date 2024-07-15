package it.unipi.healthhub.repository.mongo.doctor;

public interface CustomDoctorMongoRepository {
    boolean checkScheduleSlot(String doctorId, Integer year, Integer week, String keyDay, String slotStart);
    void bookScheduleSlot(String doctorId, Integer year, Integer week, String keyDay, String slotStart);
    void freeScheduleSlot(String doctorId, Integer year, Integer week, String keyDay, String slotStart);

}
