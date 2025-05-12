package it.unipi.healthhub.repository.neo4j;

import it.unipi.healthhub.repository.neo4j.user.CustomUserNeo4jRepository;
import it.unipi.healthhub.model.neo4j.DoctorDAO;
import it.unipi.healthhub.model.neo4j.UserDAO;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Repository
public interface UserNeo4jRepository extends Neo4jRepository<UserDAO, String>, CustomUserNeo4jRepository{
    @Query("MATCH (u:User) WHERE u.id = $userId "+
            "MATCH (d:Doctor) WHERE d.id = $doctorId " +
            "MERGE (u)-[:ENDORSED]->(d) ")
    void endorse(@Param("userId") String userId, @Param("doctorId") String doctorId);

    @Query("MATCH (u:User) WHERE u.id = $userId "+
            "MATCH (d:Doctor) WHERE d.id = $doctorId " +
            "MERGE (u)-[:REVIEWED]->(d) ")
    void review(@Param("userId") String userId, @Param("doctorId") String doctorId);

    @Query("MATCH (u:User {id: $userId})-[r:ENDORSED]->(d:Doctor {id: $doctorId}) " +
            "DELETE r")
    void unendorse(@Param("userId") String userId, @Param("doctorId") String doctorId);

    @Query("MATCH (u:User {id: $userId})-[r:REVIEWED]->(d:Doctor {id: $doctorId}) " +
            "DELETE r")
    void unreview(@Param("userId") String userId, @Param("doctorId") String doctorId);

    @Query("MATCH (u:User {id: $userId}) " +
            "SET u.name = $name " +
            "RETURN u")
    UserDAO updateName(@Param("userId") String patientId, @Param("name") String fullName);
}
