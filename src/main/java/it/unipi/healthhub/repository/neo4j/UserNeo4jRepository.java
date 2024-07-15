package it.unipi.healthhub.repository.neo4j;

import it.unipi.healthhub.dao.neo4j.User;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserNeo4jRepository extends Neo4jRepository<User, Long> {
}
