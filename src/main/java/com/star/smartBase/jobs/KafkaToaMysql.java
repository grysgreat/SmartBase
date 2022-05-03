package com.star.smartBase.jobs;

import com.star.smartBase.sinkFunctions.MysqlSink;
import com.star.smartBase.sinkFunctions.testSink;
import com.star.smartBase.utils.ParameterHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import java.util.Properties;

//TODO 数据流： kafka（json） -> flink(List):mysqlUtil,sink -> Mysql

/**
 * TODO eg.  --sorceIp 192.168.10.1 --destUrl hadoop102:9092 --saveUrl hdfs://hadoop102:8020/rng/ck --sorceUserName root --sorceUserPwd 123456 --sorceBase test --destTopic test --sourceTable clicks
 */
public class KafkaToaMysql {

    public static void main(String[] args) throws Exception {

        //参数获取
        /** @Param
         * --sorceIp:  mysql ip
         * --sorceUserName mysql username
         * --sorceUserPwd: mysql password
         * --sorceBase: mysql BaseName
         * --sourceTable: mysql table(如果不填此参数表示获取所有表的class)
         * --destUrl: kafka URL
         * --destTopic: kafka topic
         * --saveUrl： hdfs fileSystem savepoint URL
         */

        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        ParameterHelper parameterHelper = new ParameterHelper(parameterTool);


        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", parameterHelper.getDestUrl());

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


        // 1,abc,100  类似这样的数据，当然也可以是很复杂的json数据，去做解析
        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>(parameterHelper.getDestTopic(), new SimpleStringSchema(), properties);

        env.getConfig().setRestartStrategy(
                RestartStrategies.fixedDelayRestart(5, 5000));
        env.enableCheckpointing(2000);
        DataStream<String> stream = env
                .addSource(consumer);


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