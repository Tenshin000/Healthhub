package it.unipi.healthhub.repository.mongo.appointment;

import com.mongodb.DBObject;
import it.unipi.healthhub.dao.mongo.Appointment;
import it.unipi.healthhub.util.DateUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CustomAppointmentMongoRepositoryImpl implements CustomAppointmentMongoRepository {
    private final MongoTemplate mongoTemplate;

    public CustomAppointmentMongoRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Appointment> findByDoctorIdAndDay(String doctorId, LocalDate day) {
        Query query = new Query();
        query.addCriteria(Criteria.where("doctorInfo.doctorId").is(doctorId)
                .and("appointmentDateTime")
                .gte(day.atStartOfDay())
                .lt(day.plusDays(1).atStartOfDay())
        );
        return mongoTemplate.find(query, Appointment.class);
    }

    @Override
    public Map<String, Integer> getVisitsCountByTypeForDoctor(String doctorId){
        MatchOperation matchOperation = Aggregation.match(Criteria.where("doctorInfo.doctorId").is(doctorId));
        GroupOperation groupOperation = Aggregation.group("visitType").count().as("total");
        ProjectionOperation projectionOperation = Aggregation.project("total").and("visitType").previousOperation();

        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                groupOperation,
                projectionOperation
        );

        AggregationResults<DBObject> results = mongoTemplate.aggregate(aggregation, Appointment.class, DBObject.class);

        Map<String, Integer> visitsCountByType = new HashMap<>();
        for (DBObject doc : results.getMappedResults()) {
            String visitType = (String) doc.get("visitType");
            Integer total = (Integer) doc.get("total");
            visitsCountByType.put(visitType, total);
        }

        return visitsCountByType;
    }

    @Override
    public Map<String, Double> getEarningsByYearForDoctor(String doctorId, Integer year) {

        MatchOperation matchOperation = Aggregation.match(Criteria.where("doctorInfo.doctorId").is(doctorId)
                .and("appointmentDateTime")
                .gte(LocalDate.of(year, 1, 1).atStartOfDay())
                .lt(LocalDate.of(year + 1, 1, 1).atStartOfDay())
        );
        ProjectionOperation projectMonth = Aggregation.project().andExpression("month(appointmentDateTime)").as("month").and("price").as("price");
        GroupOperation groupOperation = Aggregation.group("month").sum("price").as("total");
        ProjectionOperation projectionOperation = Aggregation.project("total").and("month").previousOperation();

        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                projectMonth,
                groupOperation,
                projectionOperation
        );

        AggregationResults<DBObject> results = mongoTemplate.aggregate(aggregation, Appointment.class, DBObject.class);

        Map<String, Double> earningsByYear = new HashMap<>();
        for (DBObject doc : results.getMappedResults()) {
            Integer month = (Integer) doc.get("month");
            String monthString = new DateFormatSymbols(Locale.ENGLISH).getMonths()[month-1].toLowerCase();
            Double total = (Double) doc.get("total");
            earningsByYear.put(monthString, total);
        }

        return earningsByYear;
    }

    @Override
public Map<String, Integer> getVisitsCountByDayForDoctorWeek(String doctorId, Integer week, Integer year) {
        LocalDateTime startOfWeek = DateUtil.getFirstDayOfWeek(week, year).atStartOfDay();
        LocalDateTime endOfWeek = DateUtil.getFirstDayOfWeek(week+1, year).atStartOfDay();

        MatchOperation matchOperation = Aggregation.match(Criteria.where("doctorInfo.doctorId").is(doctorId)
                .and("appointmentDateTime")
                .gte(startOfWeek)
                .lt(endOfWeek)
        );
        ProjectionOperation projectDay = Aggregation.project().andExpression("dayOfWeek(appointmentDateTime)").as("day");
        GroupOperation groupOperation = Aggregation.group("day").count().as("total");

        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                projectDay,
                groupOperation
        );

        AggregationResults<DBObject> results = mongoTemplate.aggregate(aggregation, Appointment.class, DBObject.class);

        Map<String, Integer> visitsCountByDay = new HashMap<>();
        for (DBObject doc : results.getMappedResults()) {
            Integer day = (Integer) doc.get("_id");
            String dayString = new DateFormatSymbols(Locale.ENGLISH).getWeekdays()[day].toLowerCase();
            Integer total = (Integer) doc.get("total");
            visitsCountByDay.put(dayString, total);
        }

        return visitsCountByDay;
    }
}
