package com.star.Job;

import com.google.gson.Gson;
import com.star.model.FakeSinkFunc;
import com.star.model.MyOpcUaSource;
import com.star.sink.FrameSink;
import com.star.source.RtspSource;
import com.star.utils.OpcUaConfig;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;

import java.util.Map;

public class OpcUa {


    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment executionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();


        ParameterTool parameterTool = ParameterTool.fromArgs(args);

        String str =  parameterTool.get("config");

        str = str.replaceAll("[{]", "{\"");
        str = str.replaceAll(":", "\":\"");
        str = str.replaceAll(",", "\",\"");
        str = str.replaceAll("}", "\"}");
        str = str.replaceAll("}\",\"[{]", "},{");
        str = str.replaceAll("\"[{]", "{");
        str = str.replaceAll("}\"", "}");
        str = str.replaceAll("]\"", "]");
        str = str.replaceAll("\"\\[", "[");
        str = str.replaceAll("\":\"/", "[");
        str = str.replaceAll("!\":\"!", ":");


        str=str.replaceAll("maohao",":");

        System.out.println(str);
        OpcUaConfig opcUaConfig = new Gson().fromJson(str,OpcUaConfig.class);

        opcUaConfig.setServerUrl("opc.tcp://"+opcUaConfig.getServerUrl());



        System.out.println(opcUaConfig);

        MyOpcUaSource myOpcUaSource = new MyOpcUaSource(opcUaConfig);



        DataStreamSource<Map<String, DataValue>> mapDataStreamSource = executionEnvironment.addSource(myOpcUaSource);

        mapDataStreamSource.print();

        executionEnvironment.execute();
    }
}
