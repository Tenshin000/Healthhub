package it.unipi.healthhub.repository.mongo.appointment;

import com.mongodb.DBObject;
import it.unipi.healthhub.model.mongo.Appointment;
import it.unipi.healthhub.model.mongo.User;
import it.unipi.healthhub.util.DateUtil;
import org.springframework.data.domain.Sort;
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
        query.addCriteria(Criteria.where("doctor.id").is(doctorId)
                .and("date")
                .gte(day.atStartOfDay())
                .lt(day.plusDays(1).atStartOfDay())
        );
        return mongoTemplate.find(query, Appointment.class);
    }

    @Override
    public Map<String, Integer> getVisitsCountByTypeForDoctor(String doctorId){
        MatchOperation matchOperation = Aggregation.match(Criteria.where("doctor.id").is(doctorId));
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

        MatchOperation matchOperation = Aggregation.match(Criteria.where("doctor.id").is(doctorId)
                .and("date")
                .gte(LocalDate.of(year, 1, 1).atStartOfDay())
                .lt(LocalDate.of(year + 1, 1, 1).atStartOfDay())
        );
        ProjectionOperation projectMonth = Aggregation.project().andExpression("month(date)").as("month").and("price").as("price");
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
            Double total = (doc.get("total") instanceof Number) ? ((Number) doc.get("total")).doubleValue() : null;
            earningsByYear.put(monthString, total);
        }

        return earningsByYear;
    }

    @Override
    public Map<String, Integer> getVisitsCountByDayForDoctorWeek(String doctorId, Integer week, Integer year) {
        LocalDateTime startOfWeek = DateUtil.getFirstDayOfWeek(week, year).atStartOfDay();
        LocalDateTime endOfWeek = DateUtil.getFirstDayOfWeek(week+1, year).atStartOfDay();

        MatchOperation matchOperation = Aggregation.match(Criteria.where("doctor.id").is(doctorId)
                .and("date")
                .gte(startOfWeek)
                .lt(endOfWeek)
        );
        ProjectionOperation projectDay = Aggregation.project().andExpression("dayOfWeek(date)").as("day");
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

    @Override
    public List<Appointment> findByPatientIdFromDate(String patientId, LocalDate date) {
        Query query = new Query();
        query.addCriteria(Criteria.where("patient.id").is(patientId)
                .and("date")
                .gte(date.atStartOfDay())
        );
        query.with(Sort.by(Sort.Direction.ASC, "date"));
        return mongoTemplate.find(query, Appointment.class);
    }

    @Override
    public List<Appointment> findByPatientIdBeforeDate(String patientId, LocalDate date) {
        Query query = new Query();
        query.addCriteria(Criteria.where("patient.id").is(patientId)
                .and("date").lt(date.atStartOfDay()));
        query.with(Sort.by(Sort.Direction.ASC, "date"));  // Ordina in ordine ascendente di data

        return mongoTemplate.find(query, Appointment.class);
    }

    @Override
    public Integer findNewPatientsVisitedByDoctorInCurrentMonth(String doctorId, Integer year, Integer month){
        LocalDateTime startOfMonth = LocalDate.of(year, month, 1).atStartOfDay();
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1);

        // 1. Filter all appointments for this doctor
        MatchOperation matchDoctor = Aggregation.match(
                Criteria.where("doctor.id").is(doctorId)
        );

        // 2. Group by patient ID and compute the date of their first visit
        GroupOperation groupByPatient = Aggregation.group("patient.id")
                .min("date").as("firstVisitDate");

        // 3. Keep only those patients whose first visit falls in the current month
        MatchOperation matchFirstVisitInMonth = Aggregation.match(
                Criteria.where("firstVisitDate").gte(startOfMonth).lt(endOfMonth)
        );

        // 4. Count how many unique new patients remain
        CountOperation countNewPatients = Aggregation.count().as("newPatientsCount");

        Aggregation aggregation = Aggregation.newAggregation(
                matchDoctor,
                groupByPatient,
                matchFirstVisitInMonth,
                countNewPatients
        );

        // 5. Execute the aggregation pipeline
        AggregationResults<org.bson.Document> results =
                mongoTemplate.aggregate(aggregation, Appointment.class, org.bson.Document.class);

        List<org.bson.Document> mappedResults = results.getMappedResults();
        if(mappedResults.isEmpty()){
            return 0;
        }
        else{
            return mappedResults.get(0).getInteger("newPatientsCount", 0);
        }
    }

    @Override
    public List<Appointment> findByDoctorIdAndWeek(String doctorId, Integer week, Integer year) {
        // Calculate the start of the requested week (Monday at 00:00)
        LocalDateTime startOfWeek = DateUtil.getFirstDayOfWeek(week, year).atStartOfDay();
        // Calculate the start of the following week to use as exclusive upper bound
        LocalDateTime endOfWeek   = DateUtil.getFirstDayOfWeek(week + 1, year).atStartOfDay();

        // Build a query filtering by doctor id and date within [startOfWeek, endOfWeek)
        Query query = new Query();
        query.addCriteria(Criteria.where("doctor.id").is(doctorId)
                .and("date").gte(startOfWeek).lt(endOfWeek));
        // Optionally sort results in ascending order by date
        query.with(Sort.by(Sort.Direction.ASC, "date"));

        // Execute the query and return matching appointments
        return mongoTemplate.find(query, Appointment.class);
    }

    /**
     * Checks whether there is at least one appointment in the past between a given doctor and a given patient.
     *
     * @param doctorId  doctor id
     * @param patientId patient id
     * @return true if there is at least one appointment with date < now, false otherwise
     */
    public boolean hasPastAppointment(String doctorId, String patientId){
        Query q = new Query().addCriteria(
                Criteria.where("doctor.id").is(doctorId)
                        .and("patient.id").is(patientId)
                        .and("date").lt(LocalDateTime.now())
        );
        // Is there at least one document that satisfies?
        return mongoTemplate.exists(q, Appointment.class);
    }
}
