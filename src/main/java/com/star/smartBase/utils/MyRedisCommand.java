package com.star.smartBase.utils;

import org.apache.flink.streaming.connectors.redis.common.mapper.RedisDataType;

public enum MyRedisCommand {
    HGET(RedisDataType.HASH);

    private RedisDataType redisDataType;

    private MyRedisCommand(RedisDataType redisDataType) {
        this.redisDataType = redisDataType;
    }

    public RedisDataType getRedisDataType() {
        return this.redisDataType;
    }
}