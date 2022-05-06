package com.star.smartBase.sinkSourceFunctions;

import com.star.smartBase.Interface.MysqlConnPool;
import com.star.smartBase.utils.DestObj;
import com.star.smartBase.utils.MysqlTableUtil;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;


import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 这是kafka->Mysql的sink函数类
 *
 * 从Mysql读取元数据，然后通过表名获取字段名和字段类型,然后补充sql，填充从json获取的值
 */


public class JsonSinkFunc extends RichSinkFunction<Map<String,Object>> implements Serializable {
    private static String drivername = "com.mysql.jdbc.Driver";
    private MysqlTableUtil mysqlTableUtil=new MysqlTableUtil();
    private PreparedStatement preparedStatement;

    private Connection connection;


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

        String[] data = ((ArrayList<String>) value.get("data")).toArray(new String[((ArrayList<String>) value.get("data")).size()]);


        //数据库util传参
        mysqlTableUtil.setURL("jdbc:mysql://"+value.get("url")+"/"+value.get("baseName")+"?useUnicode=true&characterEncoding=utf8");
        mysqlTableUtil.setUSERNAME((String)value.get("userName"));
        mysqlTableUtil.setPASSWORD((String)value.get("passWord"));

        List<String> ColumnNames= mysqlTableUtil.getColumnNames((String)value.get("tableName"));;
        List<String> ColumnTypes= mysqlTableUtil.getColumnTypes((String)value.get("tableName"));;


        //使用单例模式建立连接池
        switch (destName){
            case "mysql":{
                MysqlConnPool pool = MysqlConnPool.INSTANCE;
                String name=value.get("url")+"_"+value.get("baseName")+"_"+value.get("tableName");

                if(pool.getInstance().containsKey(name)) {
                    connection=pool.getInstance().get(name);
                } else {
                    connection = DriverManager.getConnection(
                            "jdbc:mysql://" + value.get("url") + "/" + value.get("baseName"),
                            (String) value.get("userName"),
                            (String) value.get("passWord")
                    );
                    pool.setConn(name,connection);
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

                System.out.println(ColumnTypes);


                for (int i = 0; i < data.length; i++) {
                    System.out.println(data[i]);
                    if(ColumnTypes.get(i)=="VARCHAR"){
                        this.preparedStatement.setString(i+1, data[i]);
                    } else {
                        this.preparedStatement.setInt(i+1, Integer.parseInt(data[i]));
                    }

                    // this.preparedStatement.setInt(i, value[i]);
                }

                this.preparedStatement.executeUpdate();
            };


            case "kafka": ;
        }

    }



}