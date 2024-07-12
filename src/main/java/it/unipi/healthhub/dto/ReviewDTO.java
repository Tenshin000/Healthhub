package it.unipi.healthhub.dto;

import java.time.LocalDate;

public class ReviewDTO {
    private String name;
    private String text;
    private LocalDate date;

    // Costruttore vuoto richiesto da alcuni framework di serializzazione
    public ReviewDTO() {
    }

    // Costruttore per inizializzare dalla classe Review
    public ReviewDTO(String name, String text, LocalDate date) {
        this.name = name;
        this.text = text;
        this.date = date;
    }

    // Metodi getter e setter
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
