package it.unipi.healthhub;

import it.unipi.healthhub.events.UserNameUpdateEvent;
import it.unipi.healthhub.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class EventListener implements ApplicationListener<UserNameUpdateEvent> {

    @Autowired
    private UserService userService;

    @Override
    @Async
    public void onApplicationEvent(UserNameUpdateEvent event) {
        String userId = event.getUserId();
        String newName = event.getNewName();
        userService.updateNameEverywhere(userId, newName);
    }

}
