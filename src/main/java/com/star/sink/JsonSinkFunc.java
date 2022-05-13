package com.star.sink;

import com.star.pools.MysqlConnPool;
import com.star.utils.MysqlTableUtil;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.*;


/**
 * 这是kafka->Mysql的sink函数类
 *
 * 从Mysql读取元数据，然后通过表名获取字段名和字段类型,然后补充sql，填充从json获取的值
 */


public class JsonSinkFunc extends RichSinkFunction<Map<String,Object>> implements Serializable {
    private static String drivername = "com.mysql.jdbc.Driver";
    private MysqlTableUtil mysqlTableUtil=new MysqlTableUtil();
    private PreparedStatement preparedStatement;
    private DriverManager driverManager;
    private Connection connection;
    private List<String> ColumnNames;
    private List<String> ColumnTypes;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
    }

    @Override
    public void close() throws Exception {
        super.close();
    }


    /**
     *
     * @param value
     * @throws Exception
     *
     * mysql配置：url:192.168.10.1:3306, baseName:test, tableName:clicks,userName:root,passWord:123456,
     */
    @Override
    public void invoke(Map<String,Object> value) throws Exception {
        String destName=(String) value.get("destName");

        //数据库util传参
        mysqlTableUtil.setURL("jdbc:mysql://"+value.get("url")+"/"+value.get("baseName")+"?serverTimezone=GMT&useUnicode=true&characterEncoding=utf8");
        mysqlTableUtil.setUSERNAME((String)value.get("userName"));
        mysqlTableUtil.setPASSWORD((String)value.get("passWord"));

        //使用单例模式建立连接池
        switch (destName){
            case "mysql":{
                String[] structData = ((ArrayList<String>) value.get("data")).toArray(new String[((ArrayList<String>) value.get("data")).size()]);

                MysqlConnPool pool = MysqlConnPool.INSTANCE;
                String name=value.get("url")+"_"+value.get("baseName")+"_"+value.get("tableName");
                Date nowTime = new Date(System.currentTimeMillis());


                System.out.println(nowTime);
                if(pool.getConn().containsKey(name)) {
                    Date lastTime = pool.getTimeStamps().get(name);
                    int dt = (int) ((nowTime.getTime() - lastTime.getTime()) / 1000);
                    if(dt>=600) {
                        driverManager.setLoginTimeout(600);
                        connection = driverManager.getConnection(
                                "jdbc:mysql://" + value.get("url") + "/" + value.get("baseName")+"?serverTimezone=GMT",
                                (String) value.get("userName"),
                                (String) value.get("passWord")
                        );
                        ColumnNames= mysqlTableUtil.getColumnNames((String)value.get("tableName"));
                        ColumnTypes= mysqlTableUtil.getColumnTypes((String)value.get("tableName"));
                        pool.setConn(name,connection,nowTime,ColumnNames,ColumnTypes);
                    } else {

                        connection=pool.getConn().get(name);
                        ColumnNames=pool.getColumnNames().get(name);
                        ColumnTypes=pool.getColumnTypes().get(name);
                    }

                } else {
                    driverManager.setLoginTimeout(600);
                    connection = driverManager.getConnection(
                            "jdbc:mysql://" + value.get("url") + "/" + value.get("baseName")+"?serverTimezone=GMT",
                            (String) value.get("userName"),
                            (String) value.get("passWord")
                    );
                    ColumnNames= mysqlTableUtil.getColumnNames((String)value.get("tableName"));
                    ColumnTypes= mysqlTableUtil.getColumnTypes((String)value.get("tableName"));
                    pool.setConn(name,connection,nowTime,ColumnNames,ColumnTypes);
                }
                Class.forName(drivername);
                String tupleName="(";
                String unKown="(";

                //拼接sql
                for (String columnName : ColumnNames) {
                    tupleName+=columnName+",";
                    unKown+="?"+",";
                }
                tupleName = tupleName.substring(0, tupleName.length()-1)+")";
                unKown = unKown.substring(0, unKown.length()-1)+")";

                String sql = "insert into "+(String)value.get("tableName")+tupleName+" values"+unKown; //拼接好的预执行sql

                this.preparedStatement = connection.prepareStatement(sql);

                for (int i = 0; i < structData.length; i++) {
                    System.out.println(structData[i]);
                    if(ColumnTypes.get(i)=="VARCHAR"){
                        this.preparedStatement.setString(i+1, structData[i]);
                    } else {
                        this.preparedStatement.setInt(i+1, Integer.parseInt(structData[i]));
                    }

                    // this.preparedStatement.setInt(i, value[i]);
                }

                this.preparedStatement.executeUpdate();

                break;
            }
            case "kafka": {
                Date nowTime = new Date(System.currentTimeMillis());

                System.out.println(nowTime);


                String streamData = (String) value.get("sdata");
                //获取配置信息
                String url=(String) value.get("url");
                String topic=(String) value.get("topic");

                //创建kafka执行程序
                Properties prop = new Properties();
                prop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,url);
                prop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
                prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
                KafkaProducer<String, String> producer = new KafkaProducer<>(prop);
                ProducerRecord<String, String> record = new ProducerRecord<>(topic, streamData);
                producer.send(record);
                System.out.println("successKafka");
                producer.close();
                break;
            }
            default: break;
        }

    }



}