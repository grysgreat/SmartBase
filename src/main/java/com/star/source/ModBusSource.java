package com.star.source;

import com.star.model.MyModBusSource;
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
    private static String groupId = "KF-JSON";

    public ModBusSource(String url, int port) {
        this.ip = url;
        this.port = port;
    }

    @Override
    public DataStreamSource<String> getSource(StreamExecutionEnvironment env){

        DataStreamSource<String> ModBus = env.addSource(new MyModBusSource(ip,port));
        return ModBus;
    }
}
