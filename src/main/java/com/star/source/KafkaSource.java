package com.star.source;

import com.alibaba.ververica.cdc.connectors.mysql.MySQLSource;
import com.alibaba.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.alibaba.ververica.cdc.debezium.DebeziumSourceFunction;
import com.star.model.CustomerDeserialization;
import com.star.model.KafkaProducer;
import lombok.Data;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

@Data
public class KafkaSource implements MySource<String>{
    private String url;



    private String topic;
    private String port;
    private static String groupId = "KF-JSON";

    public KafkaSource(String url, String topic, String port) {
        this.url = url;
        this.topic = topic;
        this.port = port;
    }

    @Override
    public DataStreamSource<String> getSource(StreamExecutionEnvironment env){

        DataStreamSource<String> kafkaDS = env.addSource(KafkaProducer.getKafkaConsumer(topic, groupId,url+":"+port));
        return kafkaDS;
    }
}
