package com.example.doveletter.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doveletter.R;
import com.example.doveletter.activities.DraftDetailActivity;
import com.example.doveletter.adapter.DraftAdapter;
import com.example.doveletter.bean.DraftInfo;
import com.example.doveletter.interfaces.IDraftItemClickListener;
import com.example.doveletter.utils.DateUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnConfirmListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class DraftFragment extends Fragment implements View.OnClickListener, IDraftItemClickListener {
    private List<DraftInfo> draftList;
    private DraftAdapter draftAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_draft,container,false);
        draftList=getAllDrafts();
        RecyclerView recyclerView=view.findViewById(R.id.recycler_draft_view);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);//1 表示列数
        recyclerView.setLayoutManager(layoutManager);
        draftAdapter=new DraftAdapter(draftList);
        draftAdapter.setClickListener(this);
        recyclerView.setAdapter(draftAdapter);
        return view;
    }


    private List<DraftInfo> getAllDrafts(){
        List<DraftInfo> draftInfos=new ArrayList<>();
        BmobQuery<DraftInfo> bmobQuery=new BmobQuery<>();
        bmobQuery.findObjects(new FindListener<DraftInfo>() {
            @Override
            public void done(List<DraftInfo> list, BmobException e) {
                if(e==null){
                    draftInfos.addAll(list);

                    //排序
                    Collections.sort(draftInfos, (o1, o2) -> {
                        Date date1 = DateUtils.stringToDate(o1.getCreatedAt());
                        Date date2 = DateUtils.stringToDate(o2.getCreatedAt());
                        if (date1 != null && date2 != null) {
                            return date2.compareTo(date1);
                        }
                        return 1;
                    });
                    draftAdapter.notifyDataSetChanged();
                }
            }
        });
        return draftInfos;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onClickItem(String id,String address, String subject, String content, String date) {
//        Toast.makeText(getContext(),"点击成功",Toast.LENGTH_SHORT).show();
        //单击跳转草稿详情页面
        Intent intent = new Intent(getContext(), DraftDetailActivity.class);
        intent.putExtra("id",id);
        intent.putExtra("subject", subject);
        intent.putExtra("address", address);
        intent.putExtra("date", date);
        intent.putExtra("content", content);
        startActivity(intent);
    }

    @Override
    public void onLongClickItem(String mailId) {
//        Toast.makeText(getContext(),"点击成功",Toast.LENGTH_SHORT).show();
        showDeletePopup(mailId);
    }

    private void showDeletePopup(String mailId) {
        new XPopup.Builder(getContext())
                .asConfirm("是否选择删除", "删除后此草稿内容将彻底消失",
                        "取消", "确定",
                        new OnConfirmListener() {
                            @Override
                            public void onConfirm() {
                                deleteDraft(mailId);
                            }
                        },
                        null,
                        false)
                .bindLayout(R.layout.leave_popup)
                .show();
    }


    private void deleteDraft(String mailId){
        DraftInfo draftInfo=new DraftInfo();
        draftInfo.setObjectId(mailId);

        draftInfo.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Toast.makeText(getContext(),
                            "删除成功", Toast.LENGTH_SHORT).show();
                    initData();
                }else{
                    Toast.makeText(getContext(),
                            "删除失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initData() {
        draftList=getAllDrafts();
        draftAdapter=new DraftAdapter(draftList);
    }

}
