package com.mrizkypk.umsdaily.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mrizkypk.umsdaily.R;
import com.mrizkypk.umsdaily.activity.HomeActivity;
import com.mrizkypk.umsdaily.model.UserModel;


import java.util.ArrayList;

public class ChatReceivedAdapter extends RecyclerView.Adapter<ChatReceivedAdapter.ViewHolder> {
    public Context context;
    public HomeActivity activity;
    public ArrayList<String> idList;
    public ArrayList<Long> timeList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;
        public TextView tvTime;
        public ImageView ivAvatar;

        public ViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.item_chat_info_received_text_name);
            tvTime = v.findViewById(R.id.item_chat_info_received_text_time);
            ivAvatar = v.findViewById(R.id.item_chat_info_received_image_avatar);
        }
    }

    public ChatReceivedAdapter() {
    }

    public ChatReceivedAdapter(Context ctx, ArrayList<String> ids, ArrayList<Long> times) {
        context = ctx;
        idList = ids;
        timeList = times;

        activity = (HomeActivity) context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_info_received, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String id = idList.get(position);
        Long time = timeList.get(position);

        if (activity.userModelMap.containsKey(id)) {
            UserModel user = activity.userModelMap.get(id);
            holder.tvName.setText(user.getName());
            Glide.with(activity)
                    .load(user.getAvatar_url())
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.ivAvatar);
        } else {
            holder.tvName.setText(id);
        }

        holder.tvTime.setText(activity.getRelativeDateTimeString(time));

    }

    @Override
    public int getItemCount() {
        return idList.size();
    }
}
