package it.unipi.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class Doctor {
    private MongoCollection<Document> collection;

    public Doctor(DbConfig config) {
        MongoDatabase database = config.getDatabase();
        collection = database.getCollection("doctors");
    }

    public String createDoctor(Document doctorData) {
        collection.insertOne(doctorData);
        return doctorData.getObjectId("_id").toHexString();
    }

    public Document getDoctor(String doctorId) {
        return collection.find(new Document("_id", new ObjectId(doctorId))).first();
    }

    public long updateDoctor(String doctorId, Document updateData) {
        return collection.updateOne(new Document("_id", new ObjectId(doctorId)),
                new Document("$set", updateData)).getModifiedCount();
    }

    public long deleteDoctor(String doctorId) {
        return collection.deleteOne(new Document("_id", new ObjectId(doctorId))).getDeletedCount();
    }

    public List<Document> getDoctors() {
        return collection.find().into(new ArrayList<>());
    }
}
