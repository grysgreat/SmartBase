package com.star.Job;

import com.star.sink.FrameSink;
import com.star.sink.VedioPlaySink;
import com.star.source.MySource;
import com.star.source.RtmpSource;
import com.star.source.RtspSource;
import com.star.source.TextSource;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.net.URL;
import java.net.URLClassLoader;

public class RtmpPics<T> {

    private String a;

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment executionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();

        ParameterTool parameterTool = ParameterTool.fromArgs(args);

        String sorceIp =  parameterTool.get("sorceIp");


        RtmpSource rtmpPics = new RtmpSource(sorceIp);

        DataStreamSource source=rtmpPics.getSource(executionEnvironment);

        source.addSink(new FrameSink("/pics/"));

        executionEnvironment.execute();
    }

}