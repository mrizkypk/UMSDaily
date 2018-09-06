package com.mrizkypk.umsdaily.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.mrizkypk.umsdaily.R;
import com.mrizkypk.umsdaily.activity.HomeActivity;
import com.mrizkypk.umsdaily.helper.DateHelper;
import com.mrizkypk.umsdaily.model.ChatRoomModel;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class RoomFileMoreAdapter extends RecyclerView.Adapter<RoomFileMoreAdapter.ViewHolder> {
    public Context context;
    public String localPath;
    public HomeActivity activity;
    public List<ChatRoomModel> dataList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvSender;
        public TextView tvFileName;
        public TextView tvFileSize;
        public TextView tvFileExtension;
        public TextView tvTime;
        public RelativeLayout rlFileImage;
        public ProgressBar progressbar;
        public ConstraintLayout clLayout;

        public ViewHolder(View v) {
            super(v);
            tvSender = v.findViewById(R.id.item_room_file_more_text_sender);
            tvFileName = v.findViewById(R.id.item_room_file_more_text_file_name);
            tvFileSize = v.findViewById(R.id.item_room_file_more_text_file_size);
            tvFileExtension = v.findViewById(R.id.item_room_file_more_text_file_extension);
            tvTime = v.findViewById(R.id.item_room_file_more_text_time);
            rlFileImage = v.findViewById(R.id.item_room_file_more_container_image_file);
            progressbar = v.findViewById(R.id.item_room_file_more_progressbar);
            clLayout = v.findViewById(R.id.item_room_file_more_layout);
        }
    }

    public RoomFileMoreAdapter() {
    }

    public RoomFileMoreAdapter(Context ctx, LinkedList<ChatRoomModel> list) {
        context = ctx;
        dataList = list;

        activity = (HomeActivity) context;
        localPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/UMSDaily/";
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room_file_more, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ChatRoomModel model = dataList.get(position);

        Drawable background = holder.rlFileImage.getBackground();
        background.setTint(Color.parseColor(getFileExtensionIconColor(model.getFile_extension())));
        holder.rlFileImage.setBackground(background);
        holder.tvSender.setText(model.getSender_name());
        holder.tvFileName.setText(model.getFile_name());
        holder.tvFileSize.setText(model.getFile_size());
        holder.tvFileExtension.setText(model.getFile_extension().toUpperCase());
        holder.tvTime.setText(activity.getRelativeDateTimeString(model.getTimestamp()));

        //Open file
        final File file = new File(localPath + model.getFile_name());
        holder.clLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
            if (file.exists()) {
                activity.openFile(localPath + model.getFile_name());
            } else {
                downloadFile(holder, position); }
                }
        });
        //End open file
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    private String getFileExtensionIconColor(String ext) {
        switch (ext) {
            case "pdf":
                return "#D32F2F";
            case "doc":
            case "docx":
                return "#1976D2";
            case "xls":
            case "xlsx":
                return "#388E3C";
            case "ppt":
            case "pptx":
                return "#FFA000";
            default:
                return "#9E9E9E";
        }
    }

    public void downloadFile(final RoomFileMoreAdapter.ViewHolder holder, int position) {
        final ChatRoomModel model = dataList.get(position);
        holder.progressbar.setProgress(0);
        holder.progressbar.setMax(100);
        holder.progressbar.setVisibility(View.VISIBLE);

        AndroidNetworking.download(model.getFile_download_url(), localPath, model.getFile_name())
                .build()
                .setDownloadProgressListener(new DownloadProgressListener() {
                    @Override
                    public void onProgress(long bytesDownloaded, long totalBytes) {
                        int progress = (100 * (int) bytesDownloaded) / (int) totalBytes;
                        holder.progressbar.setProgress(progress);
                    }
                })
                .startDownload(new DownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        holder.progressbar.setVisibility(View.GONE);
                        activity.openFile(localPath + model.getFile_name());
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        holder.progressbar.setVisibility(View.GONE);
                    }
                });
    }
}
