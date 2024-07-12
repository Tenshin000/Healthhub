package it.unipi.healthhub.repository;

import it.unipi.healthhub.model.CalendarTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TemplateRepository extends MongoRepository<CalendarTemplate, String> {
}
