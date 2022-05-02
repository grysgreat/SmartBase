package com.star.smartBase.jobs;

import com.alibaba.ververica.cdc.connectors.mysql.MySQLSource;
import com.alibaba.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.alibaba.ververica.cdc.debezium.DebeziumSourceFunction;
import com.star.smartBase.model.CustomerDeserialization;
import com.star.smartBase.utils.KafkaProducer;
import com.star.smartBase.utils.ParameterHelper;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author star
 *
 * TODO 在使用kafka时，要在集群上开启zookeeper和kafka ——> zk.sh start | kf.sh start
 * TODO 数据流 : mysql -> flink -> kafka
 */

/**
 * TODO eg.  --sorceIp 192.168.10.1 --sorcePort 3306 --destUrl hadoop102:9092 --saveUrl hdfs://hadoop102:8020/rng/ck --sorceUserName root --sorceUserPwd 123456 --sorceBase test --destTopic kfMysql5
 */
public class MysqlToKafka {
    public static void main(String[] args) throws Exception {
        //参数获取
        /** @Param
         * --sorceIp:  mysql ip
         * --sorcePort: 3306
         * --destUrl: kafka URL
         * --sorceUserName mysql username
         * --sorceUserPwd: mysql password
         * --sorceBase: mysql BaseName
         * --saveUrl： hdfs fileSystem savepoint URL
         * --destTopic: kafka topic
         */
        ParameterTool parameterTool = ParameterTool.fromArgs(args);

        ParameterHelper parameterHelper = new ParameterHelper(parameterTool);


        //1.获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

       // 1.1 设置CK&状态后端
        env.setStateBackend(new FsStateBackend(parameterHelper.getSaveUrl()+"/tmplate"));
        env.enableCheckpointing(5000L);
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        env.getCheckpointConfig().setCheckpointTimeout(10000L);
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(2);
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(3000);

        //env.setRestartStrategy(RestartStrategies.fixedDelayRestart());

        //2.通过FlinkCDC构建SourceFunction并读取数据
        DebeziumSourceFunction<String> sourceFunction = MySQLSource.<String>builder()
                .hostname(parameterHelper.getSorceIp())
                .serverTimeZone("GMT")
                .port(parameterHelper.getSorcePort())
                .username(parameterHelper.getSorceUserName())
                .password(parameterHelper.getSorceUserPwd())
                .databaseList(parameterHelper.getSorceBase())
                .deserializer(new CustomerDeserialization())
                .startupOptions(StartupOptions.initial())
                .build();
        DataStreamSource<String> streamSource = env.addSource(sourceFunction);

        //3.打印数据并将数据写入Kafka
        streamSource.print();
        String sinkTopic = parameterHelper.getDestTopic();

        String destUrl = parameterHelper.getDestUrl();
        streamSource.addSink(KafkaProducer.getKafkaProducer(sinkTopic,destUrl));

        //4.启动任务
        env.execute("Flink-MysqlToKafka");
    }

}
