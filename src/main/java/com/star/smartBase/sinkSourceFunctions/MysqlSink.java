package com.star.smartBase.sinkSourceFunctions;

import com.star.smartBase.utils.MysqlTableUtil;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;


import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.List;


/**
 * 这是kafka->Mysql的sink函数类
 *
 * 从Mysql读取元数据，然后通过表名获取字段名和字段类型,然后补充sql，填充从json获取的值
 */
public class MysqlSink extends
        RichSinkFunction<String[]> implements Serializable {
    private Connection connection;
    private PreparedStatement preparedStatement;
    private MysqlTableUtil mysqlTableUtil=new MysqlTableUtil();
    private String username = "root";
    private String password ;
    private static String drivername = "com.mysql.jdbc.Driver";   //配置改成自己的配置
    private String dburl = "jdbc:mysql://localhost:3306";
    private String tableName = "clicks";
    private String baseName = "test1";

    private List<String> ColumnNames;
    private List<String> ColumnTypes;
    public MysqlSink() {
    }


    
    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        //获取全局变量 也就是
        ExecutionConfig.GlobalJobParameters globalParams = getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        Configuration globConf = (Configuration) globalParams;

        //获取数据库配置
        username=globConf.getString("username", null);
        password=globConf.getString("password", null);
        dburl=globConf.getString("dburl", null);
        tableName=globConf.getString("tableName", null);
        baseName=globConf.getString("baseName", null);

        //数据库util传参
        mysqlTableUtil.setURL("jdbc:mysql://"+dburl+"/"+baseName+"?useUnicode=true&characterEncoding=utf8");
        mysqlTableUtil.setUSERNAME(username);
        mysqlTableUtil.setPASSWORD(password);

        //获取表名对应的字段名和字段类型
        ColumnNames = mysqlTableUtil.getColumnNames(tableName);
        ColumnTypes = mysqlTableUtil.getColumnTypes(tableName);

        //初始化jdbc
        Class.forName(drivername);
        connection = DriverManager.getConnection("jdbc:mysql://"+dburl+"/"+baseName, username, password);
    }


    @Override
    public void invoke(String[] value) throws Exception {
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

        String sql = "insert into "+tableName+tupleName+" values"+unKown; //拼接好的预执行sql

        this.preparedStatement = this.connection.prepareStatement(sql);

        System.out.println(ColumnTypes);


        for (int i = 0; i < value.length; i++) {

            if(ColumnTypes.get(i)=="VARCHAR"){
                this.preparedStatement.setString(i+1, value[i]);
            } else {
                this.preparedStatement.setInt(i+1, Integer.parseInt(value[i]));
            }

            // this.preparedStatement.setInt(i, value[i]);
        }

        this.preparedStatement.executeUpdate();

    }

    @Override
    public void close() throws Exception {
        if (this.preparedStatement != null) {
            this.preparedStatement.close();
        }
        if (this.connection != null) {
            this.connection.close();
        }
        super.close();
    }
}