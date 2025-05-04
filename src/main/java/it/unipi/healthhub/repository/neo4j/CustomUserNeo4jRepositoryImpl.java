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
    private final Driver driver;

    public CustomUserNeo4jRepositoryImpl(Driver driver){
        this.driver = driver;
    }

    public List<DoctorDAO> recommendDoctorsBySeenSpecializations(String userId, int limit) {
        try (Session session = driver.session()) {
            return session.readTransaction(tx -> {
                Result result = tx.run(
                        "MATCH (u:User {id: $userId})-[:ENDORSED|REVIEWED]->(d:Doctor) " +
                                "<-[:ENDORSED|REVIEWED]-(other:User)-[:ENDORSED|REVIEWED]->(rec:Doctor) " +
                                "WHERE NOT (u)-[:ENDORSED|REVIEWED]->(rec) " +
                                "WITH rec, count(*) AS popularityScore " +
                                "MATCH (u)-[:ENDORSED|REVIEWED]->(d2:Doctor) " +
                                "UNWIND d2.specializations AS spec " +
                                "WITH rec, popularityScore, collect(DISTINCT spec) AS seenSpecs " +
                                "WHERE ALL(recSpec IN rec.specializations WHERE recSpec IN seenSpecs) " +
                                "RETURN rec.id AS id, rec.name AS name, rec.specializations AS specializations " +
                                "ORDER BY popularityScore DESC " +
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

    public List<DoctorDAO> recommendDoctorsByNotSeenSpecializations(String userId, int limit) {
        try (Session session = driver.session()) {
            return session.readTransaction(tx -> {
                Result result = tx.run(
                        "MATCH (u:User {id: $userId})-[:ENDORSED|REVIEWED]->(d:Doctor) " +
                                "<-[:ENDORSED|REVIEWED]-(other:User)-[:ENDORSED|REVIEWED]->(rec:Doctor) " +
                                "WHERE NOT (u)-[:ENDORSED|REVIEWED]->(rec) " +
                                "WITH rec, count(*) AS popularityScore " +
                                "MATCH (u)-[:ENDORSED|REVIEWED]->(d2:Doctor) " +
                                "UNWIND d2.specializations AS spec " +
                                "WITH rec, popularityScore, collect(DISTINCT spec) AS seenSpecs " +
                                "WHERE ALL(recSpec IN rec.specializations WHERE NOT recSpec IN seenSpecs) " +
                                "RETURN rec.id AS id, rec.name AS name, rec.specializations AS specializations " +
                                "ORDER BY popularityScore DESC " +
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

    public List<DoctorDAO> recommendPopularDoctors(int limit) {
        try (Session session = driver.session()) {
            return session.readTransaction(tx -> {
                Result result = tx.run(
                        "MATCH (d:Doctor) " +
                                "OPTIONAL MATCH (u:User)-[r:ENDORSED|REVIEWED]->(d) " +
                                "WITH d, count(r) AS popularityScore " +
                                "RETURN d.id AS id, d.name AS name, d.specializations AS specializations " +
                                "ORDER BY popularityScore DESC " +
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
