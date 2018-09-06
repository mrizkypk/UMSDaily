package com.mrizkypk.umsdaily.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mrizkypk.umsdaily.R;
import com.mrizkypk.umsdaily.activity.HomeActivity;
import com.mrizkypk.umsdaily.model.UserModel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class RoomUserAdapter extends RecyclerView.Adapter<RoomUserAdapter.ViewHolder> {
    public Context context;
    public HomeActivity activity;
    public ArrayList<String> idList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvId;
        public TextView tvName;
        public ImageView ivAvatar;
        public ConstraintLayout clLayout;

        public ViewHolder(View v) {
            super(v);
            tvId = v.findViewById(R.id.item_room_user_text_id);
            tvName = v.findViewById(R.id.item_room_user_text_name);
            ivAvatar = v.findViewById(R.id.item_room_user_image_avatar);
            clLayout = v.findViewById(R.id.item_room_user_layout);
        }
    }

    public RoomUserAdapter() {
    }

    public RoomUserAdapter(Context ctx, ArrayList<String> ids) {
        context = ctx;
        idList = ids;

        activity = (HomeActivity) context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room_user, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final String id = idList.get(position);

        if (activity.userModelMap.containsKey(id)) {
            UserModel user = activity.userModelMap.get(id);
            holder.tvName.setText(user.getName());
            holder.tvId.setText(user.getId());
            Glide.with(activity)
                    .load(user.getAvatar_url())
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.ivAvatar);
        } else {
            holder.tvName.setText(id);
            holder.tvId.setText(id);
            Glide.with(activity)
                    .load("https://api.adorable.io/avatars/56/" + id + ".png")
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.ivAvatar);
        }

        holder.clLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!activity.spm.getUserId().equals(id)) {
                    String name = "";
                    String privateRoomId = "";
                    if (activity.userModelMap.containsKey(id)) {
                        UserModel user = activity.userModelMap.get(id);
                        name = user.getName();
                    } else {
                        name = id;
                    }
                    int compare = id.compareTo(activity.spm.getUserId());
                    if (compare < 0) {
                        privateRoomId = id + "@" + activity.spm.getUserId();
                        activity.createPrivateRoom(privateRoomId);
                        activity.openPrivateChatRoomFromRoomFragment(privateRoomId, name);
                    } else {
                        privateRoomId = activity.spm.getUserId() + "@" + id;
                        activity.createPrivateRoom(privateRoomId);
                        activity.openPrivateChatRoomFromRoomFragment(privateRoomId, name);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return idList.size();
    }
}
