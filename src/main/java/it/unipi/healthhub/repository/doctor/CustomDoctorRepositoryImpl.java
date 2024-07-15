package it.unipi.healthhub.repository.doctor;

import com.mongodb.DBObject;
import it.unipi.healthhub.model.Appointment;
import it.unipi.healthhub.model.Doctor;
import it.unipi.healthhub.util.DateUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class CustomDoctorRepositoryImpl implements CustomDoctorRepository{
    private final MongoTemplate mongoTemplate;

    public CustomDoctorRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public boolean checkScheduleSlot(String doctorId, Integer year, Integer week, String keyDay, String slotStart) {
        LocalDateTime startOfWeek = DateUtil.getFirstDayOfWeek(week, year).atStartOfDay();

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("_id").is(doctorId)),
                Aggregation.unwind("schedules"),
                Aggregation.match(Criteria.where("schedules.week").is(startOfWeek)),
                Aggregation.project().and("schedules.slots."+keyDay).as("slots"),
                Aggregation.unwind("slots"),
                Aggregation.match(Criteria.where("slots.start").is(slotStart)),
                Aggregation.project("slots.taken")
        );

        AggregationResults<DBObject> result = mongoTemplate.aggregate(aggregation, Doctor.class, DBObject.class);
        if (result.getMappedResults().isEmpty()){
            System.out.println("Error: No slot found");
            return false;
        }
        return result.getMappedResults().get(0).get("taken").equals(true);
    }

    @Override
    public void bookScheduleSlot(String doctorId, Integer year, Integer week, String keyDay, String slotStart) {
        LocalDateTime startOfWeek = DateUtil.getFirstDayOfWeek(week, year).atStartOfDay();

        Query query = Query.query(
                Criteria.where("_id").is(doctorId)
                        .and("schedules.week").is(startOfWeek)
                        .and("schedules.slots." + keyDay + ".start").is(slotStart)
        );
        Update update = new Update().set("schedules.$.slots." + keyDay + ".$[slot].taken", true)
                .filterArray(Criteria.where("slot.start").is(slotStart));
        mongoTemplate.updateMulti(query, update, Doctor.class);
    }

    @Override
    public void freeScheduleSlot(String doctorId, Integer year, Integer week, String keyDay, String slotStart) {
        LocalDateTime startOfWeek = DateUtil.getFirstDayOfWeek(week, year).atStartOfDay();

        Query query = new Query(
                Criteria.where("_id").is(doctorId)
                        .and("schedules.week").is(startOfWeek)
                        .and("schedules.slots." + keyDay + ".start").is(slotStart)
        );
        Update update = new Update().set("schedules.$.slots." + keyDay + ".$[slot].taken", false)
                .filterArray(Criteria.where("slot.start").is(slotStart));
        mongoTemplate.updateMulti(query, update, Doctor.class);
    }
}
