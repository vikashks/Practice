package com.twopirad.swf.activity;

import com.reltio.dataload.SendJsonToServerV10;
import com.twopirad.swf.reltio.DataUploadClient;
import com.twopirad.swf.reltio.JsonCreatorClient;

/**
 * Created with IntelliJ IDEA.
 * User: vikash
 * Date: 3/31/15
 * Time: 12:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReltioDataUploadActivitiesImpl implements ReltioDataUploadActivities {

    @Override
    public String populateJSON(String sourceFilePath) throws Exception {
        JsonCreatorClient jsonCreatorClient = new JsonCreatorClient();
        return jsonCreatorClient.generateJSON(sourceFilePath);
    }

    @Override
    public void uploadData(String jsonFilePath) throws Exception {
        DataUploadClient uploadClient = new DataUploadClient();
        uploadClient.uploadData(jsonFilePath);
    }
}
