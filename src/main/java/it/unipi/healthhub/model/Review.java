package it.unipi.healthhub.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

public class Review {
    @Id
    private String id;

    @DBRef
    public User name;
    public String review;
    public String time;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public User getName() {
        return name;
    }
    public void setName(User name) {
        this.name = name;
    }

    public String getReview() {
        return review;
    }
    public void setReview(String review) {
        this.review = review;
    }

    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }


}
