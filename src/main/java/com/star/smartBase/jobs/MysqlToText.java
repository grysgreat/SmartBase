package com.star.smartBase.jobs;

import com.alibaba.ververica.cdc.connectors.mysql.MySQLSource;
import com.alibaba.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.alibaba.ververica.cdc.debezium.DebeziumSourceFunction;
import com.star.smartBase.model.CustomerDeserialization;
import com.star.smartBase.utils.ParameterHelper;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;


/**
 * @author star
 * TODO 数据流 : mysql -> flink -> hdfs
 */

/**
 * TODO eg.   --sorceIp 192.168.10.1 --sorcePort 3306 --destUrl hdfs://hadoop102:8020/rng/out100.txt --saveUrl hdfs://hadoop102:8020/rng/ck --sorceUserName root --sorceUserPwd 123456 --sorceBase test --sourceTable clicks
 */
public class MysqlToText {

    public static void main(String[] args) throws Exception {
        //参数获取
        /** @Param
         * --sorceIp:  mysql ip
         * --sorcePort: 3306
         * --destUrl: where save URL
         * --saveUrl： hdfsfileSystem savepoint URL
         * --sorceUserName mysql username
         * --sorceUserPwd: mysql password
         * --sorceBase: mysql BaseName
         * --sourceTable: mysql TableName
         */
        ParameterTool parameterTool = ParameterTool.fromArgs(args);

        ParameterHelper parameterHelper = new ParameterHelper(parameterTool);

        System.out.println(parameterHelper);

        //1.获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        //1.1 开启CK并指定状态后端为FS    memory  fs  rocksdb
        env.setStateBackend(new FsStateBackend(parameterHelper.getSaveUrl()+"/tmplate"));
        env.enableCheckpointing(5000L);
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        env.getCheckpointConfig().setCheckpointTimeout(10000L);
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(2);
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(3000);

        //2.通过FlinkCDC构建SourceFunction并读取数据
        DebeziumSourceFunction<String> sourceFunction = MySQLSource.<String>builder()
                .hostname(parameterHelper.getSorceIp())
                .serverTimeZone("GMT")
                .port(parameterHelper.getSorcePort())
                .username(parameterHelper.getSorceUserName())
                .password(parameterHelper.getSorceUserPwd())
                .databaseList(parameterHelper.getSorceBase())
                .tableList(parameterHelper.getSorceBase()+"."+parameterHelper.getSourceTable())   //如果不添加该参数,则消费指定数据库中所有表的数据.如果指定,指定方式为db.table
                .deserializer(new CustomerDeserialization())
                .startupOptions(StartupOptions.initial())
                .build();
        DataStreamSource<String> streamSource = env.addSource(sourceFunction);

        //3.打印数据
        streamSource.print();
        streamSource.writeAsText(parameterHelper.getDestUrl());

        //4.启动任务
        env.execute("Flink-MysqlToText");

    }

}
