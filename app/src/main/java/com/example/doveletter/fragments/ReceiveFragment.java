package com.example.doveletter.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.doveletter.R;
import com.example.doveletter.activities.ReceiveDetailActivity;
import com.example.doveletter.adapter.ReceiveAdapter;
import com.example.doveletter.bean.ReceiveInfo;
import com.example.doveletter.interfaces.IReceiveItemClickListener;
import com.example.doveletter.utils.DateUtils;
import com.example.doveletter.utils.ReceiveUtil;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;

import java.io.File;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ReceiveFragment extends Fragment implements IReceiveItemClickListener {
    private List<ReceiveInfo> receiveList;
    private ReceiveAdapter receiveAdapter;
    private BasePopupView receivePopup;
    private static final int RECEIVE_SUCCESS = 6;
    private static final int RECEIVE_FAILED = 8;
    private RecyclerView recyclerView;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case RECEIVE_SUCCESS:
                    receivePopup.dismiss();
                    Toast.makeText(getContext(), "接收成功", Toast.LENGTH_SHORT).show();
                    showMail();
                    break;
                case RECEIVE_FAILED:
                    receivePopup.dismiss();
                    Toast.makeText(getContext(), "接收失败", Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(msg);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_receive, container, false);
        recyclerView = view.findViewById(R.id.recycler_receive_view);
        receiveMail();

        return view;
    }

    private void receiveMail() {
        receivePopup = new XPopup.Builder(getContext())
                .asLoading("正在获取邮件")
                .show();
        receivePopup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });


        new Thread() {
            @Override
            public void run() {
                super.run();
                ReceiveUtil receiveUtil = new ReceiveUtil();
                Message msg = new Message();
                try {
                    receiveList = receiveUtil.receiveMail();
                    if (receiveList != null) {
                        for (ReceiveInfo ri : receiveList) {
                            Log.e("file", "接收邮件 = " + ri.toString());
                        }
                    }

                    msg.what = RECEIVE_SUCCESS;
                } catch (Exception e) {
                    e.printStackTrace();
                    msg.what = RECEIVE_FAILED;
                }
                handler.sendMessage(msg);
            }
        }.start();

    }

    private void showMail() {
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);//1 表示列数
        recyclerView.setLayoutManager(layoutManager);
        receiveAdapter = new ReceiveAdapter(receiveList);
        receiveAdapter.setClickListener(this);
        recyclerView.setAdapter(receiveAdapter);
    }

    @Override
    public void onClickItem(ReceiveInfo receiveInfo) {
        Date date = receiveInfo.getDate();
        String dateToString = DateUtils.dateToString(date);
        File file = receiveInfo.getFile();
        String absolutePath = "";
        if (file != null) {
            absolutePath = file.getAbsolutePath();
        }
//        Log.e("main", "dateToString = " + dateToString + ";absolutePath = " + absolutePath);

        Intent intent = new Intent(getContext(), ReceiveDetailActivity.class);
        intent.putExtra("subject", receiveInfo.getSubject());
        intent.putExtra("content", receiveInfo.getContent());
        intent.putExtra("date", dateToString);
        intent.putExtra("sender", receiveInfo.getSenderAddress());
        intent.putExtra("file_path", absolutePath);
        startActivity(intent);
    }
}
