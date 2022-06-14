package com.star.source;

import com.star.utils.Student;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;

import java.io.Serializable;
import java.net.URL;
import java.net.URLClassLoader;

public class PhotoSource implements MySource, Serializable {

    @Override
    public DataStreamSource<Student> getSource(StreamExecutionEnvironment env) throws Exception {

        String ip = "file:E:/tmp/FlexModel/target/NewOpTest-1.0-SNAPSHOT.jar&com.photo.MySourceFunc";
        String[] args1 = ip.split("&"); //切割json

        URLClassLoader urlClassLoader =null;
        Class<?> MyTest = null;
        //通过URLClassLoader加载外部jar
        //hdfs://hadoop102:8020/jars/  file://E:/tmp/NewOpTest/target/"+args1[0]
        //urlClassLoader = new URLClassLoader(new URL[]{new URL("hdfs:hadoop102:8020/jars/"+args1[0])});
        System.out.println(args1[1]);
        urlClassLoader = new URLClassLoader(new URL[]{new URL(args1[0])});

        //获取外部jar里面的具体类对象
        MyTest = urlClassLoader.loadClass(args1[1]);

        //创建对象实例


        // ToDo 1.source
        DataStreamSource<Student> source = env.addSource((RichSourceFunction<Student>)MyTest.newInstance());

        return source;
    }


    public static void main(String[] args) throws Exception {

        // ToDo 0.env
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // ToDo 1.source
        DataStream<Student> dataInput = new PhotoSource().getSource(env);


        dataInput.print();

        // ToDo 4.execute
        env.execute();
    }



}
