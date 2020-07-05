package com.example.doveletter.bean;

public class UserInfo {
    //用户的邮箱账号
    private String useraddress;
    //用户授权码
    private String userpassword;
    //邮箱服务器
    private String host;

    public String getUseraddress() {
        return useraddress;
    }

    public void setUseraddress(String useraddress) {
        this.useraddress = useraddress;
    }

    public String getUserpassword() {
        return userpassword;
    }

    public void setUserpassword(String userpassword) {
        this.userpassword = userpassword;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
