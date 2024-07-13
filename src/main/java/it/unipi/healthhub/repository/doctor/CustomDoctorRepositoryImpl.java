package it.unipi.healthhub.repository.doctor;

import org.springframework.data.mongodb.core.MongoTemplate;

public class CustomDoctorRepositoryImpl implements CustomDoctorRepository{
    private final MongoTemplate mongoTemplate;

    public CustomDoctorRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public boolean updateScheduleSlot(String doctorId, Integer year, Integer week, String keyDay, boolean b) {
        return false;
    }
}
