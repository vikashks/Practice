package com.twopirad.swf.workflow;

import com.amazonaws.services.simpleworkflow.flow.core.Promise;
import com.twopirad.swf.activity.HelloWorldActivitiesClient;
import com.twopirad.swf.activity.HelloWorldActivitiesClientImpl;

/**
 * Created with IntelliJ IDEA.
 * User: vikash
 * Date: 3/18/15
 * Time: 11:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class HelloWorldWorkflowImpl implements HelloWorldWorkflow {

    private HelloWorldActivitiesClient client = new HelloWorldActivitiesClientImpl();

    @Override
    public void helloWorld() {
        Promise<String> name = client.getName();
        client.printName(name);
    }
}
