package com.example.sawasawa.AddProduct;

public class Product {
    private String name;
    private double price;

    public Product(String name, double price, String qr_code) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean getId() {
        return false;
    }
}