package com.star.opretors.transforms;

import com.star.instance.MyOprator;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class OpTime implements MyOprator<String>, Serializable {
    private int key=0;
    private static Date alljobStartTime = new Date(System.currentTimeMillis());


    @Override
    public SingleOutputStreamOperator<String> getOpOut(SingleOutputStreamOperator<String> stream){
        SingleOutputStreamOperator<String> stream2 = stream.process(new ProcessFunction<String, String>() {
            @Override
            public void processElement(String s, Context context, Collector<String> collector) throws Exception {
                Date nowTime = new Date(System.currentTimeMillis());
                int dt = (int) ((nowTime.getTime() - alljobStartTime.getTime()) / 1000);
                if (key==0){
                    collector.collect(s);
                } else if (dt <= key)
                    collector.collect(s);
            }
        });
        return stream2;
    }
}
