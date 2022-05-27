package com.star.source;

import com.star.instance.MyRedisCommand;
import com.star.instance.MyRedisCommandDescription;
import com.star.model.MyHbaseSource;
import com.star.model.MyRedisSource;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author star
 */
public class HbaseSource implements MySource<String> {
    private String url;
    private String port;
    private String BaseName;
    private String tableName;
    private String topic;

    public HbaseSource(String url, String port, String baseName, String tableName, String family) {
        this.url = url;
        this.port = port;
        BaseName = baseName;
        this.tableName = tableName;
        this.topic = family;
    }

    @Override
    public DataStreamSource<String> getSource(StreamExecutionEnvironment env) throws Exception {
        DataStreamSource<String> source = env.addSource(new MyHbaseSource(url,port,BaseName,tableName,topic));
        return source;
    }
}