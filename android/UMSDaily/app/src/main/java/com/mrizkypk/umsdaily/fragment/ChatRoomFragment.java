package com.mrizkypk.umsdaily.fragment;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.mrizkypk.umsdaily.R;
import com.mrizkypk.umsdaily.adapter.ChatAdapter;
import com.mrizkypk.umsdaily.helper.DateHelper;
import com.mrizkypk.umsdaily.helper.NotificationHelper;
import com.mrizkypk.umsdaily.helper.PathHelper;
import com.mrizkypk.umsdaily.model.ChatRoomModel;
import com.mrizkypk.umsdaily.activity.AttachImageActivity;
import com.mrizkypk.umsdaily.activity.HomeActivity;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

import static android.app.Activity.RESULT_OK;
import static android.os.Environment.getExternalStoragePublicDirectory;

public class ChatRoomFragment extends Fragment {
    public static final String TAG = "MRPKChatRoomFragment";
    public View view;
    public String message;
    public String senderId;
    public String senderName;
    public String senderAvatarUrl;
    public String receiverId;
    public String receiverName;
    public String receiverAvatarUrl;
    public String roomId;
    public String roomTitle;
    public String roomType;
    public String roomReff;
    public Uri fileUri;
    public String fileFromCameraPath;
    public String fileName;
    public String fileSize;
    public String filePath;
    public String fileExtension;
    public String fileType;
    public HomeActivity activity;
    public RecyclerView recyclerview;
    public RecyclerView.LayoutManager layoutManager;
    public Query ref;
    public DatabaseReference refRoom;
    public DatabaseReference refNotif;
    public ChatRoomModel replyModel;

    @BindView(R.id.fragment_chat_room_container_toolbar)
    RelativeLayout rlToolbar;

    @BindView(R.id.fragment_chat_room_text_toolbar_title)
    TextView tvToolbarTitle;

    @BindView(R.id.fragment_chat_room_text_toolbar_subtitle)
    TextView tvToolbarSubtitle;

    @BindView(R.id.fragment_chat_room_image_toolbar_logo)
    ImageView ivToolbarLogo;

    @BindView(R.id.fragment_chat_room_container_toolbar_option)
    ConstraintLayout clToolbarOption;

    @BindView(R.id.fragment_chat_room_text_toolbar_option_selected_count)
    TextView tvToolbarOptionSelectedCount;

    @BindView(R.id.fragment_chat_room_container_toolbar_option_info)
    LinearLayout llToolbarOptionInfo;

    @BindView(R.id.fragment_chat_room_container_toolbar_option_reply)
    LinearLayout llToolbarOptionReply;

    @BindView(R.id.fragment_chat_room_container_toolbar_option_delete)
    LinearLayout llToolbarOptionDelete;

    @BindView(R.id.fragment_chat_room_input_message)
    public EditText etMessage;

    @BindView(R.id.fragment_chat_room_container_attach_option)
    LinearLayout llAttachOption;

    @BindView(R.id.fragment_chat_room_container_reply)
    LinearLayout llReply;

    @BindView(R.id.item_room_chat_image_container)
    ConstraintLayout clImage;

    @BindView(R.id.item_room_chat_file_container)
    ConstraintLayout clFile;

    @BindView(R.id.item_room_chat_message_container)
    ConstraintLayout clMessage;

    @BindView(R.id.item_room_chat_message_text_message)
    TextView tvReplyMessage;

    @BindView(R.id.item_room_chat_message_text_sender)
    TextView tvReplyMessageSender;

    @BindView(R.id.item_room_chat_file_text_sender)
    TextView tvReplyFileSender;

    @BindView(R.id.item_room_chat_image_text_sender)
    TextView tvReplyImageSender;

    @BindView(R.id.item_room_chat_image)
    ImageView ivReplyImage;

    @BindView(R.id.item_room_chat_image_text_label)
    TextView tvReplyImageLabel;

    @BindView(R.id.item_room_chat_file_text_file_name)
    TextView tvReplyFileName;

