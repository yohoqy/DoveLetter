package com.example.doveletter.adapter;

import android.content.Context;
import android.provider.Telephony;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doveletter.R;
import com.example.doveletter.bean.SentInfo;
import com.example.doveletter.interfaces.ISentItemClickListener;

import java.util.List;

public class SentAdapter extends RecyclerView.Adapter<SentAdapter.ViewHolder>{

    private Context context;
    private List<SentInfo> sentList;
    private ISentItemClickListener clickListener;

    public void setClickListener(ISentItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView tv_sent_receiver_address;
        TextView tv_sent_subject;
        TextView tv_sent_content;
        ImageView iv_sent_has_file;
        TextView tv_sent_date;
        TextView tv_sent_time;


        public ViewHolder(@NonNull View view) {
            super(view);
            cardView=(CardView) view;
            tv_sent_receiver_address=view.findViewById(R.id.tv_sent_receiver_address);
            tv_sent_subject=view.findViewById(R.id.tv_sent_subject);
            tv_sent_content=view.findViewById(R.id.tv_sent_content);
            iv_sent_has_file=view.findViewById(R.id.iv_sent_has_file);
            tv_sent_date=view.findViewById(R.id.tv_sent_date);
            tv_sent_time=view.findViewById(R.id.tv_sent_time);

        }
    }

    public SentAdapter(List<SentInfo> sentList) {
        this.sentList = sentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(context==null){
            context=parent.getContext();
        }

        View view = LayoutInflater.from(context).inflate(R.layout.sent_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SentInfo sentInfo=sentList.get(position);
        holder.tv_sent_receiver_address.setText(sentInfo.getReceiverAddress());
        holder.tv_sent_subject.setText(sentInfo.getSubject());
        holder.tv_sent_content.setText(sentInfo.getContent());
        String[] strings = sentInfo.getCreatedAt().split(" ");
        holder.tv_sent_date.setText(strings[0]);
        holder.tv_sent_time.setText(strings[1].substring(0, strings[1].length() - 3));
        if(sentInfo.getAttachment()!=null){
            holder.iv_sent_has_file.setVisibility(View.VISIBLE);
        }else {
            holder.iv_sent_has_file.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onClickItem(sentInfo.getReceiverAddress()
                        ,sentInfo.getSubject()
                        ,sentInfo.getContent()
                        ,sentInfo.getCreatedAt());
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                clickListener.onLongClickItem(sentInfo.getObjectId());
                return true;
            }
        });
//        String []str=sentInfo.getDate().split(" ");
//        holder.tv_sent_date.setText(str[0]);
//        holder.tv_sent_time.setText(str[1]);
//
//        if(!TextUtils.isEmpty(sentInfo.getAttachmentPath())){
//            holder.iv_sent_has_file.setVisibility(View.VISIBLE);
//        }else{
//            holder.iv_sent_has_file.setVisibility(View.GONE);
//        }
    }

    @Override
    public int getItemCount() {
        return sentList.size();
    }


}
