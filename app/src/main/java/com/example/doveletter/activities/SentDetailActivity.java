package com.example.doveletter.activities;

import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.doveletter.R;
import com.example.doveletter.app.MyApplication;
import com.example.doveletter.utils.StatusBarUtil;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SentDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView iv_home;
    private TextView tv_title;

    private TextView tv_subject;
    private TextView tv_content;
    private TextView tv_sender;
    private TextView tv_receiver;
    private TextView tv_date;

    private TextView tv_show_sender;
    private TextView tv_show_detail;
    private ViewStub vs_detail;


    private String subject;
    private String content;
    private String receiver;
    private String date;

    private boolean isInitViewStub = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_detail);
        StatusBarUtil.setStatusBarMode(this, true, R.color.colorMain);
        initView();
        initData();
    }

    private void initView() {
        iv_home = findViewById(R.id.iv_home);
        iv_home.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText("已发送邮件详情");
        iv_home.setOnClickListener(this);

        vs_detail = findViewById(R.id.vs_email_detail);
        tv_subject = findViewById(R.id.tv_email_detail_subject);
        tv_content = findViewById(R.id.tv_email_detail_content);

        tv_show_detail = findViewById(R.id.tv_email_detail_show);
        tv_show_sender = findViewById(R.id.tv_email_detail_sender_show);

//        tv_show_sender.setOnClickListener(this);
        tv_show_detail.setOnClickListener(this);

    }

    private void initData() {
        if (getIntent() != null) {
            receiver = getIntent().getStringExtra("address");
            subject = getIntent().getStringExtra("subject");
            date = getIntent().getStringExtra("date");
            content = getIntent().getStringExtra("content");
//            mFile = (File) getIntent().getSerializableExtra("file");

            tv_show_sender.setText(MyApplication.userInfo.getUseraddress());
            tv_subject.setText(subject);
            tv_content.setText(content);

//            if (mFile != null) {
//                initAttachmentViewStub();
//            }

        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_email_detail_show:
                if (!isInitViewStub) {
                    isInitViewStub = true;
                    initDetailViewStub();
                }
                if (tv_show_detail.getText().toString().equals("详情")) {
                    tv_show_detail.setText("隐藏");
                    vs_detail.setVisibility(View.VISIBLE);
                } else {
                    tv_show_detail.setText("详情");
                    vs_detail.setVisibility(View.GONE);
                }
                break;
            case R.id.iv_home:
                finish();
                break;
        }
    }


    private void initDetailViewStub() {
        vs_detail.inflate();
        tv_receiver = findViewById(R.id.tv_email_detail_receiver_vs);
        tv_date = findViewById(R.id.tv_email_detail_date_vs);
        tv_sender = findViewById(R.id.tv_email_detail_sender_vs);

        tv_receiver.setText(receiver);
        tv_sender.setText(MyApplication.userInfo.getUseraddress());
        tv_date.setText(date);
    }
}