    @BindView(R.id.item_room_chat_file_text_file_extension)
    TextView tvReplyFileExtension;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = ((HomeActivity) getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_room, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        view = v;

        roomId = getArguments().getString("BUNDLE_ROOM_ID");
        roomTitle = getArguments().getString("BUNDLE_ROOM_TITLE");
        roomType = getArguments().getString("BUNDLE_ROOM_TYPE");
        roomReff = getArguments().getString("BUNDLE_ROOM_REFF");

        //Clean notification sharedpreferences

        activity.spm.removeList(roomId);

        if (activity.roomMessageMap.containsKey(roomId)) {
            if (activity.roomMessageMap.get(roomId) == null) {
                etMessage.setText("");
            } else {
                etMessage.setText(activity.roomMessageMap.get(roomId));
            }
        } else {
            etMessage.setText("");
        }

        if (roomType.equals("private")) {
            senderId = activity.spm.getUserId();
            if (activity.userModelMap.containsKey(senderId)) {
                senderName = activity.userModelMap.get(senderId).getName();
                senderAvatarUrl = activity.userModelMap.get(senderId).getAvatar_url();
            } else {
                senderName = activity.spm.getUserName();
                receiverAvatarUrl = "https://api.adorable.io/avatars/56/" + senderId + ".png";
            }
            receiverId = roomId.replace("@", "").replace(senderId, "");
            if (activity.userModelMap.containsKey(receiverId)) {
                receiverName = activity.userModelMap.get(receiverId).getName();
                receiverAvatarUrl = activity.userModelMap.get(receiverId).getAvatar_url();
            } else {
                if (activity.composeModelList.contains(receiverId)) {
                    receiverName = activity.userModelMap.get(receiverId).getName();
                    receiverAvatarUrl = activity.userModelMap.get(receiverId).getAvatar_url();
                } else {
                    receiverName = roomTitle;
                    receiverAvatarUrl = "https://api.adorable.io/avatars/56/" + receiverId + ".png";
                }
            }

            tvToolbarTitle.setText(roomTitle);
            tvToolbarSubtitle.setText(receiverId);
            Glide.with(this)
                    .load(receiverAvatarUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivToolbarLogo);
            ivToolbarLogo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ChatRoomModel avatar = new ChatRoomModel();
                    avatar.setRoom_id(roomId);
                    avatar.setRoom_type("private");
                    avatar.setSender_name(roomTitle);
                    avatar.setFile_name(roomId);
                    avatar.setFile_download_url(activity.userModelMap.get(receiverId).getAvatar_url());
                    avatar.setTimestamp(DateHelper.getTimestamp());

                    activity.openPrivateImageFromRoomFragment(avatar);
                }
            });

        } else {
            tvToolbarTitle.setText(roomTitle);
            tvToolbarSubtitle.setText(roomId);

            Glide.with(this)
                    .load(activity.getRoomAvatarUrl(roomId))
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivToolbarLogo);

            ivToolbarLogo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ChatRoomModel avatar = new ChatRoomModel();
                    avatar.setRoom_id(roomId);
                    avatar.setRoom_type("public");
                    avatar.setSender_name(roomTitle);
                    avatar.setFile_name(roomId);
                    avatar.setFile_download_url(activity.getRoomAvatarUrl(roomId));
                    avatar.setTimestamp(DateHelper.getTimestamp());

                    activity.openImageFromRoomFragment(avatar);
                }
            });
        }

        layoutManager = new LinearLayoutManager(getContext());

        if (!activity.roomModelMap.containsKey(roomId)) {
            activity.roomModelMap.put(roomId, new LinkedList<ChatRoomModel>());
        }
        activity.roomAdapter = new ChatAdapter(getContext(), activity.spm.getUserId(), activity.roomModelMap.get(roomId));

        recyclerview = v.findViewById(R.id.fragment_chat_room_recyclerview);
        recyclerview.setHasFixedSize(true);

        recyclerview.setLayoutManager(layoutManager);

        recyclerview.setAdapter(activity.roomAdapter);
        activity.smootScroll = true;

        recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                activity.smootScroll = false;
                if (!recyclerView.canScrollVertically(1)) {
                    activity.smootScroll = true;
                }
            }
        });

        ((LinearLayoutManager) recyclerview.getLayoutManager()).setStackFromEnd(true);

        if (activity.roomRefMap.containsKey(roomId)) {
            ref = activity.roomRefMap.get(roomId);
        } else {
            ref = activity.db
                    .getReference("chat")
                    .child(roomId)
                    .orderByChild("timestamp");

        }
        updateReadStatus(roomId);

        rlToolbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        clToolbarOption.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        refNotif = activity.db.getReference().child("notification");
    }

    public void updateRoomAvatar(String url) {
        Glide.with(this)
                .load(url)
                .apply(RequestOptions.circleCropTransform())
                .into(ivToolbarLogo);
    }

    @Override
    public void onStart() {
        super.onStart();
        recyclerview.scrollToPosition(activity.roomAdapter.getItemCount() - 1);
    }

    public void sendNotification(String id, String content) {
        if (roomType.equals("public")) {
            NotificationHelper.send(id,"CHAT_MESSAGE_PUBLIC", "", roomId, activity.spm.getUserId(), activity.spm.getUserName(), "", "", activity.getRoomAvatarUrl(roomId), "", roomTitle, content, "");
        } else if (roomType.equals("private")) {
            NotificationHelper.send(id,"CHAT_MESSAGE_PRIVATE", "", roomId, activity.spm.getUserId(), activity.spm.getUserName(), receiverId, receiverName, senderAvatarUrl, "", activity.spm.getUserName(), content, "");
        }
    }

    public void sendImageNotification(String id, String content, String imageUrl) {
        if (roomType.equals("public")) {
            NotificationHelper.send(id,"CHAT_IMAGE_PUBLIC", "", roomId, activity.spm.getUserId(), activity.spm.getUserName(), "", "", activity.getRoomAvatarUrl(roomId), imageUrl, roomTitle, content, "");
        } else if (roomType.equals("private")) {
            NotificationHelper.send(id,"CHAT_IMAGE_PRIVATE", "", roomId, activity.spm.getUserId(), activity.spm.getUserName(), receiverId, receiverName, senderAvatarUrl, imageUrl, activity.spm.getUserName(), content, "");
        }
    }

    public void sendFileNotification(String id) {
        if (roomType.equals("public")) {
            NotificationHelper.send(id,"CHAT_MESSAGE_PUBLIC", "", roomId, activity.spm.getUserId(), activity.spm.getUserName(), "", "", activity.getRoomAvatarUrl(roomId), "", roomTitle, "Mengirim Berkas", "");
        } else if (roomType.equals("private")) {
            NotificationHelper.send(id,"CHAT_MESSAGE_PRIVATE", "", roomId, activity.spm.getUserId(), activity.spm.getUserName(), receiverId, receiverName, senderAvatarUrl, "", activity.spm.getUserName(), "Mengirim Berkas", "");
        }
    }

    public void updateReadStatus(String id) {
        if (activity.roomModelMap.containsKey(id)) {
            LinkedList<ChatRoomModel> list = activity.roomModelMap.get(id);
            for (ChatRoomModel chat : list) {
                if (!chat.getRead().containsKey(activity.spm.getUserId())) {
                    String key = chat.getId();
                    DatabaseReference readRef = activity.db
                            .getReference("chat")
                            .child(roomId)
                            .child(key)
                            .child("read")
                            .child(activity.spm.getUserId());

                    readRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Long timestamp = DateHelper.getTimestamp();
                            if (!dataSnapshot.exists() && !activity.isInBackground) {
                                dataSnapshot.getRef().setValue(timestamp);
                                //Clear notification after read
                                NotificationManager notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
                                notificationManager.cancel(roomId.hashCode());

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    if (notificationManager.getActiveNotifications().length == 1) {
                                        notificationManager.cancel(0);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        }

        //Update home badge
        activity.updateBadgeHome();
    }

    @OnClick({R.id.fragment_chat_room_container_toolbar_arrow_back, R.id.fragment_chat_room_button_toolbar_arrow_back})
    public void onClickToolbarArrowBack() {
        activity.hideSoftKeyboard();
        activity.onBackPressed();
    }

    @OnClick({R.id.fragment_chat_room_container_send, R.id.fragment_chat_room_button_send})
    public void sendMessage() {
        message = etMessage.getText().toString().trim();
        if (!message.isEmpty()) {
            if (roomType.equals("private")) {
                if (ref == null) {
                    ref = activity.roomRefMap.get(roomId);
                }
                refRoom = activity.db.getReference("room").child(roomId);
                refRoom.child("member/" + senderId).setValue(true);
                refRoom.child("member/" + receiverId).setValue(true);
            }

            etMessage.setText("");
            llAttachOption.setVisibility(View.GONE);
            Long timestamp = DateHelper.getTimestamp();

            HashMap<String, Long> read = new HashMap<>();
            read.put(activity.spm.getUserId(), timestamp);

            HashMap<String, Long> received = new HashMap<>();
            received.put(activity.spm.getUserId(), timestamp);

            final String key = ref.getRef().push().getKey();

            ChatRoomModel chat = new ChatRoomModel();
            chat.setId(key);
            chat.setRoom_id(roomId);
            chat.setTimestamp(timestamp);
            chat.setSender_id(activity.spm.getUserId());
            chat.setSender_name(activity.spm.getUserName());
            chat.setRoom_type(roomType);
            if (roomType.equals("public")) {
                chat.setRoom_title(roomTitle);
            } else {
                chat.setReceiver_id(receiverId);
                chat.setReceiver_name(receiverName);
            }
            chat.setRead(read);
            chat.setReceived(received);
            chat.setMessage(message);
            chat.setType("message");
            if (replyModel != null) {
                chat.setReply_id(replyModel.getId());
                chat.setReply_type(replyModel.getType());
                chat.setReply_sender_id(replyModel.getSender_id());
                chat.setReply_sender_name(replyModel.getSender_name());
                chat.setReply_message(replyModel.getMessage());
                chat.setReply_file_name(replyModel.getFile_name());
                chat.setReply_file_extension(replyModel.getFile_extension());
                chat.setReply_file_download_url(replyModel.getFile_download_url());
            }

            replyModel = null;
            onToolbarOptionArrowBackClick();
            onReplyCancelClick();

            ref.getRef().child(key).setValue(chat).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    sendNotification(key, message);
                    if (!activity.isInBackground && activity.spMessageSentReady) {
                        activity.spMessageSent.play(activity.spMessageSentId, activity.volume, activity.volume, 1, 0, 1f);
                    }
                }
            });

            activity.smootScroll = true;
        }
    }

    public void sendAttach() {
        Long timestamp = DateHelper.getTimestamp();

        HashMap<String, Long> read = new HashMap<>();
        read.put(activity.spm.getUserId(), timestamp);

        HashMap<String, Long> received = new HashMap<>();
        received.put(activity.spm.getUserId(), timestamp);

        final String key = ref.getRef().push().getKey();

        fileSize = Formatter.formatShortFileSize(getContext(), getFileSize(fileUri));
        fileName = getFileName(fileUri);
        filePath = activity.spm.getUserId() + "_" + timestamp + "_" + getFileName(fileUri);
        fileExtension = getFileExtension(fileName);

        ChatRoomModel chat = new ChatRoomModel();
        chat.setId(key);
        chat.setRoom_id(roomId);
        chat.setTimestamp(timestamp);
        chat.setSender_id(activity.spm.getUserId());
        chat.setSender_name(activity.spm.getUserName());
        if (roomType.equals("public")) {
            chat.setRoom_title(roomTitle);
        } else {
            chat.setReceiver_id(receiverId);
            chat.setReceiver_name(receiverName);
        }
        chat.setRead(read);
        chat.setReceived(received);

        chat.setFile_name(fileName);
        chat.setFile_path(filePath);
        chat.setFile_local_path(fileUri.toString());
        chat.setFile_size(fileSize);
        chat.setFile_extension(fileExtension);
        chat.setFile_upload_status("start");
        chat.setFile_upload_progress(0);
        chat.setMessage(message);
        chat.setType(fileType);
        chat.setRoom_type(roomType);

        ref.getRef().child(key).setValue(chat, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    StorageReference storageRef = activity.storage.getReference().child(roomId);
                    if (fileType.equals("image")) {
                        storageRef = activity.storage.getReference().child(roomId).child("images").child(filePath);
                    }

                    if (fileType.equals("file")) {
                        storageRef = activity.storage.getReference().child(roomId).child("files").child(filePath);
                    }

                    StorageTask uploadTask = storageRef.putFile(fileUri)
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    int progress = (100 * (int) taskSnapshot.getBytesTransferred()) / (int) taskSnapshot.getTotalByteCount();
                                    ref.getRef().child(key).child("file_upload_progress").setValue(progress);
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            if (!activity.isInBackground && activity.spMessageSentReady) {
                                                activity.spMessageSent.play(activity.spMessageSentId, activity.volume, activity.volume, 1, 0, 1f);
                                            }

                                            String fileDownloadUrl = uri.toString();
                                            ref.getRef().child(key).child("file_upload_status").setValue("complete");
                                            ref.getRef().child(key).child("file_download_url").setValue(fileDownloadUrl);

                                            if (fileType.equals("image")) {
                                                sendImageNotification(key, message, fileDownloadUrl);
                                            }
                                            if (fileType.equals("file")) {
                                                sendFileNotification(key);
                                            }
                                        }
                                    });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(Exception exception) {
                                    Log.d(TAG, exception.getMessage());
                                }
                            });
                    activity.storageMap.put(key, uploadTask);
                    fileUri = null;
                    replyModel = null;
                    onToolbarOptionArrowBackClick();
                    onReplyCancelClick();
                }
            });

        activity.hideSoftKeyboard();
    }

    public void cancelUpload(String key) {
        StorageTask uploadTask = activity.storageMap.get(key);
        if (uploadTask != null) {
            uploadTask.cancel();
        }
        ref.getRef().child(key).child("file_upload_status").setValue("cancel");
    }

    public void restartUpload(final ChatRoomModel model) {
        ref.getRef().child(model.getId()).child("file_upload_status").setValue("start");

        Uri uri = Uri.parse(model.getFile_local_path());
        StorageReference storageRef = activity.storage.getReference().child(model.getRoom_id());
        if (model.getType().equals("image")) {
            storageRef = activity.storage.getReference().child(model.getRoom_id()).child("images").child(model.getFile_path());
        }

        if (model.getType().equals("file")) {
            storageRef = activity.storage.getReference().child(model.getRoom_id()).child("files").child(model.getFile_path());
        }

        StorageTask uploadTask = storageRef.putFile(uri)
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        int progress = (100 * (int) taskSnapshot.getBytesTransferred()) / (int) taskSnapshot.getTotalByteCount();
                        ref.getRef().child(model.getId()).child("file_upload_progress").setValue(progress);
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String fileDownloadUrl = uri.toString();
                                ref.getRef().child(model.getId()).child("file_upload_status").setValue("complete");
                                ref.getRef().child(model.getId()).child("file_download_url").setValue(fileDownloadUrl);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception exception) {
                        Log.d(TAG, exception.getMessage());
                    }
                });
        activity.storageMap.put(model.getId(), uploadTask);
    }

    @OnClick({R.id.fragment_chat_room_container_attach, R.id.fragment_chat_room_button_attach})
    void onClickButtonAttach() {
        replyModel = null;
        llReply.setVisibility(View.GONE);
        if (llAttachOption.getVisibility() == View.VISIBLE) {
            llAttachOption.setVisibility(View.GONE);
        } else {
            llAttachOption.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.fragment_chat_room_container_attach_image, R.id.fragment_chat_room_button_attach_image})
    void onButtonAttachImageClick() {
        selectImage();
        llAttachOption.setVisibility(View.GONE);
    }

    @OnClick({R.id.fragment_chat_room_container_attach_camera, R.id.fragment_chat_room_button_attach_camera})
    void onButtonAttachCameraClick() {
        selectByCamera();
        llAttachOption.setVisibility(View.GONE);
    }

    @OnClick({R.id.fragment_chat_room_container_attach_file, R.id.fragment_chat_room_button_attach_file})
    void onButtonAttachFileClick() {
        selectByFile();
        llAttachOption.setVisibility(View.GONE);
    }

    public void selectImage() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            startActivityForResult(intent, 1);
        }
    }

    public void selectByCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFromCameraFile();
            } catch (IOException ex) {
                Log.d(TAG, ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        "com.mrizkypk.umsdaily.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 2);
            }
        }
    }

    public void selectByFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            startActivityForResult(intent, 3);
        }
    }

    public String getFileName(Uri uri) {
       String path = PathHelper.getPathFromUri(activity, uri);
       File file = new File(path);

       return file.getName();
    }

    public String getFileExtension(String name) {
        return name.substring(name.lastIndexOf(".") + 1);
    }

    public Long getFileSize(Uri uri) {
        Long size;
        if (uri.getScheme().equals("file")) {
            File f = new File(uri.getPath());
            size = f.length();
        } else {
            Cursor returnCursor =
                    activity.getContentResolver().query(uri, null, null, null, null);
            assert returnCursor != null;
            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            returnCursor.moveToFirst();
            size = returnCursor.getLong(sizeIndex);
            returnCursor.close();
        }

        return size;
    }

    private File createImageFromCameraFile() throws IOException {
        String timeStamp = String.valueOf(DateHelper.getTimestamp());
        String imageFileName = activity.spm.getUserId() + "_" + timeStamp;
        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        fileFromCameraPath = "file://" + image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            fileUri = data.getData();

            Intent intent = new Intent(getContext(), AttachImageActivity.class);
            if (roomType.equals("public")) {
                intent.putExtra("CHAT_ROOM_ID", roomId);
                intent.putExtra("CHAT_ROOM_TITLE", roomTitle);
                intent.putExtra("CHAT_ROOM_AVATAR_URL", activity.getRoomAvatarUrl(roomId));
                intent.putExtra("FILE_PATH", fileUri.toString());
            } else {
                intent.putExtra("CHAT_ROOM_ID", receiverId);
                intent.putExtra("CHAT_ROOM_TITLE", receiverName);
                intent.putExtra("CHAT_ROOM_AVATAR_URL", receiverAvatarUrl);
                intent.putExtra("FILE_PATH", fileUri.toString());
            }
            startActivityForResult(intent, 10);
            activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        }

        if (requestCode == 2 && resultCode == RESULT_OK) {
            Intent intent = new Intent(getContext(), AttachImageActivity.class);
            if (roomType.equals("public")) {
                intent.putExtra("CHAT_ROOM_ID", roomId);
                intent.putExtra("CHAT_ROOM_TITLE", roomTitle);
                intent.putExtra("CHAT_ROOM_AVATAR_URL", activity.getRoomAvatarUrl(roomId));
                intent.putExtra("FILE_PATH", fileFromCameraPath);
            } else {
                intent.putExtra("CHAT_ROOM_ID", receiverId);
                intent.putExtra("CHAT_ROOM_TITLE", receiverName);
                intent.putExtra("CHAT_ROOM_AVATAR_URL", receiverAvatarUrl);
                intent.putExtra("FILE_PATH", fileFromCameraPath);
            }
            startActivityForResult(intent, 10);
            activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        }

        if (requestCode == 10 && resultCode == RESULT_OK) {
            message = data.getStringExtra("CHAT_MESSAGE");
            fileUri = Uri.parse(data.getStringExtra("FILE_PATH"));
            fileSize = Formatter.formatShortFileSize(getContext(), getFileSize(fileUri));
            fileName = getFileName(fileUri);
            fileExtension = getFileExtension(fileName);
            fileType = "image";
            sendAttach();
        }

        if (requestCode == 3 && resultCode == RESULT_OK) {
            fileUri = data.getData();
            fileSize = Formatter.formatShortFileSize(getContext(), getFileSize(fileUri));
            fileName = getFileName(fileUri);
            fileExtension = getFileExtension(fileName);
            fileType = "file";
            sendAttach();
        }
    }

    public void addSelected(ChatRoomModel model) {
        hideToolbar();
        showToolbarOption();
        if (activity.selectedModelMap.containsKey(roomId)) {
            if (!activity.selectedModelMap.get(roomId).contains(model)) {
                activity.selectedModelMap.get(roomId).add(model);
            }
        } else {
            LinkedList list = new LinkedList<>();
            list.add(model);
            activity.selectedModelMap.put(roomId, list);
        }
    }

    public void removeSelected(ChatRoomModel model) {
        if (activity.selectedModelMap.containsKey(roomId)) {
            if (activity.selectedModelMap.get(roomId).contains(model)) {
                activity.selectedModelMap.get(roomId).remove(model);
            }
        }
    }

    public void updateSelected() {
        if (activity.selectedModelMap.containsKey(roomId)) {
            int intCount = activity.selectedModelMap.get(roomId).size();
            if (intCount == 0) {
                tvToolbarOptionSelectedCount.setText("0");
                hideToolbarOption();
                showToolbar();
            } else {
                int step = 0;
                boolean onlySender = true;

                String strCount = String.valueOf(intCount);
                tvToolbarOptionSelectedCount.setText(strCount);
                for (ChatRoomModel model : activity.selectedModelMap.get(roomId)) {
                    if (!model.getSender_id().equals(activity.spm.getUserId())) {
                        onlySender = false;
                    }

                    if (step + 1 == intCount) {
                        if (intCount > 1) {
                            if (onlySender) {
                                llToolbarOptionInfo.setVisibility(View.GONE);
                                llToolbarOptionReply.setVisibility(View.GONE);
                                llToolbarOptionDelete.setVisibility(View.VISIBLE);
                            } else {
                                llToolbarOptionInfo.setVisibility(View.GONE);
                                llToolbarOptionReply.setVisibility(View.GONE);
                                llToolbarOptionDelete.setVisibility(View.GONE);
                            }
                        } else {
                            if (onlySender) {
                                llToolbarOptionInfo.setVisibility(View.VISIBLE);
                                llToolbarOptionReply.setVisibility(View.GONE);
                                llToolbarOptionDelete.setVisibility(View.VISIBLE);
                            } else {
                                llToolbarOptionInfo.setVisibility(View.GONE);
                                llToolbarOptionReply.setVisibility(View.VISIBLE);
                                llToolbarOptionDelete.setVisibility(View.GONE);
                            }
                        }
                    }
                    step++;
                }
            };
        } else {
            tvToolbarOptionSelectedCount.setText("0");
            hideToolbarOption();
            showToolbar();
        }
    }

    public void changeAvatar(String url) {
        Glide.with(this)
                .load(url)
                .apply(RequestOptions.circleCropTransform())
                .into(ivToolbarLogo);
    }

    public boolean isAnySelected() {
        if (activity.selectedModelMap.containsKey(roomId)) {
            int intCount = activity.selectedModelMap.get(roomId).size();
            if (intCount == 0) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }


    public boolean isSelected(ChatRoomModel model) {
        if (activity.selectedModelMap.containsKey(roomId)) {
            if (activity.selectedModelMap.get(roomId).contains(model)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @OnClick({R.id.fragment_chat_room_container_toolbar_option_arrow_back, R.id.fragment_chat_room_button_toolbar_option_arrow_back})
    public void onToolbarOptionArrowBackClick() {
        replyModel = null;
        llReply.setVisibility(View.GONE);

        if (activity.selectedModelMap.containsKey(roomId)) {
            for (ChatRoomModel model : activity.selectedModelMap.get(roomId)) {
                int position = activity.roomModelMap.get(roomId).indexOf(model);
                RecyclerView.ViewHolder holder = recyclerview.findViewHolderForAdapterPosition(position);
                if (holder instanceof ChatAdapter.ViewHolderMessageSender) {
                    ((ChatAdapter.ViewHolderMessageSender) holder).clBox.setPressed(false);
                    ((ChatAdapter.ViewHolderMessageSender) holder).clLayout.setSelected(false);
                    ((ChatAdapter.ViewHolderMessageSender) holder).clContainer.setVisibility(View.GONE);
                }
                if (holder instanceof ChatAdapter.ViewHolderMessageReceiver) {
                    ((ChatAdapter.ViewHolderMessageReceiver) holder).clBox.setPressed(false);
                    ((ChatAdapter.ViewHolderMessageReceiver) holder).clLayout.setSelected(false);
                    ((ChatAdapter.ViewHolderMessageReceiver) holder).clContainer.setVisibility(View.GONE);
                }
                if (holder instanceof ChatAdapter.ViewHolderImageSender) {
                    ((ChatAdapter.ViewHolderImageSender) holder).clBox.setPressed(false);
                    ((ChatAdapter.ViewHolderImageSender) holder).clLayout.setSelected(false);
                    ((ChatAdapter.ViewHolderImageSender) holder).clContainer.setVisibility(View.GONE);
                }
                if (holder instanceof ChatAdapter.ViewHolderImageReceiver) {
                    ((ChatAdapter.ViewHolderImageReceiver) holder).clBox.setPressed(false);
                    ((ChatAdapter.ViewHolderImageReceiver) holder).clLayout.setSelected(false);
                    ((ChatAdapter.ViewHolderImageReceiver) holder).clContainer.setVisibility(View.GONE);
                }
                if (holder instanceof ChatAdapter.ViewHolderFileSender) {
                    ((ChatAdapter.ViewHolderFileSender) holder).clBox.setPressed(false);
                    ((ChatAdapter.ViewHolderFileSender) holder).clLayout.setSelected(false);
                    ((ChatAdapter.ViewHolderFileSender) holder).clContainer.setVisibility(View.GONE);
                }
                if (holder instanceof ChatAdapter.ViewHolderFileReceiver) {
                    ((ChatAdapter.ViewHolderFileReceiver) holder).clBox.setPressed(false);
                    ((ChatAdapter.ViewHolderFileReceiver) holder).clLayout.setSelected(false);
                    ((ChatAdapter.ViewHolderFileReceiver) holder).clContainer.setVisibility(View.GONE);
                }
            }

            activity.selectedModelMap.remove(roomId);
            updateSelected();
        }
    }

    @OnClick({R.id.fragment_chat_room_container_toolbar_option_info, R.id.fragment_chat_room_button_toolbar_option_info})
    public void onToolbarOptionInfoClick(View view) {
        if (llToolbarOptionInfo.getVisibility() != View.GONE) {
            if (roomType.equals("public")) {
                activity.openChatInfoFragment(roomId, roomTitle, 1);
            } else {
                activity.openPrivateChatInfoFragment(roomId, roomTitle, 1);

            }
        }
    }

    @OnClick({R.id.fragment_chat_room_container_toolbar_option_copy, R.id.fragment_chat_room_button_toolbar_option_copy})
    public void onToolbarOptionCopyClick(View view) {
        StringBuilder copyMessage = new StringBuilder();
        ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
        int step = 0;
        if (activity.selectedModelMap.containsKey(roomId)) {
            for (ChatRoomModel model : activity.selectedModelMap.get(roomId)) {
                switch (model.getType()) {
                    case "message":
                        copyMessage.append(model.getMessage());
                        break;
                    case "image":
                    case "file":
                        copyMessage.append(model.getFile_download_url());
                        break;
                }

                int position = activity.roomModelMap.get(roomId).indexOf(model);
                RecyclerView.ViewHolder holder = recyclerview.findViewHolderForAdapterPosition(position);
                if (holder instanceof ChatAdapter.ViewHolderMessageSender) {
                    ((ChatAdapter.ViewHolderMessageSender) holder).clBox.setPressed(false);
                    ((ChatAdapter.ViewHolderMessageSender) holder).clLayout.setSelected(false);
                    ((ChatAdapter.ViewHolderMessageSender) holder).clContainer.setVisibility(View.GONE);
                }
                if (holder instanceof ChatAdapter.ViewHolderMessageReceiver) {
                    ((ChatAdapter.ViewHolderMessageReceiver) holder).clBox.setPressed(false);
                    ((ChatAdapter.ViewHolderMessageReceiver) holder).clLayout.setSelected(false);
                    ((ChatAdapter.ViewHolderMessageReceiver) holder).clContainer.setVisibility(View.GONE);
                }
                if (holder instanceof ChatAdapter.ViewHolderImageSender) {
                    ((ChatAdapter.ViewHolderImageSender) holder).clBox.setPressed(false);
                    ((ChatAdapter.ViewHolderImageSender) holder).clLayout.setSelected(false);
                    ((ChatAdapter.ViewHolderImageSender) holder).clContainer.setVisibility(View.GONE);
                }
                if (holder instanceof ChatAdapter.ViewHolderImageReceiver) {
                    ((ChatAdapter.ViewHolderImageReceiver) holder).clBox.setPressed(false);
                    ((ChatAdapter.ViewHolderImageReceiver) holder).clLayout.setSelected(false);
                    ((ChatAdapter.ViewHolderImageReceiver) holder).clContainer.setVisibility(View.GONE);
                }
                if (holder instanceof ChatAdapter.ViewHolderFileSender) {
                    ((ChatAdapter.ViewHolderFileSender) holder).clBox.setPressed(false);
                    ((ChatAdapter.ViewHolderFileSender) holder).clLayout.setSelected(false);
                    ((ChatAdapter.ViewHolderFileSender) holder).clContainer.setVisibility(View.GONE);
                }
                if (holder instanceof ChatAdapter.ViewHolderFileReceiver) {
                    ((ChatAdapter.ViewHolderFileReceiver) holder).clBox.setPressed(false);
                    ((ChatAdapter.ViewHolderFileReceiver) holder).clLayout.setSelected(false);
                    ((ChatAdapter.ViewHolderFileReceiver) holder).clContainer.setVisibility(View.GONE);
                }

                step++;
                if (activity.selectedModelMap.get(roomId).size() > 1) {
                    if (step != activity.selectedModelMap.get(roomId).size()) {
                        copyMessage.append(System.getProperty("line.separator"));
                    }
                }
            }

            ClipData clip = ClipData.newPlainText("CHAT", copyMessage.toString());
            clipboard.setPrimaryClip(clip);

            activity.selectedModelMap.remove(roomId);
            hideToolbarOption();
            showToolbar();
        }
    }

    @OnTouch(R.id.fragment_chat_room_input_message)
    public boolean onMessageInputClick() {
        onToolbarOptionReplyClick();
        return false;
    }

    @OnClick({R.id.fragment_chat_room_container_toolbar_option_reply, R.id.fragment_chat_room_button_toolbar_option_reply})
    public void onToolbarOptionReplyClick() {
        if (activity.selectedModelMap.containsKey(roomId)) {
            if (activity.selectedModelMap.get(roomId).size() == 1) {
                ChatRoomModel model = activity.selectedModelMap.get(roomId).getFirst();
                if (!model.getSender_id().equals(activity.spm.getUserId())) {
                    llReply.setVisibility(View.VISIBLE);
                    llAttachOption.setVisibility(View.GONE);
                    replyModel = model;
                    switch (model.getType()) {
                        case "message":
                            clMessage.setVisibility(View.VISIBLE);
                            clFile.setVisibility(View.GONE);
                            clImage.setVisibility(View.GONE);

                            tvReplyMessage.setText(model.getMessage());
                            tvReplyMessageSender.setText(model.getSender_name());
                            tvReplyMessageSender.setTextColor(activity.getColor(model.getSender_name()));
                            break;
                        case "file":
                            clFile.setVisibility(View.VISIBLE);
                            clImage.setVisibility(View.GONE);
                            clMessage.setVisibility(View.GONE);

                            tvReplyFileName.setText(model.getFile_name());
                            tvReplyFileExtension.setText(model.getFile_extension().toUpperCase());
                            tvReplyFileSender.setText(model.getSender_name());
                            tvReplyFileSender.setTextColor(activity.getColor(model.getSender_name()));
                            break;
                        case "image":
                            clImage.setVisibility(View.VISIBLE);
                            clFile.setVisibility(View.GONE);
                            clMessage.setVisibility(View.GONE);

                            if (model.getMessage().isEmpty()) {
                                tvReplyImageLabel.setText("Gambar");
                            } else {
                                tvReplyImageLabel.setText(model.getMessage());
                            }
                            tvReplyImageSender.setText(model.getSender_name());
                            tvReplyImageSender.setTextColor(activity.getColor(model.getSender_name()));

                            Glide.with(this)
                                    .load(model.getFile_download_url())
                                    .apply(RequestOptions.centerCropTransform())
                                    .apply(RequestOptions.overrideOf(50, 50))
                                    .into(ivReplyImage);
                            break;
                    }

                    int position = activity.roomModelMap.get(roomId).indexOf(model);
                    RecyclerView.ViewHolder holder = recyclerview.findViewHolderForAdapterPosition(position);
                    if (holder instanceof ChatAdapter.ViewHolderMessageSender) {
                        ((ChatAdapter.ViewHolderMessageSender) holder).clBox.setPressed(false);
                        ((ChatAdapter.ViewHolderMessageSender) holder).clLayout.setSelected(false);
                        ((ChatAdapter.ViewHolderMessageSender) holder).clContainer.setVisibility(View.GONE);
                    }
                    if (holder instanceof ChatAdapter.ViewHolderMessageReceiver) {
                        ((ChatAdapter.ViewHolderMessageReceiver) holder).clBox.setPressed(false);
                        ((ChatAdapter.ViewHolderMessageReceiver) holder).clLayout.setSelected(false);
                        ((ChatAdapter.ViewHolderMessageReceiver) holder).clContainer.setVisibility(View.GONE);
                    }
                    if (holder instanceof ChatAdapter.ViewHolderImageSender) {
                        ((ChatAdapter.ViewHolderImageSender) holder).clBox.setPressed(false);
                        ((ChatAdapter.ViewHolderImageSender) holder).clLayout.setSelected(false);
                        ((ChatAdapter.ViewHolderImageSender) holder).clContainer.setVisibility(View.GONE);
                    }
                    if (holder instanceof ChatAdapter.ViewHolderImageReceiver) {
                        ((ChatAdapter.ViewHolderImageReceiver) holder).clBox.setPressed(false);
                        ((ChatAdapter.ViewHolderImageReceiver) holder).clLayout.setSelected(false);
                        ((ChatAdapter.ViewHolderImageReceiver) holder).clContainer.setVisibility(View.GONE);
                    }
                    if (holder instanceof ChatAdapter.ViewHolderFileSender) {
                        ((ChatAdapter.ViewHolderFileSender) holder).clBox.setPressed(false);
                        ((ChatAdapter.ViewHolderFileSender) holder).clLayout.setSelected(false);
                        ((ChatAdapter.ViewHolderFileSender) holder).clContainer.setVisibility(View.GONE);
                    }
                    if (holder instanceof ChatAdapter.ViewHolderFileReceiver) {
                        ((ChatAdapter.ViewHolderFileReceiver) holder).clBox.setPressed(false);
                        ((ChatAdapter.ViewHolderFileReceiver) holder).clLayout.setSelected(false);
                        ((ChatAdapter.ViewHolderFileReceiver) holder).clContainer.setVisibility(View.GONE);
                    }
                } else {
                    for (ChatRoomModel model2 : activity.selectedModelMap.get(roomId)) {
                        int position = activity.roomModelMap.get(roomId).indexOf(model2);
                        RecyclerView.ViewHolder holder = recyclerview.findViewHolderForAdapterPosition(position);
                        if (holder instanceof ChatAdapter.ViewHolderMessageSender) {
                            ((ChatAdapter.ViewHolderMessageSender) holder).clBox.setPressed(false);
                            ((ChatAdapter.ViewHolderMessageSender) holder).clLayout.setSelected(false);
                            ((ChatAdapter.ViewHolderMessageSender) holder).clContainer.setVisibility(View.GONE);
                        }
                        if (holder instanceof ChatAdapter.ViewHolderMessageReceiver) {
                            ((ChatAdapter.ViewHolderMessageReceiver) holder).clBox.setPressed(false);
                            ((ChatAdapter.ViewHolderMessageReceiver) holder).clLayout.setSelected(false);
                            ((ChatAdapter.ViewHolderMessageReceiver) holder).clContainer.setVisibility(View.GONE);
                        }
                        if (holder instanceof ChatAdapter.ViewHolderImageSender) {
                            ((ChatAdapter.ViewHolderImageSender) holder).clBox.setPressed(false);
                            ((ChatAdapter.ViewHolderImageSender) holder).clLayout.setSelected(false);
                            ((ChatAdapter.ViewHolderImageSender) holder).clContainer.setVisibility(View.GONE);
                        }
                        if (holder instanceof ChatAdapter.ViewHolderImageReceiver) {
                            ((ChatAdapter.ViewHolderImageReceiver) holder).clBox.setPressed(false);
                            ((ChatAdapter.ViewHolderImageReceiver) holder).clLayout.setSelected(false);
                            ((ChatAdapter.ViewHolderImageReceiver) holder).clContainer.setVisibility(View.GONE);
                        }
                        if (holder instanceof ChatAdapter.ViewHolderFileSender) {
                            ((ChatAdapter.ViewHolderFileSender) holder).clBox.setPressed(false);
                            ((ChatAdapter.ViewHolderFileSender) holder).clLayout.setSelected(false);
                            ((ChatAdapter.ViewHolderFileSender) holder).clContainer.setVisibility(View.GONE);
                        }
                        if (holder instanceof ChatAdapter.ViewHolderFileReceiver) {
                            ((ChatAdapter.ViewHolderFileReceiver) holder).clBox.setPressed(false);
                            ((ChatAdapter.ViewHolderFileReceiver) holder).clLayout.setSelected(false);
                            ((ChatAdapter.ViewHolderFileReceiver) holder).clContainer.setVisibility(View.GONE);
                        }
                    }
                }

                activity.selectedModelMap.remove(roomId);

                hideToolbarOption();
                showToolbar();
            }
        }
    }

    @OnClick({R.id.fragment_chat_room_container_toolbar_option_delete, R.id.fragment_chat_room_button_toolbar_option_delete})
    public void onToolbarOptionDeleteClick(View view) {
        if (llToolbarOptionDelete.getVisibility() != View.GONE) {
            if (activity.selectedModelMap.containsKey(roomId)) {
                //Play Delete Sound
                if (!activity.isInBackground && activity.spMessageDeleteReady) {
                    activity.spMessageDelete.play(activity.spMessageDeleteId, activity.volume, activity.volume, 1, 0, 1f);
                }

                for (final ChatRoomModel model : activity.selectedModelMap.get(roomId)) {
                    DatabaseReference refDelete = activity.db.getReference().child("chat").child(roomId).child(model.getId());
                    refDelete.removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            //
                        }
                    });
                    if (model.getType().equals("image")) {
                        StorageReference storageRef = activity.storage.getReference().child(model.getRoom_id()).child("images").child(model.getFile_path());
                        storageRef.delete();
                    }

                    if (model.getType().equals("file")) {
                        StorageReference storageRef = activity.storage.getReference().child(model.getRoom_id()).child("files").child(model.getFile_path());
                        storageRef.delete();
                    }

                    if (model.getFile_download_url() != null) {
                        if (model.getType().equals("image")) {
                            StorageReference refImageDelete = activity.storage.getReference().child(roomId).child("images").child(model.getFile_path());
                            refImageDelete.delete();
                        }
                        if (model.getType().equals("file")) {
                            StorageReference refFileDelete = activity.storage.getReference().child(roomId).child("files").child(model.getFile_path());
                            refFileDelete.delete();
                        }
                    }
                    int position = activity.roomModelMap.get(roomId).indexOf(model);
                    RecyclerView.ViewHolder holder = recyclerview.findViewHolderForAdapterPosition(position);
                    if (holder instanceof ChatAdapter.ViewHolderMessageSender) {
                        ((ChatAdapter.ViewHolderMessageSender) holder).clBox.setPressed(false);
                        ((ChatAdapter.ViewHolderMessageSender) holder).clLayout.setSelected(false);
                        ((ChatAdapter.ViewHolderMessageSender) holder).clContainer.setVisibility(View.GONE);
                    }
                    if (holder instanceof ChatAdapter.ViewHolderMessageReceiver) {
                        ((ChatAdapter.ViewHolderMessageReceiver) holder).clBox.setPressed(false);
                        ((ChatAdapter.ViewHolderMessageReceiver) holder).clLayout.setSelected(false);
                        ((ChatAdapter.ViewHolderMessageReceiver) holder).clContainer.setVisibility(View.GONE);
                    }
                    if (holder instanceof ChatAdapter.ViewHolderImageSender) {
                        ((ChatAdapter.ViewHolderImageSender) holder).clBox.setPressed(false);
                        ((ChatAdapter.ViewHolderImageSender) holder).clLayout.setSelected(false);
                        ((ChatAdapter.ViewHolderImageSender) holder).clContainer.setVisibility(View.GONE);
                    }
                    if (holder instanceof ChatAdapter.ViewHolderImageReceiver) {
                        ((ChatAdapter.ViewHolderImageReceiver) holder).clBox.setPressed(false);
                        ((ChatAdapter.ViewHolderImageReceiver) holder).clLayout.setSelected(false);
                        ((ChatAdapter.ViewHolderImageReceiver) holder).clContainer.setVisibility(View.GONE);
                    }
                    if (holder instanceof ChatAdapter.ViewHolderFileSender) {
                        ((ChatAdapter.ViewHolderFileSender) holder).clBox.setPressed(false);
                        ((ChatAdapter.ViewHolderFileSender) holder).clLayout.setSelected(false);
                        ((ChatAdapter.ViewHolderFileSender) holder).clContainer.setVisibility(View.GONE);
                    }
                    if (holder instanceof ChatAdapter.ViewHolderFileReceiver) {
                        ((ChatAdapter.ViewHolderFileReceiver) holder).clBox.setPressed(false);
                        ((ChatAdapter.ViewHolderFileReceiver) holder).clLayout.setSelected(false);
                        ((ChatAdapter.ViewHolderFileReceiver) holder).clContainer.setVisibility(View.GONE);
                    }
                }

                activity.selectedModelMap.remove(roomId);

                hideToolbarOption();
                showToolbar();
            }
        }
    }

    @OnClick({R.id.item_room_chat_message_image_cancel, R.id.item_room_chat_image_image_cancel, R.id.item_room_chat_file_image_cancel})
    public void onReplyCancelClick() {
        replyModel = null;
        hideToolbarOption();
        showToolbar();
        llReply.setVisibility(View.GONE);
        if (activity.selectedModelMap.get(roomId) != null) {
            for (ChatRoomModel model : activity.selectedModelMap.get(roomId)) {
                int position = activity.roomModelMap.get(roomId).indexOf(model);
                RecyclerView.ViewHolder holder = recyclerview.findViewHolderForAdapterPosition(position);
                if (holder instanceof ChatAdapter.ViewHolderMessageSender) {
                    ((ChatAdapter.ViewHolderMessageSender) holder).clBox.setPressed(false);
                    ((ChatAdapter.ViewHolderMessageSender) holder).clLayout.setSelected(false);
                    ((ChatAdapter.ViewHolderMessageSender) holder).clContainer.setVisibility(View.GONE);
                }
                if (holder instanceof ChatAdapter.ViewHolderMessageReceiver) {
                    ((ChatAdapter.ViewHolderMessageReceiver) holder).clBox.setPressed(false);
                    ((ChatAdapter.ViewHolderMessageReceiver) holder).clLayout.setSelected(false);
                    ((ChatAdapter.ViewHolderMessageReceiver) holder).clContainer.setVisibility(View.GONE);
                }
                if (holder instanceof ChatAdapter.ViewHolderImageSender) {
                    ((ChatAdapter.ViewHolderImageSender) holder).clBox.setPressed(false);
                    ((ChatAdapter.ViewHolderImageSender) holder).clLayout.setSelected(false);
                    ((ChatAdapter.ViewHolderImageSender) holder).clContainer.setVisibility(View.GONE);
                }
                if (holder instanceof ChatAdapter.ViewHolderImageReceiver) {
                    ((ChatAdapter.ViewHolderImageReceiver) holder).clBox.setPressed(false);
                    ((ChatAdapter.ViewHolderImageReceiver) holder).clLayout.setSelected(false);
                    ((ChatAdapter.ViewHolderImageReceiver) holder).clContainer.setVisibility(View.GONE);
                }
                if (holder instanceof ChatAdapter.ViewHolderFileSender) {
                    ((ChatAdapter.ViewHolderFileSender) holder).clBox.setPressed(false);
                    ((ChatAdapter.ViewHolderFileSender) holder).clLayout.setSelected(false);
                    ((ChatAdapter.ViewHolderFileSender) holder).clContainer.setVisibility(View.GONE);
                }
                if (holder instanceof ChatAdapter.ViewHolderFileReceiver) {
                    ((ChatAdapter.ViewHolderFileReceiver) holder).clBox.setPressed(false);
                    ((ChatAdapter.ViewHolderFileReceiver) holder).clLayout.setSelected(false);
                    ((ChatAdapter.ViewHolderFileReceiver) holder).clContainer.setVisibility(View.GONE);
                }
            }
        }
    }

    @OnClick({R.id.fragment_chat_room_text_toolbar_title, R.id.fragment_chat_room_text_toolbar_subtitle})
    public void onToolbarTitleClick() {
        if (roomType.equals("public")) {
            activity.openRoomFragment(roomId, roomTitle, roomType,1);
        } else {
            activity.openPrivateRoomFragment(roomId, roomTitle, roomType,1);
        }
    }

    public void showToolbar() {
        rlToolbar.setVisibility(View.VISIBLE);
    }

    public void hideToolbar() {
        rlToolbar.setVisibility(View.INVISIBLE);
    }

    public void showToolbarOption() {
        clToolbarOption.setVisibility(View.VISIBLE);
    }

    public void hideToolbarOption() {
        clToolbarOption.setVisibility(View.INVISIBLE);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        activity.roomMessageMap.put(roomId, etMessage.getText().toString());
    }
}
