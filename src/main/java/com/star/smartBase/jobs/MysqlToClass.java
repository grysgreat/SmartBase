package com.star.smartBase.jobs;

import com.star.smartBase.utils.MySQLGeneratorEntityUtil;
import com.star.smartBase.utils.ParameterHelper;
import org.apache.flink.api.java.utils.ParameterTool;

/**
 * @author star
 * TODO 数据流 : mysql -> flink -> javaclass
 */

/**
 * TODO eg.  --sorceIp 192.168.10.1 --sorcePort 3306 --destUrl E://OutPut  --sorceUserName root --sorceUserPwd 123456 --sorceBase test --sourceTable clicks --pakage hello
 */
public class MysqlToClass {
    public static void main(String[] args) {
        //参数获取
        /** @Param
         * --sorceIp:  mysql ip
         * --destUrl: where to save URL
         * --sorceUserName mysql username
         * --sorceUserPwd: mysql password
         * --sorceBase: mysql BaseName
         * --sourceTable: mysql table(如果不填此参数表示获取所有表的class)
         * --pakage： class包名
         */


        //参数导入
        ParameterTool parameterTool = ParameterTool.fromArgs(args);

        ParameterHelper parameterHelper = new ParameterHelper(parameterTool);

        MySQLGeneratorEntityUtil instance =
                new MySQLGeneratorEntityUtil(
                        parameterHelper.getSorceIp(),
                        parameterHelper.getSorceUserName(),
                        parameterHelper.getSorceUserPwd(),
                        parameterHelper.getSorceBase(),
                        parameterHelper.getPakage(),
                        parameterHelper.getSourceTable(),
                        parameterHelper.getDestUrl()
                );

        //instance.basePath=""; //指定生成的位置,默认是当前工程
        try {
            instance.generate();
            System.out.println("generate Entity to classes successful!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
