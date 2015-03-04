package com.twopirad.demo.model;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: vikash
 * Date: 3/3/15
 * Time: 3:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class SaleDetail {

    private String restaurantCode;

    private Map<String, List<Dish2>> items;

    private String customerId;

    private String saleCode;

    public SaleDetail() {
        items = new HashMap<String, List<Dish2>>();
    }

    public void addDish(String categoryName, Dish2 dish) {
        List<Dish2> dishes = items.get(categoryName);
        if (dishes == null) {
            dishes = new ArrayList<Dish2>();
            items.put(categoryName, dishes);
        }
        dishes.add(dish);
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Map<String, List<Dish2>> getItems() {
        return items;
    }

    public void setItems(Map<String, List<Dish2>> items) {
        this.items = items;
    }

    public String getRestaurantCode() {
        return restaurantCode;
    }

    public void setRestaurantCode(String restaurantCode) {
        this.restaurantCode = restaurantCode;
    }

    public String getSaleCode() {
        return saleCode;
    }

    public void setSaleCode(String saleCode) {
        this.saleCode = saleCode;
    }
}
