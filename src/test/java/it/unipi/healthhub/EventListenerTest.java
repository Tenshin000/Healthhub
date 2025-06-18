package it.unipi.healthhub;

import it.unipi.healthhub.event.UserNameUpdateEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;

@SpringBootTest

public class EventListenerTest {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Test
    public void testUserNameUpdateEvent() {
        String userId = "12345";
        String newName = "John Doe";

        // Create the event
        UserNameUpdateEvent event = new UserNameUpdateEvent(this, userId, newName);

        // Publish the event
        applicationEventPublisher.publishEvent(event);

        System.out.println("Event published: " + event);

        // Here you would typically verify that the event was handled correctly
        // For example, you could check if a specific method was called or if a certain state was changed
    }
}
