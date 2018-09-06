package com.mrizkypk.umsdaily.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mrizkypk.umsdaily.R;
import com.mrizkypk.umsdaily.activity.HomeActivity;
import com.mrizkypk.umsdaily.model.ChatRoomModel;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RoomFileAdapter extends RecyclerView.Adapter<RoomFileAdapter.ViewHolder> {
    public Context context;
    public String localPath;
    public HomeActivity activity;
    public List<ChatRoomModel> dataList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvFileName;
        public TextView tvFileSize;
        public TextView tvFileExtension;
        public ImageView ivFileImage;
        public Button btnMore;
        public ProgressBar progressbar;
        public ConstraintLayout clLayout;

        public ViewHolder(View v) {
            super(v);
            tvFileName = v.findViewById(R.id.item_room_file_text_file_name);
            tvFileSize = v.findViewById(R.id.item_room_file_text_file_size);
            tvFileExtension = v.findViewById(R.id.item_room_file_text_file_extension);
            ivFileImage = v.findViewById(R.id.item_room_file_image_file);
            btnMore = v.findViewById(R.id.item_room_file_button_more);
            progressbar = v.findViewById(R.id.item_room_file_progressbar);
            clLayout = v.findViewById(R.id.item_room_file_layout);
        }
    }

    public RoomFileAdapter() {
    }

    public RoomFileAdapter(Context ctx, LinkedList<ChatRoomModel> list) {
        context = ctx;
        dataList = list;

        activity = (HomeActivity) context;
        localPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/UMSDaily/";
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room_file, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ChatRoomModel model = dataList.get(position);

        if (position == getItemCount() - 1 && getItemCount() > 3) {
            holder.btnMore.setVisibility(View.VISIBLE);
            holder.btnMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (model.getRoom_type().equals("public")) {
                        activity.openRoomFileFragment(model.getRoom_id(), model.getRoom_title(), 1);
                    } else {
                        activity.openPrivateRoomFileFragment(activity.roomDetailFragment.roomId, activity.roomDetailFragment.roomTitle, 1);
                    }
                }
            });
            holder.ivFileImage.setVisibility(View.GONE);
            holder.tvFileName.setVisibility(View.GONE);
            holder.tvFileSize.setVisibility(View.GONE);
            holder.tvFileExtension.setVisibility(View.GONE);
        } else {
            holder.ivFileImage.setColorFilter(Color.parseColor(getFileExtensionIconColor(model.getFile_extension())));

            holder.btnMore.setVisibility(View.GONE);
            holder.ivFileImage.setVisibility(View.VISIBLE);
            holder.tvFileName.setVisibility(View.VISIBLE);
            holder.tvFileSize.setVisibility(View.VISIBLE);
            holder.tvFileExtension.setVisibility(View.VISIBLE);

            holder.tvFileName.setText(model.getFile_name());
            holder.tvFileSize.setText(model.getFile_size());
            holder.tvFileExtension.setText(model.getFile_extension().toUpperCase());

            //Open file
            final File file = new File(localPath + model.getFile_name());
            holder.clLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (file.exists()) {
                        activity.openFile(localPath + model.getFile_name());
                    } else {
                        downloadFile(holder, position);
                    }
                }
            });
            //End open file
        }
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

    public void downloadFile(final RoomFileAdapter.ViewHolder holder, int position) {
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
