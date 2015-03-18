package com.twopirad.swf.activity;

public class HelloWorldActivitiesImpl implements HelloWorldActivities {

    @Override
    public String getName() {
        return "Vikash";
    }

    @Override
    public void printName(String name) {
        System.out.println("Welcome " + name);
    }

}