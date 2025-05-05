package it.unipi.healthhub.repository.neo4j;

import it.unipi.healthhub.model.neo4j.DoctorDAO;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.Result;
import org.neo4j.driver.Record;
import org.neo4j.driver.Value;
import org.neo4j.driver.Values;

import java.util.ArrayList;
import java.util.List;

public class CustomUserNeo4jRepositoryImpl implements CustomUserNeo4jRepository {
    // Field
    private final Driver driver;

    // Constructor
    public CustomUserNeo4jRepositoryImpl(Driver driver){
        this.driver = driver;
    }

    /**
     * Recommends doctors based on seen specializations of the user.
     * Retrieves doctors recommended by other users who have interacted with similar doctors,
     * considering only those whose specializations match those seen by the user.
     *
     * @param userId The ID of the user for whom doctors are recommended.
     * @param limit Maximum number of doctors to recommend.
     * @return List of recommended doctors with their details.
     */
    @Override
    public List<DoctorDAO> recommendDoctorsBySeenSpecializations(String userId, int limit) {
        try (Session session = driver.session()) {
            return session.readTransaction(tx -> {
                Result result = tx.run(
                        // Find doctors reviewed/endorsed by the given user
                        "MATCH (u:User {id: $userId})-[:ENDORSED|REVIEWED]->(d:Doctor) " +
                                // Find other users who also reviewed/endorsed the same doctors
                                "<-[:ENDORSED|REVIEWED]-(other:User)-[:ENDORSED|REVIEWED]->(rec:Doctor) " +
                                // Exclude doctors already reviewed/endorsed by the given user
                                "WHERE NOT (u)-[:ENDORSED|REVIEWED]->(rec) " +
                                // Carry forward u to avoid losing the user filter
                                "WITH rec, count(*) AS popularityScore, u " +
                                // Get the specializations of doctors the user has reviewed/endorsed
                                "MATCH (u)-[:ENDORSED|REVIEWED]->(d2:Doctor) " +
                                "UNWIND d2.specializations AS spec " +
                                "WITH rec, popularityScore, collect(DISTINCT spec) AS seenSpecs " +
                                // Recommend only those whose specs are all in the seen set
                                "WHERE rec.specializations IS NOT NULL AND ALL(recSpec IN rec.specializations WHERE recSpec IN seenSpecs) " +
                                // Return the recommended doctors ordered by popularity
                                "RETURN rec.id AS id, rec.name AS name, rec.specializations AS specializations " +
                                "ORDER BY popularityScore DESC " +
                                "LIMIT $limit",
                        Values.parameters("userId", userId, "limit", limit)
                );
                List<DoctorDAO> doctors = new ArrayList<>();
                while (result.hasNext()) {
                    Record record = result.next();
                    Value specValue = record.get("specializations");
                    List<String> specializations = specValue.isNull()
                            ? new ArrayList<>()
                            : specValue.asList(Value::asString);
                    doctors.add(new DoctorDAO(
                            record.get("id").asString(),
                            record.get("name").asString(),
                            specializations
                    ));
                }
                return doctors;
            });
        }
    }

    /**
     * Recommends doctors based on specializations not seen by the user.
     * Retrieves doctors recommended by other users who have interacted with similar doctors,
     * filtering out those whose specializations overlap with those already seen by the user.
     *
     * @param userId The ID of the user for whom doctors are recommended.
     * @param limit Maximum number of doctors to recommend.
     * @return List of recommended doctors with their details.
     */
    @Override
    public List<DoctorDAO> recommendDoctorsByNotSeenSpecializations(String userId, int limit) {
        try (Session session = driver.session()) {
            return session.readTransaction(tx -> {
                Result result = tx.run(
                        // Find doctors reviewed/endorsed by the given user
                        "MATCH (u:User {id: $userId})-[:ENDORSED|REVIEWED]->(d:Doctor) " +
                                // Find other users who also reviewed/endorsed the same doctors
                                "<-[:ENDORSED|REVIEWED]-(other:User)-[:ENDORSED|REVIEWED]->(rec:Doctor) " +
                                // Exclude doctors already reviewed/endorsed by the given user
                                "WHERE NOT (u)-[:ENDORSED|REVIEWED]->(rec) " +
                                // Carry forward u to avoid losing the user filter
                                "WITH rec, count(*) AS popularityScore, u " +
                                // Get the specializations of doctors the user has reviewed/endorsed
                                "MATCH (u)-[:ENDORSED|REVIEWED]->(d2:Doctor) " +
                                "UNWIND d2.specializations AS spec " +
                                "WITH rec, popularityScore, collect(DISTINCT spec) AS seenSpecs " +
                                // Recommend only those whose specs are all new to the user
                                "WHERE rec.specializations IS NOT NULL AND ALL(recSpec IN rec.specializations WHERE NOT recSpec IN seenSpecs) " +
                                // Return the recommended doctors ordered by popularity
                                "RETURN rec.id AS id, rec.name AS name, rec.specializations AS specializations " +
                                "ORDER BY popularityScore DESC " +
                                "LIMIT $limit",
                        Values.parameters("userId", userId, "limit", limit)
                );
                List<DoctorDAO> doctors = new ArrayList<>();
                while (result.hasNext()) {
                    Record record = result.next();
                    Value specValue = record.get("specializations");
                    List<String> specializations = specValue.isNull()
                            ? new ArrayList<>()
                            : specValue.asList(Value::asString);
                    doctors.add(new DoctorDAO(
                            record.get("id").asString(),
                            record.get("name").asString(),
                            specializations
                    ));
                }
                return doctors;
            });
        }
    }

    /**
     * Recommends popular doctors based on overall endorsements or reviews.
     * Retrieves doctors ordered by their popularity score, which reflects the total number
     * of endorsements or reviews received from all users.
     *
     * @param limit Maximum number of popular doctors to recommend.
     * @return List of popular doctors with their details.
     */
    @Override
    public List<DoctorDAO> recommendPopularDoctors(int limit) {
        try (Session session = driver.session()) {
            return session.readTransaction(tx -> {
                Result result = tx.run(
                        // Match all doctors with non-null specializations
                        "MATCH (d:Doctor) WHERE d.specializations IS NOT NULL " +
                                // Optionally match endorsements or reviews to compute popularity
                                "OPTIONAL MATCH (u:User)-[r:ENDORSED|REVIEWED]->(d) " +
                                "WITH d, count(r) AS popularityScore " +
                                // Return popular doctors ordered by popularity
                                "RETURN d.id AS id, d.name AS name, d.specializations AS specializations " +
                                "ORDER BY popularityScore DESC " +
                                "LIMIT $limit",
                        Values.parameters("limit", limit)
                );
                List<DoctorDAO> doctors = new ArrayList<>();
                while (result.hasNext()) {
                    Record record = result.next();
                    Value specValue = record.get("specializations");
                    List<String> specializations = specValue.isNull()
                            ? new ArrayList<>()
                            : specValue.asList(Value::asString);
                    doctors.add(new DoctorDAO(
                            record.get("id").asString(),
                            record.get("name").asString(),
                            specializations
                    ));
                }
                return doctors;
            });
        }
    }
}
