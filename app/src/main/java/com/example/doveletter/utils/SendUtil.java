package com.example.doveletter.utils;

import com.example.doveletter.app.MyApplication;

import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

public class SendUtil {
    private String userAddress=MyApplication.userInfo.getUseraddress();
    private String password=MyApplication.userInfo.getUserpassword();
    private String host=MyApplication.userInfo.getHost();


    public void SendEmail(String receiveAddress,String subject,String content,String filePath)throws Exception{
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp"); // 使用的协议（JavaMail规范要求）
        props.setProperty("mail.smtp.host", MyApplication.userInfo.getHost()); // 发件人的邮箱的 SMTP服务器地址
        props.setProperty("mail.smtp.auth", "true"); // 请求认证，参数名称与具体实现有关
        Session session = Session.getInstance(props);// 创建Session实例对象

        //3、创建邮件的实例对象
        Message msg;
        if (!filePath.isEmpty()) {   //带附件
            msg = getMimeMessageWithAttachment(receiveAddress, subject
                    , content, session, filePath);
        } else {                    //不带附件
            msg = getMimeMessage(receiveAddress, subject
                    , content, session);
        }

        //4、根据session对象获取邮件传输对象Transport
        Transport transport = session.getTransport("smtp");
        //设置发件人的账户名和密码
        transport.connect(host, userAddress, password);
        //发送邮件，并发送到所有收件人地址，message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
        transport.sendMessage(msg, msg.getAllRecipients());

        //5、关闭邮件连接
        transport.close();
    }

    /**
     * 获得创建一封邮件的实例对象
     */
    private MimeMessage getMimeMessage(String recipientAddress, String subject, String content
            , Session session) throws Exception {
        //创建一封邮件的实例对象
        MimeMessage msg = new MimeMessage(session);
        //设置发件人地址
        msg.setFrom(new InternetAddress(userAddress));
        /*
         * 设置收件人地址（可以增加多个收件人、抄送、密送），即下面这一行代码书写多行
         * MimeMessage.RecipientType.TO:发送
         * MimeMessage.RecipientType.CC：抄送
         * MimeMessage.RecipientType.BCC：密送
         */
        msg.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(recipientAddress));
        //设置邮件主题
        msg.setSubject(subject, "UTF-8");
        //设置邮件正文
        msg.setContent(content, "text/html;charset=UTF-8");
        //设置邮件的发送时间,默认立即发送
        msg.setSentDate(new Date());

        return msg;
    }

    /**
     * 获得创建一封邮件(可添加附件)的实例对象
     */
    public MimeMessage getMimeMessageWithAttachment(String receiveAddress, String subject
            , String content, Session session, String filePath) throws Exception {
        //1.创建一封邮件的实例对象
        MimeMessage msg = new MimeMessage(session);
        //2.设置发件人地址
        msg.setFrom(new InternetAddress(userAddress));
        /*
         * 3.设置收件人地址（可以增加多个收件人、抄送、密送），即下面这一行代码书写多行
         * MimeMessage.RecipientType.TO:发送
         * MimeMessage.RecipientType.CC：抄送
         * MimeMessage.RecipientType.BCC：密送
         */
        msg.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(receiveAddress));
        //4.设置邮件主题
        msg.setSubject(subject, "UTF-8");

        //下面是设置邮件正文
        msg.setContent(content, "text/html;charset=UTF-8");

        //在文本中插入图片
        //MimeBodyPart text_image = addPhoto();

        // 5. 创建附件"节点"
        MimeBodyPart attachment = new MimeBodyPart();
        // 读取本地文件
        DataHandler dataHandler = new DataHandler(new FileDataSource(filePath));
        // 将附件数据添加到"节点"
        attachment.setDataHandler(dataHandler);
        // 设置附件的文件名（需要编码）
        attachment.setFileName(MimeUtility.encodeText(dataHandler.getName()));

        // 6. 设置（文本+图片）和 附件 的关系（合成一个大的混合"节点" / Multipart ）
        MimeMultipart mm = new MimeMultipart();
        mm.addBodyPart(attachment);     // 如果有多个附件，可以创建多个多次添加
        // mm.setSubType("mixed");         // 混合关系

        // 11. 设置整个邮件的关系（将最终的混合"节点"作为邮件的内容添加到邮件对象）
        msg.setContent(mm);
        //设置邮件的发送时间,默认立即发送

        return msg;
    }


}
