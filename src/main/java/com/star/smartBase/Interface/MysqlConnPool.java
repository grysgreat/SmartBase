package com.star.smartBase.Interface;

import org.checkerframework.checker.units.qual.C;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @param conn:JDBC连接池 ， timeStamps：时间戳， ColumnNames：数据路元数据列名， ColumnTypes：数据库元数据列类型
 */

public enum MysqlConnPool {
    INSTANCE;
    private Map<String, Connection> conn = new HashMap<String, Connection>();
    private Map<String, Date> timeStamps = new HashMap<String, Date>();
    private Map<String, List<String>> ColumnNames=new HashMap<String, List<String>>();
    private Map<String, List<String>> ColumnTypes=new HashMap<String, List<String>>();


    public Map<String, List<String>> getColumnNames() {
        return ColumnNames;
    }

    public Map<String, List<String>> getColumnTypes() {
        return ColumnTypes;
    }

    public Map<String, Date> getTimeStamps() {
        return timeStamps;
    }

    public void setConn(String name, Connection Newcon,Date time,List<String> Cname,List<String> Ctype) {
        conn.put(name,Newcon);
        timeStamps.put(name,time);
        ColumnNames.put(name,Cname);
        ColumnTypes.put(name,Ctype);

    }

    public Map<String, Connection> getConn() {
        return conn;
    }

}