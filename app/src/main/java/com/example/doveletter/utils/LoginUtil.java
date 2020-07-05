package com.example.doveletter.utils;


import com.example.doveletter.app.MyApplication;

import java.util.Properties;

import javax.mail.Session;
import javax.mail.Transport;

public class LoginUtil {
    public static void  isLoginRight(final String user, final String pwd) throws Exception{
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp"); // 使用的协议（JavaMail规范要求）
        props.setProperty("mail.smtp.host", MyApplication.userInfo.getHost()); // 发件人的邮箱的 SMTP服务器地址
        props.setProperty("mail.smtp.auth", "true"); // 请求认证，参数名称与具体实现有关
        Session session = Session.getInstance(props);// 创建Session实例对象
        Transport transport = session.getTransport("smtp");// 获取Transport对象
        transport.connect(user,pwd);// 第一个参数是发件人邮箱，第2个参数需要填写的是QQ邮箱的SMTP的授权码
        transport.close();//关闭网络连接
    }
}
