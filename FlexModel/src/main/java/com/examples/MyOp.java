package com.examples;


import com.star.instance.MyOprator;
import lombok.Data;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;

import java.io.Serializable;

@Data
public class MyOp implements MyOprator, Serializable {

    private String key;
    @Override
    public SingleOutputStreamOperator<String> getOpOut(SingleOutputStreamOperator<String> steam){
        return steam;
    }
}