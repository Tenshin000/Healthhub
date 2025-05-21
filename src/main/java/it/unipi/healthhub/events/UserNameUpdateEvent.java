package it.unipi.healthhub.events;

import org.springframework.context.ApplicationEvent;

public class UserNameUpdateEvent extends ApplicationEvent {
    private final String userId;
    private final String newName;

    public UserNameUpdateEvent(Object source, String userId, String newName) {
        super(source);
        this.userId = userId;
        this.newName = newName;
    }

    public String getUserId() {
        return userId;
    }

    public String getNewName() {
        return newName;
    }
}
