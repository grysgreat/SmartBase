package com.star.source;

import com.star.instance.MyRedisCommand;
import com.star.instance.MyRedisCommandDescription;
import com.star.instance.MyRedisRecord;
import com.star.model.MyRedisSource;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;

public class RedisSource implements MySource<String> {
    private String url;
    private int port;
    private String tableName;

    public RedisSource(String url, int port, String tableName) {
        this.url = url;
        this.port = port;
        this.tableName = tableName;
    }

    @Override
    public DataStreamSource<String> getSource(StreamExecutionEnvironment env){
        FlinkJedisPoolConfig conf = new FlinkJedisPoolConfig.Builder().setHost(url).setPort(6379).build();
        DataStreamSource<String> source = env.addSource(new MyRedisSource(conf,new MyRedisCommandDescription(MyRedisCommand.HGET,tableName)));
        return source;
    }
}
