package com.example.doveletter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doveletter.R;
import com.example.doveletter.bean.ContactInfo;
import com.example.doveletter.interfaces.IContactItemClickListener;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder>{
    private Context context;
    private List<ContactInfo> contactList;
    private IContactItemClickListener clickListener;

    public void setClickListener(IContactItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView conatctName;
        TextView contactAddress;

        public ViewHolder(@NonNull View view) {
            super(view);
            cardView=(CardView)view;
            conatctName=(TextView)view.findViewById(R.id.tv_contact_name);
            contactAddress=(TextView)view.findViewById(R.id.tv_contact_adress);
        }
    }

    public ContactAdapter(List<ContactInfo> contactList) {
        this.contactList = contactList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(context==null){
            context=parent.getContext();
        }
        View view= LayoutInflater.from(context).inflate(R.layout.contact_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ContactInfo contactInfo=contactList.get(position);
        holder.conatctName.setText(contactInfo.getContactName());
        holder.contactAddress.setText(contactInfo.getContactAddress());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onClickItem(contactInfo.getContactAddress());
            }
        });


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                clickListener.onLongClickItem(contactInfo.getObjectId());
                return true;
            }
        });

    }


    @Override
    public int getItemCount() {
        return contactList.size();
    }
}
