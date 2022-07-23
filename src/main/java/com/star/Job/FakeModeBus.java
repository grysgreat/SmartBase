package com.star.Job;

import com.star.model.FakeSinkFunc;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class FakeModeBus {
    public static void main(String[] args) throws Exception {
        //创建流式执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(1);
        //读取文件
        DataStreamSource<String> line = env.readTextFile("/opt/Modbus.txt");

        line.addSink(new FakeSinkFunc());

        line.print().setParallelism(1);
        //流启动
        env.execute();
    }
}
