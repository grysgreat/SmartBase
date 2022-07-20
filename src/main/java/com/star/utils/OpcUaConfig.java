package com.star.utils;

import java.io.Serializable;

public class OpcUaConfig implements Serializable {
    //服务器地址
    private String serverUrl = "opc.tcp://127.0.0.1:49320";
    //用户名
    private String userName;
    //密码
    private String password;
    //是否为匿名登录，匿名登录则为true，反之为false
    private Boolean isAnonymous;
    //数据标识符
    private String id;

    public OpcUaConfig(String serverUrl, Boolean isAnonymous, String id) {
        this.serverUrl = serverUrl;
        this.isAnonymous = isAnonymous;
        this.id = id;
    }

    public OpcUaConfig(String serverUrl, String userName, String password, Boolean isAnonymous, String id) {
        this.serverUrl = serverUrl;
        this.userName = userName;
        this.password = password;
        this.isAnonymous = isAnonymous;
        this.id = id;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getAnonymous() {
        return isAnonymous;
    }

    public void setAnonymous(Boolean anonymous) {
        isAnonymous = anonymous;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}