package com.example.sawasawa.product;

public class ProductShow {
    private String name;
    private String article;
    private double price;

    public ProductShow(String name, String article, double price) {
        this.name = name;
        this.article = article;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public String getArticle() {
        return article;
    }

    public double getPrice() {
        return price;
    }
}