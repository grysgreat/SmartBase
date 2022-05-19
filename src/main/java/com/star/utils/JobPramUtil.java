package com.star.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.star.instance.OpratorsPram;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class JobPramUtil {
    private List<ParameterHelper> jobList = new ArrayList<>();

    public void addJobList(String jobJson){
        //创建json解析器
        JsonParser parse = new JsonParser();
        JsonObject json = (JsonObject) parse.parse(jobJson);
        //json字符串解析,获取result节点

        int jobNum=Integer.parseInt(json.get("JobNum").toString());


        for(int i=1;i<=jobNum;i++){
            ParameterHelper tmp=new ParameterHelper();

            JsonObject jobi = json.get("job"+i).getAsJsonObject();

            JsonObject source = jobi.get("source").getAsJsonObject();

            String sourceType = source.get("types").getAsString();

            if(jobi.get("jobTime")==null){
                tmp.setJobTime(0);
            }else {
                tmp.setJobTime(Integer.parseInt(jobi.get("jobTime").getAsString()));
            }

            tmp.setSorceType(sourceType);


            switch(sourceType) {
                case "mysql":
                    tmp.setSorceIp(source.get("url").getAsString());
                    tmp.setSorcePort(Integer.parseInt(source.get("port").getAsString()));
                    tmp.setSorceUserName(source.get("username").getAsString());
                    tmp.setSorceUserPwd(source.get("password").getAsString());
                    tmp.setSorceBase(source.get("basename").getAsString());
                    tmp.setSourceTable(source.get("tablename").getAsString());

                    System.out.println("url"+source.get("url").getAsString());
                    break;
                case "kafka":
                    tmp.setSorceIp(source.get("url").getAsString());
                    tmp.setSourceTable(source.get("topic").getAsString());
                    tmp.setSorcePort(Integer.parseInt(source.get("port").getAsString()));
                    break;

                case "port":
                    tmp.setSorceIp(source.get("url").getAsString());
                    tmp.setSorcePort(Integer.parseInt(source.get("port").getAsString()));
                    break;

                case "redis":
                    tmp.setSorceIp(source.get("url").getAsString());
                    tmp.setSorcePort(Integer.parseInt(source.get("port").getAsString()));
                    tmp.setSourceTable(source.get("topic").getAsString());
                    break;

                case "text":
                    tmp.setSorceIp(source.get("url").getAsString());
                    break;

                default: break;
            }


            List<OpratorsPram> tmpOps=new ArrayList<>();
            JsonObject ops = jobi.get("operator").getAsJsonObject();
            int opNum=Integer.parseInt(ops.get("num").toString());


            for (int j = 1; j <= opNum; j++) {
                JsonObject opi = ops.get("operator"+j).getAsJsonObject();
                String opType=opi.get("type").getAsString();
                String opKey=opi.get("key").getAsString();

                tmpOps.add(new OpratorsPram(opType,opKey));
            }
            tmp.setOpList(tmpOps);


            JsonObject dest = jobi.get("dest").getAsJsonObject();
            String destType = dest.get("types").getAsString() ;
            tmp.setDestType(destType);
            switch(destType) {
                case "mysql": {
                    tmp.setDestUrl(dest.get("url").getAsString());
                    tmp.setDestPort(Integer.parseInt(dest.get("port").getAsString()));
                    tmp.setDestUserName(dest.get("username").getAsString());
                    tmp.setDestUserPwd(dest.get("password").getAsString());
                    tmp.setDestBase(dest.get("basename").getAsString());
                    tmp.setDestTopic(dest.get("tablename").getAsString());
                    break;
                }
                case "kafka":{
                    tmp.setDestUrl(dest.get("url").getAsString());
                    tmp.setDestTopic(dest.get("topic").getAsString());
                    tmp.setDestPort(Integer.parseInt(dest.get("port").getAsString()));
                    break;
                }
                case "port":{
                    tmp.setDestUrl(dest.get("url").getAsString());
                    tmp.setDestPort(Integer.parseInt(dest.get("port").getAsString()));
                    break;
                }
                case "redis":{
                    tmp.setDestUrl(dest.get("url").getAsString());
                    tmp.setDestPort(Integer.parseInt(dest.get("port").getAsString()));
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
