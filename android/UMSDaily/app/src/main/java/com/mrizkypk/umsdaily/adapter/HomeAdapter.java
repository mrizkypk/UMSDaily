package com.mrizkypk.umsdaily.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
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
import com.mrizkypk.umsdaily.helper.DateHelper;
import com.mrizkypk.umsdaily.manager.SharedPreferenceManager;
import com.mrizkypk.umsdaily.model.ChatHomeModel;
import com.mrizkypk.umsdaily.activity.HomeActivity;
import com.mrizkypk.umsdaily.model.ChatRoomModel;
import com.mrizkypk.umsdaily.model.UserModel;

import java.util.LinkedList;
import java.util.Map;

/**
 * Created by mrizkypk on 01/03/18.
 */

public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public Context context;
    public HomeActivity activity;
    public LinkedList<ChatHomeModel> dataList;
    public Map<String, Integer> unreadCountMap;
    public SharedPreferenceManager spm;

    public static final int TYPE_MESSAGE_SENDER = 0;
    public static final int TYPE_MESSAGE_RECEIVER = 1;
    public static final int TYPE_INFO_JOIN = 2;
    public static final int TYPE_IMAGE = 3;
    public static final int TYPE_FILE = 4;

    public HomeAdapter() {
    }

    public class ViewHolderMessage extends RecyclerView.ViewHolder {
        public View view;
        public TextView tvTitle;
        public TextView tvTime;
        public TextView tvMessage;
        public TextView tvUnreadBadge;
        public ImageView ivAvatar;
        public ImageView ivStatus;
        public View vDivider;

        public ViewHolderMessage(View v) {
            super(v);
            view = v;
            tvTitle = view.findViewById(R.id.item_home_text_title);
            tvTime = view.findViewById(R.id.item_home_text_time);
            tvMessage = view.findViewById(R.id.item_home_text_message);
            tvUnreadBadge = view.findViewById(R.id.item_home_text_unread_badge);
            ivAvatar = view.findViewById(R.id.item_home_image_avatar);
            ivStatus = view.findViewById(R.id.item_home_image_status);
            vDivider = view.findViewById(R.id.item_home_divider);
        }
    }

    public class ViewHolderInfo extends RecyclerView.ViewHolder {
        public View view;
        public TextView tvTitle;
        public TextView tvTime;
        public TextView tvMessage;
        public TextView tvUnreadBadge;
        public ImageView ivAvatar;
        public ImageView ivStatus;
        public View vDivider;

        public ViewHolderInfo(View v) {
            super(v);
            view = v;
            tvTitle = view.findViewById(R.id.item_home_text_title);
            tvTime = view.findViewById(R.id.item_home_text_time);
            tvMessage = view.findViewById(R.id.item_home_text_message);
            tvUnreadBadge = view.findViewById(R.id.item_home_text_unread_badge);
            ivAvatar = view.findViewById(R.id.item_home_image_avatar);
            ivStatus = view.findViewById(R.id.item_home_image_status);
            vDivider = view.findViewById(R.id.item_home_divider);
        }
    }

    public class ViewHolderImage extends RecyclerView.ViewHolder {
        public View view;
        public TextView tvTitle;
        public TextView tvTime;
        public TextView tvMessage;
        public TextView tvUnreadBadge;
        public ImageView ivAvatar;
        public ImageView ivStatus;
        public View vDivider;

        public ViewHolderImage(View v) {
            super(v);
            view = v;
            tvTitle = view.findViewById(R.id.item_home_text_title);
            tvTime = view.findViewById(R.id.item_home_text_time);
            tvMessage = view.findViewById(R.id.item_home_text_message);
            tvUnreadBadge = view.findViewById(R.id.item_home_text_unread_badge);
            ivAvatar = view.findViewById(R.id.item_home_image_avatar);
            ivStatus = view.findViewById(R.id.item_home_image_status);
            vDivider = view.findViewById(R.id.item_home_divider);
        }
    }

    public class ViewHolderFile extends RecyclerView.ViewHolder {
        public View view;
        public TextView tvTitle;
        public TextView tvTime;
        public TextView tvMessage;
        public TextView tvUnreadBadge;
        public ImageView ivAvatar;
        public ImageView ivStatus;
        public View vDivider;


        public ViewHolderFile(View v) {
            super(v);
            view = v;
            tvTitle = view.findViewById(R.id.item_home_text_title);
            tvTime = view.findViewById(R.id.item_home_text_time);
            tvMessage = view.findViewById(R.id.item_home_text_message);
            tvUnreadBadge = view.findViewById(R.id.item_home_text_unread_badge);
            ivAvatar = view.findViewById(R.id.item_home_image_avatar);
            ivStatus = view.findViewById(R.id.item_home_image_status);
            vDivider = view.findViewById(R.id.item_home_divider);
        }
    }

    public HomeAdapter(Context ctx, LinkedList<ChatHomeModel> list, SharedPreferenceManager sharedPreferenceManager) {
        dataList = list;
        context = ctx;
        activity = (HomeActivity) context;
        unreadCountMap = activity.unreadCountMap;
        spm = sharedPreferenceManager;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case TYPE_MESSAGE_SENDER:
                View v1 = inflater.inflate(R.layout.item_home, parent, false);
                viewHolder = new ViewHolderMessage(v1);
                break;
            case TYPE_MESSAGE_RECEIVER:
                View v2 = inflater.inflate(R.layout.item_home, parent, false);
                viewHolder = new ViewHolderMessage(v2);
                break;
            case TYPE_INFO_JOIN:
                View v3 = inflater.inflate(R.layout.item_home, parent, false);
                viewHolder = new ViewHolderInfo(v3);
                break;
            case TYPE_IMAGE:
                View v4 = inflater.inflate(R.layout.item_home_image, parent, false);
                viewHolder = new ViewHolderImage(v4);
                break;
            case TYPE_FILE:
                View v5 = inflater.inflate(R.layout.item_home_file, parent, false);
                viewHolder = new ViewHolderFile(v5);
                break;
            default:
                View v = inflater.inflate(R.layout.item_home, parent, false);
                viewHolder = new ViewHolderMessage(v);
                break;
        }

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final ChatHomeModel model = dataList.get(position);

        if (model.getRoom_type().equals("private")) {
            if (model.getSender_id().equals(activity.spm.getUserId())) {
                model.setRoom_title(model.getReceiver_name());
            } else {
                model.setRoom_title(model.getSender_name());
            }
        }
        if (model.getType().equals("message")) {
            if (position == dataList.size() - 1) {
                ((ViewHolderMessage) holder).vDivider.setVisibility(View.GONE);
            } else {
                ((ViewHolderMessage) holder).vDivider.setVisibility(View.VISIBLE);
            }

            ((ViewHolderMessage) holder).tvTitle.setText(model.getRoom_title());
            ((ViewHolderMessage) holder).tvTime.setText(activity.getRelativeDateTimeString(model.getTimestamp()));

            if (model.getSender_id().equals(activity.spm.getUserId())) {
                //Set chat status
                ((ViewHolderMessage) holder).ivStatus.setVisibility(View.VISIBLE);
                if (model.getReceived().size() > 1) {
                    ((ViewHolderMessage) holder).ivStatus.setImageResource(R.drawable.ic_done_all);
                } else {
                    ((ViewHolderMessage) holder).ivStatus.setImageResource(R.drawable.ic_done);
                }

                if (activity.isChatReadByAll(model)) {
                    ((ViewHolderMessage) holder).ivStatus.setColorFilter(ContextCompat.getColor(activity, R.color.colorPrimary));
                } else {
                    ((ViewHolderMessage) holder).ivStatus.setColorFilter(Color.parseColor("#6BA55B"));
                }
            } else {
                ((ViewHolderMessage) holder).ivStatus.setVisibility(View.GONE);
            }

            if (model.getRoom_type().equals("private")) {
                ((ViewHolderMessage) holder).tvMessage.setText(model.getMessage());
            } else {
                if (model.getSender_id().equals(activity.spm.getUserId())) {
                    ((ViewHolderMessage) holder).tvMessage.setText(model.getMessage());
                } else {
                    ((ViewHolderMessage) holder).tvMessage.setText(activity.getFirstWord(model.getSender_name()) + ": " + model.getMessage());
                }
            }

            if (unreadCountMap.containsKey(model.getRoom_id())) {
                if (unreadCountMap.get(model.getRoom_id()) > 0) {
                    ((ViewHolderMessage) holder).tvUnreadBadge.setText(String.valueOf(unreadCountMap.get(model.getRoom_id())));
                    ((ViewHolderMessage) holder).tvUnreadBadge.setVisibility(View.VISIBLE);
                } else {
                    unreadCountMap.remove(model.getRoom_id());
                    ((ViewHolderMessage) holder).tvUnreadBadge.setVisibility(View.GONE);
                }
            } else {
                ((ViewHolderMessage) holder).tvUnreadBadge.setVisibility(View.GONE);
            }
            ((ViewHolderMessage) holder).view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    unreadCountMap.remove(model.getRoom_id());
                    if (model.getRoom_type() != null) {
                        if (model.getRoom_type().equals("public")) {
                            activity.openChatRoomFragment(model.getRoom_id(), model.getRoom_title(), 1);
                        } else {
                            if (model.getSender_id().equals(activity.spm.getUserId())) {
                                activity.openPrivateChatRoomFragment(model.getRoom_id(), model.getReceiver_name());
                            } else {
                                activity.openPrivateChatRoomFragment(model.getRoom_id(), model.getSender_name());
                            }
                        }
                    } else {
                        activity.openChatRoomFragment(model.getRoom_id(), model.getRoom_title(), 1);
                    }
                }
            });

            if (model.getRoom_type().equals("private")) {
                final String avatarId = model.getRoom_id().replace("@", "").replace(activity.spm.getUserId(), "");
                UserModel user = activity.userModelMap.get(avatarId);
                if (user == null) {
                    user = new UserModel();
                    user.setAvatar_url("https://api.adorable.io/avatars/56/" + avatarId + ".png");
                }

                Glide.with(activity)
                        .load(user.getAvatar_url())
                        .apply(RequestOptions.circleCropTransform())
                        .into(((ViewHolderMessage) holder).ivAvatar);

                ((ViewHolderMessage) holder).ivAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UserModel user2 = activity.userModelMap.get(avatarId);
                        if (user2 == null) {
                            user2 = new UserModel();
                            user2.setAvatar_url("https://api.adorable.io/avatars/56/" + avatarId + ".png");
                        }

                        ChatRoomModel avatar = new ChatRoomModel();
                        avatar.setRoom_id(model.getRoom_id());
                        avatar.setRoom_type("private");
                        avatar.setSender_name(model.getRoom_title());
                        avatar.setFile_name(model.getRoom_id());
                        avatar.setFile_download_url(user2.getAvatar_url());
                        avatar.setTimestamp(DateHelper.getTimestamp());

                        activity.openImageFromHomeFragment(avatar);
                    }
                });
            } else {
                Glide.with(activity)
                        .load(activity.getRoomAvatarUrl(model.getRoom_id()))
                        .apply(RequestOptions.circleCropTransform())
                        .into(((ViewHolderMessage) holder).ivAvatar);

                ((ViewHolderMessage) holder).ivAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ChatRoomModel avatar = new ChatRoomModel();
                        avatar.setRoom_id(model.getRoom_id());
                        avatar.setRoom_type("public");
                        avatar.setSender_name(model.getRoom_title());
                        avatar.setFile_name(model.getRoom_id());
                        avatar.setFile_download_url(activity.getRoomAvatarUrl(model.getRoom_id()));
                        avatar.setTimestamp(DateHelper.getTimestamp());

                        activity.openImageFromHomeFragment(avatar);
                    }
                });
            }

        } else if (model.getType().equals("image")) {
            if (position == dataList.size() - 1) {
                ((ViewHolderImage) holder).vDivider.setVisibility(View.GONE);
            } else {
                ((ViewHolderImage) holder).vDivider.setVisibility(View.VISIBLE);
            }

            ((ViewHolderImage) holder).tvTitle.setText(model.getRoom_title());
            ((ViewHolderImage) holder).tvTime.setText(activity.getRelativeDateTimeString(model.getTimestamp()));

            if (model.getSender_id().equals(activity.spm.getUserId())) {
                //Set chat status
                ((ViewHolderImage) holder).ivStatus.setVisibility(View.VISIBLE);

                if (model.getReceived().size() > 1) {
                    ((ViewHolderImage) holder).ivStatus.setImageResource(R.drawable.ic_done_all);
                } else {
                    ((ViewHolderImage) holder).ivStatus.setImageResource(R.drawable.ic_done);
                }

                if (activity.isChatReadByAll(model)) {
                    ((ViewHolderImage) holder).ivStatus.setColorFilter(ContextCompat.getColor(activity, R.color.colorPrimary));
                } else {
                    ((ViewHolderImage) holder).ivStatus.setColorFilter(Color.parseColor("#6BA55B"));
                }
            } else {
                ((ViewHolderImage) holder).ivStatus.setVisibility(View.GONE);
            }

            if (model.getRoom_type().equals("private")) {
                if (model.getMessage().isEmpty()) {
                    ((ViewHolderImage) holder).tvMessage.setText("Gambar");
                } else {
                    ((ViewHolderImage) holder).tvMessage.setText(model.getMessage());
                }
            } else {
                if (model.getSender_id().equals(activity.spm.getUserId())) {
                    if (model.getMessage().isEmpty()) {
                        ((ViewHolderImage) holder).tvMessage.setText("Gambar");
                    } else {
                        ((ViewHolderImage) holder).tvMessage.setText(model.getMessage());
                    }
                } else {
                    if (model.getMessage().isEmpty()) {
                        ((ViewHolderImage) holder).tvMessage.setText(activity.getFirstWord(model.getSender_name()) + ": Gambar");
                    } else {
                        ((ViewHolderImage) holder).tvMessage.setText(activity.getFirstWord(model.getSender_name()) + ": " + model.getMessage());
                    }
                }
            }

            if (unreadCountMap.containsKey(model.getRoom_id())) {
                if (unreadCountMap.get(model.getRoom_id()) > 0) {
                    ((ViewHolderImage) holder).tvUnreadBadge.setText(String.valueOf(unreadCountMap.get(model.getRoom_id())));
                    ((ViewHolderImage) holder).tvUnreadBadge.setVisibility(View.VISIBLE);
                } else {
                    unreadCountMap.remove(model.getRoom_id());
                }
            } else {
                ((ViewHolderImage) holder).tvUnreadBadge.setVisibility(View.GONE);
            }
            ((ViewHolderImage) holder).view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    unreadCountMap.remove(model.getRoom_id());
                    if (model.getRoom_type() != null) {
                        if (model.getRoom_type().equals("public")) {
                            activity.openChatRoomFragment(model.getRoom_id(), model.getRoom_title(), 1);
                        } else {
                            if (model.getSender_id().equals(activity.spm.getUserId())) {
                                activity.openPrivateChatRoomFragment(model.getRoom_id(), model.getReceiver_name());
                            } else {
                                activity.openPrivateChatRoomFragment(model.getRoom_id(), model.getSender_name());
                            }
                        }
                    } else {
                        activity.openChatRoomFragment(model.getRoom_id(), model.getRoom_title(), 1);
                    }
                }
            });

            if (model.getRoom_type().equals("private")) {
                final String avatarId = model.getRoom_id().replace("@", "").replace(activity.spm.getUserId(), "");
                UserModel user = activity.userModelMap.get(avatarId);
                if (user == null) {
                    user = new UserModel();
                    user.setAvatar_url("https://api.adorable.io/avatars/56/" + avatarId + ".png");
                }
                Glide.with(activity)
                        .load(user.getAvatar_url())
                        .apply(RequestOptions.circleCropTransform())
                        .into(((ViewHolderImage) holder).ivAvatar);

                ((ViewHolderImage) holder).ivAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UserModel user2 = activity.userModelMap.get(avatarId);
                        if (user2 == null) {
                            user2 = new UserModel();
                            user2.setAvatar_url("https://api.adorable.io/avatars/56/" + avatarId + ".png");
                        }

                        ChatRoomModel avatar = new ChatRoomModel();
                        avatar.setRoom_id(model.getRoom_id());
                        avatar.setRoom_type("private");
                        avatar.setSender_name(model.getRoom_title());
                        avatar.setFile_name(model.getRoom_id());
                        avatar.setFile_download_url(user2.getAvatar_url());
                        avatar.setTimestamp(DateHelper.getTimestamp());

                        activity.openImageFromHomeFragment(avatar);
                    }
                });
            } else {
                Glide.with(activity)
                        .load(activity.getRoomAvatarUrl(model.getRoom_id()))
                        .apply(RequestOptions.circleCropTransform())
                        .into(((ViewHolderImage) holder).ivAvatar);

                ((ViewHolderImage) holder).ivAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ChatRoomModel avatar = new ChatRoomModel();
                        avatar.setRoom_id(model.getRoom_id());
                        avatar.setRoom_type("public");
                        avatar.setSender_name(model.getRoom_title());
                        avatar.setFile_name(model.getRoom_id());
                        avatar.setFile_download_url(activity.getRoomAvatarUrl(model.getRoom_id()));
                        avatar.setTimestamp(DateHelper.getTimestamp());

                        activity.openImageFromHomeFragment(avatar);
                    }
                });
            }
        } else if (model.getType().equals("info_join")) {
            if (position == dataList.size() - 1) {
                ((ViewHolderInfo) holder).vDivider.setVisibility(View.GONE);
            } else {
                ((ViewHolderInfo) holder).vDivider.setVisibility(View.VISIBLE);
            }

            ((ViewHolderInfo) holder).tvTitle.setText(model.getRoom_title());
            ((ViewHolderInfo) holder).tvTime.setText(activity.getRelativeDateTimeString(model.getTimestamp()));
            ((ViewHolderInfo) holder).tvMessage.setText(activity.getFirstWord(model.getSender_name()) + " bergabung dalam obrolan");

            ((ViewHolderInfo) holder).ivStatus.setVisibility(View.GONE);

            if (unreadCountMap.containsKey(model.getRoom_id())) {
                if (unreadCountMap.get(model.getRoom_id()) > 0) {
                    ((ViewHolderInfo) holder).tvUnreadBadge.setText(String.valueOf(unreadCountMap.get(model.getRoom_id())));
                    ((ViewHolderInfo) holder).tvUnreadBadge.setVisibility(View.VISIBLE);
                } else {
                    unreadCountMap.remove(model.getRoom_id());
                }
            } else {
                ((ViewHolderInfo) holder).tvUnreadBadge.setVisibility(View.GONE);
            }
            ((ViewHolderInfo) holder).view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    unreadCountMap.remove(model.getRoom_id());
                    if (model.getRoom_type() != null) {
                        if (model.getRoom_type().equals("public")) {
                            activity.openChatRoomFragment(model.getRoom_id(), model.getRoom_title(), 1);
                        } else {
                            if (model.getSender_id().equals(activity.spm.getUserId())) {
                                activity.openPrivateChatRoomFragment(model.getRoom_id(), model.getReceiver_name());
                            } else {
                                activity.openPrivateChatRoomFragment(model.getRoom_id(), model.getSender_name());
                            }
                        }
                    } else {
                        activity.openChatRoomFragment(model.getRoom_id(), model.getRoom_title(), 1);
                    }
                }
            });

            if (model.getRoom_type().equals("private")) {
                final String avatarId = model.getRoom_id().replace("@", "").replace(activity.spm.getUserId(), "");
                UserModel user = activity.userModelMap.get(avatarId);
                if (user == null) {
                    user = new UserModel();
                    user.setAvatar_url("https://api.adorable.io/avatars/56/" + avatarId + ".png");
                }
                Glide.with(activity)
                        .load(user.getAvatar_url())
                        .apply(RequestOptions.circleCropTransform())
                        .into(((ViewHolderInfo) holder).ivAvatar);

                ((ViewHolderInfo) holder).ivAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UserModel user2 = activity.userModelMap.get(avatarId);
                        if (user2 == null) {
                            user2 = new UserModel();
                            user2.setAvatar_url("https://api.adorable.io/avatars/56/" + avatarId + ".png");
                        }

                        ChatRoomModel avatar = new ChatRoomModel();
                        avatar.setRoom_id(model.getRoom_id());
                        avatar.setRoom_type("private");
                        avatar.setSender_name(model.getRoom_title());
                        avatar.setFile_name(model.getRoom_id());
                        avatar.setFile_download_url(user2.getAvatar_url());
                        avatar.setTimestamp(DateHelper.getTimestamp());

                        activity.openImageFromHomeFragment(avatar);
                    }
                });
            } else {
                Glide.with(activity)
                        .load(activity.getRoomAvatarUrl(model.getRoom_id()))
                        .apply(RequestOptions.circleCropTransform())
                        .into(((ViewHolderInfo) holder).ivAvatar);

                ((ViewHolderInfo) holder).ivAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ChatRoomModel avatar = new ChatRoomModel();
                        avatar.setRoom_id(model.getRoom_id());
                        avatar.setRoom_type("public");
                        avatar.setSender_name(model.getRoom_title());
                        avatar.setFile_name(model.getRoom_id());
                        avatar.setFile_download_url(activity.getRoomAvatarUrl(model.getRoom_id()));
                        avatar.setTimestamp(DateHelper.getTimestamp());

                        activity.openImageFromHomeFragment(avatar);
                    }
                });
            }
        } else if (model.getType().equals("file")) {
            if (position == dataList.size() - 1) {
                ((ViewHolderFile) holder).vDivider.setVisibility(View.GONE);
            } else {
                ((ViewHolderFile) holder).vDivider.setVisibility(View.VISIBLE);
            }

            ((ViewHolderFile) holder).tvTitle.setText(model.getRoom_title());
            ((ViewHolderFile) holder).tvTime.setText(activity.getRelativeDateTimeString(model.getTimestamp()));

            if (model.getSender_id().equals(activity.spm.getUserId())) {
                //Set chat status
                ((ViewHolderFile) holder).ivStatus.setVisibility(View.VISIBLE);

                if (model.getReceived().size() > 1) {
                    ((ViewHolderFile) holder).ivStatus.setImageResource(R.drawable.ic_done_all);
                } else {
                    ((ViewHolderFile) holder).ivStatus.setImageResource(R.drawable.ic_done);
                }

                if (activity.isChatReadByAll(model)) {
                    ((ViewHolderFile) holder).ivStatus.setColorFilter(ContextCompat.getColor(activity, R.color.colorPrimary));
                } else {
                    ((ViewHolderFile) holder).ivStatus.setColorFilter(Color.parseColor("#6BA55B"));
                }
            } else {
                ((ViewHolderFile) holder).ivStatus.setVisibility(View.GONE);
            }

            if (model.getRoom_type().equals("private")) {
                ((ViewHolderFile) holder).tvMessage.setText("Berkas");
            } else {
                if (model.getSender_id().equals(activity.spm.getUserId())) {
                    ((ViewHolderFile) holder).tvMessage.setText("Berkas");
                } else {
                    ((ViewHolderFile) holder).tvMessage.setText(activity.getFirstWord(model.getSender_name()) + ": Berkas");
                }
            }

            if (unreadCountMap.containsKey(model.getRoom_id())) {
                if (unreadCountMap.get(model.getRoom_id()) > 0) {
                    ((ViewHolderFile) holder).tvUnreadBadge.setText(String.valueOf(unreadCountMap.get(model.getRoom_id())));
                    ((ViewHolderFile) holder).tvUnreadBadge.setVisibility(View.VISIBLE);
                } else {
                    unreadCountMap.remove(model.getRoom_id());
                }
            } else {
                ((ViewHolderFile) holder).tvUnreadBadge.setVisibility(View.GONE);
            }
            ((ViewHolderFile) holder).view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    unreadCountMap.remove(model.getRoom_id());
                    if (model.getRoom_type() != null) {
                        if (model.getRoom_type().equals("public")) {
                            activity.openChatRoomFragment(model.getRoom_id(), model.getRoom_title(), 1);
                        } else {
                            if (model.getSender_id().equals(activity.spm.getUserId())) {
                                activity.openPrivateChatRoomFragment(model.getRoom_id(), model.getReceiver_name());
                            } else {
                                activity.openPrivateChatRoomFragment(model.getRoom_id(), model.getSender_name());
                            }
                        }
                    } else {
                        activity.openChatRoomFragment(model.getRoom_id(), model.getRoom_title(), 1);
                    }
                }
            });

            if (model.getRoom_type().equals("private")) {
                final String avatarId = model.getRoom_id().replace("@", "").replace(activity.spm.getUserId(), "");
                UserModel user = activity.userModelMap.get(avatarId);
                if (user == null) {
                    user = new UserModel();
                    user.setAvatar_url("https://api.adorable.io/avatars/56/" + avatarId + ".png");
                }
                Glide.with(activity)
                        .load(user.getAvatar_url())
                        .apply(RequestOptions.circleCropTransform())
                        .into(((ViewHolderFile) holder).ivAvatar);

                ((ViewHolderFile) holder).ivAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UserModel user2 = activity.userModelMap.get(avatarId);
                        if (user2 == null) {
                            user2 = new UserModel();
                            user2.setAvatar_url("https://api.adorable.io/avatars/56/" + avatarId + ".png");
                        }

                        ChatRoomModel avatar = new ChatRoomModel();
                        avatar.setRoom_id(model.getRoom_id());
                        avatar.setRoom_type("private");
                        avatar.setSender_name(model.getRoom_title());
                        avatar.setFile_name(model.getRoom_id());
                        avatar.setFile_download_url(user2.getAvatar_url());
                        avatar.setTimestamp(DateHelper.getTimestamp());

                        activity.openImageFromHomeFragment(avatar);
                    }
                });
            } else {
                Glide.with(activity)
                        .load(activity.getRoomAvatarUrl(model.getRoom_id()))
                        .apply(RequestOptions.circleCropTransform())
                        .into(((ViewHolderFile) holder).ivAvatar);

                ((ViewHolderFile) holder).ivAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ChatRoomModel avatar = new ChatRoomModel();
                        avatar.setRoom_id(model.getRoom_id());
                        avatar.setRoom_type("public");
                        avatar.setSender_name(model.getRoom_title());
                        avatar.setFile_name(model.getRoom_id());
                        avatar.setFile_download_url(activity.getRoomAvatarUrl(model.getRoom_id()));
                        avatar.setTimestamp(DateHelper.getTimestamp());

                        activity.openImageFromHomeFragment(avatar);
                    }
                });
            }
        }

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        ChatHomeModel model = dataList.get(position);
        if (model.getType().equals("message")) {
            if (model.getSender_id().equals(spm.getUserId())) {
                return TYPE_MESSAGE_SENDER;
            } else {
                return TYPE_MESSAGE_RECEIVER;
            }
        } else if (model.getType().equals("info_join")) {
            return TYPE_INFO_JOIN;
        } else if (model.getType().equals("image")) {
            return TYPE_IMAGE;
        } else if (model.getType().equals("file")) {
            return TYPE_FILE;
        }

        return -1;
    }
}