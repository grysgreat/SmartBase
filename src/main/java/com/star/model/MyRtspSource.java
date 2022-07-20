package com.star.model;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;

public class MyRtspSource extends RichSourceFunction<Frame> {
    //rtsp视频流的URL
    private String RtspUrl = "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mp4";
    private FFmpegFrameGrabber fg = null;

    boolean running =true;

    public MyRtspSource(String rtspUrl) {
        RtspUrl = rtspUrl;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        fg = new FFmpegFrameGrabber(RtspUrl);
        fg.setOption("rtsp_transport", "tcp");  //选择TCP，否则丢包会很严重
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