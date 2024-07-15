package it.unipi.healthhub.repository.mongo;

import it.unipi.healthhub.dao.mongo.CalendarTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TemplateMongoRepository extends MongoRepository<CalendarTemplate, String> {
}
