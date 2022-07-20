package com.star.Job.FrameJobs;

import com.star.sink.FrameSink;
import com.star.sink.VedioPlaySink;
import com.star.source.RtspSource;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class FramePlayJob {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment executionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
        RtspSource rtspSource = new RtspSource("rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mp4");
        DataStreamSource source=rtspSource.getSource(executionEnvironment);


        source.addSink(new VedioPlaySink());
        executionEnvironment.execute();
    }
}
