package com.star.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.star.instance.OpratorsPram;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
/**
 * @author star
 */
@Data
public class TestUtil {
    private List<ParameterHelper> jobList = new ArrayList<>();

    public void addJobList(String jobJson){
        JsonArray array = new JsonParser().parse(jobJson).getAsJsonArray();

        for (int i = 0; i < array.size(); i++) {
            ParameterHelper tmp=new ParameterHelper();

            JsonObject jobi = array.get(i).getAsJsonObject();

            if(jobi.get("jobTime")==null){
                tmp.setJobTime(0);
            }else {
                tmp.setJobTime(Integer.parseInt(jobi.get("jobTime").getAsString()));
            }



            JsonObject source = jobi.get("source").getAsJsonObject();
            System.out.println(source);

            String sourceType = source.get("types").getAsString();
            tmp.setSorceType(sourceType);
            switch(sourceType) {
                case "mysql":
                    tmp.setSorceIp(source.get("url").getAsString());
                    tmp.setSorcePort(source.get("port").getAsInt());
                    tmp.setSorceUserName(source.get("username").getAsString());
                    tmp.setSorceUserPwd(source.get("password").getAsString());
                    tmp.setSorceBase(source.get("basename").getAsString());
                    tmp.setSourceTable(source.get("tablename").getAsString());

                    System.out.println("url"+source.get("url").getAsString());
                    break;
                case "kafka":
                    tmp.setSorceIp(source.get("url").getAsString());
                    tmp.setSourceTable(source.get("topic").getAsString());
                    tmp.setSorcePort(source.get("port").getAsInt());
                    break;

                case "port":
                    tmp.setSorceIp(source.get("url").getAsString());
                    tmp.setSorcePort(source.get("port").getAsInt());
                    break;

                case "redis":
                    tmp.setSorceIp(source.get("url").getAsString());
                    tmp.setSorcePort(source.get("port").getAsInt());
                    tmp.setSourceTable(source.get("topic").getAsString());
                    break;

                case "text":
                    tmp.setSorceIp(source.get("url").getAsString());
                    break;

                default: break;
            }

            List<OpratorsPram> tmpOps=new ArrayList<>();
            JsonArray ops=jobi.getAsJsonArray("operators");
            for (int j = 0; j < ops.size(); j++) {
                JsonObject opi = ops.get(j).getAsJsonObject();
                String opType=opi.get("type").getAsString();
                String opKey=opi.get("key").getAsString();

                tmpOps.add(new OpratorsPram(opType,opKey));

            }
            tmp.setOpList(tmpOps);

            JsonObject dest = jobi.get("dest").getAsJsonObject();

            String destType = dest.get("types").getAsString();
            tmp.setDestType(destType);
            switch(destType) {
                case "mysql": {
                    tmp.setDestUrl(dest.get("url").getAsString());
                    tmp.setDestPort(dest.get("port").getAsInt());
                    tmp.setDestUserName(dest.get("username").getAsString());
                    tmp.setDestUserPwd(dest.get("password").getAsString());
                    tmp.setDestBase(dest.get("basename").getAsString());
                    tmp.setDestTopic(dest.get("tablename").getAsString());
                    break;
                }
                case "kafka":{
                    tmp.setDestUrl(dest.get("url").getAsString());
                    tmp.setDestTopic(dest.get("topic").getAsString());
                    tmp.setDestPort(dest.get("port").getAsInt());
                    break;
                }
                case "port":{
                    tmp.setDestUrl(dest.get("url").getAsString());
                    tmp.setDestPort(dest.get("port").getAsInt());
                    break;
                }
                case "redis":{
                    tmp.setDestUrl(dest.get("url").getAsString());
                    tmp.setDestPort(dest.get("port").getAsInt());
                    tmp.setDestTopic(dest.get("topic").getAsString());
                    break;
                }
                case "text":{
                    tmp.setDestUrl(dest.get("url").getAsString());
                    break;
                }
                default: break;
            }
            jobList.add(tmp);

        }

    }

}
