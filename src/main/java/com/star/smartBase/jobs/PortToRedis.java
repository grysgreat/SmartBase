package com.star.smartBase.jobs;

import com.star.smartBase.sinkSourceFunctions.MysqlSink;
import com.star.smartBase.utils.KafkaProducer;
import com.star.smartBase.utils.ParameterHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.redis.RedisSink;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommand;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommandDescription;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisMapper;


import java.util.Properties;


//TODO 数据流： 自定义socket输出源 -> flink(String):sink -> redis

//可以自定义输入数据格式(暂时不行)

/**
 * TODO eg.  --sorceIp hadoop102 --sorcePort 7777 --destUrl hadoop102 --destPort 6379 --destTopic clicks8 --saveUrl hdfs://hadoop102:8020/rng/ck
 */
public class PortToRedis {

    public static String topic;
    public static void main(String[] args) throws Exception {
        //参数获取
        /** @Param
         * --sorceIp:  Port ip
         * --sorcePort: Port
         * --destUrl: redis URL
         * --destPort redis port
         * --saveUrl： hdfs fileSystem savepoint URL
         * --destTopic: redis key field
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

        topic=parameterHelper.getDestTopic();


        DataStream<String> stream = env.socketTextStream(parameterHelper.getSorceIp(),parameterHelper.getSorcePort());


        DataStream<Tuple2<String,String>> sourceStream = stream.filter((FilterFunction<String>) value -> StringUtils.isNotBlank(value))
                .map((MapFunction<String, Tuple2<String,String>>) value -> {
                    String[] args1 = value.split(","); //切割json
                    Tuple2<String, String> tp = new Tuple2<String, String>(args1[0],args1[1]);
                    return tp;
                }).returns(new TypeHint<Tuple2<String,String>>() {});


        sourceStream.print();

        // 创建一个到redis连接的配置
        FlinkJedisPoolConfig conf = new FlinkJedisPoolConfig.Builder().setHost(parameterHelper.getDestUrl()).setPort(parameterHelper.getDestPort()).build();

        sourceStream.addSink(new RedisSink<Tuple2<String,String>>(conf, new MyRedisMapper()));

        env.execute();

    }
    public static class MyRedisMapper implements RedisMapper<Tuple2<String,String>> {
        @Override
        public String getKeyFromData(Tuple2<String,String> e) {
            return e.f0;
        }

        @Override
        public String getValueFromData(Tuple2<String,String> e) {
            System.out.println(e.f0+":"+e.f1);
            return e.f1;
        }

        @Override
        public RedisCommandDescription getCommandDescription() {
            return new RedisCommandDescription(RedisCommand.HSET, topic);
        }

    }
}
