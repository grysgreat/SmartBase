package com.star.source;

import com.star.model.MyRtmpSource;
import com.star.model.MyRtspSource;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.bytedeco.javacv.Frame;

public class RtmpSource implements MySource<Frame>{
    private String RtmpUrl = "rtmp://ns8.indexforce.com/home/mystream";

    public RtmpSource(String rtmpUrl) {
        RtmpUrl = rtmpUrl;
    }

    @Override
    public DataStreamSource<Frame> getSource(StreamExecutionEnvironment env){

        DataStreamSource<Frame> source = env.addSource(new MyRtmpSource(RtmpUrl));
        return source;
    }
}
