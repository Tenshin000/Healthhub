package it.unipi.healthhub.repository.appointment;

import com.mongodb.DBObject;
import it.unipi.healthhub.model.Appointment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.thymeleaf.util.DateUtils;

import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CustomAppointmentRepositoryImpl implements CustomAppointmentRepository{
    private final MongoTemplate mongoTemplate;

    public CustomAppointmentRepositoryImpl(MongoTemplate mongoTemplate) {
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

        // Problemi di casting
        // Da verificare una volta che il database è popolato
        Map<String, Double> earningsByYear = new HashMap<>();
        for (DBObject doc : results.getMappedResults()) {
            Integer month = (Integer) doc.get("month");
            String monthString = new DateFormatSymbols(Locale.ENGLISH).getMonths()[month-1].toLowerCase();
            Double total = (Double) doc.get("total");
            earningsByYear.put(monthString, total);
        }

        return earningsByYear;
    }
}
