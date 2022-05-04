package com.star.smartBase.jobs;

import com.star.smartBase.sinkSourceFunctions.RedisSource;
import com.star.smartBase.utils.MyRedisCommand;
import com.star.smartBase.utils.MyRedisCommandDescription;
import com.star.smartBase.utils.MyRedisRecord;
import com.star.smartBase.utils.ParameterHelper;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;
import org.apache.flink.util.Collector;

/**
 * @author star
 * TODO 数据流 : redis -> flink -> hdfs
 * TODO eg.  --sorceIp hadoop102 --destUrl E://tmp/outredis.txt --sourceTable click4
 */
public class test {
    public static void main(String[] args) throws Exception {
        //参数获取
        /** @Param
         * --sorceIp:  redis ip
         * --destUrl: where save URL
         * --sourceTable: redis key
         */
        ParameterTool parameterTool = ParameterTool.fromArgs(args);

        ParameterHelper parameterHelper = new ParameterHelper(parameterTool);


        StreamExecutionEnvironment executionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();

        executionEnvironment.setParallelism(1);
        FlinkJedisPoolConfig conf = new FlinkJedisPoolConfig.Builder().setHost(parameterHelper.getSorceIp()).setPort(6379).build();

        DataStreamSource<MyRedisRecord> source = executionEnvironment.addSource(new RedisSource(conf,new MyRedisCommandDescription(MyRedisCommand.HGET,parameterHelper.getSourceTable()))).setParallelism(1);

        SingleOutputStreamOperator<String> returns = source.flatMap((MyRedisRecord in, Collector<String> out) -> {
            out.collect(in.toString());
        }).returns(Types.STRING);

        returns.print();

        executionEnvironment.execute();

    }
}
