package com.star.smartBase.Interface;

import org.checkerframework.checker.units.qual.C;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public enum MysqlConnPool {
    INSTANCE;
    private Map<String, Connection> conn = new HashMap<String, Connection>();
    private Map<String, Date> timeStamps = new HashMap<String, Date>();

    public Map<String, Date> getTimeStamps() {
        return timeStamps;
    }

    public void setTimeStamps(Map<String, Date> timeStamps) {
        this.timeStamps = timeStamps;
    }

    public void setConn(String name, Connection Newcon,Date time) {
        conn.put(name,Newcon);
        timeStamps.put(name,time);
    }

    public Map<String, Connection> getInstance() {
        return conn;
    }

}