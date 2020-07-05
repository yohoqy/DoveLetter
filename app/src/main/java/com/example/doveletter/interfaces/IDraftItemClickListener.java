package com.example.doveletter.interfaces;

public interface IDraftItemClickListener {
    void onClickItem(String id,String address,String subject,String content,String date);

    void onLongClickItem(String mailId);
}
