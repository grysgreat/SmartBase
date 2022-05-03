package com.star.smartBase.model;

import com.star.smartBase.utils.MyRedisRecord;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisDataType;
import org.apache.flink.util.Collector;

import java.util.Map;

public class MyMapRedisRecordSplitter implements FlatMapFunction<MyRedisRecord, Tuple2<String,Integer>> {
    @Override
    public void flatMap(MyRedisRecord myRedisRecord, Collector<Tuple2<String, Integer>> collector) throws Exception {
        assert myRedisRecord.getRedisDataType() == RedisDataType.HASH;
        Map<String,String> map = (Map<String,String>)myRedisRecord.getData();
        for(Map.Entry<String,String> e : map.entrySet()){
            collector.collect(new Tuple2<>(e.getKey(),Integer.valueOf(e.getValue())));
        }
    }
}


