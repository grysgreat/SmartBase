package com.star.opretors.transforms;

import com.star.instance.MyOprator;
import com.star.utils.StringKmpUtil;
import lombok.Data;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.util.Collector;

import java.io.Serializable;

@Data
public class OpFilt implements MyOprator, Serializable {
    private String key;
    private StringKmpUtil kmpUtil;
    @Override
    public SingleOutputStreamOperator<String> getOpOut(SingleOutputStreamOperator<String> steam){


        SingleOutputStreamOperator<String> res = steam.filter(new FilterFunction<String>() {
            @Override
            public boolean filter(String str) throws Exception {
                //性别（1男，2女）
                return kmpUtil.kmpSearch(str,key);
            }

        });

        return res;
    }
}
