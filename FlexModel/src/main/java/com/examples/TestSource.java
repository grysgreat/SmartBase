package com.examples;

import com.star.source.MySource;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.io.Serializable;

public class TestSource implements MySource, Serializable {



    @Override
    public DataStreamSource<String> getSource(StreamExecutionEnvironment env) throws Exception {
        DataStreamSource<String> source = env.socketTextStream("hadoop102", 7777);
        return source;
    }
}
