package com.star.model;


import com.star.instance.MyRedisCommand;
import com.star.instance.MyRedisCommandDescription;
import com.star.instance.MyRedisCommandsContainer;
import com.star.instance.MyRedisRecord;
import com.star.utils.MyRedisCommandsContainerBuilder;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisConfigBase;
import org.apache.flink.util.Preconditions;

public class MyRedisSource extends RichSourceFunction<String>{

    private static final long serialVersionUID = 1L;
    private String additionalKey;
    private MyRedisCommand redisCommand;
    private FlinkJedisConfigBase flinkJedisConfigBase;
    private MyRedisCommandsContainer redisCommandsContainer;
    private volatile boolean isRunning = true;

    public MyRedisSource(FlinkJedisConfigBase flinkJedisConfigBase, MyRedisCommandDescription redisCommandDescription) {
        Preconditions.checkNotNull(flinkJedisConfigBase, "Redis connection pool config should not be null");
        Preconditions.checkNotNull(redisCommandDescription, "MyRedisCommandDescription  can not be null");
        this.flinkJedisConfigBase = flinkJedisConfigBase;
        this.redisCommand = redisCommandDescription.getCommand();
        this.additionalKey = redisCommandDescription.getAdditionalKey();
    }


    @Override
    public void open(Configuration parameters) throws Exception {
        this.redisCommandsContainer = MyRedisCommandsContainerBuilder.build(this.flinkJedisConfigBase);
    }

    @Override
    public void run(SourceContext sourceContext) throws Exception {
            switch(this.redisCommand) {
                case HGET:
                    sourceContext.collect(new MyRedisRecord(this.redisCommandsContainer.hget(this.additionalKey), this.redisCommand.getRedisDataType()).toString());
                    break;
                default:
                    throw new IllegalArgumentException("Cannot process such data type: " + this.redisCommand);
            }
    }

    @Override
    public void close() throws Exception {
//        super.close();
    }

    @Override
    public void cancel()  {
        isRunning = false;
        if (this.redisCommandsContainer != null) {
            this.redisCommandsContainer.close();
        }
    }
}
