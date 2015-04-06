package com.twopirad.swf.reltio;

import com.reltio.dataload.SendJsonToServerV10;

public class DataUploadClient {
    public void uploadData(String jsonFilePath) throws Exception {
        System.out.println("json = " + jsonFilePath);
        StringBuilder builder = new StringBuilder();
        builder.append("5").append("::")
                .append("10").append("::")
                .append(jsonFilePath).append("::")
                .append("https://sndbx.reltio.com/reltio/api/us01fo01/entities?returnUriOnly=true").append("::")
                .append("debajyoti.lahiri@reltio.com").append("::")
                .append("M@ther2001").append("::")
                .append("5000");
        //java -jar ReltioDataLoadTool-v2.jar 5 10 tgt_json_request_hco_simple_attributes.txt http://dataload.pilot.reltio.com/reltio/api/ce7vm613/entities?returnUriOnly=true
        // <username> <password> 500000 > tgt_json_request_hco_simple_attributes.log
        String[] args = builder.toString().split("::");
        SendJsonToServerV10.main(args);
    }
}
