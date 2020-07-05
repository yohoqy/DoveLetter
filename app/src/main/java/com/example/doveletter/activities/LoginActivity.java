package com.example.doveletter.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.doveletter.R;
import com.example.doveletter.app.MyApplication;
import com.example.doveletter.utils.EmailFormatUtil;
import com.example.doveletter.utils.LoginUtil;
import com.example.doveletter.utils.StatusBarUtil;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;

import javax.mail.MessagingException;

import cn.bmob.v3.Bmob;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText et_address;
    private EditText et_password;
    private Button btn_login;
    private CheckBox cb_remember;
    private CheckBox cb_auto;
    private BasePopupView loginPopup;
    private static final String USER_ADDRESS = "user_address";
    private static final String USER_PASSWORD = "user_password";
    private static final String REMEMBER_PASSWORD = "remember_password";
    private static final int CONNECT_SUCCESS=1;
    private static final int CONNECT_FAILED=2;
    private SharedPreferences sp;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case CONNECT_SUCCESS:
                    loginPopup.dismiss();
                    startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                    break;
                case CONNECT_FAILED:
                    loginPopup.dismiss();
                    Toast.makeText(LoginActivity.this, "账号或密码错误！", Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        StatusBarUtil.setStatusBarMode(this, true, R.color.colorWhite);
        sp = this.getPreferences(Context.MODE_PRIVATE);
        initView();
        Bmob.initialize(this, "78db5f52819ddec39771d8d8497642cb");
        boolean isRemember=sp.getBoolean(REMEMBER_PASSWORD,false);
        if(isRemember){
            String address=sp.getString(USER_ADDRESS,"");
            String password=sp.getString(USER_PASSWORD,"");
            et_address.setText(address);
            et_password.setText(password);
            cb_remember.setChecked(true);
        }
    }

    //控件实例化
    private void initView() {
        et_address = findViewById(R.id.et_address);
        et_password = findViewById(R.id.et_password);
        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
        cb_remember = findViewById(R.id.cb_remember);
        cb_remember.setOnClickListener(this);
        cb_auto = findViewById(R.id.cb_autologin);
        cb_auto.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                LoginEmail();
                break;
            case R.id.cb_remember:
//                rememberPwd();
                break;
            case R.id.cb_autologin:
                break;
        }
    }


    /**
     * 邮箱登录
     */
    private void LoginEmail(){
        final String address=et_address.getText().toString().trim();
        final String password=et_password.getText().toString().trim();

        //验证输入框是否为空
        if(TextUtils.isEmpty(address)){
            Toast.makeText(LoginActivity.this, "账号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(LoginActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        //验证邮箱格式是否正确
        if(!EmailFormatUtil.emailFormat(address)){
            Toast.makeText(LoginActivity.this, "邮箱格式不正确", Toast.LENGTH_SHORT).show();
            return;
        }else{
            if(cb_remember.isChecked()){
                sp.edit().putBoolean(REMEMBER_PASSWORD,true).apply();
                sp.edit().putString(USER_ADDRESS,address).apply();
                sp.edit().putString(USER_PASSWORD,password).apply();
            }
        }
        String host = "smtp." + address.substring(address.lastIndexOf("@") + 1);
        MyApplication.userInfo.setHost(host);
        MyApplication.userInfo.setUseraddress(address);
        MyApplication.userInfo.setUserpassword(password);
        loginPopup = new XPopup.Builder(this)
                .asLoading("正在登录中...")
                .show();

        /**
         * 访问网络
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg=new Message();
                try{
                    LoginUtil.isLoginRight(address,password);
                    msg.what=CONNECT_SUCCESS;
                }catch (Exception e){
                    msg.what=CONNECT_FAILED;
                    e.printStackTrace();
                }
                try{
                    Thread.sleep(3000);
                }catch (Exception e){
                    e.printStackTrace();
                }

                handler.sendMessage(msg);

            }
        }).start();


    }



}
