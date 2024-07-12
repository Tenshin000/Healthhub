package it.unipi.healthhub.service;

import it.unipi.healthhub.model.User;
import it.unipi.healthhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUser(){
        return userRepository.findAll();
    }

    public Optional<User> getUserById(String id){
        return userRepository.findById(id);
    }

    public User createUser(User user){
        return userRepository.save(user);
    }

    public User updateUser(String id, User user){
        Optional<User> userOptional = userRepository.findById(id);
        if(userOptional.isPresent()){
            User userToUpdate = userOptional.get();
            // Update the user
            return userRepository.save(userToUpdate);
        }
        return null;
    }

    public void deleteUser(String id){
        userRepository.deleteById(id);
    }

    public User loginUser(String username, String password) {
        User user = userRepository.findByUsername(username);

        if (user != null && user.getPassword().equals(password)) {
            System.out.println("User found");
            return user;
        }

        return null;
    }

    public boolean hasEndorsed(String patientId, String doctorId) {
        Optional<User> patient = userRepository.findById(patientId);
        if (patient.isPresent()) {
            return patient.get().getEndorsedDoctors().contains(doctorId);
        }
        return false;
    }
}
