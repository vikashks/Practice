package com.twopirad.demo.service;

import com.datastax.spark.connector.japi.CassandraJavaUtil;
import com.datastax.spark.connector.japi.SparkContextJavaFunctions;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;

/**
 * Created with IntelliJ IDEA.
 * User: vikash
 * Date: 3/4/15
 * Time: 11:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class SparkClient {

    private SparkContextJavaFunctions functions;
    private String cassandraConnectionHost;


    public SparkContextJavaFunctions getFunctions() {
        if (functions == null) {
            SparkConf conf = new SparkConf();
            conf.setAppName("Demo");
            conf.setMaster("local");
            conf.set("spark.cassandra.connection.host", cassandraConnectionHost);
            SparkContext context = new SparkContext(conf);
            functions = CassandraJavaUtil.javaFunctions(context);
        }
        return functions;
    }

    public String getCassandraConnectionHost() {
        return cassandraConnectionHost;
    }

    public void setCassandraConnectionHost(String cassandraConnectionHost) {
        this.cassandraConnectionHost = cassandraConnectionHost;
    }
}
