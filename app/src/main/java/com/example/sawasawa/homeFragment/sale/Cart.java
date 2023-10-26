package com.example.sawasawa.homeFragment.sale;

import java.util.ArrayList;
import java.util.List;

public class Cart {

    private List<Saleshow> sales;

    public Cart() {
        sales = new ArrayList<>();
    }

    public void addSale(Saleshow sale) {
        sales.add(sale);
    }

    public List<Saleshow> getSales() {
        return sales;
    }

    public double getTotalPrice() {
        double total = 0;
        for (Saleshow sale : sales) {
            total += sale.getPrice();
        }
        return total;
    }
}