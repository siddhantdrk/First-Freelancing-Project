package com.example.order_eckn2015;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class profileContactsRecyclerAdapter extends RecyclerView.Adapter<profileContactsRecyclerAdapter.profileContactViewHolder> {

    private final Context context;
    private final List<profileContactItem> profileContactItemList;

    profileContactsRecyclerAdapter(Context context, List<profileContactItem> profileContactItemList) {
        this.context = context;
        this.profileContactItemList = profileContactItemList;
    }

    @NonNull
    @Override
    public profileContactsRecyclerAdapter.profileContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.profile_contact_item, parent, false);
        return new profileContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull profileContactsRecyclerAdapter.profileContactViewHolder holder, int position) {
        String cardinalNum = (position + 1) + ".";
        holder.cardinalNum.setText(cardinalNum);
        holder.contactName.setText(profileContactItemList.get(position).getName());
        holder.contactNum.setText(profileContactItemList.get(position).getContactNum());
    }


    @Override
    public int getItemCount() {
        return profileContactItemList.size();
    }

    public static class profileContactViewHolder extends RecyclerView.ViewHolder {
        TextView cardinalNum, contactName, contactNum;

        public profileContactViewHolder(@NonNull View itemView) {
            super(itemView);
            cardinalNum = itemView.findViewById(R.id.cardinal_num);
            contactName = itemView.findViewById(R.id.contact_name);
            contactNum = itemView.findViewById(R.id.contact_num);
        }
    }
}
