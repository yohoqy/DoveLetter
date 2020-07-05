package com.example.doveletter.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doveletter.R;
import com.example.doveletter.adapter.ContactAdapter;
import com.example.doveletter.bean.ContactInfo;
import com.example.doveletter.interfaces.IContactItemClickListener;
import com.example.doveletter.utils.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class ContactActivity extends AppCompatActivity implements View.OnClickListener, IContactItemClickListener {
    private TextView tv_title;
    private ImageView iv_home;
    private List<ContactInfo> contactlist;
    private ContactAdapter contactAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        StatusBarUtil.setStatusBarMode(this, true, R.color.colorMain);
        initView();
    }

    private void initView() {
        iv_home=findViewById(R.id.iv_home);
        iv_home.setOnClickListener(this);
        iv_home.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        tv_title=findViewById(R.id.tv_title);
        tv_title.setText("通讯录");


        contactlist=getAllContacts();
        recyclerView=findViewById(R.id.recycler_choose_contact_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);//1 表示列数
        recyclerView.setLayoutManager(layoutManager);
        contactAdapter=new ContactAdapter(contactlist);
        contactAdapter.setClickListener(this);
        recyclerView.setAdapter(contactAdapter);


    }


    private List<ContactInfo> getAllContacts(){
        List<ContactInfo> contactInfos=new ArrayList<>();
        BmobQuery<ContactInfo> bmobQuery=new BmobQuery<>();
        bmobQuery.findObjects(new FindListener<ContactInfo>() {
            @Override
            public void done(List<ContactInfo> list, BmobException e) {
                if(e==null){
                    contactInfos.addAll(list);
                    contactAdapter.notifyDataSetChanged();
                }
            }
        });
        return contactInfos;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_home:
                finish();
        }
    }

    @Override
    public void onClickItem(String address) {
        Toast.makeText(this,address,Toast.LENGTH_SHORT).show();
//        Intent intent=new Intent(this, WriteActivity.class);
        Intent intent=new Intent();
        intent.putExtra("respond",address);
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    public void onLongClickItem(String contactId) {

    }
}
