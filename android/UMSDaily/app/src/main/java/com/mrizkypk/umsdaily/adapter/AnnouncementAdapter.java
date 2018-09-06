package com.mrizkypk.umsdaily.adapter;

import android.app.NotificationManager;
import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DatabaseReference;
import com.mrizkypk.umsdaily.R;
import com.mrizkypk.umsdaily.activity.HomeActivity;
import com.mrizkypk.umsdaily.model.AnnouncementModel;

import java.text.SimpleDateFormat;
import java.util.LinkedList;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.ViewHolder> {
    public Context context;
    public HomeActivity activity;
    public LinkedList<AnnouncementModel> dataList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView tvTitle;
        public TextView tvDate;
        public TextView tvFilter;
        public ImageView ivContent;
        public TextView tvContent;
        public ImageView ivDelete;

        public ViewHolder(View v) {
            super(v);
            view = v;
            tvTitle = view.findViewById(R.id.item_announcement_text_title);
            tvDate = view.findViewById(R.id.item_announcement_text_date);
            tvFilter = view.findViewById(R.id.item_announcement_text_filter);
            ivContent = view.findViewById(R.id.item_announcement_image_content);
            tvContent = view.findViewById(R.id.item_announcement_text_content);
            ivDelete = view.findViewById(R.id.item_announcement_image_delete);
        }
    }

    public AnnouncementAdapter() {

    }

    public AnnouncementAdapter(Context ctx, LinkedList<AnnouncementModel> list) {
        dataList = list;
        context = ctx;
        activity = (HomeActivity) ctx;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_announcement, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final AnnouncementModel model = dataList.get(position);
        //Clear notification after read
        NotificationManager notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(model.getId().hashCode());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (notificationManager.getActiveNotifications().length == 1) {
                notificationManager.cancel(0);
            }
        }

        String dateString = new SimpleDateFormat("dd - MM - yyyy").format(model.getTimestamp());

        holder.tvTitle.setText(model.getTitle());
        holder.tvDate.setText("Dikirim oleh " + model.getSender_name() + " pada " + dateString);

        if (activity.spm.getUserType().equals("staff")) {
            holder.tvFilter.setVisibility(View.VISIBLE);
            if (model.getFilter() == null) {
                holder.tvFilter.setText("Dikirim kepada: Semua");
            } else {
                if (model.getFilter().equals("")) {
                    holder.tvFilter.setText("Dikirim kepada: Semua");
                } else {
                    holder.tvFilter.setText("Dikirim kepada: " + model.getFilter());
                }
            }
        } else {
            holder.tvFilter.setVisibility(View.GONE);
        }

        if (activity.spm.getUserType().equals("staff")) {
            holder.ivDelete.setVisibility(View.VISIBLE);
            holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setMessage("Anda yakin untuk menghapus pengumuman ini?")
                            .setTitle("Hapus Pengumuman");
                    builder.setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            DatabaseReference reference = activity.db.getReference().child("announcement").child(model.getId());
                            reference.removeValue();
                        }
                    });
                    builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        } else {
            holder.ivDelete.setVisibility(View.GONE);
        }

        if (model.getImage_url().isEmpty()) {
            holder.ivContent.setVisibility(View.GONE);
        } else {
            holder.ivContent.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(model.getImage_url())
                    .apply(RequestOptions.centerCropTransform())
                    .into(holder.ivContent);

            holder.ivContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.openImageFromAnnouncementFragment(model);
                }
            });
        }
        holder.tvContent.setText(model.getContent() + " ...");

        if (model.getUrl().isEmpty()) {
            holder.tvTitle.setTextColor(Color.parseColor("#212121"));
        } else {
            holder.tvTitle.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            holder.tvTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(model.getUrl()));
                    context.startActivity(i);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
