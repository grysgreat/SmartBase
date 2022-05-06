package com.star.smartBase.sinkSourceFunctions;

import com.alibaba.fastjson.JSONObject;
import com.star.smartBase.utils.DestObj;
import com.star.smartBase.utils.Json2Map;
import org.apache.flink.api.common.functions.MapFunction;

import java.util.Map;

public class JsonMapFunc implements MapFunction<String,Map<String,Object>> {
    @Override
    public Map<String,Object> map(String str) throws Exception {
        Map<String,Object> map = new Json2Map().json2Map(str);
        return map;
    }
}
