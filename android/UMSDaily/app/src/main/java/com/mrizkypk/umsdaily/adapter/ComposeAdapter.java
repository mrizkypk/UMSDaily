package com.mrizkypk.umsdaily.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mrizkypk.umsdaily.R;
import com.mrizkypk.umsdaily.activity.HomeActivity;
import com.mrizkypk.umsdaily.model.UserModel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ComposeAdapter extends RecyclerView.Adapter<ComposeAdapter.ViewHolder> {
    public Context context;
    public HomeActivity activity;
    public LinkedList<UserModel> dataList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvId;
        public TextView tvName;
        public ImageView ivAvatar;
        public ConstraintLayout clLayout;

        public ViewHolder(View v) {
            super(v);
            tvId = v.findViewById(R.id.item_compose_text_id);
            tvName = v.findViewById(R.id.item_compose_text_name);
            ivAvatar = v.findViewById(R.id.item_compose_image_avatar);
            clLayout = v.findViewById(R.id.item_compose_layout);
        }
    }

    public ComposeAdapter() {
    }

    public ComposeAdapter(Context ctx, LinkedList<UserModel> ids) {
        context = ctx;
        dataList = ids;

        activity = (HomeActivity) context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_compose, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final UserModel user = dataList.get(position);

        holder.tvName.setText(user.getName());
        holder.tvId.setText(user.getId());
        Glide.with(activity)
                .load(user.getAvatar_url())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.ivAvatar);
        holder.clLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!activity.spm.getUserId().equals(user.getId())) {
                    String name = user.getName();
                    String privateRoomId = "";
                    int compare = user.getId().compareTo(activity.spm.getUserId());
                    if (compare < 0) {
                        privateRoomId = user.getId() + "@" + activity.spm.getUserId();
                        activity.createPrivateRoom(privateRoomId);
                        activity.openPrivateChatRoomFromComposeFragment(privateRoomId, name);
                    } else {
                        privateRoomId = activity.spm.getUserId() + "@" + user.getId();
                        activity.createPrivateRoom(privateRoomId);
                        activity.openPrivateChatRoomFromComposeFragment(privateRoomId, name);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
