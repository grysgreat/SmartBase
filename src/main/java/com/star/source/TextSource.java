package com.star.source;

import com.star.instance.MyRedisCommand;
import com.star.instance.MyRedisCommandDescription;
import com.star.instance.MyRedisRecord;
import com.star.model.MyRedisSource;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;

public class TextSource implements MySource<String> {
    private String url;

    public TextSource(String url) {
        this.url = url;
    }

    @Override
    public DataStreamSource<String> getSource(StreamExecutionEnvironment env){
        DataStreamSource<String> source = env.readTextFile(url);
        return source;
    }
}
