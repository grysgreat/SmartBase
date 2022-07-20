package com.star.model;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;

public class MyRtmpSource extends RichSourceFunction<Frame> {
    //rtmp视频流的URL
    private String RtmpUrl = "rtmp://ns8.indexforce.com/home/mystream";
    private static FFmpegFrameGrabber fg = null;

    boolean running =true;

    public MyRtmpSource(String rtmpUrl) {
        RtmpUrl = rtmpUrl;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        fg = new FFmpegFrameGrabber(RtmpUrl);
        fg.start();
    }

    @Override
    public void run(SourceContext<Frame> sourceContext) throws Exception {
        while(running){
            Frame frame = fg.grab();
            sourceContext.collect(frame);
        }
    }

    @Override
    public void cancel() {
        running = false;
    }
}