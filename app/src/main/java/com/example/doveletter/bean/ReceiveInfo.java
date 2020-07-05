package com.example.doveletter.bean;

import java.io.File;
import java.util.Date;

import cn.bmob.v3.BmobObject;

public class ReceiveInfo {

    //发件人邮箱
    private String senderAddress;
    //主题
    private String subject;
    //邮件内容
    private String content;
    //邮件日期
    private Date date;
    //附件
    private File file;
    //是否是新邮件
    private boolean isNew;


    public ReceiveInfo(String senderAddress, String subject, String content, Date date, boolean isNew) {
        this.senderAddress = senderAddress;
        this.subject = subject;
        this.content = content;
        this.date = date;
        this.isNew = isNew;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    @Override
    public String toString() {
        return "ReceiveInfo{" +
                "senderAddress='" + senderAddress + '\'' +
                ", subject='" + subject + '\'' +
                ", content='" + content + '\'' +
                ", date=" + date +
                ", file=" + file +
                ", isNew=" + isNew +
                '}';
    }
}
