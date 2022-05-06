package com.star.smartBase.Interface;

import org.checkerframework.checker.units.qual.C;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public enum MysqlConnPool {
    INSTANCE;
    private Map<String, Connection> conn = new HashMap<String, Connection>();

    public void setConn(String name,Connection Newcon) {
        conn.put(name,Newcon);
    }

    public Map<String, Connection> getInstance() {
        return conn;
    }
}