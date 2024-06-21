package it.unipi.healthhub.repository;

import it.unipi.healthhub.model.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRepository extends MongoRepository<User, String>{
    User findByUsername(String username);
    User findById(ObjectId id);
    List<User> findByName(String name);
    List<User> findByAge(int age);
}
