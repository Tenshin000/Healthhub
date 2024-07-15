package it.unipi.healthhub.dao.mongo;

import java.time.LocalDate;

public class Review {
    public String name;
    public String text;
    public LocalDate date;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }


}
