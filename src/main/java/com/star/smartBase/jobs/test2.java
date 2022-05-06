package com.star.smartBase.jobs;


import com.alibaba.fastjson.JSONObject;
import com.star.smartBase.utils.DestObj;
import com.star.smartBase.utils.Json2Map;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;


//TODO 数据流： 自定义socket输出源 -> flink(List):mysqlUtil,sink -> Mysql

//可以自定义输入数据格式

/**
 * TODO eg.  --sorceIp hadoop102 --sorcePort 7777 --destUrl hadoop102:9092 --destTopic portT --saveUrl --saveUrl hdfs://hadoop102:8020/rng/ck
 */
public class test2 {
    public static void main(String[] args) throws Exception {
        File file = new File("F://tmp/test.json");
        String jsonString = new String(Files.readAllBytes(Paths.get(file.getPath())));


        System.out.println(jsonString);
        Map<String,Object> map = new Json2Map().json2Map(jsonString);

        System.out.println(map);

    }
}
