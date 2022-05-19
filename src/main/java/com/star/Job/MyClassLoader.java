package com.star.Job;

import org.apache.commons.lang.StringUtils;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class MyClassLoader {

    public static void main(String[] args){

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<String> stream = env.socketTextStream("hadoop102",7777);

        SingleOutputStreamOperator<String> streamPre = stream.filter((FilterFunction<String>) value -> StringUtils.isNotBlank(value));

        //外部jar所在位置
        String path = "file:F:\\tmp\\smart-base2\\BaseHub\\target\\BaseHub-1.0-SNAPSHOT-jar-with-dependencies.jar";
        URLClassLoader urlClassLoader =null;
        Class<?> MyTest = null;
        try {
            //通过URLClassLoader加载外部jar
            urlClassLoader = new URLClassLoader(new URL[]{new URL(path)});
            //获取外部jar里面的具体类对象
            MyTest = urlClassLoader.loadClass("com.star.opretors.transforms.OpCount");
            //创建对象实例
            Object instance = MyTest.newInstance();
            //获取实例当中的方法名为show，参数只有一个且类型为string的public方法
            Method method = MyTest.getMethod("getOpOut", SingleOutputStreamOperator.class);
            //传入实例以及方法参数信息执行这个方法
            SingleOutputStreamOperator<String> ada = (SingleOutputStreamOperator)method.invoke(instance, streamPre);

            ada.print();
            System.out.println("123");
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //卸载关闭外部jar
            try {
                urlClassLoader.close();
            } catch (IOException e) {
                System.out.println("关闭外部jar失败："+e.getMessage());
            }
        }
    }
}