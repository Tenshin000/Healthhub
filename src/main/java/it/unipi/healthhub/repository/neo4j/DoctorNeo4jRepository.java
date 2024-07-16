package it.unipi.healthhub.repository.neo4j;

import it.unipi.healthhub.model.neo4j.DoctorDAO;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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

}
