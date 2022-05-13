package com.star.opretors.mapper;

import lombok.Data;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommand;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommandDescription;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisMapper;

@Data
public  class MyRedisMapper implements RedisMapper<Tuple2<String,String>> {
    private String topic;
    @Override
    public String getKeyFromData(Tuple2<String,String> e) {
        return e.f0;
    }

    @Override
    public String getValueFromData(Tuple2<String,String> e) {
        System.out.println(e.f0+":"+e.f1);
        return e.f1;
    }

    @Override
    public RedisCommandDescription getCommandDescription() {
        return new RedisCommandDescription(RedisCommand.HSET, topic);
    }

}