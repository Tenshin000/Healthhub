package it.unipi.healthhub.repository.neo4j;

import it.unipi.healthhub.model.neo4j.DoctorDAO;
import it.unipi.healthhub.projection.DoctorNeo4jProjection;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Repository
public interface DoctorNeo4jRepository extends Neo4jRepository<DoctorDAO, String>{
    @Query("MATCH (d:Doctor {id: $id}) " +
            "SET d.specializations = d.specializations + $specialization " +
            "RETURN d")
    DoctorDAO addSpecialization(@Param("id") String id, @Param("specialization") String specialization);

    @Query("MATCH (d:Doctor {id: $id}) " +
            "SET d.specializations = [s IN d.specializations WHERE s <> $specialization] " +
            "RETURN d")
    DoctorDAO removeSpecialization(@Param("id") String id, @Param("specialization") String specialization);

    @Query("MATCH (d:Doctor {id: $id}) " +
            "DELETE d")
    void deleteDoctor(@Param("id") String id);

    @Query("MATCH (d:Doctor {id: $id}) " +
            "SET d.name = $name " +
            "RETURN d")
    DoctorDAO updateName(@Param("id") String id, @Param("name") String name);

    @Query("MATCH path = (u:User {id:$uid})-[:REVIEWED|ENDORSED*1..3]-(d:Doctor) " +
            "WHERE toLower(d.name) CONTAINS toLower($search) " +
            " OR any(spec IN d.specializations WHERE toLower(spec) CONTAINS toLower($search)) " +
            "WITH d, min(length(path)) AS steps " +
            "RETURN d as doctor, steps as score " +
            "ORDER BY steps " +
            "LIMIT 500")
    List<DoctorNeo4jProjection> findConnectedDoctorsBySteps(@Param("uid") String patientId, @Param("search") String search);
}
