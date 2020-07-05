package com.example.doveletter.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.Telephony;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doveletter.R;
import com.example.doveletter.activities.DraftDetailActivity;
import com.example.doveletter.activities.SentDetailActivity;
import com.example.doveletter.activities.WriteActivity;
import com.example.doveletter.adapter.SentAdapter;
import com.example.doveletter.bean.DraftInfo;
import com.example.doveletter.bean.SentInfo;
import com.example.doveletter.interfaces.ISentItemClickListener;
import com.example.doveletter.utils.DateUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnConfirmListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class SentFragment extends Fragment implements ISentItemClickListener {
    private List<SentInfo> sentList;
    private SentAdapter sentAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_sent,container,false);

        sentList=getAllSents();
        RecyclerView recyclerView=view.findViewById(R.id.recycler_sent_view);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);//1 表示列数
        recyclerView.setLayoutManager(layoutManager);
        sentAdapter=new SentAdapter(sentList);
        sentAdapter.setClickListener(this);
        recyclerView.setAdapter(sentAdapter);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FloatingActionButton btn_write=getActivity().findViewById(R.id.btn_write);
        btn_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), WriteActivity.class);
                startActivity(intent);
            }
        });

    }

    private List<SentInfo> getAllSents(){
        List<SentInfo> sentInfos=new ArrayList<>();
        BmobQuery<SentInfo>bmobQuery=new BmobQuery<>();
        bmobQuery.findObjects(new FindListener<SentInfo>() {
            @Override
            public void done(List<SentInfo> list, BmobException e) {
                if(e==null){
                    sentInfos.addAll(list);
                    Toast.makeText(getContext(),list.size()+"条数据",Toast.LENGTH_SHORT);

                    //排序
                    Collections.sort(sentInfos, new Comparator<SentInfo>() {
                        @Override
                        public int compare(SentInfo o1, SentInfo o2) {
                            Date date1 = DateUtils.stringToDate(o1.getCreatedAt());
                            Date date2 = DateUtils.stringToDate(o2.getCreatedAt());
                            if (date1 != null && date2 != null) {
                                return date2.compareTo(date1);
                            }
                            return 1;
                        }
                    });

                    sentAdapter.notifyDataSetChanged();
                }
            }
        });
        return sentInfos;
    }

    @Override
    public void onClickItem(String address, String subject, String content, String date) {
        //单击跳转已发送邮件详情页面
        Intent intent = new Intent(getContext(), SentDetailActivity.class);
        intent.putExtra("subject", subject);
        intent.putExtra("address", address);
        intent.putExtra("date", date);
        intent.putExtra("content", content);
        startActivity(intent);
    }

    @Override
    public void onLongClickItem(String mailId) {
        showDeletePopup(mailId);
    }

    private void showDeletePopup(String mailId) {
        new XPopup.Builder(getContext())
                .asConfirm("是否选择删除", "删除后此邮件内容将彻底消失",
                        "取消", "确定",
                        new OnConfirmListener() {
                            @Override
                            public void onConfirm() {
                                deleteSentMail(mailId);
                            }
                        },
                        null,
                        false)
                .bindLayout(R.layout.leave_popup)
                .show();
    }


    private void deleteSentMail(String mailId){
        SentInfo sentInfo=new SentInfo();
        sentInfo.setObjectId(mailId);

        sentInfo.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Toast.makeText(getContext(),
                            "删除成功", Toast.LENGTH_LONG).show();
//                    initData();
                }else{
                    Toast.makeText(getContext(),
                            "删除失败", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
