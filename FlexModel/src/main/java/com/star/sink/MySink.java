package com.star.sink;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public interface MySink<T> {
    public void addMySink(StreamExecutionEnvironment env, SingleOutputStreamOperator<T> stream) throws Exception;
}
