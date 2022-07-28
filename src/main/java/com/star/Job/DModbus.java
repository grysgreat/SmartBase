package com.star.Job;

import com.google.gson.Gson;
import com.star.source.ModBusSource;
import com.star.utils.ModbusConfig;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class DModbus {


    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment executionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();

        ModbusConfig modbusConfig = new ModbusConfig(702,1,"172.31.0.22","[{\"type\":1,\"slave_id\":1,\"offset\":0,\"datatype\":8},{\"type\":1,\"slave_id\":1,\"offset\":1,\"datatype\":8},{\"type\":1,\"slave_id\":1,\"offset\":6,\"datatype\":8},{\"type\":2,\"slave_id\":1,\"offset\":0,\"datatype\":8},{\"type\":2,\"slave_id\":1,\"offset\":1,\"datatype\":8},{\"type\":2,\"slave_id\":1,\"offset\":2,\"datatype\":8},{\"type\":3,\"slave_id\":1,\"offset\":1,\"datatype\":8},{\"type\":3,\"slave_id\":1,\"offset\":3,\"datatype\":8},{\"type\":4,\"slave_id\":1,\"offset\":0,\"datatype\":8},{\"type\":4,\"slave_id\":1,\"offset\":2,\"datatype\":8}]","modbus");


        ModBusSource modBusSource = new ModBusSource(modbusConfig);

        DataStreamSource<String> source = modBusSource.getSource(executionEnvironment);

        source.print();

        executionEnvironment.execute();
    }
}
