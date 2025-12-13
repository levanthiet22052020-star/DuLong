package com.example.dulong.model;

import java.io.Serializable;

public class Product implements Serializable {
    private String _id;
    private String name;
    private double price;
    private String image;
    private String weight;
    private String balance;
    private String flex;
    private String description;
    private String type;
    private String color;
    private int quantity;

    public Product() {
        this.quantity = 1;
    }

    public Product(String _id, String name, double price, String image, String weight, 
                   String balance, String flex, String description, String type, String color, int quantity) {
        this._id = _id;
        this.name = name;
        this.price = price;
        this.image = image;
        this.weight = weight;
        this.balance = balance;
        this.flex = flex;
        this.description = description;
        this.type = type;
        this.color = color;
        this.quantity = quantity;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
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

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getFlex() {
        return flex;
    }

    public void setFlex(String flex) {
        this.flex = flex;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}