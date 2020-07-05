package com.example.doveletter.app;

import android.app.Application;

import com.example.doveletter.bean.UserInfo;

import cn.bmob.v3.Bmob;

public class MyApplication extends Application {
    public static UserInfo userInfo=new UserInfo();

    @Override
    public void onCreate() {
        super.onCreate();
        Bmob.initialize(this, "78db5f52819ddec39771d8d8497642cb");
    }
}
