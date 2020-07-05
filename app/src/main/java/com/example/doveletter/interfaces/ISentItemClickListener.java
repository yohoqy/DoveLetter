package com.example.doveletter.interfaces;

public interface ISentItemClickListener {
    void onClickItem(String address,String subject,String content,String date);

    void onLongClickItem(String mailId);
}
