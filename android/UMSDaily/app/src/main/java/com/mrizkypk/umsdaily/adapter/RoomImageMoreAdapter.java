package com.mrizkypk.umsdaily.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.mrizkypk.umsdaily.R;
import com.mrizkypk.umsdaily.activity.HomeActivity;
import com.mrizkypk.umsdaily.model.ChatRoomModel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RoomImageMoreAdapter extends RecyclerView.Adapter<RoomImageMoreAdapter.ViewHolder> {
    public Context context;
    public HomeActivity activity;
    public List<ChatRoomModel> dataList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivThumbnail;
        public ProgressBar progressbar;

        public ViewHolder(View v) {
            super(v);
            ivThumbnail = v.findViewById(R.id.item_room_image_more_thumbnail);
            progressbar = v.findViewById(R.id.item_room_image_more_progressbar);
        }
    }

    public RoomImageMoreAdapter() {
    }

    public RoomImageMoreAdapter(Context ctx, LinkedList<ChatRoomModel> list) {
        context = ctx;
        dataList = list;

        activity = (HomeActivity) context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room_image_more, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.progressbar.setVisibility(View.VISIBLE);
        holder.ivThumbnail.setVisibility(View.VISIBLE);

        final ChatRoomModel model = dataList.get(position);

        holder.ivThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (model.getRoom_type().equals("public")) {
                        activity.openImageFromRoomImageFragment(model);
                    } else {
                        activity.openPrivateImageFromRoomImageFragment(model);
                    }
                }
            });

        Glide.with(activity)
                .load(model.getFile_download_url())
                .apply(RequestOptions.overrideOf(150, 150))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progressbar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressbar.setVisibility(View.GONE);
                        return false;
                    }
                }).into(holder.ivThumbnail);

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
