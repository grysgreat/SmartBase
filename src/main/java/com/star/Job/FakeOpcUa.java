package com.star.Job;

import com.star.model.FakeSinkFunc;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class FakeOpcUa {
    public static void main(String[] args) throws Exception {
        //创建流式执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(1);
        //读取文件
        DataStreamSource<String> line = env.readTextFile("/opt/Opc.txt");

        line.addSink(new FakeSinkFunc());

        line.print().setParallelism(1);
        //流启动
        env.execute();
    }
}
