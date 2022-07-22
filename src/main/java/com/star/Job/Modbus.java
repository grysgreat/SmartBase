package com.star.Job;

import com.google.gson.Gson;
import com.star.sink.FrameSink;
import com.star.source.ModBusSource;
import com.star.source.RtmpSource;
import com.star.utils.ModbusConfig;
import com.star.utils.ParameterHelper;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class Modbus {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment executionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();

        ParameterTool parameterTool = ParameterTool.fromArgs(args);

        String sorceIp =  parameterTool.get("sorceIp");

        Integer port = Integer.parseInt(parameterTool.get("sorcePort"));

        String mdConfig =  parameterTool.get("config");

        ModbusConfig modbusConfig = new Gson().fromJson(mdConfig,ModbusConfig.class);



        ModBusSource modBusSource = new ModBusSource(modbusConfig);

        DataStreamSource<String> source = modBusSource.getSource(executionEnvironment);

        source.print();

        executionEnvironment.execute();
    }
}
