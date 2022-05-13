package com.star.source;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public interface MySource<T> {
    public DataStreamSource<T> getSource(StreamExecutionEnvironment env) throws Exception;
}
