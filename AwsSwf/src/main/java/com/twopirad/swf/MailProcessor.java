package com.twopirad.swf;

import com.twopirad.swf.workflow.ReltioDataUploadWorkflowClientExternal;
import com.twopirad.swf.workflow.ReltioDataUploadWorkflowClientExternalFactory;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.component.mail.MailMessage;

import javax.activation.DataHandler;
import javax.mail.Flags;
import javax.mail.MessagingException;
import java.io.*;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: vikash
 * Date: 4/6/15
 * Time: 7:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class MailProcessor implements Processor {

    private final ReltioDataUploadWorkflowClientExternalFactory factory;
    int counter = 0;

    public MailProcessor(ReltioDataUploadWorkflowClientExternalFactory factory) {
        this.factory = factory;
    }

    @Override
    public void process(final Exchange exchange) throws Exception {
        counter++;
        Message message = exchange.getIn();
        javax.mail.Message originalMessage = exchange.getIn(MailMessage.class).getOriginalMessage();
        try {
            originalMessage.setFlag(Flags.Flag.SEEN, true);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        String fileName = "source/" + counter + "/source.csv";
        BufferedWriter writer = null;
        String line;
        try {
            File file = new File(fileName);
            file.getParentFile().mkdir();
            file.createNewFile();
            writer = new BufferedWriter(new FileWriter(file));
            //read attachments
            Map<String, DataHandler> attachments = message.getAttachments();
            if (attachments.size() > 0) {
                int count = 0;
                for (String name : attachments.keySet()) {
                    count++;
                    DataHandler dh = attachments.get(name);
                    BufferedReader reader = null;
                    boolean headerProcess = true;
                    try {
                        reader = new BufferedReader(new InputStreamReader(dh.getInputStream()));
                        while ((line = reader.readLine()) != null) {
                            //skip header from attachments
                            if (count != 1 && headerProcess) {
                                headerProcess = false;
                                continue;
                            }
                            writer.write(line);
                            writer.newLine();
                        }
                    } finally {
                        if (reader != null) {
                            reader.close();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        ReltioDataUploadWorkflowClientExternal client = factory.getClient();
        client.dataUpload(fileName);
    }
}
