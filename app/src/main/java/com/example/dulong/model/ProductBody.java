package com.example.dulong.model;

public class ProductBody {
    private String name;
    private double price;
    private String image;
    private String type;

    public ProductBody(String name, double price, String image, String type) {
        this.name = name;
        this.price = price;
        this.image = image;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}