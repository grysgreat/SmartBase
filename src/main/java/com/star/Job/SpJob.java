package com.star.Job;

import com.star.instance.MyOprator;
import com.star.instance.OpratorsPram;
import com.star.opretors.OperatorController;
import com.star.sink.MySink;
import com.star.source.MySource;
import com.star.utils.ParameterHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import com.star.sink.MySink;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

public class SpJob<T> {

    public void dispose(StreamExecutionEnvironment env, ParameterHelper jobPram) throws Exception {
        String[] args1 = jobPram.getSorceIp().split("&"); //切割json

        URLClassLoader urlClassLoader =null;
        Class<?> MyTest = null;
        //通过URLClassLoader加载外部jar
        //hdfs://hadoop102:8020/jars/  file://E:/tmp/NewOpTest/target/"+args1[0]
        //urlClassLoader = new URLClassLoader(new URL[]{new URL("hdfs:hadoop102:8020/jars/"+args1[0])});
        urlClassLoader = new URLClassLoader(new URL[]{new URL(args1[0])});
        //获取外部jar里面的具体类对象
        MyTest = urlClassLoader.loadClass(args1[1]);
        //创建对象实例
        MySource<T> instance = (MySource<T>)MyTest.newInstance();


        DataStreamSource<T> streamIn = instance.getSource(env);
        streamIn.print();


        SingleOutputStreamOperator<T> stream = streamIn.filter((FilterFunction<T>) value -> StringUtils.isNotBlank(value.toString()));



        List<OpratorsPram> opList = jobPram.getOpList();

        for (OpratorsPram opratorsPram : opList) {
            OperatorController operatorController = new OperatorController();
            operatorController.setNowOp(opratorsPram);
            MyOprator op = operatorController.getOp();
            stream=op.getOpOut(stream);
        }

        String[] args2 = jobPram.getDestUrl().split("&"); //切割json

        URLClassLoader urlClassLoader2 =null;
        Class<?> MySink = null;
        //通过URLClassLoader加载外部jar
        //hdfs://hadoop102:8020/jars/  file://E:/tmp/NewOpTest/target/"+args1[0]

        //urlClassLoader2 = new URLClassLoader(new URL[]{new URL("hdfs:hadoop102:8020/jars/"+args2[0])});
        urlClassLoader2 = new URLClassLoader(new URL[]{new URL(args2[0])});
        //获取外部jar里面的具体类对象
        MySink = urlClassLoader2.loadClass(args2[1]);
        //创建对象实例
        MySink<T> sinkClass = (MySink<T>)MySink.newInstance();

        sinkClass.addMySink(env,stream);

        stream.print();

    }
}
