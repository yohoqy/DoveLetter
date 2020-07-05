package com.example.doveletter.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.doveletter.R;
import com.example.doveletter.activities.WriteActivity;
import com.example.doveletter.adapter.ContactAdapter;
import com.example.doveletter.bean.ContactInfo;
import com.example.doveletter.bean.DraftInfo;
import com.example.doveletter.interfaces.IContactItemClickListener;
import com.example.doveletter.utils.EmailFormatUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnConfirmListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.http.I;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import static android.app.Activity.RESULT_OK;

public class ContactFragment extends Fragment implements IContactItemClickListener{
    private List<ContactInfo> contactlist;
    private ContactAdapter contactAdapter;
//    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private TextView tv_contact_number;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_contact,container,false);

//        swipeRefreshLayout=view.findViewById(R.id.swipe_refresh_contact);
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                refreshContacts();
//            }
//        });


        tv_contact_number=view.findViewById(R.id.tv_contact_number);
        recyclerView=view.findViewById(R.id.recycler_contact_view);
//        contactlist=getAllContacts();
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);//1 表示列数
        recyclerView.setLayoutManager(layoutManager);
//        contactAdapter=new ContactAdapter(contactlist);
//        contactAdapter.setClickListener(this);
//        recyclerView.setAdapter(contactAdapter);
        initData();
        return view;
    }


    private void initData(){
        contactlist=getAllContacts();
        contactAdapter=new ContactAdapter(contactlist);
        contactAdapter.setClickListener(this);
        recyclerView.setAdapter(contactAdapter);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FloatingActionButton btn_addContact=getActivity().findViewById(R.id.btn_addcontact);
        btn_addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                builder.setTitle("添加联系人");
                View view= getLayoutInflater().inflate(R.layout.add_contact_popup, null);
                builder.setView(view);
                final EditText name=view.findViewById(R.id.et_contact_name);
                final EditText addr= view.findViewById(R.id.et_contact_address);

                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        insertContact(name.getText().toString().trim(),addr.getText().toString().trim());
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();


            }
        });
    }

    private void insertContact(String name,String addr){
        if(name==null){
            Toast.makeText(getActivity(), "用户名不能为空", Toast.LENGTH_SHORT).show();
        }else{
            if(addr==null){
                Toast.makeText(getActivity(), "邮箱不能为空", Toast.LENGTH_SHORT).show();
            }else{
                if(!EmailFormatUtil.emailFormat(addr)){
                    Toast.makeText(getActivity(), "邮箱格式不正确", Toast.LENGTH_SHORT).show();
                }else{
                    ContactInfo contactInfo=new ContactInfo(name,addr);
                    contactInfo.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if(e==null){
                                Toast.makeText(getContext(),"添加成功",Toast.LENGTH_SHORT).show();
                                initData();
                            }else{
                                Toast.makeText(getContext(),"添加失败",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }
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
                    tv_contact_number.setText(list.size()+"位联系人");
                }
            }
        });
        return contactInfos;
    }

//    private void refreshContacts(){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(2000);
//                }catch (InterruptedException e){
//                    e.printStackTrace();
//                }
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        getAllContacts();
//                        contactAdapter=new ContactAdapter(contactlist);
//                        contactAdapter.notifyDataSetChanged();
//                        swipeRefreshLayout.setRefreshing(false);
//                    }
//                });
//            }
//        }).start();
//
//
//    }


    @Override
    public void onClickItem(String address) {


    }

    @Override
    public void onLongClickItem(String contactId) {
        showDeletePopup(contactId);
    }

    private void showDeletePopup(String contactId) {
        new XPopup.Builder(getContext())
                .asConfirm("是否选择删除", "删除后此草稿内容将彻底消失",
                        "取消", "确定",
                        new OnConfirmListener() {
                            @Override
                            public void onConfirm() {
                                deleteContact(contactId);
                            }
                        },
                        null,
                        false)
                .bindLayout(R.layout.leave_popup)
                .show();
    }

    private void deleteContact(String contactId){
        ContactInfo contactInfo=new ContactInfo();
        contactInfo.setObjectId(contactId);

        contactInfo.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Toast.makeText(getContext(),
                            "删除成功", Toast.LENGTH_LONG).show();
                    initData();
                }else{
                    Toast.makeText(getContext(),
                            "删除失败", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
