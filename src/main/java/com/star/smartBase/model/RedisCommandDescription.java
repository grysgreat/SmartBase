package com.star.smartBase.model;

import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommand;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisDataType;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisDataType;
import org.apache.flink.util.Preconditions;

import java.io.Serializable;

public class RedisCommandDescription implements Serializable {
    private static final long serialVersionUID = 1L;
    private RedisCommand redisCommand;
    private String additionalKey;

    public RedisCommandDescription(RedisCommand redisCommand, String additionalKey) {
        Preconditions.checkNotNull(redisCommand, "Redis command type can not be null");
        this.redisCommand = redisCommand;
        this.additionalKey = additionalKey;
        if ((redisCommand.getRedisDataType() == RedisDataType.HASH || redisCommand.getRedisDataType() == RedisDataType.SORTED_SET) && additionalKey == null) {
            throw new IllegalArgumentException("Hash and Sorted Set should have additional key");
        }
    }

    public RedisCommandDescription(RedisCommand redisCommand) {
        this(redisCommand, (String)null);
    }

    public RedisCommand getCommand() {
        return this.redisCommand;
    }

    public String getAdditionalKey() {
        return this.additionalKey;
    }
}

