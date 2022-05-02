package com.star.smartBase.jobs;

import com.star.smartBase.sinkFunctions.MysqlSink;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.Properties;

public class FlinkKafkaMysql {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "hadoop102:9092");

// 1,abc,100  类似这样的数据，当然也可以是很复杂的json数据，去做解析
        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>("test", new SimpleStringSchema(), properties);

        env.getConfig().setRestartStrategy(
                RestartStrategies.fixedDelayRestart(5, 5000));
        env.enableCheckpointing(2000);
        DataStream<String> stream = env
                .addSource(consumer);


        DataStream<Tuple3<Integer, String, Integer>> sourceStream = stream.filter((FilterFunction<String>) value -> StringUtils.isNotBlank(value))
                .map((MapFunction<String, Tuple3<Integer, String, Integer>>) value -> {
                    String[] args1 = value.split(",");
                    return new Tuple3<Integer, String, Integer>(
                            Integer.valueOf(args1[0]),
                            args1[1],
                            Integer.valueOf(args1[2]));
                }).returns(new TypeHint<Tuple3<Integer, String, Integer>>() {});

        sourceStream.addSink(new MysqlSink());
        env.execute("data to mysql start");
    }
}