package com.star.model;

import io.debezium.time.Time;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.io.Serializable;
import java.util.Map;

public class FakeSinkFunc extends RichSinkFunction<String> implements Serializable {
    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
    }

    @Override
    public void close() throws Exception {
        super.close();
    }

    @Override
    public void invoke(String value) throws Exception {
        Thread.sleep(2000);
    }
}
