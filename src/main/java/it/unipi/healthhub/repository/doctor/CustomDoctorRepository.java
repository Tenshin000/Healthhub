package it.unipi.healthhub.repository.doctor;

public interface CustomDoctorRepository {
    public boolean updateScheduleSlot(String doctorId, Integer year, Integer week, String keyDay, boolean b);

}
