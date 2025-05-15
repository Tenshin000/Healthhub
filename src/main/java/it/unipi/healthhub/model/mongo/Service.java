package it.unipi.healthhub.model.mongo;

public class Service {
    private String service;
    private double price;

    // Constructors
    public Service() {}
    public Service(String service, double price) {
        this.service = service;
        this.price = price;
    }

    // Getters and Setters
    public String getService() {
        return service;
    }
    public void setService(String service) {
        this.service = service;
    }

    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
}
