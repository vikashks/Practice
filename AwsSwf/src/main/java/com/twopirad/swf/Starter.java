package com.twopirad.swf;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflow;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflowClient;
import com.twopirad.swf.workflow.ReltioDataUploadWorkflowClientExternalFactory;
import com.twopirad.swf.workflow.ReltioDataUploadWorkflowClientExternalFactoryImpl;
import org.apache.camel.CamelContext;
import org.apache.camel.Consumer;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultCamelContext;


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
        final ReltioDataUploadWorkflowClientExternalFactory factory = new ReltioDataUploadWorkflowClientExternalFactoryImpl(service, domain);
        final CamelContext camelContext = new DefaultCamelContext();
        String password = System.getenv("EMAIL_PASSWORD");
        Endpoint endpoint = camelContext.getEndpoint("imaps://imap.gmail.com?username=vikashs@2pirad.com&password=" + password +
                "&delete=true&maxMessagesPerPoll=10&fetchSize=10&searchTerm.subject='Aws Swf Testing'&consumer.delay=10000");
        try {
            Consumer consumer = endpoint.createConsumer(new MailProcessor(factory));
            consumer.start();
            System.out.println("press any key to stop...");
            System.in.read();
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    camelContext.stop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }));

        //re8.5%QcvY&
        //["[Ljava.lang.Object;",[]]
    }

}
