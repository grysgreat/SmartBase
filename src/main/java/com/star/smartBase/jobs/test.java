package com.star.smartBase.jobs;

import com.star.smartBase.sinkFunctions.MysqlSink;
import com.star.smartBase.sinkFunctions.testSink;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;


import java.util.ArrayList;
import java.util.Properties;

public class test {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "hadoop102:9092");

        //TODO 参数传递-- mysql 库名，表名，
        Configuration conf = new Configuration();
        conf.setString("baseName","test");
        conf.setString("tableName","clicks");
        conf.setString("username","root");
        conf.setString("password","123456");
        conf.setString("dburl","localhost:3306");
        env.getConfig().setGlobalJobParameters(conf);


        // 1,abc,100  类似这样的数据，当然也可以是很复杂的json数据，去做解析
        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>("test", new SimpleStringSchema(), properties);

        env.getConfig().setRestartStrategy(
                RestartStrategies.fixedDelayRestart(5, 5000));
        env.enableCheckpointing(2000);
        DataStream<String> stream = env
                .addSource(consumer);

        DataStream<String[]> sourceStream = stream.filter((FilterFunction<String>) value -> StringUtils.isNotBlank(value))
                .map((MapFunction<String, String[]>) value -> {
                    String[] args1 = value.split(","); //切割json
                    return args1;
                }).returns(new TypeHint<String[]>() {});


        sourceStream.addSink(new testSink());
        env.execute("data to mysql start");
    }


}