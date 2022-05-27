package com.star.Job;

import com.star.instance.MyRedisRecord;
import com.star.source.*;
import com.star.utils.ParameterHelper;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class GetMySource<T> {
    private DataStreamSource<String> source;
    private DataStreamSource<T> spSource;

    private DataStreamSource<T> getSpSource(ParameterHelper para, StreamExecutionEnvironment env) throws Exception {



        return spSource;
    }


    public DataStreamSource<String> getSource(ParameterHelper para, StreamExecutionEnvironment env) throws Exception {
        switch (para.getSorceType()){
            case "mysql":{
                MysqlSource mysqlSource = new MysqlSource(
                        para.getSorceIp(),
                        para.getSorcePort(),
                        para.getSorceUserName(),
                        para.getSorceUserPwd(),
                        para.getSorceBase(),
                        para.getSourceTable(),
                        para.getDestType()
                );
                source = mysqlSource.getSource(env);
                break;
            }
            case "kafka":{

                KafkaSource kafkaSource = new KafkaSource(para.getSorceIp(),para.getSourceTable(),para.getSorcePort()+"");
                source=kafkaSource.getSource(env);
                break;
            }
            case "port":{
                PortSource portSource = new PortSource(para.getSorceIp(),para.getSorcePort());
                source=portSource.getSource(env);
                break;
            }
            case "redis":{
                RedisSource redisSource = new RedisSource(para.getSorceIp(),para.getSorcePort(),para.getSourceTable());
                source=redisSource.getSource(env);
                break;
            }
            case "text":{
                TextSource textSource = new TextSource(para.getSorceIp());
                source=textSource.getSource(env);
                break;
            }
            case "hbase":{
                //TODO family未确定
                HbaseSource hbaseSource = new HbaseSource(para.getSorceIp(),para.getSorcePort()+"",para.getSorceBase(),para.getSourceTable(),"family");
                source=hbaseSource.getSource(env);
                break;
            }
        }
        return source;
    }


}
