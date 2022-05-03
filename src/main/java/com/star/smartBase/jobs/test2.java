package com.star.smartBase.jobs;

import com.star.smartBase.utils.MysqlTableUtil;



public class test2 {

    public static void main(String[] args) {
        MysqlTableUtil mysqlTableUtil = new MysqlTableUtil();
        String tableName="test1";
        //数据库util传参
        mysqlTableUtil.setURL("jdbc:mysql://"+"192.168.10.1:3306"+"/"+"test"+"?useUnicode=true&characterEncoding=utf8");
        mysqlTableUtil.setUSERNAME("root");
        mysqlTableUtil.setPASSWORD("123456");

        System.out.println("ColumnNames:" + mysqlTableUtil.getColumnNames(tableName));
        System.out.println("ColumnTypes:" + mysqlTableUtil.getColumnTypes(tableName));

    }
}
