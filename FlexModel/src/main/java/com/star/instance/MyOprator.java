package com.star.instance;

import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;

public interface MyOprator {
    public SingleOutputStreamOperator<String> getOpOut(SingleOutputStreamOperator<String> steam);


}
