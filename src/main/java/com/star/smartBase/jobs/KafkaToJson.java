package com.star.smartBase.jobs;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.star.smartBase.utils.KafkaProducer;
import com.star.smartBase.utils.ParameterHelper;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

//数据流：web/app -> Nginx -> SpringBoot -> Kafka(ods) -> FlinkApp -> Kafka(dwd)
//程  序：mockLog -> Nginx -> Logger.sh  -> Kafka(ZK)  -> BaseLogApp -> kafka

/**
 * @author star
 *
 * TODO 在使用kafka时，要在集群上开启zookeeper和kafka ——> zk.sh start | kf.sh start
 * TODO 数据流 : mysql -> flink -> kafka
 */

/**
 * TODO eg.  --sorceIp hadoop102:9092 --destUrl E://OutPut/kafka.json --saveUrl hdfs://hadoop102:8020/rng/ck --destTopic kfMysql5
 */
public class KafkaToJson {

    public static void main(String[] args) throws Exception {
        //参数获取
        /** @Param
         * --sorceIp:  kafka ip
         * --destUrl: where to save URL
         * --saveUrl： hdfsfileSystem savepoint URL
         * --destTopic: kafka topic
         */

        //参数导入
        ParameterTool parameterTool = ParameterTool.fromArgs(args);

        ParameterHelper parameterHelper = new ParameterHelper(parameterTool);

        //TODO 1.获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        //1.1 开启CK并指定状态后端为FS    memory  fs  rocksdb
        env.setStateBackend(new FsStateBackend(parameterHelper.getSaveUrl()+"/tmplate"));
        env.enableCheckpointing(5000L);
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        env.getCheckpointConfig().setCheckpointTimeout(10000L);
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(2);
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(3000);


        //env.setRestartStrategy(RestartStrategies.fixedDelayRestart());

        //TODO 2.消费  主题数据创建流
        String sourceTopic = parameterHelper.getDestTopic();
        System.out.println(sourceTopic);
        System.out.println(parameterHelper.getSorceIp());

        String groupId = "KF-JSON";
        DataStreamSource<String> kafkaDS = env.addSource(KafkaProducer.getKafkaConsumer(sourceTopic, groupId,parameterHelper.getSorceIp()));

        //TODO 3.将每行数据转换为JSON对象
        OutputTag<String> outputTag = new OutputTag<String>("Dirty") {
        };
        SingleOutputStreamOperator<JSONObject> jsonObjDS = kafkaDS.process(new ProcessFunction<String, JSONObject>() {
            @Override
            public void processElement(String value, Context ctx, Collector<JSONObject> out) throws Exception {
                try {
                    JSONObject jsonObject = JSON.parseObject(value);
                    out.collect(jsonObject);
                } catch (Exception e) {
                    //发生异常,将数据写入侧输出流
                    ctx.output(outputTag, value);
                }
            }
        });
        jsonObjDS.print();

        //打印脏数据
        jsonObjDS.getSideOutput(outputTag).print("Dirty>>>>>>>>>>>");

        jsonObjDS.writeAsText(parameterHelper.getDestUrl());

        //TODO 8.启动任务
        env.execute("BaseLogApp");

    }

}
