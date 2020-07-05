package com.example.doveletter.bean;

import cn.bmob.v3.BmobObject;

public class DraftInfo extends BmobObject {
    private String receiverAddress;
    private String subject;
    private String content;

    public DraftInfo(String receiverAddress, String subject, String content) {
        this.receiverAddress = receiverAddress;
        this.subject = subject;
        this.content = content;
    }

    public DraftInfo(){}

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
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
}
