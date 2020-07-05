package com.example.doveletter.activities;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.doveletter.R;
import com.example.doveletter.app.MyApplication;
import com.example.doveletter.utils.FileUtil;
import com.example.doveletter.utils.StatusBarUtil;
import com.example.doveletter.utils.ZoomImageLoader;
import com.lxj.xpopup.XPopup;

import java.io.File;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ReceiveDetailActivity extends AppCompatActivity implements View.OnClickListener {

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
    private boolean isInitViewStub = false;

    private ViewStub vs_attachment_detail;


    private String subject;
    private String content;
    private String sender;
    private String date;
    private File file;

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
        iv_home.setOnClickListener(this);
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText("邮件详情");

        vs_detail = findViewById(R.id.vs_email_detail);
        vs_detail.setOnClickListener(this);
        tv_subject = findViewById(R.id.tv_email_detail_subject);
        tv_content = findViewById(R.id.tv_email_detail_content);

        tv_show_detail = findViewById(R.id.tv_email_detail_show);
        tv_show_detail.setOnClickListener(this);
        tv_show_sender = findViewById(R.id.tv_email_detail_sender_show);


        vs_attachment_detail = findViewById(R.id.vs_attachment_email_detail);
    }

    private void initData() {
        subject = getIntent().getStringExtra("subject");
        content = getIntent().getStringExtra("content");
        date = getIntent().getStringExtra("date");
        sender = getIntent().getStringExtra("sender");
        String filePath = getIntent().getStringExtra("file_path");
        if (!TextUtils.isEmpty(filePath)) {
            file = FileUtil.getFileByPath(filePath);
        }

        tv_show_sender.setText(sender);
        tv_subject.setText(subject);
        tv_content.setText(content);

        if (file != null) {
            initAttachmentViewStub();
        }

    }

    private void initAttachmentViewStub() {
        vs_attachment_detail.inflate();
        ImageView iv_attachmnet_logo = findViewById(R.id.iv_logo_attachment_info);
        TextView tv_attachment_name = findViewById(R.id.tv_name_attachment_info);
        TextView tv_attachment_size = findViewById(R.id.tv_size_attachment_info);

        findViewById(R.id.ll_attachment_box).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new XPopup.Builder(iv_attachmnet_logo.getContext())
                        .asImageViewer(
                                iv_attachmnet_logo
                                , Uri.fromFile(file)
                                , new ZoomImageLoader())
                        .show();
            }
        });

        if (file != null) {
            Glide.with(this).load(file).into(iv_attachmnet_logo);
            tv_attachment_name.setText(file.getName());
            tv_attachment_size.setText((((int) file.length()) / 1024) + "k");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_home:
                finish();
                break;

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
        }
    }


    private void initDetailViewStub() {
        vs_detail.inflate();
        tv_receiver = findViewById(R.id.tv_email_detail_receiver_vs);
        tv_date = findViewById(R.id.tv_email_detail_date_vs);
        tv_sender = findViewById(R.id.tv_email_detail_sender_vs);

        tv_receiver.setText(MyApplication.userInfo.getUseraddress());
        tv_sender.setText(sender);
        tv_date.setText(date);
    }


}
