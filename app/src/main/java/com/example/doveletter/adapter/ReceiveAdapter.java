package com.example.doveletter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doveletter.R;
import com.example.doveletter.app.MyApplication;
import com.example.doveletter.bean.ReceiveInfo;
import com.example.doveletter.interfaces.IReceiveItemClickListener;

import java.text.SimpleDateFormat;
import java.util.List;

public class ReceiveAdapter extends RecyclerView.Adapter<ReceiveAdapter.ViewHolder> {
    private Context context;
    private List<ReceiveInfo> receiveList;
    private IReceiveItemClickListener clickListener;

    public void setClickListener(IReceiveItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView tv_mail_sender;
        TextView tv_mail_subject;
        TextView tv_mail_content;
        ImageView point;
        TextView tv_mail_date;
        ImageView hasFile;


        public ViewHolder(@NonNull View view) {
            super(view);

            cardView=(CardView) view;
            tv_mail_sender=(TextView)view.findViewById(R.id.tv_mail_sender);
            tv_mail_subject=(TextView)view.findViewById(R.id.tv_mail_subject);
            tv_mail_content=(TextView)view.findViewById(R.id.tv_mail_content);
            point=(ImageView)view.findViewById(R.id.iv_has_read);
            tv_mail_date=(TextView)view.findViewById(R.id.tv_mail_date);
            hasFile=(ImageView)view.findViewById(R.id.iv_has_file);
        }
    }

    public ReceiveAdapter(List<ReceiveInfo> receiveList) {
        this.receiveList = receiveList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(context==null){
            context=parent.getContext();
        }

        View view = LayoutInflater.from(context).inflate(R.layout.mail_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);

//        holder.mRelativeLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                int position = holder.getAdapterPosition();
//
//                Intent intent = new Intent(mContext, MailDetailActivity.class);
//                intent.putExtra("messageId", mData.get(position).getMessageId());
//
//                mContext.startActivity(intent);
//            }
//        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReceiveInfo receiveInfo=receiveList.get(position);
        holder.tv_mail_sender.setText(receiveInfo.getSenderAddress());
        holder.tv_mail_subject.setText(receiveInfo.getSubject());
        holder.tv_mail_content.setText(receiveInfo.getContent());
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd");
        String date=formatter.format(receiveInfo.getDate());
        holder.tv_mail_date.setText(date);
        if(receiveInfo.getFile()!=null){
            holder.hasFile.setVisibility(View.VISIBLE);
        }else{
            holder.hasFile.setVisibility(View.GONE);
        }

        if(receiveInfo.isNew()){
            holder.point.setVisibility(View.VISIBLE);
        }else{
            holder.point.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickListener!=null){
                    clickListener.onClickItem(receiveInfo);
                }
            }
        });
//        if(receiveInfo.getSenderAddress().contains(MyApplication.userInfo.getUseraddress())){
//            holder.tv_mail_sender.setText("我");
//        }else{
//            holder.tv_mail_sender.setText(receiveInfo.getSenderName());
//        }
//
//        if(receiveInfo.getIsNew()==1){
//            holder.point.setVisibility(View.VISIBLE);
//        }else{
//            holder.point.setVisibility(View.GONE);
//        }
//
//        if(receiveInfo.getHasFile()==1){
//            holder.hasFile.setVisibility(View.VISIBLE);
//        }else{
//            holder.hasFile.setVisibility(View.GONE);
//        }
//
//        holder.tv_mail_date.setText(receiveInfo.getMailDate());
//        holder.tv_mail_subject.setText(receiveInfo.getMailSubject());
//        holder.tv_mail_content.setText(receiveInfo.getMailContent());

    }


    @Override
    public int getItemCount() {
        return receiveList.size();
    }
}
