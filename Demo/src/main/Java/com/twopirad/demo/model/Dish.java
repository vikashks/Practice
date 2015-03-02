package com.twopirad.demo.model;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: vikash
 * Date: 3/2/15
 * Time: 10:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class Dish implements Serializable{

    private String category;

    private int price;

    private int profit;

    private String description;
    private String quantity;
    private String name;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getProfit() {
        return profit;
    }

    public void setProfit(int profit) {
        this.profit = profit;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
