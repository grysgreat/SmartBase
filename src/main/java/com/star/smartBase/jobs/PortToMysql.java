package com.star.smartBase.jobs;

import com.star.smartBase.sinkSourceFunctions.MysqlSink;
import com.star.smartBase.utils.ParameterHelper;
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

import java.util.Properties;


//TODO 数据流： 自定义socket输出源 -> flink(List):mysqlUtil,sink -> Mysql

//可以自定义输入数据格式

/**
 * TODO eg.  --sorceIp 192.168.10.1 --destUrl hadoop102 --destPort 7777 --saveUrl hdfs://hadoop102:8020/rng/ck --sorceUserName root --sorceUserPwd 123456 --sorceBase sys --sourceTable clicks
 */
public class PortToMysql {
    public static void main(String[] args) throws Exception {
        //参数获取
        /** @Param
         * --sorceIp:  mysql ip
         * --sorcePort: 3306
         * --destUrl: Port URL
         * --sorceUserName mysql username
         * --sorceUserPwd: mysql password
         * --sorceBase: mysql BaseName
         * --saveUrl： hdfs fileSystem savepoint URL
         * --destTopic: kafka topic
         */

        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        ParameterHelper parameterHelper = new ParameterHelper(parameterTool);


        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 1.1 设置CK&状态后端
        env.setStateBackend(new FsStateBackend(parameterHelper.getSaveUrl()+"/tmplate"));
        env.enableCheckpointing(5000L);
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        env.getCheckpointConfig().setCheckpointTimeout(10000L);
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(2);
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(3000);

        //TODO Flink-Sink内部  参数传递-- mysql 库名，表名，
        Configuration conf = new Configuration();
        conf.setString("baseName",parameterHelper.getSorceBase());
        conf.setString("tableName",parameterHelper.getSourceTable());
        conf.setString("username",parameterHelper.getSorceUserName());
        conf.setString("password",parameterHelper.getSorceUserPwd());
        conf.setString("dburl",parameterHelper.getSorceIp()+":3306");
        env.getConfig().setGlobalJobParameters(conf);


        DataStream<String> stream = env.socketTextStream(parameterHelper.getDestUrl(),parameterHelper.getDestPort());

        stream.print();

        //从kafka数据源处理数据为String数组
        DataStream<String[]> sourceStream = stream.filter((FilterFunction<String>) value -> StringUtils.isNotBlank(value))
                .map((MapFunction<String, String[]>) value -> {
                    String[] args1 = value.split(","); //切割json
                    return args1;
                }).returns(new TypeHint<String[]>() {});


        sourceStream.addSink(new MysqlSink());
        env.execute("data to mysql start");
    }
}
