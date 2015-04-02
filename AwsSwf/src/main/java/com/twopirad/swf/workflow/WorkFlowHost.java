package com.twopirad.swf.workflow;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflow;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflowClient;
import com.amazonaws.services.simpleworkflow.flow.WorkflowWorker;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: vikash
 * Date: 3/18/15
 * Time: 12:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class WorkFlowHost {

    public static void main(String[] args) throws Exception {
        ClientConfiguration configuration = new ClientConfiguration();
        String accessKeyId = System.getenv("AWS_ACCESS_KEY_ID");
        String secretKey = System.getenv("AWS_SECRET_KEY");
        AWSCredentials credentials = new BasicAWSCredentials(accessKeyId, secretKey);

        AmazonSimpleWorkflow service = new AmazonSimpleWorkflowClient(credentials, configuration);
        service.setEndpoint("https://swf.us-east-1.amazonaws.com");
        String domain = "Practice";
        String taskListToPoll = "ReltioDataUpload";

        final WorkflowWorker wfw = new WorkflowWorker(service, domain, taskListToPoll);
        wfw.addWorkflowImplementationType(ReltioDataUploadWorkflowImpl.class);
        wfw.start();

        System.out.println("Workflow Host Service Started...");

        Runtime.getRuntime().addShutdownHook(new Thread() {

            public void run() {
                try {
                    wfw.shutdownAndAwaitTermination(1, TimeUnit.MINUTES);
                    System.out.println("Workflow Host Service Terminated...");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        System.out.println("Please press any key to terminate service.");

        try {
            System.in.read();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        System.exit(0);
    }

}
