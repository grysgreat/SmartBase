package com.star.smartBase.sinkFunctions;

import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class MysqlSink extends
        RichSinkFunction<Tuple3<Integer, String, Integer>> {
    private Connection connection;
    private PreparedStatement preparedStatement;
    String username = "root";
    String password = "123456";
    String drivername = "com.mysql.cj.jdbc.Driver";   //配置改成自己的配置
    String dburl = "jdbc:mysql://localhost:3306/test";
    String tableName = "test1";
    @Override
    public void invoke(Tuple3<Integer, String, Integer> value) throws Exception {
        Class.forName(drivername);
        connection = DriverManager.getConnection(dburl, username, password);
        String sql = "insert into "+tableName+"(id,num,price) values(?,?,?)"; //假设mysql 有3列 id,num,price
        this.preparedStatement = this.connection.prepareStatement(sql);
        this.preparedStatement.setInt(1, value.f0);
        this.preparedStatement.setString(2, value.f1);
        this.preparedStatement.setInt(3, value.f2);
        this.preparedStatement.executeUpdate();
        if (this.preparedStatement != null) {
            this.preparedStatement.close();
        }
        if (this.connection != null) {
            this.connection.close();
        }
    }
}