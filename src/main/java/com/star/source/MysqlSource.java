package com.star.source;

import com.alibaba.ververica.cdc.connectors.mysql.MySQLSource;
import com.alibaba.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.alibaba.ververica.cdc.debezium.DebeziumSourceFunction;
import com.star.model.CustomerDeserialization;
import com.star.model.MysqlRawDataDeserialization;
import lombok.Data;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

@Data
public class MysqlSource implements MySource<String>{
    private String URL;
    private int port;
    private String userName;
    private String passWord;
    private String BaseName;
    private String tableName;
    private DebeziumSourceFunction<String> sourceFunction;
    private String dest;

    public MysqlSource(String URL, int port, String userName, String passWord, String baseName, String tableName,String dest) {
        this.dest=dest;
        this.URL = URL;
        this.port = port;
        this.userName = userName;
        this.passWord = passWord;
        this.BaseName = baseName;
        this.tableName = tableName;
    }

    @Override
    public DataStreamSource<String> getSource(StreamExecutionEnvironment env) throws Exception {

        if(dest.equals("mysql")){
            sourceFunction = MySQLSource.<String>builder()
                    .hostname(URL)
                    .serverTimeZone("GMT")
                    .port(port)
                    .username(userName)
                    .password(passWord)
                    .databaseList(BaseName)
                    .tableList(BaseName+"."+tableName)
                    .deserializer(new MysqlRawDataDeserialization())
                    .startupOptions(StartupOptions.initial())
                    .build();
        } else{
            sourceFunction = MySQLSource.<String>builder()
                    .hostname(URL)
                    .serverTimeZone("GMT")
                    .port(port)
                    .username(userName)
                    .password(passWord)
                    .databaseList(BaseName)
                    .tableList(BaseName+"."+tableName)
                    .deserializer(new CustomerDeserialization())
                    .startupOptions(StartupOptions.initial())
                    .build();

        }




        DataStreamSource<String> streamSource = env.addSource(sourceFunction);
        return streamSource;
    }

}
