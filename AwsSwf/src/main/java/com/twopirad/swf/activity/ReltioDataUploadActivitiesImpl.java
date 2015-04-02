package com.twopirad.swf.activity;

/**
 * Created with IntelliJ IDEA.
 * User: vikash
 * Date: 3/31/15
 * Time: 12:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReltioDataUploadActivitiesImpl implements ReltioDataUploadActivities {

    int i;

    @Override
    public String populateJSON(String data) {
        return (i++) + data;
    }

    @Override
    public void uploadData(String json) {
        System.out.println("json = " + json);
    }
}
