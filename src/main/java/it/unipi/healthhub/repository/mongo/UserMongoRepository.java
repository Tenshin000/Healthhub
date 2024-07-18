package it.unipi.healthhub.repository.mongo;

import it.unipi.healthhub.model.mongo.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserMongoRepository extends MongoRepository<User, String>{
    User findByUsername(String username);
}
