package it.unipi.healthhub.service;

import it.unipi.healthhub.model.mongo.User;
import it.unipi.healthhub.model.neo4j.UserDAO;
import it.unipi.healthhub.repository.mongo.UserMongoRepository;
import it.unipi.healthhub.repository.neo4j.UserNeo4jRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserMongoRepository userMongoRepository;
    @Autowired
    private UserNeo4jRepository userNeo4jRepository;

    public List<User> getAllUser(){
        return userMongoRepository.findAll();
    }

    public Optional<User> getUserById(String id){
        return userMongoRepository.findById(id);
    }

    @Transactional
    public User createUser(User user){
        User savedUser = userMongoRepository.save(user);
        UserDAO userDAO = new UserDAO(savedUser.getId(), savedUser.getName());
        userNeo4jRepository.save(userDAO);
        return savedUser;
    }

    public User updateUser(String id, User user){
        Optional<User> userOptional = userMongoRepository.findById(id);
        if(userOptional.isPresent()){
            User userToUpdate = userOptional.get();
            // Update the user
            return userMongoRepository.save(userToUpdate);
        }
        return null;
    }

    public void deleteUser(String id){
        userMongoRepository.deleteById(id);
    }

    public User loginUser(String username, String password) {
        User user = userMongoRepository.findByUsername(username);

        if (user != null && user.getPassword().equals(password)) {
            System.out.println("User found");
            return user;
        }

        return null;
    }

    public boolean hasEndorsed(String patientId, String doctorId) {
        UserDAO userDAO = userNeo4jRepository.findById(patientId).orElseThrow(() -> new RuntimeException("User not found"));
        return userDAO.getEndorsedDoctors().stream().anyMatch(doctor -> doctor.getId().equals(doctorId));
    }
}
