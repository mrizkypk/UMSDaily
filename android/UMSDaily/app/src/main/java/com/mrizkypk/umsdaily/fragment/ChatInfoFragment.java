package com.mrizkypk.umsdaily.fragment;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mrizkypk.umsdaily.R;
import com.mrizkypk.umsdaily.activity.HomeActivity;
import com.mrizkypk.umsdaily.adapter.ChatAdapter;
import com.mrizkypk.umsdaily.adapter.ChatReadAdapter;
import com.mrizkypk.umsdaily.adapter.ChatReceivedAdapter;
import com.mrizkypk.umsdaily.helper.DateHelper;
import com.mrizkypk.umsdaily.model.ChatRoomModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatInfoFragment extends Fragment {
    public String roomId;
    public String roomTitle;
    public HomeActivity activity;
    public RecyclerView readRecyclerview;
    public RecyclerView receivedRecyclerview;
    public RecyclerView.LayoutManager readLayoutmanager;
    public RecyclerView.LayoutManager receivedLayoutmanager;

    public ArrayList<String> readIdList;
    public ArrayList<Long> readTimeList;
    public ArrayList<String> receivedIdList;
    public ArrayList<Long> receivedTimeList;
    public ChatRoomModel currentChat;

    @BindView(R.id.fragment_chat_info_container_toolbar)
    RelativeLayout rlToolbar;

    @BindView(R.id.fragment_chat_info_text_toolbar_title)
    TextView tvToolbarTitle;

    @BindView(R.id.fragment_chat_info_text_toolbar_subtitle)
    TextView tvToolbarSubtitle;

    @BindView(R.id.fragment_chat_info_container_file)
    ConstraintLayout clFile;

    @BindView(R.id.fragment_chat_info_container_image_file)
    RelativeLayout rlFile;

    @BindView(R.id.fragment_chat_info_text_file_time)
    TextView tvFileTime;

    @BindView(R.id.fragment_chat_info_text_file_name)
    TextView tvFileName;

    @BindView(R.id.fragment_chat_info_text_file_size)
    TextView tvFileSize;

    @BindView(R.id.fragment_chat_info_text_file_extension)
    TextView tvFileExtension;

    @BindView(R.id.fragment_chat_info_image_file_status)
    ImageView ivFileStatus;

    @BindView(R.id.fragment_chat_info_container_chat)
    ConstraintLayout clMessage;

    @BindView(R.id.fragment_chat_info_text_chat_time)
    TextView tvMessageTime;

    @BindView(R.id.fragment_chat_info_text_chat_message)
    TextView tvChatMessage;

    @BindView(R.id.fragment_chat_info_image_chat_status)
    ImageView ivMessageStatus;

    @BindView(R.id.fragment_chat_info_image_chat_image)
    ImageView ivImage;

    @BindView(R.id.fragment_chat_info_text_label_read)
    TextView tvRead;

    @BindView(R.id.fragment_chat_info_text_label_received)
    TextView tvReceived;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = ((HomeActivity) getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_info, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        roomId = getArguments().getString("BUNDLE_ROOM_ID");
        roomTitle = getArguments().getString("BUNDLE_ROOM_TITLE");

        readLayoutmanager = new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        receivedLayoutmanager = new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        readIdList = new ArrayList<>();
        readTimeList = new ArrayList<>();

        readRecyclerview = view.findViewById(R.id.fragment_chat_info_recyclerview_read);
        receivedRecyclerview = view.findViewById(R.id.fragment_chat_info_recyclerview_received);
        readRecyclerview.setHasFixedSize(true);
        receivedRecyclerview.setHasFixedSize(true);

        readRecyclerview.setLayoutManager(readLayoutmanager);
        receivedRecyclerview.setLayoutManager(receivedLayoutmanager);

        setHistory();

        tvToolbarTitle.setText(roomTitle);
        tvToolbarSubtitle.setText("Informasi Pesan");

        rlToolbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
    }

    public void setHistory() {
        if (activity.selectedModelMap.containsKey(roomId)) {
            for (ChatRoomModel model : activity.selectedModelMap.get(roomId)) {
                int position = activity.roomModelMap.get(roomId).indexOf(model);
                currentChat = activity.roomModelMap.get(roomId).get(position);

                currentChat.getRead().remove(activity.spm.getUserId());
                currentChat.getReceived().remove(activity.spm.getUserId());

                tvRead.setText("Dibaca oleh (" + currentChat.getRead().size() + ")");
                tvReceived.setText("Diterima oleh (" + currentChat.getReceived().size() + ")");

                readIdList = new ArrayList<>(currentChat.getRead().keySet());

                readTimeList = new ArrayList<>(currentChat.getRead().values());

                receivedIdList = new ArrayList<>(currentChat.getReceived().keySet());

                receivedTimeList = new ArrayList<>(currentChat.getReceived().values());

                activity.readAdapter = new ChatReadAdapter(getContext(), readIdList, readTimeList);
                readRecyclerview.setAdapter(activity.readAdapter);

                activity.receivedAdapter = new ChatReceivedAdapter(getContext(), receivedIdList, receivedTimeList);
                receivedRecyclerview.setAdapter(activity.receivedAdapter);

                RecyclerView.ViewHolder holder = activity.roomFragment.recyclerview.findViewHolderForAdapterPosition(position);
                if (holder instanceof ChatAdapter.ViewHolderMessageSender) {
                    ((ChatAdapter.ViewHolderMessageSender) holder).clBox.setPressed(false);
                    ((ChatAdapter.ViewHolderMessageSender) holder).clLayout.setSelected(false);
                    ((ChatAdapter.ViewHolderMessageSender) holder).clContainer.setVisibility(View.GONE);
                }

                setCurrentMessage(model);
            }
            activity.roomFragment.onToolbarOptionArrowBackClick();
        }
    }

    public void updateHistory(ChatRoomModel model) {
        int position = activity.roomModelMap.get(roomId).indexOf(model);
        currentChat = model;

        currentChat.getRead().remove(activity.spm.getUserId());
        currentChat.getReceived().remove(activity.spm.getUserId());

        tvRead.setText("Dibaca oleh (" + currentChat.getRead().size() + ")");
        tvReceived.setText("Diterima oleh (" + currentChat.getReceived().size() + ")");

        readIdList = new ArrayList<>(currentChat.getRead().keySet());

        readTimeList = new ArrayList<>(currentChat.getRead().values());

        receivedIdList = new ArrayList<>(currentChat.getReceived().keySet());

        receivedTimeList = new ArrayList<>(currentChat.getReceived().values());

        activity.readAdapter = new ChatReadAdapter(getContext(), readIdList, readTimeList);
        readRecyclerview.setAdapter(activity.readAdapter);

        activity.receivedAdapter = new ChatReceivedAdapter(getContext(), receivedIdList, receivedTimeList);
        receivedRecyclerview.setAdapter(activity.receivedAdapter);

        RecyclerView.ViewHolder holder = activity.roomFragment.recyclerview.findViewHolderForAdapterPosition(position);
        if (holder instanceof ChatAdapter.ViewHolderMessageSender) {
            ((ChatAdapter.ViewHolderMessageSender) holder).clBox.setPressed(false);
            ((ChatAdapter.ViewHolderMessageSender) holder).clLayout.setSelected(false);
            ((ChatAdapter.ViewHolderMessageSender) holder).clContainer.setVisibility(View.GONE);
        }

        activity.roomFragment.onToolbarOptionArrowBackClick();

    }

    public void setCurrentMessage(ChatRoomModel model) {
        currentChat = model;
        if (currentChat.getType().equals("message")) {
            clFile.setVisibility(View.GONE);
            tvChatMessage.setText(currentChat.getMessage());

            //Set chat status

            if (currentChat.getReceived().size() > 0) {
                ivMessageStatus.setImageResource(R.drawable.ic_done_all);
            } else {
                ivMessageStatus.setImageResource(R.drawable.ic_done);
            }

            currentChat.getRead().put(activity.spm.getUserId(), DateHelper.getTimestamp());

            if (activity.isChatReadByAll(currentChat)) {
                ivMessageStatus.setColorFilter(ContextCompat.getColor(activity, R.color.colorPrimary));
            } else {
                ivMessageStatus.setColorFilter(Color.parseColor("#6BA55B"));
            }
            tvMessageTime.setText(activity.getFormattedTime(currentChat.getTimestamp()));

        } else if (currentChat.getType().equals("image")) {
            clFile.setVisibility(View.GONE);
            tvChatMessage.setText(currentChat.getMessage());

            //Set chat status

            if (currentChat.getReceived().size() > 0) {
                ivMessageStatus.setImageResource(R.drawable.ic_done_all);
            } else {
                ivMessageStatus.setImageResource(R.drawable.ic_done);
            }

            currentChat.getRead().put(activity.spm.getUserId(), DateHelper.getTimestamp());

            if (activity.isChatReadByAll(currentChat)) {
                ivMessageStatus.setColorFilter(ContextCompat.getColor(activity, R.color.colorPrimary));
            } else {
                ivMessageStatus.setColorFilter(Color.parseColor("#6BA55B"));
            }
            tvMessageTime.setText(activity.getFormattedTime(currentChat.getTimestamp()));
            ivImage.setVisibility(View.VISIBLE);

            Glide.with(this)
                    .load(currentChat.getFile_download_url())
                    .apply(RequestOptions.centerCropTransform())
                    .into(ivImage);

        } else if (currentChat.getType().equals("file")) {
            clMessage.setVisibility(View.GONE);
            tvFileName.setText(currentChat.getFile_name());
            tvFileSize.setText(currentChat.getFile_size());
            tvFileExtension.setText(currentChat.getFile_extension());

            Drawable background = rlFile.getBackground();
            background.setTint(Color.parseColor(getFileExtensionIconColor(model.getFile_extension())));
            rlFile.setBackground(background);

            //Set chat status

            if (currentChat.getReceived().size() > 0) {
                ivFileStatus.setImageResource(R.drawable.ic_done_all);
            } else {
                ivFileStatus.setImageResource(R.drawable.ic_done);
            }

            currentChat.getRead().put(activity.spm.getUserId(), DateHelper.getTimestamp());

            if (activity.isChatReadByAll(currentChat)) {
                ivFileStatus.setColorFilter(ContextCompat.getColor(activity, R.color.colorPrimary));
            } else {
                ivFileStatus.setColorFilter(Color.parseColor("#6BA55B"));
            }
            tvFileTime.setText(activity.getFormattedTime(currentChat.getTimestamp()));
        }
    }

    @OnClick({R.id.fragment_chat_info_container_toolbar_arrow_back, R.id.fragment_chat_info_button_toolbar_arrow_back})
    public void onToolbarArrowBackClick() {
        activity.onBackPressed();
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
}
