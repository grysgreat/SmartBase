package com.star.smartBase.jobs;

import com.star.smartBase.model.MyMapRedisRecordSplitter;
import com.star.smartBase.sinkSourceFunctions.RedisSource;
import com.star.smartBase.utils.MyRedisCommand;
import com.star.smartBase.utils.MyRedisCommandDescription;
import com.star.smartBase.utils.MyRedisRecord;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;


//TODO redis带参读入和聚合运算
public class RedisCount{
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment executionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();

        executionEnvironment.setParallelism(1);
        FlinkJedisPoolConfig conf = new FlinkJedisPoolConfig.Builder().setHost("hadoop102").setPort(6379).build();
        DataStreamSource<MyRedisRecord> source = executionEnvironment.addSource(new RedisSource(conf,new MyRedisCommandDescription(MyRedisCommand.HGET,"click4"))).setParallelism(1);
        source.print().setParallelism(1);
        source.writeAsText("./redisout.txt");
        DataStream<Tuple2<String, Integer>> max = source.flatMap(new MyMapRedisRecordSplitter()).timeWindowAll(Time.milliseconds(5000)).maxBy(1);
        max.print().setParallelism(1);
        executionEnvironment.execute();
    }
}