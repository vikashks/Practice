package com.twopirad.demo.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: vikash
 * Date: 3/2/15
 * Time: 10:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class Menu implements Serializable {

    private String name;
    private String code;
    private Map<String, List<Dish>> categories;

    public Menu() {
        categories = new HashMap<String, List<Dish>>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Map<String, List<Dish>> getCategories() {
        return categories;
    }

    public void addDish(String categoryName, Dish dish) {
        List<Dish> dishes = categories.get(categoryName);
        if (dishes == null) {
            dishes = new ArrayList<Dish>();
            categories.put(categoryName, dishes);
        }
        dishes.add(dish);
    }
}

