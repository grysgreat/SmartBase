package com.star.sink;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FrameSink extends RichSinkFunction<Frame> {

    private String url;

    public FrameSink(String url) {
        this.url = url;
    }

    static long cnt = 0;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
    }

    @Override
    public void invoke(Frame value, Context context) throws Exception {
        super.invoke(value, context);

        //保存图片
        //设置保存图片路径
        File outPut = new File(url+cnt+".jpg");
        if (value != null&&value.image!=null) {
            try {
                ImageIO.write(FrameToBufferedImage(value), "jpg", outPut);
            } catch (IOException e) {
                e.printStackTrace();
            }
            cnt++;
            //设置时间
            Thread.sleep(3000);
        }
    }

    /**
     * 创建BufferedImage对象
     */
    private BufferedImage FrameToBufferedImage(Frame frame) {
        Java2DFrameConverter converter = new Java2DFrameConverter();
        return converter.getBufferedImage(frame);
    }
}