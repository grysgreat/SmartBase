package com.star.Job;

import com.star.sink.FrameSink;
import com.star.sink.VedioPlaySink;
import com.star.source.MySource;
import com.star.source.RtmpSource;
import com.star.source.RtspSource;
import com.star.source.TextSource;
import com.star.utils.ParameterHelper;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.net.URL;
import java.net.URLClassLoader;
//rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mp4
public class RtspPics{

    private String a;

    public static void main(String[] args) throws Exception {

        ParameterTool parameterTool = ParameterTool.fromArgs(args);

        String sorceIp =  parameterTool.get("sorceIp");

        StreamExecutionEnvironment executionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
        RtspSource rtspSource = new RtspSource(sorceIp);
        DataStreamSource source=rtspSource.getSource(executionEnvironment);

        source.addSink(new FrameSink("/pics/"));

        executionEnvironment.execute();
    }

}