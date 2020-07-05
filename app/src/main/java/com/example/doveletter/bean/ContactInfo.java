package com.example.doveletter.bean;

import cn.bmob.v3.BmobObject;

public class ContactInfo extends BmobObject {
    String contactName;
    String contactAddress;

    public ContactInfo(String contactName, String contactAddress) {
        this.contactName = contactName;
        this.contactAddress = contactAddress;
    }

    public ContactInfo(){}

    public String getContactAddress() {
        return contactAddress;
    }

    public void setContactAddress(String contactAddress) {
        this.contactAddress = contactAddress;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
}
