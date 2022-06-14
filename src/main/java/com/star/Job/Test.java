package com.star.Job;

import com.star.source.MySource;
import com.star.source.TextSource;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.net.URL;
import java.net.URLClassLoader;

public class Test<T> {

 private String a;

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment executionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
        TextSource textSource = new TextSource("hdfs://hadoop102:8020/README.md");
        DataStreamSource source=textSource.getSource(executionEnvironment);
        source.print();
        source.writeAsText("hdfs://hadoop102:8020/pics/kk2.txt").setParallelism(1);
        executionEnvironment.execute();
    }

}