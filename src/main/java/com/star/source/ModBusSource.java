package com.star.source;

import com.star.model.MyModBusSource;
import com.star.utils.ModbusConfig;
import lombok.Data;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
/**
 * @author star
 */
@Data
public class ModBusSource implements MySource<String>{
    private String ip;

    private int port;

    private ModbusConfig modbusConfig;

    private static String groupId = "KF-JSON";

    public ModBusSource(ModbusConfig modbusConfig) {
        this.modbusConfig = modbusConfig;
    }

    @Override
    public DataStreamSource<String> getSource(StreamExecutionEnvironment env){

        DataStreamSource<String> ModBus = env.addSource(new MyModBusSource(modbusConfig));
        return ModBus;
    }
}
