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
     * Recommends doctors to a specific user based on collaborative filtering:
     * 1) Finds “similar” users who have at least 3 doctors in common via endorsement/review.
     * 2) Gets their favorite doctors, excluding those already endorsed/reviewed by the target user.
     * 3) Sorts the doctors by aggregated score and returns the top-N results.
     *
     * Complexity: O(E_shared + E_rec), where E_shared is the number of shared relationships
     * and E_rec is the number of endorsement/review relationships of the similar users.
     * The query is parameterized and uses indexes.
     *
     * @param limit Maximum number of doctors to recommend.
     * @return List of recommended doctors with their details.
     */
    @Override
    public List<DoctorDAO> recommendDoctorsForUser(String userId, int limit) {
        try (Session session = driver.session()) {
            return session.readTransaction(tx -> {
                String cypher =
                        "MATCH (me:User {id:$uid})-[:ENDORSED|REVIEWED]->(d:Doctor)<-[:ENDORSED|REVIEWED]-(other:User) " +
                                "WITH me, other, count(d) AS sharedCount " +
                                "WHERE sharedCount >= 3 AND me <> other " +
                                "LIMIT 200 " +
                                // Get other doctors from 'other' that 'me' has not yet endorsed/reviewed
                                "MATCH (other)-[r:ENDORSED|REVIEWED]->(rec:Doctor) " +
                                "WHERE NOT (me)-[:ENDORSED|REVIEWED]->(rec) " +
                                // Aggregate a score: more relationships and more similar users increase the score
                                "WITH rec, sum(sharedCount) AS score, count(r) AS endorsements " +
                                "ORDER BY score DESC, endorsements DESC " +
                                "RETURN rec.id   AS id, " +
                                "rec.name AS name, " +
                                "rec.specializations AS specializations " +
                                "LIMIT $limit";

                Result result = tx.run(cypher,
                        Values.parameters("uid", userId, "limit", limit));
                List<DoctorDAO> out = new ArrayList<>(limit);
                while (result.hasNext()) {
                    Record rec = result.next();
                    out.add(new DoctorDAO(
                            rec.get("id").asString(),
                            rec.get("name").asString(),
                            rec.get("specializations").asList(Value::asString)
                    ));
                }
                return out;
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
