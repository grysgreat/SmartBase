package com.star.instance;

import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;

public interface MyOprator<T> {
    public SingleOutputStreamOperator<T> getOpOut(SingleOutputStreamOperator<String> steam);
}
