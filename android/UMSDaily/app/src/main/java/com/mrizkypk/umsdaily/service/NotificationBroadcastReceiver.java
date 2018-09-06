package com.mrizkypk.umsdaily.service;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.RemoteInput;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mrizkypk.umsdaily.activity.HomeActivity;
import com.mrizkypk.umsdaily.helper.DateHelper;
import com.mrizkypk.umsdaily.helper.FirebaseRestHelper;
import com.mrizkypk.umsdaily.helper.NotificationHelper;
import com.mrizkypk.umsdaily.manager.DatabaseManager;
import com.mrizkypk.umsdaily.manager.IntentManager;
import com.mrizkypk.umsdaily.manager.SharedPreferenceManager;
import com.mrizkypk.umsdaily.model.ChatRoomModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NotificationBroadcastReceiver extends BroadcastReceiver {

    private static final String KEY_REPLY = "UMSDailyNotification";
    private static final String REPLY_ACTION = "UMSDailyNotification.Action.Reply";
    private static final String MARK_ACTION = "UMSDailyNotification.Action.Mark";
    private static final String DELETE_ACTION = "UMSDailyNotification.Action.Delete";

    private FirebaseDatabase db;
    private SharedPreferenceManager spm;
    private DatabaseReference ref;
    private FirebaseRestHelper frh;

    private String message;
    private String type;
    private String roomId;
    private String roomType;
    private String roomTitle;
    private String avatarUrl;
    private String senderId;
    private String senderName;
    private String receiverId;
    private String receiverName;

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (REPLY_ACTION.equals(intent.getAction())) {
            db = DatabaseManager.getInstance();
            spm = new SharedPreferenceManager(context);
            frh = new FirebaseRestHelper();

            message = getReplyMessage(intent).toString();
            type = "message";
            roomId = intent.getStringExtra("EXTRA_ROOM_ID");
            roomType = intent.getStringExtra("EXTRA_ROOM_TYPE");
            roomTitle = intent.getStringExtra("EXTRA_ROOM_TITLE");
            avatarUrl = intent.getStringExtra("EXTRA_AVATAR_URL");
            receiverId = intent.getStringExtra("EXTRA_RECEIVER_ID");
            receiverName = intent.getStringExtra("EXTRA_RECEIVER_NAME");

            senderId = spm.getUserId();
            senderName = spm.getUserName();

            Long timestamp = DateHelper.getTimestamp();

            HashMap<String, Long> read = new HashMap<>();
            read.put(spm.getUserId(), timestamp);

            HashMap<String, Long> received = new HashMap<>();
            received.put(spm.getUserId(), timestamp);

            ref = db.getReference().child("chat").child(roomId).push();
            final String id = ref.getKey();

            ChatRoomModel model = new ChatRoomModel();
            model.setId(id);
            model.setRoom_id(roomId);
            model.setRoom_type(roomType);
            if (roomType.equals("private")) {
                model.setReceiver_id(receiverId);
                model.setReceiver_name(receiverName);
            } else {
                model.setRoom_title(roomTitle);
            }
            model.setSender_id(senderId);
            model.setSender_name(senderName);
            model.setMessage(message);
            model.setTimestamp(timestamp);
            model.setRead(read);
            model.setReceived(received);
            model.setType(type);

            ref.setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if (roomType.equals("public")) {
                        NotificationHelper.send(id, "CHAT_MESSAGE_PUBLIC", "", roomId, spm.getUserId(), spm.getUserName(), "", "", avatarUrl, "", roomTitle, message, "");
                    } else if (roomType.equals("private")) {
                        NotificationHelper.send(id, "CHAT_MESSAGE_PRIVATE", "", roomId, spm.getUserId(), spm.getUserName(), receiverId, receiverName, avatarUrl, "", spm.getUserName(), message, "");
                    }

                    //Clear notification after reply
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(roomId.hashCode());

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (notificationManager.getActiveNotifications().length == 1) {
                            notificationManager.cancel(0);
                        }
                    }

                    ArrayList<String> datas = spm.getList(roomId);
                    if (datas != null) {
                        for (String data : datas) {
                            String id = data.split("UMSDAILY_DIVIDER")[0];
                            frh.updateChatRead(roomId, id, spm.getUserId());
                            frh.updateChatReceived(roomId, id, spm.getUserId());
                        }
                    }

                    //Clean badge
                    if (HomeActivity.unreadCountMap != null) {
                        HomeActivity.unreadCountMap.remove(roomId);
                    }

                    //Clean notification sharedpreferences

                    spm.removeList(roomId);
                }
            });
        }
        if (MARK_ACTION.equals(intent.getAction())) {
            db = DatabaseManager.getInstance();
            spm = new SharedPreferenceManager(context);
            frh = new FirebaseRestHelper();

            roomId = intent.getStringExtra("EXTRA_ROOM_ID");

            //Clear notification
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(roomId.hashCode());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (notificationManager.getActiveNotifications().length == 1) {
                    notificationManager.cancel(0);
                }
            }

            ArrayList<String> datas = spm.getList(roomId);
            if (datas != null) {
                for (String data : datas) {
                    String id = data.split("UMSDAILY_DIVIDER")[0];
                    frh.updateChatRead(roomId, id, spm.getUserId());
                    frh.updateChatReceived(roomId, id, spm.getUserId());
                }
            }

            //Clean badge
            if (HomeActivity.unreadCountMap != null) {
                HomeActivity.unreadCountMap.remove(roomId);
            }

            //Clean notification sharedpreferences

            spm.removeList(roomId);
        }
        if (DELETE_ACTION.equals(intent.getAction())) {
            //Clear notification
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (notificationManager.getActiveNotifications().length == 1) {
                    notificationManager.cancel(0);
                }
            }
        }
    }

    private CharSequence getReplyMessage(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(KEY_REPLY);
        }
        return null;
    }
}
