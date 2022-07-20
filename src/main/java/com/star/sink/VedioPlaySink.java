package com.star.sink;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;

import javax.swing.*;

public class VedioPlaySink extends RichSinkFunction<Frame> {

    private static CanvasFrame canvasFrame = new CanvasFrame("");

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        canvasFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        canvasFrame.setAlwaysOnTop(true);
    }

    @Override
    public void invoke(Frame value, Context context) throws Exception {
        super.invoke(value, context);

        canvasFrame.showImage(value);
    }
}