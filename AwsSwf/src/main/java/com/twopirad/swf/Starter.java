package com.twopirad.swf;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflow;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflowClient;
import com.twopirad.swf.workflow.ReltioDataUploadWorkflowClientExternal;
import com.twopirad.swf.workflow.ReltioDataUploadWorkflowClientExternalFactory;
import com.twopirad.swf.workflow.ReltioDataUploadWorkflowClientExternalFactoryImpl;
import org.apache.camel.*;
import org.apache.camel.component.mail.MailMessage;
import org.apache.camel.impl.DefaultCamelContext;

import javax.mail.Flags;


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
            Consumer consumer = endpoint.createConsumer(new Processor() {
                @Override
                public void process(Exchange exchange) throws Exception {
                    //read individual messages
                    Message message = exchange.getIn();
                    String body = message.getBody(String.class);
                    javax.mail.Message originalMessage = exchange.getIn(MailMessage.class).getOriginalMessage();
                    originalMessage.setFlag(Flags.Flag.SEEN, true);
                    ReltioDataUploadWorkflowClientExternal client = factory.getClient();
                    client.dataUpload(body);

                    //read attachments
                    /*Map<String, DataHandler> attachments = message.getAttachments();
                    if (attachments.size() > 0) {
                        for (String name : attachments.keySet()) {
                            DataHandler dh = attachments.get(name);
                            // get the file name
                            String filename = dh.getName();

                            // get the content and convert it to byte[]
                            byte[] data = exchange.getContext().getTypeConverter()
                                    .convertTo(byte[].class, dh.getInputStream());

                            // write the data to a file
                            FileOutputStream out = new FileOutputStream(filename);
                            out.write(data);
                            out.flush();
                            out.close();
                        }
                    }*/

                }
            });
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
