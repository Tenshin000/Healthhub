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

public class CustomUserNeo4jRepositoryImpl implements CustomUserNeo4jRepository{
    // Field
    private final Driver driver;

    // Constructor
    public CustomUserNeo4jRepositoryImpl(Driver driver){
        this.driver = driver;
    }

    // Methods
    /**
     * Recommends doctors based on seen specializations of the user.
     * Retrieves doctors recommended by other users who have interacted with similar doctors,
     * considering only those whose specializations match those seen by the user.
     *
     * @param userId The ID of the user for whom doctors are recommended.
     * @param limit Maximum number of doctors to recommend.
     * @return List of recommended doctors with their details.
     */
    public List<DoctorDAO> recommendDoctorsBySeenSpecializations(String userId, int limit) {
        try(Session session = driver.session()){
            return session.readTransaction(tx -> {
                Result result = tx.run(
                        // Match the user and the doctors they have endorsed or reviewed
                        "MATCH (u:User {id: $userId})-[:ENDORSED|REVIEWED]->(d:Doctor) " +
                                // Find other users who have endorsed/reviewed the same doctors
                                "<-[:ENDORSED|REVIEWED]-(other:User)-[:ENDORSED|REVIEWED]->(rec:Doctor) " +
                                // Exclude doctors that the original user has already interacted with
                                "WHERE NOT (u)-[:ENDORSED|REVIEWED]->(rec) " +
                                // Count how many times each doctor is recommended (popularity score)
                                "WITH rec, count(*) AS popularityScore " +
                                // Match again the doctors the user has already interacted with
                                "MATCH (u)-[:ENDORSED|REVIEWED]->(d2:Doctor) " +
                                // Extract specializations from those doctors
                                "UNWIND d2.specializations AS spec " +
                                // Collect all seen specializations into a list (distinct to avoid duplicates)
                                "WITH rec, popularityScore, collect(DISTINCT spec) AS seenSpecs " +
                                // Keep only recommended doctors whose all specializations were already seen by the user
                                "WHERE ALL(recSpec IN rec.specializations WHERE recSpec IN seenSpecs) " +
                                // Return doctor data
                                "RETURN rec.id AS id, rec.name AS name, rec.specializations AS specializations " +
                                // Order by popularity score descending
                                "ORDER BY popularityScore DESC " +
                                // Limit the number of returned doctors
                                "LIMIT $limit",
                        Values.parameters("userId", userId, "limit", limit)
                );
                List<DoctorDAO> doctors = new ArrayList<>();
                while (result.hasNext()) {
                    org.neo4j.driver.Record record = result.next();
                    doctors.add(new DoctorDAO(
                            record.get("id").asString(),
                            record.get("name").asString(),
                            record.get("specializations").asList(Value::asString)
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
    public List<DoctorDAO> recommendDoctorsByNotSeenSpecializations(String userId, int limit){
        try(Session session = driver.session()){
            return session.readTransaction(tx -> {
                Result result = tx.run(
                        // Match the user and the doctors they have endorsed or reviewed
                        "MATCH (u:User {id: $userId})-[:ENDORSED|REVIEWED]->(d:Doctor) " +
                                // Find other users who have endorsed/reviewed the same doctors
                                "<-[:ENDORSED|REVIEWED]-(other:User)-[:ENDORSED|REVIEWED]->(rec:Doctor) " +
                                // Exclude doctors that the original user has already interacted with
                                "WHERE NOT (u)-[:ENDORSED|REVIEWED]->(rec) " +
                                // Count how many times each doctor is recommended (popularity score)
                                "WITH rec, count(*) AS popularityScore " +
                                // Match again the doctors the user has already interacted with
                                "MATCH (u)-[:ENDORSED|REVIEWED]->(d2:Doctor) " +
                                // Extract specializations from those doctors
                                "UNWIND d2.specializations AS spec " +
                                // Collect all seen specializations into a list (distinct to avoid duplicates)
                                "WITH rec, popularityScore, collect(DISTINCT spec) AS seenSpecs " +
                                // Keep only recommended doctors whose all specializations are NOT among the user's seen ones
                                "WHERE ALL(recSpec IN rec.specializations WHERE NOT recSpec IN seenSpecs) " +
                                // Return doctor data
                                "RETURN rec.id AS id, rec.name AS name, rec.specializations AS specializations " +
                                // Order by popularity score descending
                                "ORDER BY popularityScore DESC " +
                                // Limit the number of returned doctors
                                "LIMIT $limit",
                        Values.parameters("userId", userId, "limit", limit)
                );
                List<DoctorDAO> doctors = new ArrayList<>();
                while (result.hasNext()) {
                    org.neo4j.driver.Record record = result.next();
                    doctors.add(new DoctorDAO(
                            record.get("id").asString(),
                            record.get("name").asString(),
                            record.get("specializations").asList(Value::asString)
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
    public List<DoctorDAO> recommendPopularDoctors(int limit){
        try(Session session = driver.session()){
            return session.readTransaction(tx -> {
                Result result = tx.run(
                        // Match all doctors
                        "MATCH (d:Doctor) " +
                                // Optionally match relationships from users to each doctor via endorsement or review
                                "OPTIONAL MATCH (u:User)-[r:ENDORSED|REVIEWED]->(d) " +
                                // Count how many times each doctor has been endorsed or reviewed (popularity score)
                                "WITH d, count(r) AS popularityScore " +
                                // Return doctor data
                                "RETURN d.id AS id, d.name AS name, d.specializations AS specializations " +
                                // Order by popularity score descending
                                "ORDER BY popularityScore DESC " +
                                // Limit the number of returned doctors
                                "LIMIT $limit",
                        Values.parameters("limit", limit)
                );
                List<DoctorDAO> doctors = new ArrayList<>();
                while (result.hasNext()) {
                    Record record = result.next();
                    doctors.add(new DoctorDAO(
                            record.get("id").asString(),
                            record.get("name").asString(),
                            record.get("specializations").asList(Value::asString)
                    ));
                }
                return doctors;
            });
        }
    }
}
