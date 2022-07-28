package com.star.Job;

import com.google.gson.Gson;
import com.star.model.FakeSinkFunc;
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


        ModbusConfig modbusConfig = new Gson().fromJson(str,ModbusConfig.class);



        ModBusSource modBusSource = new ModBusSource(modbusConfig);

        DataStreamSource<String> source = modBusSource.getSource(executionEnvironment);

        source.print();

        executionEnvironment.execute();
    }
}
