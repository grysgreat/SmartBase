package com.star.Job;

import com.star.sink.FrameSink;
import com.star.sink.VedioPlaySink;
import com.star.source.MySource;
import com.star.source.RtmpSource;
import com.star.source.RtspSource;
import com.star.source.TextSource;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.net.URL;
import java.net.URLClassLoader;

public class Test2<T> {

    private String a;

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment executionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
        RtspSource rtspSource = new RtspSource("rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mp4");
        DataStreamSource source=rtspSource.getSource(executionEnvironment);

        source.addSink(new FrameSink("/pics"));

        executionEnvironment.execute();
    }

}