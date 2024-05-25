package it.unipi.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DbConfig {
    private MongoClient mongoClient;
    private MongoDatabase database;

    public DbConfig() {
        Properties properties = loadProperties();
        String uri = properties.getProperty("spring.data.mongodb.uri");
        String dbName = properties.getProperty("spring.data.mongodb.database");

        mongoClient = MongoClients.create(uri);
        database = mongoClient.getDatabase(dbName);
    }

    private Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return properties;
            }
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return properties;
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public void close() {
        mongoClient.close();
    }
}
