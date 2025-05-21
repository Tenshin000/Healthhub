package it.unipi.healthhub.repository.neo4j;

import it.unipi.healthhub.model.neo4j.UserDAO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
public class UserNeo4jRepositoryTest {
    @Autowired
    private UserNeo4jRepository userNeo4jRepository;

    @Test
    void getUserByIdTest() {
        String userId = "682c9e6475b57f839e6d763e"; // Replace with a valid user ID
        Optional<UserDAO> user = userNeo4jRepository.findById(userId);
        if (user.isPresent()) {
            UserDAO userDAO = user.get();
            System.out.println("User found: " + userDAO.getName());
            System.out.println("Endorsed Doctors: " + userDAO.getEndorsedDoctors());
            System.out.println("Reviewed Doctors: " + userDAO.getReviewedDoctors());
        } else {
            System.out.println("User not found");
        }
    }
}
