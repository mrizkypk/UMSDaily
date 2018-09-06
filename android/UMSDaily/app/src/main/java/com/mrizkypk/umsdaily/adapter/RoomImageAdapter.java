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

public class RoomImageAdapter extends RecyclerView.Adapter<RoomImageAdapter.ViewHolder> {
    public Context context;
    public HomeActivity activity;
    public List<ChatRoomModel> dataList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public Button btnMore;
        public ImageView ivThumbnail;
        public ProgressBar progressbar;

        public ViewHolder(View v) {
            super(v);
            btnMore = v.findViewById(R.id.item_room_image_button_more);
            ivThumbnail = v.findViewById(R.id.item_room_image_thumbnail);
            progressbar = v.findViewById(R.id.item_room_image_progressbar);
        }
    }

    public RoomImageAdapter() {
    }

    public RoomImageAdapter(Context ctx, LinkedList<ChatRoomModel> list) {
        context = ctx;
        dataList = list;

        activity = (HomeActivity) context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room_image, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ChatRoomModel model = dataList.get(position);
        if (position == getItemCount() - 1 && getItemCount() > 3) {
            holder.btnMore.setVisibility(View.VISIBLE);
            holder.btnMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (model.getRoom_type().equals("public")) {
                        activity.openRoomImageFragment(model.getRoom_id(), model.getRoom_title(), 1);
                    } else {
                        activity.openPrivateRoomImageFragment(activity.roomDetailFragment.roomId,activity. roomDetailFragment.roomTitle, 1);
                    }
                }
            });
            holder.ivThumbnail.setVisibility(View.GONE);
        } else {
            holder.progressbar.setVisibility(View.VISIBLE);
            holder.ivThumbnail.setVisibility(View.VISIBLE);
            holder.btnMore.setVisibility(View.GONE);

            holder.ivThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (model.getRoom_type().equals("public")) {
                        activity.openImageFromRoomFragment(model);
                    } else {
                        activity.openPrivateImageFromRoomFragment(model);
                    }
                }
            });

            Glide.with(activity)
                    .load(model.getFile_download_url())
                    .apply(RequestOptions.overrideOf(75, 75))
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
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
