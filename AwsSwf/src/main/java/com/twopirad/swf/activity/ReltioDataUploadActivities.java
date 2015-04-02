package com.twopirad.swf.activity;

import com.amazonaws.services.simpleworkflow.flow.annotations.Activities;
import com.amazonaws.services.simpleworkflow.flow.annotations.Activity;
import com.amazonaws.services.simpleworkflow.flow.annotations.ActivityRegistrationOptions;

/**
 * Created with IntelliJ IDEA.
 * User: vikash
 * Date: 3/31/15
 * Time: 12:29 PM
 * To change this template use File | Settings | File Templates.
 */

@Activities
public interface ReltioDataUploadActivities {

    @Activity(name = "populateJSON", version = "1.1")
    @ActivityRegistrationOptions(defaultTaskScheduleToStartTimeoutSeconds = 30, defaultTaskStartToCloseTimeoutSeconds = 60)
    String populateJSON(String data);

    @Activity(name = "uploadData", version = "1.1")
    @ActivityRegistrationOptions(defaultTaskScheduleToStartTimeoutSeconds = 30, defaultTaskStartToCloseTimeoutSeconds = 60)
    void uploadData(String json);


}
