package com.star.source;

import com.star.instance.MyRedisCommand;
import com.star.instance.MyRedisCommandDescription;
import com.star.model.MyRedisSource;
import com.star.model.MyRtspSource;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;
import org.bytedeco.javacv.Frame;

public class RtspSource implements MySource<Frame>{
    private String RtmpUrl = "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mp4";;

    public RtspSource(String rtmpUrl) {
        RtmpUrl = rtmpUrl;
    }

    @Override
    public DataStreamSource<Frame> getSource(StreamExecutionEnvironment env){
        DataStreamSource<Frame> source = env.addSource(new MyRtspSource(RtmpUrl));
        return source;
    }
}
