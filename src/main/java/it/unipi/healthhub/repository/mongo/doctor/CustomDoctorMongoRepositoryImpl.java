package it.unipi.healthhub.repository.mongo.doctor;

import com.mongodb.DBObject;
import it.unipi.healthhub.model.mongo.Doctor;
import it.unipi.healthhub.projection.DoctorMongoProjection;
import it.unipi.healthhub.util.DateUtil;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

public class CustomDoctorMongoRepositoryImpl implements CustomDoctorMongoRepository {
    private final MongoTemplate mongoTemplate;

    public CustomDoctorMongoRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public boolean checkScheduleSlot(String doctorId, Integer year, Integer week, String keyDay, String slotStart) {
        LocalDateTime startOfWeek = DateUtil.getFirstDayOfWeek(week, year).atStartOfDay();

        Aggregation aggregation = newAggregation(
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

    @Override
    public List<DoctorMongoProjection> searchDoctors(String text) {
        // 1) filtro OR: almeno un campo contiene la stringa (case‑insensitive)
        AggregationOperation match = match(new Criteria().orOperator(
                Criteria.where("name").regex(text, "i"),
                Criteria.where("specializations").regex(text, "i"),
                Criteria.where("address.city").regex(text, "i"),
                Criteria.where("address.province").regex(text, "i")
        ));

        // 2) per ciascun documento calcolo uno score = min( nome?0 :5, spec?1:5, city?2:5, prov?3:5 )
        Document nameCond = new Document("$cond", Arrays.asList(
                new Document("$regexMatch", new Document("input", "$name").append("regex", text).append("options", "i")),
                0,
                5
        ));
        Document specFilter = new Document("$filter", new Document("input", "$specializations")
                .append("as", "s")
                .append("cond", new Document("$regexMatch",
                        new Document("input", "$$s").append("regex", text).append("options", "i")))
        );
        Document specCond = new Document("$cond", Arrays.asList(
                new Document("$gt", Arrays.asList(new Document("$size", specFilter), 0)),
                1,
                5
        ));
        Document cityCond = new Document("$cond", Arrays.asList(
                new Document("$regexMatch", new Document("input", "$address.city").append("regex", text).append("options", "i")),
                2,
                5
        ));
        Document provCond = new Document("$cond", Arrays.asList(
                new Document("$regexMatch", new Document("input", "$address.province").append("regex", text).append("options", "i")),
                3,
                5
        ));
        Document scoreExpr = new Document("$min", Arrays.asList(nameCond, specCond, cityCond, provCond));

        AggregationOperation project = ctx -> new Document("$project",
                new Document("doctor", "$$ROOT")
                        .append("score", scoreExpr)
        );

        // 3) ordino per score crescente (0 = match su name, 1 = su specializzazioni, 2 = su city, 3 = su province)
        AggregationOperation sort = sort(Sort.Direction.ASC, "score");

        AggregationOperation limit = limit(250);

        Aggregation agg = newAggregation(match, project, sort, limit);

        AggregationResults<DoctorMongoProjection> results =
                mongoTemplate.aggregate(agg, "doctors", DoctorMongoProjection.class);

        return results.getMappedResults();
    }
}
