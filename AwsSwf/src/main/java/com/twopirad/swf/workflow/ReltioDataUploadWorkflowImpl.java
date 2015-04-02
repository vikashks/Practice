package com.twopirad.swf.workflow;

import com.amazonaws.services.simpleworkflow.flow.core.Promise;
import com.twopirad.swf.activity.ReltioDataUploadActivitiesClient;
import com.twopirad.swf.activity.ReltioDataUploadActivitiesClientImpl;

/**
 * Created with IntelliJ IDEA.
 * User: vikash
 * Date: 3/31/15
 * Time: 12:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReltioDataUploadWorkflowImpl implements ReltioDataUploadWorkflow {

    ReltioDataUploadActivitiesClient client = new ReltioDataUploadActivitiesClientImpl();

    @Override
    public void dataUpload(String message) {
        Promise<String> testing = client.populateJSON(message);
        client.uploadData(testing);

       /* while (true) {
            Promise<String> testing = client.populateJSON(test());
            client.uploadData(testing);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/
    }

}
