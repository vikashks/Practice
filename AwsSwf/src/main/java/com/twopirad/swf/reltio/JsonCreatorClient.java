package com.twopirad.swf.reltio;

import com.twopiradian.Runner;

import java.io.File;

public class JsonCreatorClient {

    public String generateJSON(String filePath) throws Exception {
        File file = new File(filePath);
        String outputFile = "source.json";
        String parentPath = file.getParentFile().getAbsolutePath();
        StringBuilder builder = new StringBuilder()
                .append("-u=debajyoti.lahiri@reltio.com").append("::")
                .append("-p=M@ther2001").append("::")
                .append("-t=https://sndbx.reltio.com/reltio/api/us01fo01").append("::")
                .append("-o=").append(parentPath).append("::")
                .append("-e=configuration/entityTypes/SoldToCustomer").append("::")
                .append("-s=").append(file.getAbsolutePath()).append("::")
                .append("-d=[|]{2}").append("::")
                .append("-st=USF_Cust_Corp").append("::")
                .append("-ci=UsfCorpCustomer").append("::")
                .append("-of=").append(outputFile);
        String[] arguments = builder.toString().split("::");
        Runner.main(arguments);
        return new File(file.getParentFile(),"source.json").getAbsolutePath();
    }

}
