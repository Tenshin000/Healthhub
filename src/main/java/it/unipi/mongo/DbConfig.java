package it.unipi.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class DbConfig {
    private MongoClient mongoClient;
    private MongoDatabase database;

    public DbConfig(String uri, String dbName) {
        mongoClient = MongoClients.create(uri);
        database = mongoClient.getDatabase(dbName);
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public void close() {
        mongoClient.close();
    }
}
