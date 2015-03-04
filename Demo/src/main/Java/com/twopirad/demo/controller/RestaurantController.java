package com.twopirad.demo.controller;

import com.datastax.driver.core.*;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.twopirad.demo.model.Dish;
import com.twopirad.demo.model.Dish2;
import com.twopirad.demo.model.Menu;
import com.twopirad.demo.model.SaleDetail;
import com.twopirad.demo.service.CassandraClient;
import com.twopirad.demo.service.SparkClient;
import com.twopirad.demo.service.CassandraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;

/**
 * Created with IntelliJ IDEA.
 * User: vikash
 * Date: 3/2/15
 * Time: 10:38 AM
 * To change this template use File | Settings | File Templates.
 */

@RestController
public class RestaurantController{

    @Autowired
    private CassandraClient cassandraClient;

    @Autowired
    private SparkClient sparkClient;

    @Autowired
    private CassandraService cassandraService;

    @RequestMapping(value = "/menu", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
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

    @RequestMapping(value = "/menu", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public Menu addItems(@RequestBody String dishConfiguration) {
        Menu menu = populateMenu(dishConfiguration);
        saveMenu(menu);
        return menu;
    }

    @RequestMapping(value = "/menu", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Menu> getMenu(@RequestParam(required = false) String code) {
        Cluster cluster = cassandraClient.getCluster();
        Session session = cluster.connect();
        List<Menu> menus = new ArrayList<Menu>();
        Select select = QueryBuilder.select().all().from("demo", "restaurant");
        if (code != null) {
            select.where(eq("code", code));
        }
        ResultSet resultSet = session.execute(select);
        Menu menu = null;
        String name;
        boolean flag = true;
        for (Row row : resultSet) {
            if (flag || !code.equals(row.getString("code"))) {
                flag = false;
                code = row.getString("code");
                name = row.getString("name");
                menu = new Menu();
                menus.add(menu);
                menu.setName(name);
                menu.setCode(code);
            }
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

        return menus;
    }

    @RequestMapping(value = "/sale/entry", method = RequestMethod.POST)
    public void salesEntry(@RequestBody List<SaleDetail> saleDetails) {
        String salesTableCreationSql = new StringBuilder()
                .append(" CREATE TABLE IF NOT EXISTS demo.sales (")
                .append(" restaurantCode text,")
                .append(" saleCode text,")
                .append(" customerCode text,")
                .append(" category text,")
                .append(" items map<text,int>,")
                .append(" PRIMARY KEY (restaurantCode, saleCode));")
                .toString();
        Date date = new Date();
        long timestamp = date.getTime();
        Session session = null;
        Cluster cluster;
        try {
            cluster = cassandraClient.getCluster();
            session = cluster.connect();
            session.execute(salesTableCreationSql);
            StringBuilder batchInsertSqlBuilder = new StringBuilder().append("BEGIN BATCH USING TIMESTAMP ").append(timestamp);
            List<Object> params = new ArrayList<Object>();
            int count = 0;
            for (SaleDetail saleDetail : saleDetails) {
                count++;
                String restaurantCode = saleDetail.getRestaurantCode();
                String customerCode = saleDetail.getCustomerId();
                for (Map.Entry<String, List<Dish2>> entry : saleDetail.getItems().entrySet()) {
                    List<Dish2> items = entry.getValue();
                    batchInsertSqlBuilder.append(" INSERT INTO demo.sales (restaurantCode, saleCode, customerCode, category, items) VALUES (?, ?, ?, ?, ?);");
                    params.add(restaurantCode);
                    params.add(saleDetail.getSaleCode());
                    params.add(customerCode);
                    params.add(entry.getKey());
                    Map<String, Integer> map = new HashMap<String, Integer>();
                    for (Dish2 item : items) {
                        map.put(item.getName(), item.getQuantity());
                    }
                    params.add(map);
                }
                if (count >= 400) {
                    batchInsertSqlBuilder.append(" APPLY BATCH");
                    PreparedStatement insertPreparedStatement = session.prepare(batchInsertSqlBuilder.toString());
                    session.execute(insertPreparedStatement.bind(params.toArray()));
                    batchInsertSqlBuilder = new StringBuilder().append("BEGIN BATCH USING TIMESTAMP ").append(timestamp);
                    count = 0;
                    params.clear();
                }
            }
            if (count > 0) {
                batchInsertSqlBuilder.append(" APPLY BATCH");
                PreparedStatement insertPreparedStatement = session.prepare(batchInsertSqlBuilder.toString());
                session.execute(insertPreparedStatement.bind(params.toArray()));
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }

    }

    @RequestMapping(value = "/sale/detail", method = RequestMethod.GET)
    public String getSaleDetail() {
        return cassandraService.getSalesDetail();
    }


}
