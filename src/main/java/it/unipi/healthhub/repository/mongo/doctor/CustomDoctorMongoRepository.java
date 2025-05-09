package it.unipi.healthhub.repository.mongo.doctor;

import it.unipi.healthhub.dto.DoctorDTO;
import it.unipi.healthhub.projection.DoctorMongoProjection;

import java.util.List;

public interface CustomDoctorMongoRepository {
    boolean checkScheduleSlot(String doctorId, Integer year, Integer week, String keyDay, String slotStart);
    void bookScheduleSlot(String doctorId, Integer year, Integer week, String keyDay, String slotStart);
    void freeScheduleSlot(String doctorId, Integer year, Integer week, String keyDay, String slotStart);
    public List<DoctorMongoProjection> searchDoctors(String text);
}
