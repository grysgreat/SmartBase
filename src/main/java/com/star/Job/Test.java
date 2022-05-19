package com.star.Job;

import com.star.instance.MyOprator;
import com.star.instance.OpratorsPram;
import com.star.opretors.OperatorController;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.net.MalformedURLException;



public class Test {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<String> stream = env.socketTextStream("hadoop102",7777);

        SingleOutputStreamOperator<String> streamPre = stream.filter((FilterFunction<String>) value -> StringUtils.isNotBlank(value));


        OpratorsPram opP = new OpratorsPram("OpNew", "file:F:/tmp/smart-base2/BaseHub/target/BaseHub-1.0-SNAPSHOT-jar-with-dependencies.jar&com.star.opretors.transforms.OpCount");
        OperatorController operatorController = new OperatorController();
        operatorController.setNowOp(opP);
        MyOprator op = operatorController.getOp();

        streamPre=op.getOpOut(streamPre);
        streamPre.print();
        env.execute();
    }
}