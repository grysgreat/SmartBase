package com.star.smartBase.jobs;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.star.smartBase.Interface.MysqlConnPool;
import com.star.smartBase.sinkSourceFunctions.JsonMapFunc;
import com.star.smartBase.sinkSourceFunctions.JsonSinkFunc;
import com.star.smartBase.sinkSourceFunctions.MysqlSink;
import com.star.smartBase.utils.DestObj;
import com.star.smartBase.utils.KafkaProducer;
import com.star.smartBase.utils.ParameterHelper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;
import java.util.Properties;


//TODO 数据流： 自定义socket输出源 -> flink(List):mysqlUtil,sink -> Mysql

//可以自定义输入数据格式

/**
 * TODO eg.  --sorceIp hadoop102 --sorcePort 7777 --destUrl E://tmp/port333.txt
 */
public class PortController {
    public static void main(String[] args) throws Exception {
        //参数获取
        /** @Param
         * --sorceIp:  Port ip
         * --sorcePort: Port
         * --destUrl: save flie URL
         * --saveUrl： hdfs fileSystem savepoint URL
         */




        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        ParameterHelper parameterHelper = new ParameterHelper(parameterTool);


        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

//        // 1.1 设置CK&状态后端
//        env.setStateBackend(new FsStateBackend(parameterHelper.getSaveUrl()+"/tmplate"));
//        env.enableCheckpointing(5000L);
//        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
//        env.getCheckpointConfig().setCheckpointTimeout(10000L);
//        env.getCheckpointConfig().setMaxConcurrentCheckpoints(2);
//        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(3000);



        DataStream<String> stream = env.socketTextStream("hadoop102",7777);

        stream.print();
        //从kafka数据源处理数据为String数组


        stream.filter((FilterFunction<String>) value -> StringUtils.isNotBlank(value))
                .map(new JsonMapFunc())
                .returns(new TypeHint<Map<String,Object>>() {})
                .addSink(new JsonSinkFunc());
        //4.启动任务
        env.execute("Flink-PortToKafka");
    }
}
