package com.twopirad.demo.service;

import com.datastax.spark.connector.japi.CassandraRow;
import com.datastax.spark.connector.japi.SparkContextJavaFunctions;
import com.datastax.spark.connector.japi.rdd.CassandraJavaRDD;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scala.Tuple2;

import java.io.Serializable;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: vikash
 * Date: 3/4/15
 * Time: 1:32 PM
 * To change this template use File | Settings | File Templates.
 */

@Service
public class CassandraService implements Serializable {

    @Autowired
    private transient CassandraClient cassandraClient;

    @Autowired
    private transient SparkClient sparkClient;

    public String getSalesDetail() {
        SparkContextJavaFunctions functions = sparkClient.getFunctions();
        JavaPairRDD<String, Iterable<CassandraRow>> menuRdd = functions.cassandraTable("demo", "restaurant").filter(new Function<CassandraRow, Boolean>() {
            @Override
            public Boolean call(CassandraRow row) throws Exception {
                return row.getString("code").equals("R0001");
            }
        }).groupBy(new Function<CassandraRow, String>() {
            @Override
            public String call(CassandraRow row) throws Exception {
                return row.getString("code");
            }
        });

        JavaPairRDD<String, Integer> pairRDD = menuRdd.flatMapToPair(new PairFlatMapFunction<Tuple2<String, Iterable<CassandraRow>>, String, Integer>() {
            @Override
            public Iterable<Tuple2<String, Integer>> call(Tuple2<String, Iterable<CassandraRow>> iterable) throws Exception {
                List<Tuple2<String, Integer>> list = new ArrayList<>();
                Iterator<CassandraRow> cassandraRows = iterable._2.iterator();
                while (cassandraRows.hasNext()) {
                    CassandraRow row = cassandraRows.next();
                    list.add(new Tuple2<>(row.getString("dish_name"), row.getInt("dish_price")));
                }
                return list;
            }
        });
        List<Tuple2<String, Integer>> list = pairRDD.toArray();
        Map<String, Integer> dishPriceMap = new HashMap<>();
        for (Tuple2<String, Integer> tuple : list) {
            dishPriceMap.put(tuple._1, tuple._2);

        }
        CassandraJavaRDD<CassandraRow> salesRdd = functions.cassandraTable("demo", "sales");
        Iterator<CassandraRow> iterator = salesRdd.toLocalIterator();
        StringBuilder builder = new StringBuilder();
        //add header
        builder.append("Restaurant Code").append(",").append("Customer Id").append(",").append("Sale Code").append(",")
                .append("Dish Name").append(",").append("Dish Quantity").append(",").append("Cost").append(",").append("Total").append("\n");
        while (iterator.hasNext()) {
            CassandraRow row = iterator.next();
            String restaurantcode = row.getString("restaurantcode");
            String customercode = row.getString("customercode");
            String salecode = row.getString("salecode");
            builder.append(restaurantcode).append(",").append(customercode).append(",").append(salecode).append(",");
            Map<Object, Object> items = row.getMap("items");
            int count = 0, total = 0, size = items.size();
            for (Map.Entry<Object, Object> entry : items.entrySet()) {
                count++;
                String dishName = String.valueOf(entry.getKey());
                int quantity = Integer.parseInt(String.valueOf(entry.getValue()));
                int price = dishPriceMap.get(dishName) * quantity;
                total += price;
                builder.append(dishName).append(",").append(quantity).append(",").append(price).append(",");
                if (count >= size) {
                    builder.append(total);
                }
            }
            builder.append("\n");
        }
        return builder.toString();
    }


}
