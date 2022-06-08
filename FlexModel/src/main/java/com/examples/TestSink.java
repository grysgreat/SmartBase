package com.examples;

import com.star.sink.MySink;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.io.Serializable;

public class TestSink implements MySink, Serializable {
    @Override
    public void addMySink(StreamExecutionEnvironment env, SingleOutputStreamOperator stream) throws Exception {
        stream.print();
    }
}
