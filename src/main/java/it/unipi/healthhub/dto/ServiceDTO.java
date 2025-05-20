package it.unipi.healthhub.dto;

public class ServiceDTO {
    private String service;
    private Double price;
    private Integer index;

    // Empty constructor needed for JSON deserialization
    public ServiceDTO() {}

    // Constructor with parameters
    public ServiceDTO(String service, Double price, Integer index) {
        this.service = service;
        this.price = price;
        this.index = index;
    }

    // Getters and Setters
    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}

