package com.star.source;

import com.star.model.KafkaProducer;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
/**
 * @author star
 */
public class PortSource implements MySource<String>{
    private String url;
    private int port;

    public PortSource(String url, int port) {
        this.url = url;
        this.port = port;
    }

    @Override
    public DataStreamSource<String> getSource(StreamExecutionEnvironment env){
        DataStreamSource<String> source = env.socketTextStream(url, port);
        return source;
    }
}
