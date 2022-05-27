package com.star.Job;

import com.star.instance.MyOprator;
import com.star.instance.OpratorsPram;
import com.star.opretors.OperatorController;
import com.star.utils.JobPramUtil;
import com.star.utils.ParameterHelper;
import com.star.utils.TestUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;


public class Test {

 private String a;

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment executionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();

        ParameterHelper parameterHelper = new ParameterHelper();
        parameterHelper.setSorceIp("file:E:/tmp/NewOpTest/target/OPnew.jar&com.TestSource");
        parameterHelper.setDestUrl("file:E:/tmp/NewOpTest/target/OPnew.jar&com.TestSink");

        List<OpratorsPram> opList = new ArrayList<>();
        parameterHelper.setOpList(opList);

        SpJob<Object> objectSpJob = new SpJob<>();
        objectSpJob.dispose(executionEnvironment,parameterHelper);

        executionEnvironment.execute();
    }

}