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

    /**
     * Check if a specific schedule slot is taken for a given doctor/week/day/time.
     */
    @Override
    public boolean checkScheduleSlot(String doctorId, Integer year, Integer week, String keyDay, String slotStart) {
        // Compute the LocalDateTime representing the first day of the given week/year
        LocalDateTime startOfWeek = DateUtil.getFirstDayOfWeek(week, year).atStartOfDay();

        // Build aggregation pipeline:
        // 1) Match the doctor by _id
        // 2) Unwind the schedules array
        // 3) Match the schedule for the requested week
        // 4) Project only the slots for the given keyDay
        // 5) Unwind the slots array
        // 6) Match the slot with the requested start time
        // 7) Project only the 'taken' field of that slot
        Aggregation aggregation = newAggregation(
                match(Criteria.where("_id").is(doctorId)),
                unwind("schedules"),
                match(Criteria.where("schedules.week").is(startOfWeek)),
                project().and("schedules.slots."+keyDay).as("slots"),
                unwind("slots"),
                match(Criteria.where("slots.start").is(slotStart)),
                project("slots.taken")
        );

        // Execute aggregation on the Doctor collection
        AggregationResults<DBObject> result = mongoTemplate.aggregate(aggregation, Doctor.class, DBObject.class);

        // If no matching slot found, print error and return false
        if (result.getMappedResults().isEmpty()){
            System.out.println("Error: No slot found");
            return false;
        }
        // Return whether the slot is taken
        return result.getMappedResults().get(0).get("taken").equals(true);
    }

    /**
     * Mark a specific schedule slot as booked (taken = true).
     */
    @Override
    public void bookScheduleSlot(String doctorId, Integer year, Integer week, String keyDay, String slotStart) {
        LocalDateTime startOfWeek = DateUtil.getFirstDayOfWeek(week, year).atStartOfDay();

        // Build a query matching the doctor, week, and specific slot start time
        Query query = Query.query(
                Criteria.where("_id").is(doctorId)
                        .and("schedules.week").is(startOfWeek)
                        .and("schedules.slots." + keyDay + ".start").is(slotStart)
        );
        // Build update to set the slot's 'taken' flag to true, using arrayFilters to target correct slot
        Update update = new Update().set("schedules.$.slots." + keyDay + ".$[slot].taken", true)
                .filterArray(Criteria.where("slot.start").is(slotStart));

        // Execute updateMulti to apply on all matching documents (should be one)
        mongoTemplate.updateMulti(query, update, Doctor.class);
    }

    /**
     * Free up a specific schedule slot (taken = false).
     */
    @Override
    public void freeScheduleSlot(String doctorId, Integer year, Integer week, String keyDay, String slotStart) {
        LocalDateTime startOfWeek = DateUtil.getFirstDayOfWeek(week, year).atStartOfDay();

        // Query matching same criteria as booking
        Query query = new Query(
                Criteria.where("_id").is(doctorId)
                        .and("schedules.week").is(startOfWeek)
                        .and("schedules.slots." + keyDay + ".start").is(slotStart)
        );
        // Update to set 'taken' to false
        Update update = new Update().set("schedules.$.slots." + keyDay + ".$[slot].taken", false)
                .filterArray(Criteria.where("slot.start").is(slotStart));

        mongoTemplate.updateMulti(query, update, Doctor.class);
    }

    /**
     * Full‑text–style search across multiple fields, with a custom ranking score.
     */
    @Override
    public List<DoctorMongoProjection> searchDoctors(String text) {
        // 1) Filter: at least one of name, specializations, city or province matches the text (case‑insensitive)
        AggregationOperation match = match(new Criteria().orOperator(
                Criteria.where("name").regex(text, "i"),
                Criteria.where("specializations").regex(text, "i"),
                Criteria.where("address.city").regex(text, "i"),
                Criteria.where("address.province").regex(text, "i")
        ));

        // 2) Build conditional expressions for scoring:
        //    - nameCond: 0 if name matches, else 5
        Document nameCond = new Document("$cond", Arrays.asList(
                new Document("$regexMatch", new Document("input", "$name").append("regex", text).append("options", "i")),
                0,
                5
        ));
        //    - specCond: 1 if any specialization matches, else 5
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
        //    - cityCond: 2 if city matches, else 5
        Document cityCond = new Document("$cond", Arrays.asList(
                new Document("$regexMatch", new Document("input", "$address.city").append("regex", text).append("options", "i")),
                2,
                5
        ));
        //    - provCond: 3 if province matches, else 5
        Document provCond = new Document("$cond", Arrays.asList(
                new Document("$regexMatch", new Document("input", "$address.province").append("regex", text).append("options", "i")),
                3,
                5
        ));
        // Combine into a score: minimum of the four conditions
        Document scoreExpr = new Document("$min", Arrays.asList(nameCond, specCond, cityCond, provCond));

        // Project the entire doctor document plus the computed score
        AggregationOperation project = ctx -> new Document("$project",
                new Document("doctor", "$$ROOT")
                        .append("score", scoreExpr)
        );

        // 3) Sort by score ascending (best matches first)
        AggregationOperation sort = sort(Sort.Direction.ASC, "score");

        // Limit to top 250 results
        AggregationOperation limit = limit(250);

        // Assemble the pipeline
        Aggregation agg = newAggregation(match, project, sort, limit);

        // Execute aggregation on the "doctors" collection, mapping into DoctorMongoProjection
        AggregationResults<DoctorMongoProjection> results =
                mongoTemplate.aggregate(agg, "doctors", DoctorMongoProjection.class);

        return results.getMappedResults();
    }
}
