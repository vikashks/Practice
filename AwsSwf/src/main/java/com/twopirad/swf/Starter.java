package com.twopirad.swf;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflow;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflowClient;
import com.amazonaws.services.simpleworkflow.model.WorkflowExecution;
import com.twopirad.swf.workflow.HelloWorldWorkflowClientExternal;
import com.twopirad.swf.workflow.HelloWorldWorkflowClientExternalFactory;
import com.twopirad.swf.workflow.HelloWorldWorkflowClientExternalFactoryImpl;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: vikash
 * Date: 3/18/15
 * Time: 12:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class Starter {

    public static void main(String[] args) throws Exception {
        ClientConfiguration configuration = new ClientConfiguration();
        String accessKeyId = System.getenv("AWS_ACCESS_KEY_ID");
        String secretKey = System.getenv("AWS_SECRET_KEY");
        AWSCredentials credentials = new BasicAWSCredentials(accessKeyId, secretKey);

        AmazonSimpleWorkflow service = new AmazonSimpleWorkflowClient(credentials, configuration);
        service.setEndpoint("https://swf.us-east-1.amazonaws.com");
        String domain = "Practice";

        HelloWorldWorkflowClientExternalFactory factory = new HelloWorldWorkflowClientExternalFactoryImpl(service, domain);
        HelloWorldWorkflowClientExternal client = factory.getClient();
        client.helloWorld();

        WorkflowExecution execution = client.getWorkflowExecution();
        System.out.println("Started helloWorld workflow with workflowId=\"" + execution.getWorkflowId()
                + "\" and runId=\"" + execution.getRunId() + "\"");

        System.in.read();
    }

}
