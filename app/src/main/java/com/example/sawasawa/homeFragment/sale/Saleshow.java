package com.example.sawasawa.homeFragment.sale;

public class Saleshow {

    private String name;
    private double price;

    public Saleshow(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }
}