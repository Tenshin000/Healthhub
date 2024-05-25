package it.unipi.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class Appointment {
    private MongoCollection<Document> collection;

    public Appointment(DbConfig config) {
        MongoDatabase database = config.getDatabase();
        collection = database.getCollection("appointments");
    }

    public String createAppointment(Document appointmentData) {
        collection.insertOne(appointmentData);
        return appointmentData.getObjectId("_id").toHexString();
    }

    public Document getAppointment(String appointmentId) {
        return collection.find(new Document("_id", new ObjectId(appointmentId))).first();
    }

    public long updateAppointment(String appointmentId, Document updateData) {
        return collection.updateOne(new Document("_id", new ObjectId(appointmentId)),
                new Document("$set", updateData)).getModifiedCount();
    }

    public long deleteAppointment(String appointmentId) {
        return collection.deleteOne(new Document("_id", new ObjectId(appointmentId))).getDeletedCount();
    }

    public List<Document> getAppointments() {
        return collection.find().into(new ArrayList<>());
    }
}
