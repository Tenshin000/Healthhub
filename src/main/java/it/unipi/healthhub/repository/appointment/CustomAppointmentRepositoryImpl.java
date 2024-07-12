package it.unipi.healthhub.repository.appointment;

import it.unipi.healthhub.model.Appointment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDate;
import java.util.List;

public class CustomAppointmentRepositoryImpl implements CustomAppointmentRepository{
    private final MongoTemplate mongoTemplate;

    public CustomAppointmentRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Appointment> findByDoctorIdAndDay(String doctorId, LocalDate day) {
        Query query = new Query();
        query.addCriteria(Criteria.where("doctorInfo.doctorId").is(doctorId)
                .and("appointmentDateTime").gte(day.atStartOfDay()).lt(day.plusDays(1).atStartOfDay())
        );
        return mongoTemplate.find(query, Appointment.class);
    }
}
