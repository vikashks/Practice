package com.twopirad.demo.controller;

import com.datastax.driver.core.*;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Using;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.twopirad.demo.model.Dish;
import com.twopirad.demo.model.Menu;
import com.twopirad.demo.service.CassandraClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: vikash
 * Date: 3/2/15
 * Time: 10:38 AM
 * To change this template use File | Settings | File Templates.
 */

@RestController(value = "/menu")
public class RestaurantController {

    @Autowired
    private CassandraClient cassandraClient;

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Menu applyMenu(@RequestBody String menuConfiguration) {
        Menu menu = populateMenu(menuConfiguration);
        saveMenu(menu);
        return menu;
    }

    private Menu populateMenu(String menuConfiguration) {
        Gson gson = new Gson();
        JsonObject menuJson = gson.fromJson(menuConfiguration, JsonObject.class);
        Menu menu = new Menu();
        if (menuJson.has("name")) {
            menu.setName(menuJson.get("name").getAsString());
        }
        if (menuJson.has("code")) {
            menu.setCode(menuJson.get("code").getAsString());
        }
        if (menuJson.has("categories") && menuJson.get("categories").isJsonArray()) {
            JsonArray categories = menuJson.get("categories").getAsJsonArray();
            for (JsonElement category : categories) {
                JsonObject categoryJsonObject = (JsonObject) category;
                for (Map.Entry<String, JsonElement> entry : categoryJsonObject.entrySet()) {
                    String categoryName = entry.getKey();
                    JsonArray dishes = (JsonArray) entry.getValue();
                    for (JsonElement dishJsonElement : dishes) {
                        JsonObject dishJsonObject = (JsonObject) dishJsonElement;
                        String dishName = dishJsonObject.getAsJsonPrimitive("name").getAsString();
                        String quantity = dishJsonObject.getAsJsonPrimitive("quantity").getAsString();
                        int price = dishJsonObject.getAsJsonPrimitive("price").getAsInt();
                        int profit = dishJsonObject.getAsJsonPrimitive("profit").getAsInt();
                        String description = dishJsonObject.getAsJsonPrimitive("description").getAsString();
                        Dish dish = new Dish();
                        dish.setCategory(categoryName);
                        dish.setDescription(description);
                        dish.setPrice(price);
                        dish.setProfit(profit);
                        dish.setQuantity(quantity);
                        dish.setName(dishName);
                        menu.addDish(categoryName, dish);
                    }
                }
            }
        }
        return menu;
    }

    public void saveMenu(Menu menu) {
        long timestamp = new Date().getTime();
        Cluster cluster = cassandraClient.getCluster();
        String menuSchemaCreationSql = new StringBuilder()
                .append(" CREATE TABLE IF NOT EXISTS demo.restaurant (")
                .append(" code text,")
                .append(" name text,")
                .append(" category text,")
                .append(" dish_name text,")
                .append(" dish_price int,")
                .append(" dish_profit int,")
                .append(" dish_quantity text,")
                .append(" dish_description text,")
                .append(" PRIMARY KEY (code, category, dish_name));")
                .toString();

        StringBuilder batchInsertSqlBuilder = new StringBuilder()
                .append("BEGIN BATCH USING TIMESTAMP ")
                .append(timestamp);
        Map<String, List<Dish>> categories = menu.getCategories();
        List<Object> params = new ArrayList<Object>();
        for (int i = 0; i < categories.size(); i++) {
            for (Map.Entry<String, List<Dish>> entry : categories.entrySet()) {
                String category = entry.getKey();
                List<Dish> dishes = entry.getValue();
                for (Dish dish : dishes) {
                    batchInsertSqlBuilder
                            .append(" INSERT INTO demo.restaurant (code, name, category, dish_name, dish_price, dish_profit, dish_quantity, dish_description) ")
                            .append("VALUES (?, ?, ?, ?, ?, ?, ?, ?);");
                    params.add(menu.getCode());
                    params.add(menu.getName());
                    params.add(category);
                    params.add(dish.getName());
                    params.add(dish.getPrice());
                    params.add(dish.getProfit());
                    params.add(dish.getQuantity());
                    params.add(dish.getDescription());
                }
            }
        }
        batchInsertSqlBuilder.append(" APPLY BATCH");
        Session session = null;
        try {
            session = cluster.connect();
            session.execute(menuSchemaCreationSql);
            PreparedStatement insertPreparedStatement = session.prepare(batchInsertSqlBuilder.toString());
            session.execute(insertPreparedStatement.bind(params.toArray()));
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public Menu addItems(@RequestBody String dishConfiguration) {
        Menu menu = populateMenu(dishConfiguration);
        saveMenu(menu);
        return menu;
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Menu getMenu() {
        Cluster cluster = cassandraClient.getCluster();
        Session session = cluster.connect();
        //Select select = QueryBuilder.select().all().from("demo", "restaurant");
        ResultSet resultSet = session.execute("select * from demo.restaurant");
        Menu menu = new Menu();
        String name = null;
        String code = null;
        for (Row row : resultSet) {
            code = row.getString("code");
            name = row.getString("name");
            String category = row.getString("category");
            String dishName = row.getString("dish_name");
            int dishPrice = row.getInt("dish_price");
            int dishProfit = row.getInt("dish_profit");
            String dishQuantity = row.getString("dish_quantity");
            String dishDescription = row.getString("dish_description");
            Dish dish = new Dish();
            dish.setName(dishName);
            dish.setCategory(category);
            dish.setPrice(dishPrice);
            dish.setProfit(dishProfit);
            dish.setDescription(dishDescription);
            dish.setQuantity(dishQuantity);
            menu.addDish(category, dish);
        }
        menu.setName(name);
        menu.setCode(code);
        return menu;
    }

}
