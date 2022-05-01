package com.star.smartBase.utils;

import org.apache.flink.api.java.utils.ParameterTool;

public class ParameterHelper {

    //数据源ip
    private String sorceIp;

    //存储url
    private String destUrl;

    //保存点url——为hdfs的url
    private String saveUrl;

    //数据源端口号
    private int sorcePort=0;

    //存储端口号
    private int destPort=0;

    //数据源用户账号
    private String sorceUserName;

    //存储用户账号
    private String destUserName;

    //数据源用户密码
    private String sorceUserPwd;

    //存储用户密码
    private String destUserPwd;

    //数据源库名
    private String sorceBase;

    //数据源表名
    private String sourceTable;

    //一般为kafka的topic名
    private String destTopic;

    public ParameterHelper() {
    }



    //参数传递函数
    //TODO 传入一个 parameterTool ，返回helper参数类
    public ParameterHelper(ParameterTool parameterTool){
        this.sorceIp = parameterTool.get("sorceIp");

        this.destUrl = parameterTool.get("destUrl");

        if(parameterTool.get("sorcePort")!=null)
            this.sorcePort = Integer.parseInt(parameterTool.get("sorcePort"));

        if(parameterTool.get("destPort")!=null)
            this.destPort = Integer.parseInt(parameterTool.get("destPort"));

        this.saveUrl=parameterTool.get("saveUrl");

        this.sorceUserName = parameterTool.get("sorceUserName");

        this.destUserName = parameterTool.get("destUserName");

        this.sorceUserPwd = parameterTool.get("sorceUserPwd");

        this.destUserPwd = parameterTool.get("destUserPwd");

        this.sorceBase = parameterTool.get("sorceBase");

        this.sourceTable = parameterTool.get("sourceTable");

        this.destTopic = parameterTool.get("destTopic");
    }

    @Override
    public String toString() {
        return "ParameterHelper{" +
                "sorceIp='" + sorceIp + '\'' +
                ", destUrl='" + destUrl + '\'' +
                ", sorceUserName='" + sorceUserName + '\'' +
                ", destUserName='" + destUserName + '\'' +
                ", sorceUserPwd='" + sorceUserPwd + '\'' +
                ", destUserPwd='" + destUserPwd + '\'' +
                ", sorceBase='" + sorceBase + '\'' +
                ", sourceTable='" + sourceTable + '\'' +
                '}';
    }

    public String getSaveUrl() {
        return saveUrl;
    }

    public void setSaveUrl(String saveUrl) {
        this.saveUrl = saveUrl;
    }

    public String getSorceIp() {
        return sorceIp;
    }

    public void setSorceIp(String sorceIp) {
        this.sorceIp = sorceIp;
    }

    public String getDestUrl() {
        return destUrl;
    }

    public void setDestUrl(String destUrl) {
        this.destUrl = destUrl;
    }

    public int getSorcePort() {
        return sorcePort;
    }

    public void setSorcePort(int sorcePort) {
        this.sorcePort = sorcePort;
    }

    public int getDestPort() {
        return destPort;
    }

    public void setDestPort(int destPort) {
        this.destPort = destPort;
    }

    public String getSorceUserName() {
        return sorceUserName;
    }

    public void setSorceUserName(String sorceUserName) {
        this.sorceUserName = sorceUserName;
    }

    public String getDestUserName() {
        return destUserName;
    }

    public void setDestUserName(String destUserName) {
        this.destUserName = destUserName;
    }

    public String getSorceUserPwd() {
        return sorceUserPwd;
    }

    public void setSorceUserPwd(String sorceUserPwd) {
        this.sorceUserPwd = sorceUserPwd;
    }

    public String getDestUserPwd() {
        return destUserPwd;
    }

    public void setDestUserPwd(String destUserPwd) {
        this.destUserPwd = destUserPwd;
    }

    public String getSorceBase() {
        return sorceBase;
    }

    public void setSorceBase(String sorceBase) {
        this.sorceBase = sorceBase;
    }

    public String getSourceTable() {
        return sourceTable;
    }

    public void setSourceTable(String sourceTable) {
        this.sourceTable = sourceTable;
    }

    public String getDestTopic() {
        return destTopic;
    }

    public void setDestTopic(String destTopic) {
        this.destTopic = destTopic;
    }
}