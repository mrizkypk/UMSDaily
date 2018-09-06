package com.mrizkypk.umsdaily.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.mrizkypk.umsdaily.R;
import com.mrizkypk.umsdaily.model.ChatRoomModel;
import com.mrizkypk.umsdaily.activity.HomeActivity;

import java.io.File;
import java.util.LinkedList;


/**
 * Created by mrizkypk on 22/03/18.
 */


public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public String userId;
    public String localPath;
    public LinkedList<ChatRoomModel> dataList;
    public Context context;
    public HomeActivity activity;

    public static final int TYPE_MESSAGE_SENDER = 0;
    public static final int TYPE_MESSAGE_RECEIVER = 1;
    public static final int TYPE_INFO_JOIN = 2;
    public static final int TYPE_IMAGE_SENDER = 3;
    public static final int TYPE_IMAGE_RECEIVER = 4;
    public static final int TYPE_FILE_SENDER = 5;
    public static final int TYPE_FILE_RECEIVER = 6;

    public ChatAdapter() {
    }

    public class ViewHolderMessageSender extends RecyclerView.ViewHolder {
        public View view;
        public TextView tvMessage;
        public TextView tvTime;
        public ImageView ivStatus;
        public ConstraintLayout clBox;
        public ConstraintLayout clLayout;
        public ConstraintLayout clContainer;
        public LinearLayout llReply;
        public LinearLayout clFile;
        public LinearLayout clImage;
        public LinearLayout clMessage;
        public TextView tvReplyFileSender;
        public TextView tvReplyImageSender;
        public TextView tvReplyMessageSender;
        public TextView tvReplyImage;
        public TextView tvReplyMessage;
        public ImageView ivReplyImage;
        public TextView tvReplyFileName;
        public TextView tvReplyFileExtension;

        public ViewHolderMessageSender(View v) {
            super(v);
            view = v;
            tvMessage = view.findViewById(R.id.item_room_chat_message_right_text_message);
            tvTime = view.findViewById(R.id.item_room_chat_message_right_text_time);
            ivStatus = view.findViewById(R.id.item_room_chat_message_right_image_status);
            clBox = view.findViewById(R.id.item_room_chat_message_right_container);
            clLayout = view.findViewById(R.id.item_room_chat_message_right_layout);
            clContainer = view.findViewById(R.id.item_room_chat_message_right_container_transparent);
            llReply = view.findViewById(R.id.item_room_chat_message_right_container_reply);
            clFile = view.findViewById(R.id.item_room_chat_file_container);
            clImage = view.findViewById(R.id.item_room_chat_image_container);
            clMessage = view.findViewById(R.id.item_room_chat_message_container);
            tvReplyFileSender = view.findViewById(R.id.item_room_chat_file_text_sender);
            tvReplyImageSender = view.findViewById(R.id.item_room_chat_image_text_sender);
            tvReplyMessageSender = view.findViewById(R.id.item_room_chat_message_text_sender);
            tvReplyImage = view.findViewById(R.id.item_room_chat_image_text_label);
            tvReplyMessage = view.findViewById(R.id.item_room_chat_message_text_message);
            ivReplyImage = view.findViewById(R.id.item_room_chat_image);
            tvReplyFileName = view.findViewById(R.id.item_room_chat_file_text_file_name);
            tvReplyFileExtension = view.findViewById(R.id.item_room_chat_file_text_file_extension);
        }
    }

    public class ViewHolderMessageReceiver extends RecyclerView.ViewHolder {
        public View view;
        public TextView tvSender;
        public TextView tvMessage;
        public TextView tvTime;
        public ConstraintLayout clBox;
        public ConstraintLayout clLayout;
        public ConstraintLayout clContainer;
        public LinearLayout llReply;
        public LinearLayout clFile;
        public LinearLayout clImage;
        public LinearLayout clMessage;
        public TextView tvReplyFileSender;
        public TextView tvReplyImageSender;
        public TextView tvReplyMessageSender;
        public TextView tvReplyImage;
        public TextView tvReplyMessage;
        public ImageView ivReplyImage;
        public TextView tvReplyFileName;
        public TextView tvReplyFileExtension;

        public ViewHolderMessageReceiver(View v) {
            super(v);
            view = v;
            tvSender = view.findViewById(R.id.item_room_chat_message_left_text_sender);
            tvMessage = view.findViewById(R.id.item_room_chat_message_left_text_message);
            tvTime = view.findViewById(R.id.item_room_chat_message_left_text_time);
            clBox = view.findViewById(R.id.item_room_chat_message_left_container);
            clLayout = view.findViewById(R.id.item_room_chat_message_left_layout);
            clContainer = view.findViewById(R.id.item_room_chat_message_left_container_transparent);
            llReply = view.findViewById(R.id.item_room_chat_message_left_container_reply);
            clFile = view.findViewById(R.id.item_room_chat_file_container);
            clImage = view.findViewById(R.id.item_room_chat_image_container);
            clMessage = view.findViewById(R.id.item_room_chat_message_container);
            tvReplyFileSender = view.findViewById(R.id.item_room_chat_file_text_sender);
            tvReplyImageSender = view.findViewById(R.id.item_room_chat_image_text_sender);
            tvReplyMessageSender = view.findViewById(R.id.item_room_chat_message_text_sender);
            tvReplyImage = view.findViewById(R.id.item_room_chat_image_text_label);
            tvReplyMessage = view.findViewById(R.id.item_room_chat_message_text_message);
            ivReplyImage = view.findViewById(R.id.item_room_chat_image);
            tvReplyFileName = view.findViewById(R.id.item_room_chat_file_text_file_name);
            tvReplyFileExtension = view.findViewById(R.id.item_room_chat_file_text_file_extension);
        }
    }

    public class ViewHolderImageSender extends RecyclerView.ViewHolder {
        public View view;
        public TextView tvMessage;
        public ImageView ivImage;
        public ProgressBar progressbar;
        public ProgressBar loadProgressbar;
        public TextView tvProgressbarStop;
        public TextView tvTime;
        public ImageView ivProgressbarReupload;
        public ImageView ivStatus;
        public ConstraintLayout clBox;
        public ConstraintLayout clLayout;
        public ConstraintLayout clContainer;

        public ViewHolderImageSender(View v) {
            super(v);
            view = v;
            tvMessage = view.findViewById(R.id.item_room_chat_message_right_text_message);
            ivImage = view.findViewById(R.id.item_room_chat_message_right_image);
            tvTime = view.findViewById(R.id.item_room_chat_message_right_text_time);
            progressbar = view.findViewById(R.id.item_room_chat_message_right_progressbar_percentage);
            loadProgressbar = view.findViewById(R.id.item_room_chat_message_right_progressbar);
            tvProgressbarStop = view.findViewById(R.id.item_room_chat_message_right_text_progressbar_stop);
            ivProgressbarReupload = view.findViewById(R.id.item_room_chat_message_right_image_progressbar_reupload);
            clBox = view.findViewById(R.id.item_room_chat_message_right_container);
            clLayout = view.findViewById(R.id.item_room_chat_message_right_layout);
            clContainer = view.findViewById(R.id.item_room_chat_message_right_container_transparent);
            ivStatus = view.findViewById(R.id.item_room_chat_message_right_image_status);
        }
    }

    public class ViewHolderImageReceiver extends RecyclerView.ViewHolder {
        public View view;
        public TextView tvSender;
        public TextView tvMessage;
        public ImageView ivImage;
        public ProgressBar progressbar;
        public ProgressBar loadProgressbar;
        public TextView tvProgressbarStop;
        public TextView tvTime;
        public ImageView ivProgressbarReupload;
        public ConstraintLayout clBox;
        public ConstraintLayout clLayout;
        public ConstraintLayout clContainer;

        public ViewHolderImageReceiver(View v) {
            super(v);
            view = v;
            tvSender = view.findViewById(R.id.item_room_chat_message_left_text_sender);
            tvMessage = view.findViewById(R.id.item_room_chat_message_left_text_message);
            ivImage = view.findViewById(R.id.item_room_chat_message_left_image);
            tvTime = view.findViewById(R.id.item_room_chat_message_left_text_time);
            progressbar = view.findViewById(R.id.item_room_chat_message_left_progressbar_percentage);
            loadProgressbar = view.findViewById(R.id.item_room_chat_message_left_progressbar);
            tvProgressbarStop = view.findViewById(R.id.item_room_chat_message_left_text_progressbar_stop);
            ivProgressbarReupload = view.findViewById(R.id.item_room_chat_message_left_image_progressbar_reupload);
            clBox = view.findViewById(R.id.item_room_chat_message_left_container);
            clLayout = view.findViewById(R.id.item_room_chat_message_left_layout);
            clContainer = view.findViewById(R.id.item_room_chat_message_left_container_transparent);
        }
    }

    public class ViewHolderFileSender extends RecyclerView.ViewHolder {
        public View view;
        public ProgressBar progressbar;
        public TextView tvProgressbarStop;
        public RelativeLayout rlFile;
        public TextView tvTime;
        public TextView tvFileName;
        public TextView tvFileSize;
        public TextView tvFileExtension;
        public ImageView ivProgressbarReupload;
        public ImageView ivStatus;
        public ImageView ivFileDownload;
        public ConstraintLayout clBox;
        public ConstraintLayout clLayout;
        public ConstraintLayout clContainer;

        public ViewHolderFileSender(View v) {
            super(v);
            view = v;
            tvTime = view.findViewById(R.id.item_room_chat_file_right_text_time);
            rlFile = view.findViewById(R.id.item_room_chat_file_right_container_image_file);
            tvFileName = view.findViewById(R.id.item_room_chat_file_right_text_file_name);
            tvFileSize = view.findViewById(R.id.item_room_chat_file_right_text_file_size);
            tvFileExtension = view.findViewById(R.id.item_room_chat_file_right_text_file_extension);
            progressbar = view.findViewById(R.id.item_room_chat_file_right_progressbar_percentage);
            tvProgressbarStop = view.findViewById(R.id.item_room_chat_file_right_text_progressbar_stop);
            ivProgressbarReupload = view.findViewById(R.id.item_room_chat_file_right_image_progressbar_reupload);
            clBox = view.findViewById(R.id.item_room_chat_file_right_container);
            clLayout = view.findViewById(R.id.item_room_chat_file_right_layout);
            clContainer = view.findViewById(R.id.item_room_chat_file_right_container_transparent);
            ivStatus = view.findViewById(R.id.item_room_chat_file_right_image_status);
            ivFileDownload = view.findViewById(R.id.item_room_chat_file_right_image_file_download);
        }
    }

    public class ViewHolderFileReceiver extends RecyclerView.ViewHolder {
        public View view;
        public TextView tvSender;
        public ProgressBar progressbar;
        public TextView tvProgressbarStop;
        public TextView tvTime;
        public RelativeLayout rlFile;
        public TextView tvFileName;
        public TextView tvFileSize;
        public TextView tvFileExtension;
        public ImageView ivProgressbarReupload;
        public ImageView ivFileDownload;
        public ConstraintLayout clBox;
        public ConstraintLayout clLayout;
        public ConstraintLayout clContainer;

        public ViewHolderFileReceiver(View v) {
            super(v);
            view = v;
            tvSender = view.findViewById(R.id.item_room_chat_file_left_text_sender);
            tvTime = view.findViewById(R.id.item_room_chat_file_left_text_time);
            rlFile = view.findViewById(R.id.item_room_chat_file_left_container_image_file);
            tvFileName = view.findViewById(R.id.item_room_chat_file_left_text_file_name);
            tvFileSize = view.findViewById(R.id.item_room_chat_file_left_text_file_size);
            tvFileExtension = view.findViewById(R.id.item_room_chat_file_left_text_file_extension);
            progressbar = view.findViewById(R.id.item_room_chat_file_left_progressbar_percentage);
            tvProgressbarStop = view.findViewById(R.id.item_room_chat_file_left_text_progressbar_stop);
            ivProgressbarReupload = view.findViewById(R.id.item_room_chat_file_left_image_progressbar_reupload);
            clBox = view.findViewById(R.id.item_room_chat_file_left_container);
            clLayout = view.findViewById(R.id.item_room_chat_file_left_layout);
            clContainer = view.findViewById(R.id.item_room_chat_file_left_container_transparent);
            ivFileDownload = view.findViewById(R.id.item_room_chat_file_left_image_file_download);
        }
    }

    public class ViewHolderInfo extends RecyclerView.ViewHolder {
        public View view;
        public TextView tvMessage;

        public ViewHolderInfo(View view) {
            super(view);
            this.view = view;
            tvMessage = view.findViewById(R.id.item_room_chat_info_text_message);
        }
    }

    public ChatAdapter(Context ctx, String id, LinkedList<ChatRoomModel> list) {
        userId = id;
        dataList = list;
        context = ctx;
        activity = ((HomeActivity) ctx);
        localPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/UMSDaily/";
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case TYPE_MESSAGE_SENDER:
                View v1 = inflater.inflate(R.layout.item_room_chat_message_right, parent, false);
                viewHolder = new ViewHolderMessageSender(v1);
                break;
            case TYPE_MESSAGE_RECEIVER:
                View v2 = inflater.inflate(R.layout.item_room_chat_message_left, parent, false);
                viewHolder = new ViewHolderMessageReceiver(v2);
                break;
            case TYPE_IMAGE_SENDER:
                View v3 = inflater.inflate(R.layout.item_room_chat_message_right, parent, false);
                viewHolder = new ViewHolderImageSender(v3);
                break;
            case TYPE_IMAGE_RECEIVER:
                View v4 = inflater.inflate(R.layout.item_room_chat_message_left, parent, false);
                viewHolder = new ViewHolderImageReceiver(v4);
                break;
            case TYPE_FILE_SENDER:
                View v5 = inflater.inflate(R.layout.item_room_chat_file_right, parent, false);
                viewHolder = new ViewHolderFileSender(v5);
                break;
            case TYPE_FILE_RECEIVER:
                View v6 = inflater.inflate(R.layout.item_room_chat_file_left, parent, false);
                viewHolder = new ViewHolderFileReceiver(v6);
                break;
            case TYPE_INFO_JOIN:
                View v7 = inflater.inflate(R.layout.item_room_chat_info, parent, false);
                viewHolder = new ViewHolderInfo(v7);
                break;
            default:
                View v = inflater.inflate(R.layout.item_room_chat_message_right, parent, false);
                viewHolder = new ViewHolderMessageSender(v);
                break;
        }

        return viewHolder;

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final ChatRoomModel model = dataList.get(position);
        ChatRoomModel prevModel = null;
        if (position - 1 <= dataList.size() - 1) {
            if (position - 1 >= 0) {
                prevModel = dataList.get(position - 1);
            }
        }

        if (model.getType().equals("message")) {
            if (model.getSender_id().equals(userId)) {
                //Set chat margin
                if (prevModel != null) {
                    if (model.getSender_id().equals(prevModel.getSender_id())) {
                        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) ((ViewHolderMessageSender) holder).clLayout.getLayoutParams();
                        params.setMargins(8, 4, 8, 4);
                        ((ViewHolderMessageSender) holder).clLayout.setLayoutParams(params);
                    } else {
                        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) ((ViewHolderMessageSender) holder).clLayout.getLayoutParams();
                        params.setMargins(8, 16, 8, 4);
                        ((ViewHolderMessageSender) holder).clLayout.setLayoutParams(params);
                    }
                } else {
                    RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) ((ViewHolderMessageSender) holder).clLayout.getLayoutParams();
                    params.setMargins(8, 8, 8, 4);
                    ((ViewHolderMessageSender) holder).clLayout.setLayoutParams(params);
                }

                //Set chat status

                if (model.getReceived().size() > 1) {
                    ((ViewHolderMessageSender) holder).ivStatus.setImageResource(R.drawable.ic_done_all);
                } else {
                    ((ViewHolderMessageSender) holder).ivStatus.setImageResource(R.drawable.ic_done);
                }

                if (activity.isChatReadByAll(model)) {
                    ((ViewHolderMessageSender) holder).ivStatus.setColorFilter(ContextCompat.getColor(activity, R.color.colorPrimary));
                } else {
                    ((ViewHolderMessageSender) holder).ivStatus.setColorFilter(Color.parseColor("#6BA55B"));
                }

                //Set Chat Reply

                if (model.getReply_type() != null) {
                    ((ViewHolderMessageSender) holder).llReply.setVisibility(View.VISIBLE);

                    StateListDrawable stateListDrawable = (StateListDrawable) ((ViewHolderMessageSender) holder).llReply.getBackground();
                    DrawableContainer.DrawableContainerState drawableContainerState = (DrawableContainer.DrawableContainerState) stateListDrawable.getConstantState();
                    Drawable[] children = drawableContainerState.getChildren();

                    LayerDrawable layerDrawable = (LayerDrawable) children[1];
                    GradientDrawable gradientDrawable = (GradientDrawable) layerDrawable.findDrawableByLayerId(R.id.layer_list_item_room_chat_message_right_reply_border);
                    gradientDrawable.setColor(activity.getColor(model.getReply_sender_name()));

                    switch (model.getReply_type()) {
                        case "message":
                            ((ViewHolderMessageSender) holder).clFile.setVisibility(View.GONE);
                            ((ViewHolderMessageSender) holder).clImage.setVisibility(View.GONE);
                            ((ViewHolderMessageSender) holder).clMessage.setVisibility(View.VISIBLE);

                            ((ViewHolderMessageSender) holder).tvReplyMessage.setText(model.getReply_message());
                            ((ViewHolderMessageSender) holder).tvReplyMessageSender.setText(model.getReply_sender_name());
                            ((ViewHolderMessageSender) holder).tvReplyMessageSender.setTextColor(activity.getColor(model.getReply_sender_name()));

                            break;
                        case "file":
                            ((ViewHolderMessageSender) holder).clFile.setVisibility(View.VISIBLE);
                            ((ViewHolderMessageSender) holder).clImage.setVisibility(View.GONE);
                            ((ViewHolderMessageSender) holder).clMessage.setVisibility(View.GONE);

                            ((ViewHolderMessageSender) holder).tvReplyFileName.setText(model.getReply_file_name());
                            ((ViewHolderMessageSender) holder).tvReplyFileExtension.setText(model.getReply_file_extension().toUpperCase());
                            ((ViewHolderMessageSender) holder).tvReplyFileSender.setText(model.getReply_sender_name());
                            ((ViewHolderMessageSender) holder).tvReplyFileSender.setTextColor(activity.getColor(model.getReply_sender_name()));

                            break;
                        case "image":
                            ((ViewHolderMessageSender) holder).clFile.setVisibility(View.GONE);
                            ((ViewHolderMessageSender) holder).clImage.setVisibility(View.VISIBLE);
                            ((ViewHolderMessageSender) holder).clMessage.setVisibility(View.GONE);

                            Glide.with(activity)
                                    .load(model.getReply_file_download_url())
                                    .apply(RequestOptions.centerCropTransform())
                                    .apply(RequestOptions.overrideOf(50, 50))
                                    .into(((ViewHolderMessageSender) holder).ivReplyImage);

                            if (!model.getReply_message().isEmpty()) {
                                ((ViewHolderMessageSender) holder).tvReplyImage.setText(model.getReply_message());
                            } else {
                                ((ViewHolderMessageSender) holder).tvReplyImage.setText("Gambar");
                            }
                            ((ViewHolderMessageSender) holder).tvReplyImageSender.setText(model.getReply_sender_name());
                            ((ViewHolderMessageSender) holder).tvReplyImageSender.setTextColor(activity.getColor(model.getReply_sender_name()));

                            break;
                    }
                } else {
                    ((ViewHolderMessageSender) holder).llReply.setVisibility(View.GONE);
                }

                ((ViewHolderMessageSender) holder).tvMessage.setText(model.getMessage());
                ((ViewHolderMessageSender) holder).tvTime.setText(activity.getFormattedTime(model.getTimestamp()));
                ((ViewHolderMessageSender) holder).clLayout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (activity.roomFragment.isSelected(model)) {
                            view.setSelected(false);
                            ((ViewHolderMessageSender) holder).clContainer.setVisibility(View.GONE);
                            activity.roomFragment.removeSelected(model);
                            activity.roomFragment.updateSelected();
                        } else {
                            view.setSelected(true);
                            ((ViewHolderMessageSender) holder).clContainer.setVisibility(View.VISIBLE);
                            activity.roomFragment.addSelected(model);
                            activity.roomFragment.updateSelected();
                        }
                        return false;
                    }
                });
                ((ViewHolderMessageSender) holder).clBox.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_UP:
                                if (((ViewHolderMessageSender) holder).clLayout.isSelected()) {
                                    view.setPressed(false);
                                } else {
                                    view.setPressed(false);
                                }
                                return false;
                            case MotionEvent.ACTION_DOWN:
                                if (((ViewHolderMessageSender) holder).clLayout.isSelected()) {
                                    view.setPressed(false);
                                } else {
                                    view.setPressed(true);
                                }
                                return false;
                        }
                        return false;
                    }
                });

                if (activity.roomFragment.isSelected(model)) {
                    ((ViewHolderMessageSender) holder).clBox.setPressed(false);
                    ((ViewHolderMessageSender) holder).clLayout.setSelected(true);
                    ((ViewHolderMessageSender) holder).clContainer.setVisibility(View.VISIBLE);

                } else {
                    ((ViewHolderMessageSender) holder).clBox.setPressed(false);
                    ((ViewHolderMessageSender) holder).clLayout.setSelected(false);
                    ((ViewHolderMessageSender) holder).clContainer.setVisibility(View.GONE);
                }

            } else {
                //Set chat margin
                if (prevModel != null) {
                    if (model.getSender_id().equals(prevModel.getSender_id()) && !model.getType().equals("info_join")) {
                        ((ViewHolderMessageReceiver) holder).tvSender.setVisibility(View.GONE);

                        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) ((ViewHolderMessageReceiver) holder).clLayout.getLayoutParams();
                        params.setMargins(8, 4, 8, 4);
                        ((ViewHolderMessageReceiver) holder).clLayout.setLayoutParams(params);
                    } else {
                        ((ViewHolderMessageReceiver) holder).tvSender.setVisibility(View.VISIBLE);

                        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) ((ViewHolderMessageReceiver) holder).clLayout.getLayoutParams();
                        params.setMargins(8, 16, 8, 4);
                        ((ViewHolderMessageReceiver) holder).clLayout.setLayoutParams(params);
                    }
                } else {
                    ((ViewHolderMessageReceiver) holder).tvSender.setVisibility(View.VISIBLE);
                    RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) ((ViewHolderMessageReceiver) holder).clLayout.getLayoutParams();
                    params.setMargins(8, 8, 8, 4);
                    ((ViewHolderMessageReceiver) holder).clLayout.setLayoutParams(params);
                }

                //Set Chat Reply

                if (model.getReply_type() != null) {
                    ((ViewHolderMessageReceiver) holder).llReply.setVisibility(View.VISIBLE);

                    StateListDrawable stateListDrawable = (StateListDrawable) ((ViewHolderMessageReceiver) holder).llReply.getBackground();
                    DrawableContainer.DrawableContainerState drawableContainerState = (DrawableContainer.DrawableContainerState) stateListDrawable.getConstantState();
                    Drawable[] children = drawableContainerState.getChildren();

                    LayerDrawable layerDrawable = (LayerDrawable) children[1];
                    GradientDrawable gradientDrawable = (GradientDrawable) layerDrawable.findDrawableByLayerId(R.id.layer_list_item_room_chat_message_left_reply_border);
                    gradientDrawable.setColor(activity.getColor(model.getReply_sender_name()));

                    switch (model.getReply_type()) {
                        case "message":
                            ((ViewHolderMessageReceiver) holder).clFile.setVisibility(View.GONE);
                            ((ViewHolderMessageReceiver) holder).clImage.setVisibility(View.GONE);
                            ((ViewHolderMessageReceiver) holder).clMessage.setVisibility(View.VISIBLE);

                            ((ViewHolderMessageReceiver) holder).tvReplyMessage.setText(model.getReply_message());
                            ((ViewHolderMessageReceiver) holder).tvReplyMessageSender.setText(model.getReply_sender_name());
                            ((ViewHolderMessageReceiver) holder).tvReplyMessageSender.setTextColor(activity.getColor(model.getReply_sender_name()));

                            break;
                        case "file":
                            ((ViewHolderMessageReceiver) holder).clFile.setVisibility(View.VISIBLE);
                            ((ViewHolderMessageReceiver) holder).clImage.setVisibility(View.GONE);
                            ((ViewHolderMessageReceiver) holder).clMessage.setVisibility(View.GONE);

                            ((ViewHolderMessageReceiver) holder).tvReplyFileName.setText(model.getReply_file_name());
                            ((ViewHolderMessageReceiver) holder).tvReplyFileExtension.setText(model.getReply_file_extension().toUpperCase());
                            ((ViewHolderMessageReceiver) holder).tvReplyFileSender.setText(model.getReply_sender_name());
                            ((ViewHolderMessageReceiver) holder).tvReplyFileSender.setTextColor(activity.getColor(model.getReply_sender_name()));

                            break;
                        case "image":
                            ((ViewHolderMessageReceiver) holder).clFile.setVisibility(View.GONE);
                            ((ViewHolderMessageReceiver) holder).clImage.setVisibility(View.VISIBLE);
                            ((ViewHolderMessageReceiver) holder).clMessage.setVisibility(View.GONE);

                            Glide.with(activity)
                                    .load(model.getReply_file_download_url())
                                    .apply(RequestOptions.centerCropTransform())
                                    .apply(RequestOptions.overrideOf(50, 50))
                                    .into(((ViewHolderMessageReceiver) holder).ivReplyImage);

                            if (!model.getReply_message().isEmpty()) {
                                ((ViewHolderMessageReceiver) holder).tvReplyImage.setText(model.getReply_message());
                            } else {
                                ((ViewHolderMessageReceiver) holder).tvReplyImage.setText("Gambar");
                            }
                            ((ViewHolderMessageReceiver) holder).tvReplyImageSender.setText(model.getReply_sender_name());
                            ((ViewHolderMessageReceiver) holder).tvReplyImageSender.setTextColor(activity.getColor(model.getReply_sender_name()));

                            break;
                    }
                } else {
                    ((ViewHolderMessageReceiver) holder).llReply.setVisibility(View.GONE);
                }

                ((ViewHolderMessageReceiver) holder).tvSender.setText(model.getSender_name());
                ((ViewHolderMessageReceiver) holder).tvSender.setTextColor(activity.getColor(model.getSender_name()));
                ((ViewHolderMessageReceiver) holder).tvTime.setText(activity.getFormattedTime(model.getTimestamp()));

                ((ViewHolderMessageReceiver) holder).tvMessage.setText(model.getMessage());
                ((ViewHolderMessageReceiver) holder).tvTime.setText(activity.getFormattedTime(model.getTimestamp()));
                ((ViewHolderMessageReceiver) holder).clLayout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (activity.roomFragment.isSelected(model)) {
                            view.setSelected(false);
                            ((ViewHolderMessageReceiver) holder).clContainer.setVisibility(View.GONE);
                            activity.roomFragment.removeSelected(model);
                            activity.roomFragment.updateSelected();
                        } else {
                            view.setSelected(true);
                            ((ViewHolderMessageReceiver) holder).clContainer.setVisibility(View.VISIBLE);
                            activity.roomFragment.addSelected(model);
                            activity.roomFragment.updateSelected();
                        }
                        return false;
                    }
                });
                ((ViewHolderMessageReceiver) holder).clBox.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_UP:
                                if (((ViewHolderMessageReceiver) holder).clLayout.isSelected()) {
                                    view.setPressed(false);
                                } else {
                                    view.setPressed(false);
                                }
                                return false;
                            case MotionEvent.ACTION_DOWN:
                                if (((ViewHolderMessageReceiver) holder).clLayout.isSelected()) {
                                    view.setPressed(false);
                                } else {
                                    view.setPressed(true);
                                }
                                return false;
                        }
                        return false;
                    }
                });

                if (activity.roomFragment.isSelected(model)) {
                    ((ViewHolderMessageReceiver) holder).clBox.setPressed(false);
                    ((ViewHolderMessageReceiver) holder).clLayout.setSelected(true);
                    ((ViewHolderMessageReceiver) holder).clContainer.setVisibility(View.VISIBLE);

                } else {
                    ((ViewHolderMessageReceiver) holder).clBox.setPressed(false);
                    ((ViewHolderMessageReceiver) holder).clLayout.setSelected(false);
                    ((ViewHolderMessageReceiver) holder).clContainer.setVisibility(View.GONE);
                }

            }
        } else if (model.getType().equals("image")) {
            if (model.getSender_id().equals(userId)) {
                ((ViewHolderImageSender) holder).tvTime.setText(activity.getFormattedTime(model.getTimestamp()));

                //Set chat margin
                if (prevModel != null) {
                    if (model.getSender_id().equals(prevModel.getSender_id())) {
                        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) ((ViewHolderImageSender) holder).clLayout.getLayoutParams();
                        params.setMargins(8, 4, 8, 4);
                        ((ViewHolderImageSender) holder).clLayout.setLayoutParams(params);
                    } else {
                        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) ((ViewHolderImageSender) holder).clLayout.getLayoutParams();
                        params.setMargins(8, 16, 8, 4);
                        ((ViewHolderImageSender) holder).clLayout.setLayoutParams(params);
                    }
                } else {
                    RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) ((ViewHolderImageSender) holder).clLayout.getLayoutParams();
                    params.setMargins(8, 8, 8, 4);
                    ((ViewHolderImageSender) holder).clLayout.setLayoutParams(params);
                }

                //Set chat status

                if (model.getReceived().size() > 1) {
                    ((ViewHolderImageSender) holder).ivStatus.setImageResource(R.drawable.ic_done_all);
                } else {
                    ((ViewHolderImageSender) holder).ivStatus.setImageResource(R.drawable.ic_done);
                }

                if (activity.isChatReadByAll(model)) {
                    ((ViewHolderImageSender) holder).ivStatus.setColorFilter(ContextCompat.getColor(activity, R.color.colorPrimary));
                } else {
                    ((ViewHolderImageSender) holder).ivStatus.setColorFilter(Color.parseColor("#6BA55B"));
                }

                if (model.getMessage().isEmpty()) {
                    ((ViewHolderImageSender) holder).tvMessage.setVisibility(View.GONE);
                } else {
                    ((ViewHolderImageSender) holder).tvMessage.setVisibility(View.VISIBLE);
                    ((ViewHolderImageSender) holder).tvMessage.setText(model.getMessage());
                }
                ((ViewHolderImageSender) holder).tvTime.setText(activity.getFormattedTime(model.getTimestamp()));
                ((ViewHolderImageSender) holder).clLayout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (activity.roomFragment.isSelected(model)) {
                            view.setSelected(false);
                            ((ViewHolderImageSender) holder).clContainer.setVisibility(View.GONE);
                            activity.roomFragment.removeSelected(model);
                            activity.roomFragment.updateSelected();
                        } else {
                            view.setSelected(true);
                            ((ViewHolderImageSender) holder).clContainer.setVisibility(View.VISIBLE);
                            activity.roomFragment.addSelected(model);
                            activity.roomFragment.updateSelected();
                        }
                        return false;
                    }
                });
                ((ViewHolderImageSender) holder).clBox.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_UP:
                                if (((ViewHolderImageSender) holder).clLayout.isSelected()) {
                                    view.setPressed(false);
                                } else {
                                    view.setPressed(false);
                                }
                                return false;
                            case MotionEvent.ACTION_DOWN:
                                if (((ViewHolderImageSender) holder).clLayout.isSelected()) {
                                    view.setPressed(false);
                                } else {
                                    view.setPressed(true);
                                }
                                return false;
                        }
                        return false;
                    }
                });

                if (activity.roomFragment.isSelected(model)) {
                    ((ViewHolderImageSender) holder).clBox.setPressed(false);
                    ((ViewHolderImageSender) holder).clLayout.setSelected(true);
                    ((ViewHolderImageSender) holder).clContainer.setVisibility(View.VISIBLE);

                } else {
                    ((ViewHolderImageSender) holder).clBox.setPressed(false);
                    ((ViewHolderImageSender) holder).clLayout.setSelected(false);
                    ((ViewHolderImageSender) holder).clContainer.setVisibility(View.GONE);
                }

                if (model.getFile_download_url() == null && model.getFile_local_path() != null) {
                    if (model.getFile_upload_progress() == 100) {
                        ((ViewHolderImageSender) holder).progressbar.setVisibility(View.GONE);
                        ((ViewHolderImageSender) holder).loadProgressbar.setVisibility(View.VISIBLE);
                        ((ViewHolderImageSender) holder).tvProgressbarStop.setVisibility(View.GONE);
                        ((ViewHolderImageSender) holder).ivProgressbarReupload.setVisibility(View.GONE);
                    } else {
                        if (model.getFile_upload_status().equals("cancel")) {
                            ((ViewHolderImageSender) holder).progressbar.setVisibility(View.GONE);
                            ((ViewHolderImageSender) holder).tvProgressbarStop.setVisibility(View.GONE);
                            ((ViewHolderImageSender) holder).ivProgressbarReupload.setVisibility(View.VISIBLE);
                            ((ViewHolderImageSender) holder).ivProgressbarReupload.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    activity.roomFragment.restartUpload(model);
                                }
                            });
                        } else {
                            ((ViewHolderImageSender) holder).ivProgressbarReupload.setVisibility(View.GONE);
                            ((ViewHolderImageSender) holder).tvProgressbarStop.setVisibility(View.VISIBLE);
                            ((ViewHolderImageSender) holder).tvProgressbarStop.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    activity.roomFragment.cancelUpload(model.getId());
                                }
                            });
                            ((ViewHolderImageSender) holder).progressbar.setVisibility(View.VISIBLE);
                            ((ViewHolderImageSender) holder).progressbar.setMax(100);
                            ((ViewHolderImageSender) holder).progressbar.setProgress(model.getFile_upload_progress());
                        }
                    }

                    ((ViewHolderImageSender) holder).ivImage.setVisibility(View.VISIBLE);

                    Glide.with(activity)
                            .load(model.getFile_local_path())
                            .apply(RequestOptions.centerCropTransform())
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    ((ViewHolderImageSender) holder).loadProgressbar.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    ((ViewHolderImageSender) holder).loadProgressbar.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .into(((ViewHolderImageSender) holder).ivImage);

                    ((ViewHolderImageSender) holder).ivImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (model.getRoom_type().equals("public")) {
                                activity.openImageFragment(model);
                            } else {
                                activity.openPrivateImageFragment(model);
                            }
                        }
                    });

                } else {
                    ((ViewHolderImageSender) holder).progressbar.setVisibility(View.GONE);
                    ((ViewHolderImageSender) holder).loadProgressbar.setVisibility(View.VISIBLE);
                    ((ViewHolderImageSender) holder).tvProgressbarStop.setVisibility(View.GONE);
                    ((ViewHolderImageSender) holder).ivProgressbarReupload.setVisibility(View.GONE);

                    ((ViewHolderImageSender) holder).ivImage.setVisibility(View.VISIBLE);

                    Glide.with(activity)
                            .load(model.getFile_download_url())
                            .apply(RequestOptions.centerCropTransform())
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    ((ViewHolderImageSender) holder).loadProgressbar.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    ((ViewHolderImageSender) holder).loadProgressbar.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .into(((ViewHolderImageSender) holder).ivImage);

                    ((ViewHolderImageSender) holder).ivImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (model.getRoom_type().equals("public")) {
                                activity.openImageFragment(model);
                            } else {
                                activity.openPrivateImageFragment(model);
                            }
                        }
                    });

                }

            } else {
                ((ViewHolderImageReceiver) holder).tvSender.setText(model.getSender_name());
                ((ViewHolderImageReceiver) holder).tvSender.setTextColor(activity.getColor(model.getSender_name()));

                ((ViewHolderImageReceiver) holder).tvTime.setText(activity.getFormattedTime(model.getTimestamp()));

                //Set chat margin
                if (prevModel != null) {
                    if (model.getSender_id().equals(prevModel.getSender_id()) && !model.getType().equals("info_join")) {
                        ((ViewHolderImageReceiver) holder).tvSender.setVisibility(View.GONE);

                        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) ((ViewHolderImageReceiver) holder).clLayout.getLayoutParams();
                        params.setMargins(8, 4, 8, 4);
                        ((ViewHolderImageReceiver) holder).clLayout.setLayoutParams(params);
                    } else {
                        ((ViewHolderImageReceiver) holder).tvSender.setVisibility(View.VISIBLE);

                        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) ((ViewHolderImageReceiver) holder).clLayout.getLayoutParams();
                        params.setMargins(8, 16, 8, 4);
                        ((ViewHolderImageReceiver) holder).clLayout.setLayoutParams(params);
                    }
                } else {
                    ((ViewHolderImageReceiver) holder).tvSender.setVisibility(View.VISIBLE);

                    RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) ((ViewHolderImageReceiver) holder).clLayout.getLayoutParams();
                    params.setMargins(8, 8, 8, 4);
                    ((ViewHolderImageReceiver) holder).clLayout.setLayoutParams(params);
                }

                if (model.getMessage().isEmpty()) {
                    ((ViewHolderImageReceiver) holder).tvMessage.setVisibility(View.GONE);
                } else {
                    ((ViewHolderImageReceiver) holder).tvMessage.setVisibility(View.VISIBLE);
                    ((ViewHolderImageReceiver) holder).tvMessage.setText(model.getMessage());
                }
                ((ViewHolderImageReceiver) holder).tvTime.setText(activity.getFormattedTime(model.getTimestamp()));
                ((ViewHolderImageReceiver) holder).clLayout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (activity.roomFragment.isSelected(model)) {
                            view.setSelected(false);
                            ((ViewHolderImageReceiver) holder).clContainer.setVisibility(View.GONE);
                            activity.roomFragment.removeSelected(model);
                            activity.roomFragment.updateSelected();
                        } else {
                            view.setSelected(true);
                            ((ViewHolderImageReceiver) holder).clContainer.setVisibility(View.VISIBLE);
                            activity.roomFragment.addSelected(model);
                            activity.roomFragment.updateSelected();
                        }
                        return false;
                    }
                });
                ((ViewHolderImageReceiver) holder).clBox.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_UP:
                                if (((ViewHolderImageReceiver) holder).clLayout.isSelected()) {
                                    view.setPressed(false);
                                } else {
                                    view.setPressed(false);
                                }
                                return false;
                            case MotionEvent.ACTION_DOWN:
                                if (((ViewHolderImageReceiver) holder).clLayout.isSelected()) {
                                    view.setPressed(false);
                                } else {
                                    view.setPressed(true);
                                }
                                return false;
                        }
                        return false;
                    }
                });

                if (activity.roomFragment.isSelected(model)) {
                    ((ViewHolderImageReceiver) holder).clBox.setPressed(false);
                    ((ViewHolderImageReceiver) holder).clLayout.setSelected(true);
                    ((ViewHolderImageReceiver) holder).clContainer.setVisibility(View.VISIBLE);

                } else {
                    ((ViewHolderImageReceiver) holder).clBox.setPressed(false);
                    ((ViewHolderImageReceiver) holder).clLayout.setSelected(false);
                    ((ViewHolderImageReceiver) holder).clContainer.setVisibility(View.GONE);
                }

                if (model.getFile_download_url() != null) {
                    ((ViewHolderImageReceiver) holder).progressbar.setVisibility(View.GONE);
                    ((ViewHolderImageReceiver) holder).loadProgressbar.setVisibility(View.VISIBLE);
                    ((ViewHolderImageReceiver) holder).tvProgressbarStop.setVisibility(View.GONE);
                    ((ViewHolderImageReceiver) holder).ivProgressbarReupload.setVisibility(View.GONE);

                    ((ViewHolderImageReceiver) holder).ivImage.setVisibility(View.VISIBLE);

                    Glide.with(activity)
                            .load(model.getFile_download_url())
                            .apply(RequestOptions.centerCropTransform())
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    ((ViewHolderImageReceiver) holder).loadProgressbar.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    ((ViewHolderImageReceiver) holder).loadProgressbar.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .into(((ViewHolderImageReceiver) holder).ivImage);

                    ((ViewHolderImageReceiver) holder).ivImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (model.getRoom_type().equals("public")) {
                                activity.openImageFragment(model);
                            } else {
                                activity.openPrivateImageFragment(model);
                            }
                        }
                    });

                } else {
                    ((ViewHolderImageReceiver) holder).ivImage.setVisibility(View.VISIBLE);
                    if (model.getFile_upload_progress() == 100) {
                        ((ViewHolderImageReceiver) holder).progressbar.setVisibility(View.GONE);
                        ((ViewHolderImageReceiver) holder).tvProgressbarStop.setVisibility(View.GONE);
                        ((ViewHolderImageReceiver) holder).ivProgressbarReupload.setVisibility(View.GONE);
                    } else {
                        if (model.getFile_upload_status().equals("cancel")) {
                            ((ViewHolderImageReceiver) holder).progressbar.setVisibility(View.GONE);
                            ((ViewHolderImageReceiver) holder).tvProgressbarStop.setVisibility(View.GONE);
                            ((ViewHolderImageReceiver) holder).ivProgressbarReupload.setImageResource(R.drawable.ic_broken_image);
                            ((ViewHolderImageReceiver) holder).ivProgressbarReupload.setVisibility(View.VISIBLE);
                        } else {
                            ((ViewHolderImageReceiver) holder).ivProgressbarReupload.setVisibility(View.GONE);

                            ((ViewHolderImageReceiver) holder).progressbar.setVisibility(View.VISIBLE);
                            ((ViewHolderImageReceiver) holder).progressbar.setMax(100);
                            ((ViewHolderImageReceiver) holder).progressbar.setProgress(model.getFile_upload_progress());

                            ((ViewHolderImageReceiver) holder).tvProgressbarStop.setVisibility(View.GONE);
                        }

                    }
                }
            }
        } else if (model.getType().equals("file")) {
            if (model.getSender_id().equals(userId)) {
                Drawable background = ((ViewHolderFileSender) holder).rlFile.getBackground();
                background.setTint(Color.parseColor(getFileExtensionIconColor(model.getFile_extension())));
                ((ViewHolderFileSender) holder).rlFile.setBackground(background);
                ((ViewHolderFileSender) holder).tvFileName.setText(model.getFile_name());
                ((ViewHolderFileSender) holder).tvFileSize.setText(model.getFile_size());
                ((ViewHolderFileSender) holder).tvFileExtension.setText(model.getFile_extension().toUpperCase());
                ((ViewHolderFileSender) holder).tvTime.setText(activity.getFormattedTime(model.getTimestamp()));

                final File file = new File(localPath + model.getFile_name());

                //Set chat margin
                if (prevModel != null) {
                    if (model.getSender_id().equals(prevModel.getSender_id())) {
                        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) ((ViewHolderFileSender) holder).clLayout.getLayoutParams();
                        params.setMargins(8, 4, 8, 4);
                        ((ViewHolderFileSender) holder).clLayout.setLayoutParams(params);
                    } else {
                        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) ((ViewHolderFileSender) holder).clLayout.getLayoutParams();
                        params.setMargins(8, 16, 8, 4);
                        ((ViewHolderFileSender) holder).clLayout.setLayoutParams(params);
                    }
                } else {
                    RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) ((ViewHolderFileSender) holder).clLayout.getLayoutParams();
                    params.setMargins(8, 8, 8, 4);
                    ((ViewHolderFileSender) holder).clLayout.setLayoutParams(params);
                }

                //Set download status

                if (model.getFile_download_url() == null) {
                    ((ViewHolderFileSender) holder).ivFileDownload.setVisibility(View.GONE);
                } else {
                    if (file.exists()) {
                        ((ViewHolderFileSender) holder).ivFileDownload.setVisibility(View.GONE);
                    } else {
                        ((ViewHolderFileSender) holder).ivFileDownload.setVisibility(View.VISIBLE);
                        ((ViewHolderFileSender) holder).ivFileDownload.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                downloadFile(holder, position);
                            }
                        });
                    }
                }
                // End set download status

                //Open file
                ((ViewHolderFileSender) holder).tvFileName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (file.exists()) {
                            activity.openFile(localPath + model.getFile_name());
                        }
                    }
                });
                //End open file

                //Set chat status

                if (model.getReceived().size() > 1) {
                    ((ViewHolderFileSender) holder).ivStatus.setImageResource(R.drawable.ic_done_all);
                } else {
                    ((ViewHolderFileSender) holder).ivStatus.setImageResource(R.drawable.ic_done);
                }

                if (activity.isChatReadByAll(model)) {
                    ((ViewHolderFileSender) holder).ivStatus.setColorFilter(ContextCompat.getColor(activity, R.color.colorPrimary));
                } else {
                    ((ViewHolderFileSender) holder).ivStatus.setColorFilter(Color.parseColor("#6BA55B"));
                }
                ((ViewHolderFileSender) holder).tvTime.setText(activity.getFormattedTime(model.getTimestamp()));
                ((ViewHolderFileSender) holder).clLayout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (activity.roomFragment.isSelected(model)) {
                            view.setSelected(false);
                            ((ViewHolderFileSender) holder).clContainer.setVisibility(View.GONE);
                            activity.roomFragment.removeSelected(model);
                            activity.roomFragment.updateSelected();
                        } else {
                            view.setSelected(true);
                            ((ViewHolderFileSender) holder).clContainer.setVisibility(View.VISIBLE);
                            activity.roomFragment.addSelected(model);
                            activity.roomFragment.updateSelected();
                        }
                        return false;
                    }
                });
                ((ViewHolderFileSender) holder).clBox.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_UP:
                                if (((ViewHolderFileSender) holder).clLayout.isSelected()) {
                                    view.setPressed(false);
                                } else {
                                    view.setPressed(false);
                                }
                                return false;
                            case MotionEvent.ACTION_DOWN:
                                if (((ViewHolderFileSender) holder).clLayout.isSelected()) {
                                    view.setPressed(false);
                                } else {
                                    view.setPressed(true);
                                }
                                return false;
                        }
                        return false;
                    }
                });

                if (activity.roomFragment.isSelected(model)) {
                    ((ViewHolderFileSender) holder).clBox.setPressed(false);
                    ((ViewHolderFileSender) holder).clLayout.setSelected(true);
                    ((ViewHolderFileSender) holder).clContainer.setVisibility(View.VISIBLE);

                } else {
                    ((ViewHolderFileSender) holder).clBox.setPressed(false);
                    ((ViewHolderFileSender) holder).clLayout.setSelected(false);
                    ((ViewHolderFileSender) holder).clContainer.setVisibility(View.GONE);
                }

                if (model.getFile_download_url() != null) {
                    ((ViewHolderFileSender) holder).progressbar.setVisibility(View.GONE);
                    ((ViewHolderFileSender) holder).tvProgressbarStop.setVisibility(View.GONE);
                    ((ViewHolderFileSender) holder).ivProgressbarReupload.setVisibility(View.GONE);
                    Uri uri = Uri.parse(model.getFile_download_url());

                } else {
                    if (model.getFile_upload_progress() == 100) {
                        ((ViewHolderFileSender) holder).progressbar.setVisibility(View.GONE);
                        ((ViewHolderFileSender) holder).tvProgressbarStop.setVisibility(View.GONE);
                        ((ViewHolderFileSender) holder).ivProgressbarReupload.setVisibility(View.GONE);
                    } else {
                        if (model.getFile_upload_status().equals("cancel")) {
                            ((ViewHolderFileSender) holder).progressbar.setVisibility(View.GONE);
                            ((ViewHolderFileSender) holder).tvProgressbarStop.setVisibility(View.GONE);
                            ((ViewHolderFileSender) holder).ivProgressbarReupload.setVisibility(View.VISIBLE);
                            ((ViewHolderFileSender) holder).ivProgressbarReupload.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    activity.roomFragment.restartUpload(model);
                                }
                            });
                        } else {
                            ((ViewHolderFileSender) holder).ivProgressbarReupload.setVisibility(View.GONE);
                            ((ViewHolderFileSender) holder).tvProgressbarStop.setVisibility(View.VISIBLE);
                            ((ViewHolderFileSender) holder).tvProgressbarStop.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    activity.roomFragment.cancelUpload(model.getId());
                                }
                            });

                            ((ViewHolderFileSender) holder).progressbar.setVisibility(View.VISIBLE);
                            ((ViewHolderFileSender) holder).progressbar.setMax(100);
                            ((ViewHolderFileSender) holder).progressbar.setProgress(model.getFile_upload_progress());
                        }
                    }
                }
            } else {
                Drawable background = ((ViewHolderFileReceiver) holder).rlFile.getBackground();
                background.setTint(Color.parseColor(getFileExtensionIconColor(model.getFile_extension())));
                ((ViewHolderFileReceiver) holder).rlFile.setBackground(background);
                ((ViewHolderFileReceiver) holder).tvSender.setText(model.getSender_name());
                ((ViewHolderFileReceiver) holder).tvSender.setTextColor(activity.getColor(model.getSender_name()));
                ((ViewHolderFileReceiver) holder).tvTime.setText(activity.getFormattedTime(model.getTimestamp()));
                ((ViewHolderFileReceiver) holder).tvFileName.setText(model.getFile_name());
                ((ViewHolderFileReceiver) holder).tvFileSize.setText(model.getFile_size());
                ((ViewHolderFileReceiver) holder).tvFileExtension.setText(model.getFile_extension().toUpperCase());
                ((ViewHolderFileReceiver) holder).tvTime.setText(activity.getFormattedTime(model.getTimestamp()));

                //Set chat margin
                if (prevModel != null) {
                    if (model.getSender_id().equals(prevModel.getSender_id()) && !model.getType().equals("info_join")) {
                        ((ViewHolderFileReceiver) holder).tvSender.setVisibility(View.GONE);

                        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) ((ViewHolderFileReceiver) holder).clLayout.getLayoutParams();
                        params.setMargins(8, 4, 8, 4);
                        ((ViewHolderFileReceiver) holder).clLayout.setLayoutParams(params);
                    } else {
                        ((ViewHolderFileReceiver) holder).tvSender.setVisibility(View.VISIBLE);

                        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) ((ViewHolderFileReceiver) holder).clLayout.getLayoutParams();
                        params.setMargins(8, 16, 8, 8);
                        ((ViewHolderFileReceiver) holder).clLayout.setLayoutParams(params);
                    }
                } else {
                    ((ViewHolderFileReceiver) holder).tvSender.setVisibility(View.VISIBLE);

                    RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) ((ViewHolderFileReceiver) holder).clLayout.getLayoutParams();
                    params.setMargins(8, 8, 8, 4);
                    ((ViewHolderFileReceiver) holder).clLayout.setLayoutParams(params);
                }

                //Set download status
                final File file = new File(localPath + model.getFile_name());

                if (model.getFile_download_url() == null) {
                    ((ViewHolderFileReceiver) holder).ivFileDownload.setVisibility(View.GONE);
                } else {
                    if (file.exists()) {
                        ((ViewHolderFileReceiver) holder).ivFileDownload.setVisibility(View.GONE);
                    } else {
                        ((ViewHolderFileReceiver) holder).ivFileDownload.setVisibility(View.VISIBLE);
                        ((ViewHolderFileReceiver) holder).ivFileDownload.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                downloadFile(holder, position);
                            }
                        });
                    }
                }
                // End set download status

                //Open file
                ((ViewHolderFileReceiver) holder).tvFileName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (file.exists()) {
                            activity.openFile(localPath + model.getFile_name());
                        }
                    }
                });
                //End open file

                ((ViewHolderFileReceiver) holder).clLayout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (activity.roomFragment.isSelected(model)) {
                            view.setSelected(false);
                            ((ViewHolderFileReceiver) holder).clContainer.setVisibility(View.GONE);
                            activity.roomFragment.removeSelected(model);
                            activity.roomFragment.updateSelected();
                        } else {
                            view.setSelected(true);
                            ((ViewHolderFileReceiver) holder).clContainer.setVisibility(View.VISIBLE);
                            activity.roomFragment.addSelected(model);
                            activity.roomFragment.updateSelected();
                        }
                        return false;
                    }
                });
                ((ViewHolderFileReceiver) holder).clBox.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_UP:
                                if (((ViewHolderFileReceiver) holder).clLayout.isSelected()) {
                                    view.setPressed(false);
                                } else {
                                    view.setPressed(false);
                                }
                                return false;
                            case MotionEvent.ACTION_DOWN:
                                if (((ViewHolderFileReceiver) holder).clLayout.isSelected()) {
                                    view.setPressed(false);
                                } else {
                                    view.setPressed(true);
                                }
                                return false;
                        }
                        return false;
                    }
                });

                if (activity.roomFragment.isSelected(model)) {
                    ((ViewHolderFileReceiver) holder).clBox.setPressed(false);
                    ((ViewHolderFileReceiver) holder).clLayout.setSelected(true);
                    ((ViewHolderFileReceiver) holder).clContainer.setVisibility(View.VISIBLE);

                } else {
                    ((ViewHolderFileReceiver) holder).clBox.setPressed(false);
                    ((ViewHolderFileReceiver) holder).clLayout.setSelected(false);
                    ((ViewHolderFileReceiver) holder).clContainer.setVisibility(View.GONE);
                }

                if (model.getFile_download_url() != null) {

                } else {
                    if (model.getFile_upload_progress() == 100) {
                        ((ViewHolderFileReceiver) holder).progressbar.setVisibility(View.GONE);
                        ((ViewHolderFileReceiver) holder).tvProgressbarStop.setVisibility(View.GONE);
                        ((ViewHolderFileReceiver) holder).ivProgressbarReupload.setVisibility(View.GONE);
                    } else {
                        if (model.getFile_upload_status().equals("cancel")) {
                            ((ViewHolderFileReceiver) holder).progressbar.setVisibility(View.GONE);
                            ((ViewHolderFileReceiver) holder).tvProgressbarStop.setVisibility(View.GONE);
                            ((ViewHolderFileReceiver) holder).ivProgressbarReupload.setImageResource(R.drawable.ic_broken_image);
                            ((ViewHolderFileReceiver) holder).ivProgressbarReupload.setVisibility(View.VISIBLE);
                        } else {
                            ((ViewHolderFileReceiver) holder).ivProgressbarReupload.setVisibility(View.GONE);

                            ((ViewHolderFileReceiver) holder).progressbar.setVisibility(View.VISIBLE);
                            ((ViewHolderFileReceiver) holder).progressbar.setMax(100);
                            ((ViewHolderFileReceiver) holder).progressbar.setProgress(model.getFile_upload_progress());

                            ((ViewHolderFileReceiver) holder).tvProgressbarStop.setVisibility(View.GONE);
                        }
                    }
                }
            }
        } else if (model.getType().equals("info_join")) {
            ((ViewHolderInfo) holder).tvMessage.setText(model.getSender_name() + " bergabung dalam obrolan");
        }
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

    @Override
    public int getItemCount() {
        if (dataList == null) {
            return 0;
        } else {
            return dataList.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        ChatRoomModel model = dataList.get(position);
        if (model.getType().equals("message")) {
            if (model.getSender_id().equals(userId)) {
                return TYPE_MESSAGE_SENDER;
            } else {
                return TYPE_MESSAGE_RECEIVER;
            }
        } else if (model.getType().equals("image")) {
            if (model.getSender_id().equals(userId)) {
                return TYPE_IMAGE_SENDER;
            } else {
                return TYPE_IMAGE_RECEIVER;
            }
        }  else if (model.getType().equals("file")) {
            if (model.getSender_id().equals(userId)) {
                return TYPE_FILE_SENDER;
            } else {
                return TYPE_FILE_RECEIVER;
            }
        } else if (model.getType().equals("info_join")) {
            return TYPE_INFO_JOIN;
        }

        return -1;
    }

    public void downloadFile(final RecyclerView.ViewHolder holder, int position) {
        ChatRoomModel model = dataList.get(position);
        if (model.getSender_id().equals(activity.spm.getUserId())) {
            ((ViewHolderFileSender) holder).ivFileDownload.setVisibility(View.GONE);
            ((ViewHolderFileSender) holder).progressbar.setProgress(0);
            ((ViewHolderFileSender) holder).progressbar.setMax(100);
            ((ViewHolderFileSender) holder).progressbar.setVisibility(View.VISIBLE);

            AndroidNetworking.download(model.getFile_download_url(), localPath, model.getFile_name())
                    .build()
                    .setDownloadProgressListener(new DownloadProgressListener() {
                        @Override
                        public void onProgress(long bytesDownloaded, long totalBytes) {
                            int progress = (100 * (int) bytesDownloaded) / (int) totalBytes;
                            ((ViewHolderFileSender) holder).progressbar.setProgress(progress);
                        }
                    })
                    .startDownload(new DownloadListener() {
                        @Override
                        public void onDownloadComplete() {
                            ((ViewHolderFileSender) holder).progressbar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(ANError error) {
                            // handle error
                            ((ViewHolderFileSender) holder).progressbar.setVisibility(View.GONE);
                            ((ViewHolderFileSender) holder).ivFileDownload.setVisibility(View.VISIBLE);
                        }
                    });

        } else {
            ((ViewHolderFileReceiver) holder).ivFileDownload.setVisibility(View.GONE);
            ((ViewHolderFileReceiver) holder).progressbar.setProgress(0);
            ((ViewHolderFileReceiver) holder).progressbar.setMax(100);
            ((ViewHolderFileReceiver) holder).progressbar.setVisibility(View.VISIBLE);

            AndroidNetworking.download(model.getFile_download_url(), localPath, model.getFile_name())
                    .build()
                    .setDownloadProgressListener(new DownloadProgressListener() {
                        @Override
                        public void onProgress(long bytesDownloaded, long totalBytes) {
                            int progress = (100 * (int) bytesDownloaded) / (int) totalBytes;
                            ((ViewHolderFileReceiver) holder).progressbar.setProgress(progress);
                        }
                    })
                    .startDownload(new DownloadListener() {
                        @Override
                        public void onDownloadComplete() {
                            ((ViewHolderFileReceiver) holder).progressbar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(ANError error) {
                            // handle error
                            ((ViewHolderFileReceiver) holder).progressbar.setVisibility(View.GONE);
                            ((ViewHolderFileReceiver) holder).ivFileDownload.setVisibility(View.VISIBLE);
                        }
                    });
        }
    }



}