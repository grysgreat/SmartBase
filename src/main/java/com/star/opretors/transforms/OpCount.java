package com.star.opretors.transforms;

import com.star.instance.MyOprator;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.util.Collector;

public class OpCount implements MyOprator {
    @Override
    public SingleOutputStreamOperator<String> getOpOut(SingleOutputStreamOperator<String> steam){


        //转换计算
        SingleOutputStreamOperator<Tuple2<String, Long>> returns = steam.flatMap((String line1, Collector<Tuple2<String, Long>> out) -> {
            String[] words = line1.split(",");
            for (String word : words) {
                out.collect(Tuple2.of(word, 1L));
            }
        }).returns(Types.TUPLE(Types.STRING, Types.LONG));
        //分组
        KeyedStream<Tuple2<String, Long>, String> wordAndoneString = returns.keyBy(data -> data.f0);

        //求和
        SingleOutputStreamOperator<Tuple2<String, Long>> sum = wordAndoneString.sum(1);

        //转换计算
        SingleOutputStreamOperator<String> res = sum.flatMap((Tuple2<String, Long> line1, Collector<String> out) -> {
            out.collect(line1.toString());
        }).returns(Types.STRING);

        return res;
    }
}
