package it.unipi.healthhub.repository.mongo;

import it.unipi.healthhub.model.mongo.CalendarTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TemplateMongoRepository extends MongoRepository<CalendarTemplate, String> {
    boolean existsByNameIgnoreCaseAndIdIn(String name, List<String> ids);
    boolean existsByNameIgnoreCaseAndIdInAndIdNot(String name, List<String> ids, String excludeId);
}
