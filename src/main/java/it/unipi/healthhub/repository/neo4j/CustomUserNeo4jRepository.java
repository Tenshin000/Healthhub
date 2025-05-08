package it.unipi.healthhub.repository.neo4j;

import it.unipi.healthhub.model.neo4j.DoctorDAO;
import java.util.List;

/**
 * Custom fragment interface for UserNeo4jRepository.
 * Contains complex recommendation queries that cannot be mapped automatically by SDN.
 */
public interface CustomUserNeo4jRepository {
    List<DoctorDAO> recommendDoctorsForUser(String userId, int limit);
    List<DoctorDAO> recommendPopularDoctors(int limit);
}