package it.unipi.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class User {
    private MongoCollection<Document> collection;

    public User(DbConfig config) {
        MongoDatabase database = config.getDatabase();
        collection = database.getCollection("users");
    }

    public String createUser(Document userData) {
        collection.insertOne(userData);
        return userData.getObjectId("_id").toHexString();
    }

    public Document getUser(String userId) {
        return collection.find(new Document("_id", new ObjectId(userId))).first();
    }

    public long updateUser(String userId, Document updateData) {
        return collection.updateOne(new Document("_id", new ObjectId(userId)),
                new Document("$set", updateData)).getModifiedCount();
    }

    public long deleteUser(String userId) {
        return collection.deleteOne(new Document("_id", new ObjectId(userId))).getDeletedCount();
    }

    public List<Document> getUsers() {
        return collection.find().into(new ArrayList<>());
    }
}
