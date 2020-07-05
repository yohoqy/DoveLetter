package com.example.doveletter.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doveletter.R;
import com.example.doveletter.app.MyApplication;
import com.example.doveletter.bean.DraftInfo;
import com.example.doveletter.utils.StatusBarUtil;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class DraftDetailActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView iv_home;
    private TextView tv_title;
    private ImageView iv_operation;

    private TextView tv_subject;
    private TextView tv_receiver;
    private TextView tv_content;
    private TextView tv_date;
    private TextView tv_sender;

    private String subject;
    private String sender;
    private String receiver;
    private String date;
    private String content;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draft_detail);
        StatusBarUtil.setStatusBarMode(this, true, R.color.colorMain);
        initView();
        initData();
    }



    private void initView() {
        iv_home=findViewById(R.id.iv_home);
        iv_home.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        tv_title=findViewById(R.id.tv_title);
        tv_title.setText("草稿详情");
        iv_operation=findViewById(R.id.iv_operation);
        iv_operation.setImageDrawable(getResources().getDrawable(R.drawable.ic_assignment_white_24dp));
        iv_home.setOnClickListener(this);
        iv_operation.setOnClickListener(this);

        tv_subject=findViewById(R.id.tv_draft_detail_subject);
        tv_sender=findViewById(R.id.tv_draft_detail_sender);
        tv_receiver=findViewById(R.id.tv_draft_detail_receiver);
        tv_content=findViewById(R.id.tv_draft_detail_content);
        tv_date=findViewById(R.id.tv_draft_detail_date);
    }

    private void initData() {

        if(getIntent()!=null){
            sender= MyApplication.userInfo.getUseraddress();
            receiver=getIntent().getStringExtra("address");
            subject = getIntent().getStringExtra("subject");
            date = getIntent().getStringExtra("date");
            content = getIntent().getStringExtra("content");
            id=getIntent().getStringExtra("id");

            tv_sender.setText(sender);
            tv_receiver.setText(receiver);
            tv_subject.setText(subject);
            tv_content.setText(content);
            tv_date.setText(date);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_home:
                finish();
                break;
            case R.id.iv_operation:
                WriteEmail();
                break;
        }
    }

    private void WriteEmail(){
        Intent intent=new Intent(this,WriteActivity.class);
        intent.putExtra("subject", subject);
        intent.putExtra("receiver", receiver);
        intent.putExtra("content", content);
        intent.putExtra("jumpfrom",1);
        startActivity(intent);
        deleteDraft();
        finish();
    }

    private void deleteDraft(){
        DraftInfo draftInfo=new DraftInfo();
        draftInfo.setObjectId(id);

        draftInfo.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Toast.makeText(DraftDetailActivity.this,
                            "删除成功", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(DraftDetailActivity.this,
                            "删除失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
