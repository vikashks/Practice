package com.twopirad.demo.service;

import com.datastax.driver.core.Cluster;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Created with IntelliJ IDEA.
 * User: vikash
 * Date: 3/2/15
 * Time: 2:12 PM
 * To change this template use File | Settings | File Templates.
 */

public class CassandraClient{

    private Cluster cluster;

    private String nodes;

    public Cluster getCluster() {
        if (cluster == null) {
            cluster = Cluster.builder().addContactPoint(nodes).build();
        }
        return cluster;
    }


    public String getNodes() {
        return nodes;
    }

    public void setNodes(String nodes) {
        this.nodes = nodes;
    }

    @PreDestroy
    public void close() {
        if (cluster != null) {
            cluster.close();
        }
    }
}
