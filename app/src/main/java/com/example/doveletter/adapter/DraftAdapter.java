package com.example.doveletter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doveletter.R;
import com.example.doveletter.bean.DraftInfo;
import com.example.doveletter.interfaces.IDraftItemClickListener;

import java.util.List;

public class DraftAdapter extends RecyclerView.Adapter<DraftAdapter.ViewHolder> {
    private Context context;
    private List<DraftInfo> draftList;
    private IDraftItemClickListener clickListener;

    public void setClickListener(IDraftItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(context==null){
            context=parent.getContext();
        }

        View view = LayoutInflater.from(context).inflate(R.layout.sent_item, parent, false);
        final DraftAdapter.ViewHolder holder = new DraftAdapter.ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DraftInfo draftInfo=draftList.get(position);
        holder.tv_sent_receiver_address.setText(draftInfo.getReceiverAddress());
        holder.tv_sent_subject.setText(draftInfo.getSubject());
        holder.tv_sent_content.setText(draftInfo.getContent());
        String[] strings=draftInfo.getCreatedAt().split(" ");
        holder.tv_sent_date.setText(strings[0]);
        holder.tv_sent_time.setText(strings[1].substring(0, strings[1].length() - 3));
        holder.iv_sent_has_file.setVisibility(View.GONE);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onClickItem(draftInfo.getObjectId()
                        ,draftInfo.getReceiverAddress()
                        ,draftInfo.getSubject()
                        ,draftInfo.getContent()
                        ,draftInfo.getCreatedAt());
            }
        });


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                clickListener.onLongClickItem(draftInfo.getObjectId());
                return true;
            }
        });



    }

    @Override
    public int getItemCount() {
        return draftList.size();
    }

    public DraftAdapter(List<DraftInfo> draftList) {
        this.draftList = draftList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tv_sent_receiver_address;
        TextView tv_sent_subject;
        TextView tv_sent_content;
        ImageView iv_sent_has_file;
        TextView tv_sent_date;
        TextView tv_sent_time;


        public ViewHolder(@NonNull View view) {
            super(view);
            cardView = (CardView) view;
            tv_sent_receiver_address = view.findViewById(R.id.tv_sent_receiver_address);
            tv_sent_subject = view.findViewById(R.id.tv_sent_subject);
            tv_sent_content = view.findViewById(R.id.tv_sent_content);
            iv_sent_has_file = view.findViewById(R.id.iv_sent_has_file);
            tv_sent_date = view.findViewById(R.id.tv_sent_date);
            tv_sent_time = view.findViewById(R.id.tv_sent_time);

        }

    }
}
