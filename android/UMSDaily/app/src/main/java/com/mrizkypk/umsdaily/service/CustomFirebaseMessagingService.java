package com.mrizkypk.umsdaily.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mrizkypk.umsdaily.R;
import com.mrizkypk.umsdaily.activity.HomeActivity;
import com.mrizkypk.umsdaily.helper.DateHelper;
import com.mrizkypk.umsdaily.manager.DatabaseManager;
import com.mrizkypk.umsdaily.manager.SharedPreferenceManager;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;

public class CustomFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MRPKFirebaseMessage";
    private static final String KEY_REPLY = "UMSDailyNotification";
    private static final String REPLY_ACTION = "UMSDailyNotification.Action.Reply";
    private static final String MARK_ACTION = "UMSDailyNotification.Action.Mark";
    private static final String DELETE_ACTION = "UMSDailyNotification.Action.Delete";
    private static final String CHANNEL_ID = "UMSDaily";
    private static final String CHANNEL_SILENT_ID = "UMSDailySilent";
    private static final String GROUP_ID = "com.mrizkypk.umsdaily.CHAT_NOTIFICATION";

    public SharedPreferenceManager spm;

    @Override
    public void onCreate() {
        super.onCreate();
        spm = new SharedPreferenceManager(getApplicationContext());
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        FirebaseDatabase db = DatabaseManager.getInstance();
        String refreshedToken = s;

        final String id = spm.getUserId();

        if (!id.equals("EMPTY")) {
            db.getReference().child("user").child(id + "/device_token").setValue(refreshedToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("MRPK", "Update token for " + id);
                }
            });
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            if (HomeActivity.currentFragment != null) {
                if (HomeActivity.currentFragment.equals("ROOM_FRAGMENT") || HomeActivity.currentFragment.equals("PRIVATE_ROOM_FRAGMENT") || HomeActivity.currentFragment.equals("PRIVATE_ROOM_FROM_ROOM_FRAGMENT") || HomeActivity.currentFragment.equals("PRIVATE_ROOM_FROM_COMPOSE_FRAGMENT")) {
                    if (HomeActivity.isInBackground) {
                        showNotification(remoteMessage);
                    }
                } else {
                    showNotification(remoteMessage);
                }
            } else {
                showNotification(remoteMessage);
            }

        }
    }

    public void showNotification(RemoteMessage remoteMessage) {
        createNotificationChannel();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            //Set Notification Group Summary

            NotificationCompat.Builder mBuilder2 = new NotificationCompat.Builder(this, CHANNEL_SILENT_ID)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setGroup(GROUP_ID)
                    .setGroupSummary(true)
                    .setColor(ContextCompat.getColor(this, R.color.colorPrimary));
            notificationManager.notify(0, mBuilder2.build());
        }
        ArrayList<String> messages = spm.getList(remoteMessage.getData().get("room_id"));

        if (remoteMessage.getData().get("type").equals("CHAT_IMAGE_PUBLIC")) {
            if (messages == null) {
                Intent resultIntent = new Intent(this, HomeActivity.class);
                resultIntent.setAction("FROM_CHAT_IMAGE_PUBLIC_NOTIFICATION");
                resultIntent.putExtra("EXTRA_ROOM_ID", remoteMessage.getData().get("room_id"));
                resultIntent.putExtra("EXTRA_ROOM_TITLE", remoteMessage.getData().get("title"));
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                Bitmap image = getBitmapfromUrl(remoteMessage.getData().get("image_url"));
                Bitmap avatar = getBitmapfromUrl(remoteMessage.getData().get("avatar_url"));
                String notifMessage = remoteMessage.getData().get("id") + "UMSDAILY_DIVIDER" + "Mengirim Gambar";

                ArrayList<String> newMessages = new ArrayList<>();
                newMessages.add(notifMessage);

                spm.setList(remoteMessage.getData().get("room_id"), newMessages);

                if (remoteMessage.getData().get("content").isEmpty()) {
                    remoteMessage.getData().put("content", "Mengirim Gambar");
                }

                //Delete
                Intent deleteIntent = new Intent(this, NotificationBroadcastReceiver.class);
                deleteIntent.setAction(DELETE_ACTION);
                deleteIntent.putExtra("EXTRA_ROOM_ID", remoteMessage.getData().get("room_id"));
                PendingIntent deletePendingIntent =
                        PendingIntent.getBroadcast(this, 2, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                //Mark as Read
                Intent markIntent = new Intent(this, NotificationBroadcastReceiver.class);
                markIntent.setAction(MARK_ACTION);
                markIntent.putExtra("EXTRA_ROOM_ID", remoteMessage.getData().get("room_id"));
                PendingIntent markAsReadPendingIntent =
                        PendingIntent.getBroadcast(this, 3, markIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                //Direct Reply
                String replyLabel = "Balas";
                RemoteInput remoteInput = new RemoteInput.Builder(KEY_REPLY)
                        .setLabel(replyLabel)
                        .build();

                NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(
                        R.drawable.ic_reply, replyLabel, getReplyPendingIntent(
                        remoteMessage.getData().get("room_id"),
                        "public",
                        remoteMessage.getData().get("title"),
                        remoteMessage.getData().get("avatar_url"),
                        remoteMessage.getData().get("sender_id"),
                        remoteMessage.getData().get("sender_name"),
                        remoteMessage.getData().get("content")
                )).addRemoteInput(remoteInput)
                        .setAllowGeneratedReplies(true)
                        .build();

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setLargeIcon(avatar)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image))
                        .setAutoCancel(true)
                        .setGroup(GROUP_ID)
                        .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                        .setContentTitle(remoteMessage.getData().get("title"))
                        .setContentText(remoteMessage.getData().get("sender_name") + ": " + remoteMessage.getData().get("content"))
                        .setContentIntent(pendingIntent)
                        .setDeleteIntent(deletePendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    mBuilder.addAction(R.drawable.ic_refresh, "Tandai Sudah Dibaca", markAsReadPendingIntent)
                            .addAction(replyAction);
                }
                notificationManager.notify(remoteMessage.getData().get("room_id").hashCode(), mBuilder.build());
            } else {
                Intent resultIntent = new Intent(this, HomeActivity.class);
                resultIntent.setAction("FROM_CHAT_IMAGE_PUBLIC_NOTIFICATION");
                resultIntent.putExtra("EXTRA_ROOM_ID", remoteMessage.getData().get("room_id"));
                resultIntent.putExtra("EXTRA_ROOM_TITLE", remoteMessage.getData().get("title"));
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                Bitmap image = getBitmapfromUrl(remoteMessage.getData().get("image_url"));
                Bitmap avatar = getBitmapfromUrl(remoteMessage.getData().get("avatar_url"));
                String notifMessage = remoteMessage.getData().get("id") + "UMSDAILY_DIVIDER" + "Mengirim Gambar";

                messages.add(notifMessage);
                spm.setList(remoteMessage.getData().get("room_id"), messages);

                NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle(spm.getUserName());
                messagingStyle.setConversationTitle(remoteMessage.getData().get("title") + " (" + messages.size() + ")");

                int fromIndex = 0;
                if (messages.size() > 7) {
                    fromIndex = messages.size() - 7;
                }
                for (String data : messages.subList(fromIndex, messages.size())) {
                    String message = data.split("UMSDAILY_DIVIDER")[1];
                    messagingStyle.addMessage(message, 0, remoteMessage.getData().get("sender_name"));
                }

                //Delete
                Intent deleteIntent = new Intent(this, NotificationBroadcastReceiver.class);
                deleteIntent.setAction(DELETE_ACTION);
                deleteIntent.putExtra("EXTRA_ROOM_ID", remoteMessage.getData().get("room_id"));
                PendingIntent deletePendingIntent =
                        PendingIntent.getBroadcast(this, 2, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                //Mark as Read
                Intent markIntent = new Intent(this, NotificationBroadcastReceiver.class);
                markIntent.setAction(MARK_ACTION);
                markIntent.putExtra("EXTRA_ROOM_ID", remoteMessage.getData().get("room_id"));
                PendingIntent markAsReadPendingIntent =
                        PendingIntent.getBroadcast(this, 3, markIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                //Direct Reply
                String replyLabel = "Balas";
                RemoteInput remoteInput = new RemoteInput.Builder(KEY_REPLY)
                        .setLabel(replyLabel)
                        .build();

                NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(
                        R.drawable.ic_reply, replyLabel, getReplyPendingIntent(
                        remoteMessage.getData().get("room_id"),
                        "public",
                        remoteMessage.getData().get("title"),
                        remoteMessage.getData().get("avatar_url"),
                        remoteMessage.getData().get("sender_id"),
                        remoteMessage.getData().get("sender_name"),
                        remoteMessage.getData().get("content")
                        ))
                        .addRemoteInput(remoteInput)
                        .setAllowGeneratedReplies(true)
                        .build();

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setLargeIcon(avatar)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setStyle(messagingStyle)
                        .setAutoCancel(true)
                        .setGroup(GROUP_ID)
                        .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                        .setContentTitle(remoteMessage.getData().get("title") + " (" + messages.size() + ")")
                        .setContentText(remoteMessage.getData().get("sender_name") + ": Mengirim Gambar")
                        .setContentIntent(pendingIntent)
                        .setDeleteIntent(deletePendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    mBuilder.addAction(R.drawable.ic_refresh, "Tandai Sudah Dibaca", markAsReadPendingIntent)
                            .addAction(replyAction);
                }

                notificationManager.notify(remoteMessage.getData().get("room_id").hashCode(), mBuilder.build());
            }
        }

        if (remoteMessage.getData().get("type").equals("CHAT_FILE_PUBLIC")) {
            if (messages == null) {
                Intent resultIntent = new Intent(this, HomeActivity.class);
                resultIntent.setAction("FROM_CHAT_FILE_PUBLIC_NOTIFICATION");
                resultIntent.putExtra("EXTRA_ROOM_ID", remoteMessage.getData().get("room_id"));
                resultIntent.putExtra("EXTRA_ROOM_TITLE", remoteMessage.getData().get("title"));
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                String notifMessage = remoteMessage.getData().get("id") + "UMSDAILY_DIVIDER" + "Mengirim Berkas";

                ArrayList<String> newMessages = new ArrayList<>();
                newMessages.add(notifMessage);

                spm.setList(remoteMessage.getData().get("room_id"), newMessages);

                Bitmap avatar = getBitmapfromUrl(remoteMessage.getData().get("avatar_url"));

                NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle(spm.getUserName());
                messagingStyle.setConversationTitle(remoteMessage.getData().get("title"));

                //Delete
                Intent deleteIntent = new Intent(this, NotificationBroadcastReceiver.class);
                deleteIntent.setAction(DELETE_ACTION);
                deleteIntent.putExtra("EXTRA_ROOM_ID", remoteMessage.getData().get("room_id"));
                PendingIntent deletePendingIntent =
                        PendingIntent.getBroadcast(this, 2, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                //Mark as Read
                Intent markIntent = new Intent(this, NotificationBroadcastReceiver.class);
                markIntent.setAction(MARK_ACTION);
                markIntent.putExtra("EXTRA_ROOM_ID", remoteMessage.getData().get("room_id"));
                PendingIntent markAsReadPendingIntent =
                        PendingIntent.getBroadcast(this, 3, markIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                //Direct Reply
                String replyLabel = "Balas";
                RemoteInput remoteInput = new RemoteInput.Builder(KEY_REPLY)
                        .setLabel(replyLabel)
                        .build();

                NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(
                        R.drawable.ic_reply, replyLabel, getReplyPendingIntent(
                        remoteMessage.getData().get("room_id"),
                        "public",
                        remoteMessage.getData().get("title"),
                        remoteMessage.getData().get("avatar_url"),
                        remoteMessage.getData().get("sender_id"),
                        remoteMessage.getData().get("sender_name"),
                        remoteMessage.getData().get("content")
                        ))
                        .addRemoteInput(remoteInput)
                        .setAllowGeneratedReplies(true)
                        .build();

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setLargeIcon(avatar)
                        .setStyle(messagingStyle)
                        .setAutoCancel(true)
                        .setGroup(GROUP_ID)
                        .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                        .setContentTitle(remoteMessage.getData().get("title"))
                        .setContentText(remoteMessage.getData().get("sender_name") + ": Mengirim Berkas")
                        .setContentIntent(pendingIntent)
                        .setDeleteIntent(deletePendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    mBuilder.addAction(R.drawable.ic_refresh, "Tandai Sudah Dibaca", markAsReadPendingIntent)
                            .addAction(replyAction);
                }

                notificationManager.notify(remoteMessage.getData().get("room_id").hashCode(), mBuilder.build());
            } else {
                Intent resultIntent = new Intent(this, HomeActivity.class);
                resultIntent.setAction("FROM_CHAT_FILE_PUBLIC_NOTIFICATION");
                resultIntent.putExtra("EXTRA_ROOM_ID", remoteMessage.getData().get("room_id"));
                resultIntent.putExtra("EXTRA_ROOM_TITLE", remoteMessage.getData().get("title"));
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                String notifMessage = remoteMessage.getData().get("id") + "UMSDAILY_DIVIDER" + "Mengirim Berkas";

                Bitmap avatar = getBitmapfromUrl(remoteMessage.getData().get("avatar_url"));

                messages.add(notifMessage);
                spm.setList(remoteMessage.getData().get("room_id"), messages);

                NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle(spm.getUserName());
                messagingStyle.setConversationTitle(remoteMessage.getData().get("title") + " (" + messages.size() + ")");

                int fromIndex = 0;
                if (messages.size() > 7) {
                    fromIndex = messages.size() - 7;
                }
                for (String data : messages.subList(fromIndex, messages.size())) {
                    String message = data.split("UMSDAILY_DIVIDER")[1];
                    messagingStyle.addMessage(message, 0, remoteMessage.getData().get("sender_name"));
                }

                //Delete
                Intent deleteIntent = new Intent(this, NotificationBroadcastReceiver.class);
                deleteIntent.setAction(DELETE_ACTION);
                deleteIntent.putExtra("EXTRA_ROOM_ID", remoteMessage.getData().get("room_id"));
                PendingIntent deletePendingIntent =
                        PendingIntent.getBroadcast(this, 2, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                //Mark as Read
                Intent markIntent = new Intent(this, NotificationBroadcastReceiver.class);
                markIntent.setAction(MARK_ACTION);
                markIntent.putExtra("EXTRA_ROOM_ID", remoteMessage.getData().get("room_id"));
                PendingIntent markAsReadPendingIntent =
                        PendingIntent.getBroadcast(this, 3, markIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                //Direct Reply
                String replyLabel = "Balas";
                RemoteInput remoteInput = new RemoteInput.Builder(KEY_REPLY)
                        .setLabel(replyLabel)
                        .build();

                NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(
                        R.drawable.ic_reply, replyLabel, getReplyPendingIntent(
                        remoteMessage.getData().get("room_id"),
                        "public",
                        remoteMessage.getData().get("title"),
                        remoteMessage.getData().get("avatar_url"),
                        remoteMessage.getData().get("sender_id"),
                        remoteMessage.getData().get("sender_name"),
                        remoteMessage.getData().get("content")
                ))
                        .addRemoteInput(remoteInput)
                        .setAllowGeneratedReplies(true)
                        .build();

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setLargeIcon(avatar)
                        .setStyle(messagingStyle)
                        .setAutoCancel(true)
                        .setGroup(GROUP_ID)
                        .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                        .setContentTitle(remoteMessage.getData().get("title") + " (" + messages.size() + ")")
                        .setContentText(remoteMessage.getData().get("sender_name") + ": Mengirim Berkas")
                        .setContentIntent(pendingIntent)
                        .setDeleteIntent(deletePendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    mBuilder.addAction(R.drawable.ic_refresh, "Tandai Sudah Dibaca", markAsReadPendingIntent)
                            .addAction(replyAction);
                }

                notificationManager.notify(remoteMessage.getData().get("room_id").hashCode(), mBuilder.build());
            }
        }

        if (remoteMessage.getData().get("type").equals("CHAT_MESSAGE_PUBLIC")) {
            if (messages == null) {
                Intent resultIntent = new Intent(this, HomeActivity.class);
                resultIntent.setAction("FROM_CHAT_MESSAGE_PUBLIC_NOTIFICATION");
                resultIntent.putExtra("EXTRA_ROOM_ID", remoteMessage.getData().get("room_id"));
                resultIntent.putExtra("EXTRA_ROOM_TITLE", remoteMessage.getData().get("title"));
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                String notifMessage = remoteMessage.getData().get("id") + "UMSDAILY_DIVIDER" + remoteMessage.getData().get("content");
                Bitmap avatar = getBitmapfromUrl(remoteMessage.getData().get("avatar_url"));

                NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle(spm.getUserName());
                messagingStyle.setConversationTitle(remoteMessage.getData().get("title"));
                messagingStyle.addMessage(remoteMessage.getData().get("content"), 0, remoteMessage.getData().get("sender_name"));

                ArrayList<String> newMessages = new ArrayList<>();
                newMessages.add(notifMessage);

                spm.setList(remoteMessage.getData().get("room_id"), newMessages);

                //Delete
                Intent deleteIntent = new Intent(this, NotificationBroadcastReceiver.class);
                deleteIntent.setAction(DELETE_ACTION);
                deleteIntent.putExtra("EXTRA_ROOM_ID", remoteMessage.getData().get("room_id"));
                PendingIntent deletePendingIntent = PendingIntent.getBroadcast(this, DateHelper.getTimestamp().intValue(), deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                //Mark as Read
                Intent markIntent = new Intent(this, NotificationBroadcastReceiver.class);
                markIntent.setAction(MARK_ACTION);
                markIntent.putExtra("EXTRA_ROOM_ID", remoteMessage.getData().get("room_id"));
                PendingIntent markAsReadPendingIntent =
                        PendingIntent.getBroadcast(this, DateHelper.getTimestamp().intValue(), markIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                //Direct Reply
                String replyLabel = "Balas";
                RemoteInput remoteInput = new RemoteInput.Builder(KEY_REPLY)
                        .setLabel(replyLabel)
                        .build();

                NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(
                        R.drawable.ic_reply, replyLabel, getReplyPendingIntent(
                        remoteMessage.getData().get("room_id"),
                        "public",
                        remoteMessage.getData().get("title"),
                        remoteMessage.getData().get("avatar_url"),
                        remoteMessage.getData().get("sender_id"),
                        remoteMessage.getData().get("sender_name"),
                        remoteMessage.getData().get("content")
                        ))
                        .addRemoteInput(remoteInput)
                        .setAllowGeneratedReplies(true)
                        .build();

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setLargeIcon(avatar)
                        .setStyle(messagingStyle)
                        .setAutoCancel(true)
                        .setGroup(GROUP_ID)
                        .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                        .setContentTitle(remoteMessage.getData().get("title"))
                        .setContentText(remoteMessage.getData().get("sender_name") + ": " + remoteMessage.getData().get("content"))
                        .setContentIntent(pendingIntent)
                        .setDeleteIntent(deletePendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    mBuilder.addAction(R.drawable.ic_refresh, "Tandai Sudah Dibaca", markAsReadPendingIntent)
                            .addAction(replyAction);
                }

                notificationManager.notify(remoteMessage.getData().get("room_id").hashCode(), mBuilder.build());
            } else {
                Intent resultIntent = new Intent(this, HomeActivity.class);
                resultIntent.setAction("FROM_CHAT_MESSAGE_PUBLIC_NOTIFICATION");
                resultIntent.putExtra("EXTRA_ROOM_ID", remoteMessage.getData().get("room_id"));
                resultIntent.putExtra("EXTRA_ROOM_TITLE", remoteMessage.getData().get("title"));
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                String boldSenderMessage = remoteMessage.getData().get("id") + "UMSDAILY_DIVIDER" + remoteMessage.getData().get("content");
                Bitmap avatar = getBitmapfromUrl(remoteMessage.getData().get("avatar_url"));

                messages.add(boldSenderMessage);
                spm.setList(remoteMessage.getData().get("room_id"), messages);

                NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle(spm.getUserName());
                messagingStyle.setConversationTitle(remoteMessage.getData().get("title") + " (" + messages.size() + ")");

                int fromIndex = 0;
                if (messages.size() > 7) {
                    fromIndex = messages.size() - 7;
                }
                for (String data : messages.subList(fromIndex, messages.size())) {
                    String message = data.split("UMSDAILY_DIVIDER")[1];
                    messagingStyle.addMessage(message, 0, remoteMessage.getData().get("sender_name"));
                }

                //Delete
                Intent deleteIntent = new Intent(this, NotificationBroadcastReceiver.class);
                deleteIntent.setAction(DELETE_ACTION);
                deleteIntent.putExtra("EXTRA_ROOM_ID", remoteMessage.getData().get("room_id"));
                PendingIntent deletePendingIntent =
                        PendingIntent.getBroadcast(this, DateHelper.getTimestamp().intValue(), deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                //Mark as Read
                Intent markIntent = new Intent(this, NotificationBroadcastReceiver.class);
                markIntent.setAction(MARK_ACTION);
                markIntent.putExtra("EXTRA_ROOM_ID", remoteMessage.getData().get("room_id"));
                PendingIntent markAsReadPendingIntent =
                        PendingIntent.getBroadcast(this, DateHelper.getTimestamp().intValue(), markIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                //Direct Reply
                String replyLabel = "Balas";
                RemoteInput remoteInput = new RemoteInput.Builder(KEY_REPLY)
                        .setLabel(replyLabel)
                        .build();

                NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(
                        R.drawable.ic_reply, replyLabel, getReplyPendingIntent(
                        remoteMessage.getData().get("room_id"),
                        "public",
                        remoteMessage.getData().get("title"),
                        remoteMessage.getData().get("avatar_url"),
                        remoteMessage.getData().get("sender_id"),
                        remoteMessage.getData().get("sender_name"),
                        remoteMessage.getData().get("content")
                        ))
                        .addRemoteInput(remoteInput)
                        .setAllowGeneratedReplies(true)
                        .build();

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setLargeIcon(avatar)
                        .setStyle(messagingStyle)
                        .setAutoCancel(true)
                        .setGroup(GROUP_ID)
                        .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                        .setContentTitle(remoteMessage.getData().get("title") + " (" + messages.size() + ")")
                        .setContentText(remoteMessage.getData().get("sender_name") + ": " + remoteMessage.getData().get("content"))
                        .setContentIntent(pendingIntent)
                        .setDeleteIntent(deletePendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    mBuilder.addAction(R.drawable.ic_refresh, "Tandai Sudah Dibaca", markAsReadPendingIntent)
                            .addAction(replyAction);
                }

                notificationManager.notify(remoteMessage.getData().get("room_id").hashCode(), mBuilder.build());
            }
        }

        if (remoteMessage.getData().get("type").equals("CHAT_MESSAGE_PRIVATE")) {
            if (messages == null) {
                ArrayList<String> newMessages = new ArrayList<>();
                newMessages.add(remoteMessage.getData().get("id") + "UMSDAILY_DIVIDER" + remoteMessage.getData().get("content"));

                spm.setList(remoteMessage.getData().get("room_id"), newMessages);

                Intent resultIntent = new Intent(this, HomeActivity.class);
                resultIntent.setAction("FROM_CHAT_MESSAGE_PRIVATE_NOTIFICATION");
                resultIntent.putExtra("EXTRA_ROOM_ID", remoteMessage.getData().get("room_id"));
                resultIntent.putExtra("EXTRA_ROOM_TITLE", remoteMessage.getData().get("title"));
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                Bitmap avatar = getBitmapfromUrl(remoteMessage.getData().get("avatar_url"));

                NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
                bigTextStyle.setBigContentTitle(remoteMessage.getData().get("title"));
                bigTextStyle.setSummaryText(remoteMessage.getData().get("content"));

                //Delete
                Intent deleteIntent = new Intent(this, NotificationBroadcastReceiver.class);
                deleteIntent.setAction(DELETE_ACTION);
                deleteIntent.putExtra("EXTRA_ROOM_ID", remoteMessage.getData().get("room_id"));
                PendingIntent deletePendingIntent =
                        PendingIntent.getBroadcast(this, DateHelper.getTimestamp().intValue(), deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                //Mark as Read
                Intent markIntent = new Intent(this, NotificationBroadcastReceiver.class);
                markIntent.setAction(MARK_ACTION);
                markIntent.putExtra("EXTRA_ROOM_ID", remoteMessage.getData().get("room_id"));
                PendingIntent markAsReadPendingIntent =
                        PendingIntent.getBroadcast(this, DateHelper.getTimestamp().intValue(), markIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                //Direct Reply
                String replyLabel = "Balas";
                RemoteInput remoteInput = new RemoteInput.Builder(KEY_REPLY)
                        .setLabel(replyLabel)
                        .build();

                NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(
                        R.drawable.ic_reply, replyLabel, getReplyPendingIntent(
                                remoteMessage.getData().get("room_id"),
                        "private",
                            remoteMessage.getData().get("title"),
                            remoteMessage.getData().get("avatar_url"),
                            remoteMessage.getData().get("sender_id"),
                            remoteMessage.getData().get("sender_name"),
                            remoteMessage.getData().get("content")
                        ))
                        .addRemoteInput(remoteInput)
                        .setAllowGeneratedReplies(true)
                        .build();

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setLargeIcon(avatar)
                        .setStyle(bigTextStyle)
                        .setAutoCancel(true)
                        .setGroup(GROUP_ID)
                        .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                        .setContentTitle(remoteMessage.getData().get("title"))
                        .setContentText(remoteMessage.getData().get("content"))
                        .setContentIntent(pendingIntent)
                        .setDeleteIntent(deletePendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    mBuilder.addAction(R.drawable.ic_refresh, "Tandai Sudah Dibaca", markAsReadPendingIntent)
                            .addAction(replyAction);
                }

                notificationManager.notify(remoteMessage.getData().get("room_id").hashCode(), mBuilder.build());
            } else {
                messages.add(remoteMessage.getData().get("id") + "UMSDAILY_DIVIDER" + remoteMessage.getData().get("content"));
                spm.setList(remoteMessage.getData().get("room_id"), messages);

                Intent resultIntent = new Intent(this, HomeActivity.class);
                resultIntent.setAction("FROM_CHAT_MESSAGE_PRIVATE_NOTIFICATION");
                resultIntent.putExtra("EXTRA_ROOM_ID", remoteMessage.getData().get("room_id"));
                resultIntent.putExtra("EXTRA_ROOM_TITLE", remoteMessage.getData().get("title"));
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                Bitmap avatar = getBitmapfromUrl(remoteMessage.getData().get("avatar_url"));

                NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                inboxStyle.setBigContentTitle(remoteMessage.getData().get("title") + " (" + messages.size() + ")");
                inboxStyle.setSummaryText(remoteMessage.getData().get(messages.size() + " Pesan Belum Dibaca"));

                int fromIndex = 0;
                if (messages.size() > 7) {
                    fromIndex = messages.size() - 7;
                }
                for (String data : messages.subList(fromIndex, messages.size())) {
                    String message = data.split("UMSDAILY_DIVIDER")[1];
                    inboxStyle.addLine(message);
                }

                //Delete
                Intent deleteIntent = new Intent(this, NotificationBroadcastReceiver.class);
                deleteIntent.setAction(DELETE_ACTION);
                deleteIntent.putExtra("EXTRA_ROOM_ID", remoteMessage.getData().get("room_id"));
                PendingIntent deletePendingIntent =
                        PendingIntent.getBroadcast(this, DateHelper.getTimestamp().intValue(), deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                //Mark as Read
                Intent markIntent = new Intent(this, NotificationBroadcastReceiver.class);
                markIntent.setAction(MARK_ACTION);
                markIntent.putExtra("EXTRA_ROOM_ID", remoteMessage.getData().get("room_id"));
                PendingIntent markAsReadPendingIntent =
                        PendingIntent.getBroadcast(this, DateHelper.getTimestamp().intValue(), markIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                //Direct Reply
                String replyLabel = "Balas";
                RemoteInput remoteInput = new RemoteInput.Builder(KEY_REPLY)
                        .setLabel(replyLabel)
                        .build();

                NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(
                        R.drawable.ic_reply, replyLabel, getReplyPendingIntent(
                        remoteMessage.getData().get("room_id"),
                        "private",
                        remoteMessage.getData().get("title"),
                        remoteMessage.getData().get("avatar_url"),
                        remoteMessage.getData().get("sender_id"),
                        remoteMessage.getData().get("sender_name"),
                        remoteMessage.getData().get("content")
                        ))
                        .addRemoteInput(remoteInput)
                        .setAllowGeneratedReplies(true)
                        .build();

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setLargeIcon(avatar)
                        .setStyle(inboxStyle)
                        .setAutoCancel(true)
                        .setGroup(GROUP_ID)
                        .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                        .setContentTitle(remoteMessage.getData().get("title") + " (" + messages.size() + ")")
                        .setContentText(remoteMessage.getData().get("content"))
                        .setContentIntent(pendingIntent)
                        .setDeleteIntent(deletePendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    mBuilder.addAction(R.drawable.ic_refresh, "Tandai Sudah Dibaca", markAsReadPendingIntent)
                            .addAction(replyAction);
                }

                notificationManager.notify(remoteMessage.getData().get("room_id").hashCode(), mBuilder.build());

            }
        }

        if (remoteMessage.getData().get("type").equals("CHAT_IMAGE_PRIVATE")) {
            if (messages == null) {
                ArrayList<String> newMessages = new ArrayList<>();
                newMessages.add(remoteMessage.getData().get("id") + "UMSDAILY_DIVIDER" + "Mengirim Gambar");

                spm.setList(remoteMessage.getData().get("room_id"), newMessages);

                Intent resultIntent = new Intent(this, HomeActivity.class);
                resultIntent.setAction("FROM_CHAT_IMAGE_PRIVATE_NOTIFICATION");
                resultIntent.putExtra("EXTRA_ROOM_ID", remoteMessage.getData().get("room_id"));
                resultIntent.putExtra("EXTRA_ROOM_TITLE", remoteMessage.getData().get("title"));
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                Bitmap image = getBitmapfromUrl(remoteMessage.getData().get("image_url"));
                Bitmap avatar = getBitmapfromUrl(remoteMessage.getData().get("avatar_url"));

                //Delete
                Intent deleteIntent = new Intent(this, NotificationBroadcastReceiver.class);
                deleteIntent.setAction(DELETE_ACTION);
                deleteIntent.putExtra("EXTRA_ROOM_ID", remoteMessage.getData().get("room_id"));
                PendingIntent deletePendingIntent =
                        PendingIntent.getBroadcast(this, DateHelper.getTimestamp().intValue(), deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                //Mark as Read
                Intent markIntent = new Intent(this, NotificationBroadcastReceiver.class);
                markIntent.setAction(MARK_ACTION);
                markIntent.putExtra("EXTRA_ROOM_ID", remoteMessage.getData().get("room_id"));
                PendingIntent markAsReadPendingIntent =
                        PendingIntent.getBroadcast(this, DateHelper.getTimestamp().intValue(), markIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                //Direct Reply
                String replyLabel = "Balas";
                RemoteInput remoteInput = new RemoteInput.Builder(KEY_REPLY)
                        .setLabel(replyLabel)
                        .build();

                NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(
                        R.drawable.ic_reply, replyLabel, getReplyPendingIntent(
                        remoteMessage.getData().get("room_id"),
                        "private",
                        remoteMessage.getData().get("title"),
                        remoteMessage.getData().get("avatar_url"),
                        remoteMessage.getData().get("sender_id"),
                        remoteMessage.getData().get("sender_name"),
                        remoteMessage.getData().get("content")
                        ))
                        .addRemoteInput(remoteInput)
                        .setAllowGeneratedReplies(true)
                        .build();

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setLargeIcon(avatar)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image))
                        .setAutoCancel(true)
                        .setGroup(GROUP_ID)
                        .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                        .setContentTitle(remoteMessage.getData().get("title"))
                        .setContentIntent(pendingIntent)
                        .setDeleteIntent(deletePendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    mBuilder.addAction(R.drawable.ic_refresh, "Tandai Sudah Dibaca", markAsReadPendingIntent)
                            .addAction(replyAction);
                }

                if (remoteMessage.getData().get("content").isEmpty()) {
                    mBuilder.setContentText("Mengirim Gambar");
                } else {
                    mBuilder.setContentText(remoteMessage.getData().get("content"));
                }
                notificationManager.notify(remoteMessage.getData().get("room_id").hashCode(), mBuilder.build());
            } else {
                Intent resultIntent = new Intent(this, HomeActivity.class);
                resultIntent.setAction("FROM_CHAT_IMAGE_PRIVATE_NOTIFICATION");
                resultIntent.putExtra("EXTRA_ROOM_ID", remoteMessage.getData().get("room_id"));
                resultIntent.putExtra("EXTRA_ROOM_TITLE", remoteMessage.getData().get("title"));
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                Bitmap image = getBitmapfromUrl(remoteMessage.getData().get("image_url"));
                Bitmap avatar = getBitmapfromUrl(remoteMessage.getData().get("avatar_url"));

                messages.add(remoteMessage.getData().get("id") + "UMSDAILY_DIVIDER" + "Mengirim Gambar");
                spm.setList(remoteMessage.getData().get("room_id"), messages);

                NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                inboxStyle.setBigContentTitle(remoteMessage.getData().get("title") + " (" + messages.size() + ")");
                inboxStyle.setSummaryText(remoteMessage.getData().get(messages.size() + " Pesan Belum Dibaca"));

                int fromIndex = 0;
                if (messages.size() > 7) {
                    fromIndex = messages.size() - 7;
                }
                for (String data : messages.subList(fromIndex, messages.size())) {
                    String message = data.split("UMSDAILY_DIVIDER")[1];
                    inboxStyle.addLine(message);
                }

                //Delete
                Intent deleteIntent = new Intent(this, NotificationBroadcastReceiver.class);
                deleteIntent.setAction(DELETE_ACTION);
                deleteIntent.putExtra("EXTRA_ROOM_ID", remoteMessage.getData().get("room_id"));
                PendingIntent deletePendingIntent =
                        PendingIntent.getBroadcast(this, DateHelper.getTimestamp().intValue(), deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                //Mark as Read
                Intent markIntent = new Intent(this, NotificationBroadcastReceiver.class);
                markIntent.setAction(MARK_ACTION);
                markIntent.putExtra("EXTRA_ROOM_ID", remoteMessage.getData().get("room_id"));
                PendingIntent markAsReadPendingIntent =
                        PendingIntent.getBroadcast(this, DateHelper.getTimestamp().intValue(), markIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                //Direct Reply
                String replyLabel = "Balas";
                RemoteInput remoteInput = new RemoteInput.Builder(KEY_REPLY)
                        .setLabel(replyLabel)
                        .build();

                NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(
                        R.drawable.ic_reply, replyLabel, getReplyPendingIntent(
                        remoteMessage.getData().get("room_id"),
                        "private",
                        remoteMessage.getData().get("title"),
                        remoteMessage.getData().get("avatar_url"),
                        remoteMessage.getData().get("sender_id"),
                        remoteMessage.getData().get("sender_name"),
                        remoteMessage.getData().get("content")
                        ))
                        .addRemoteInput(remoteInput)
                        .setAllowGeneratedReplies(true)
                        .build();

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setLargeIcon(avatar)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setStyle(inboxStyle)
                        .setAutoCancel(true)
                        .setGroup(GROUP_ID)
                        .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                        .setContentTitle(remoteMessage.getData().get("title") + " (" + messages.size() + ")")
                        .setContentIntent(pendingIntent)
                        .setDeleteIntent(deletePendingIntent)
                        .setContentText("Mengirim Gambar")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    mBuilder.addAction(R.drawable.ic_refresh, "Tandai Sudah Dibaca", markAsReadPendingIntent)
                            .addAction(replyAction);
                }

                notificationManager.notify(remoteMessage.getData().get("room_id").hashCode(), mBuilder.build());
            }
        }

        if (remoteMessage.getData().get("type").equals("CHAT_FILE_PRIVATE")) {
            if (messages == null) {
                ArrayList<String> newMessages = new ArrayList<>();
                newMessages.add(remoteMessage.getData().get("id") + "UMSDAILY_DIVIDER" + "Mengirim Berkas");

                spm.setList(remoteMessage.getData().get("room_id"), newMessages);

                Intent resultIntent = new Intent(this, HomeActivity.class);
                resultIntent.setAction("FROM_CHAT_FILE_PRIVATE_NOTIFICATION");
                resultIntent.putExtra("EXTRA_ROOM_ID", remoteMessage.getData().get("room_id"));
                resultIntent.putExtra("EXTRA_ROOM_TITLE", remoteMessage.getData().get("title"));
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
                bigTextStyle.setBigContentTitle(remoteMessage.getData().get("title"));
                bigTextStyle.setSummaryText("Mengirim Berkas");

                Bitmap avatar = getBitmapfromUrl(remoteMessage.getData().get("avatar_url"));

                //Delete
                Intent deleteIntent = new Intent(this, NotificationBroadcastReceiver.class);
                deleteIntent.setAction(DELETE_ACTION);
                deleteIntent.putExtra("EXTRA_ROOM_ID", remoteMessage.getData().get("room_id"));
                PendingIntent deletePendingIntent =
                        PendingIntent.getBroadcast(this, DateHelper.getTimestamp().intValue(), deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                //Mark as Read
                Intent markIntent = new Intent(this, NotificationBroadcastReceiver.class);
                markIntent.setAction(MARK_ACTION);
                markIntent.putExtra("EXTRA_ROOM_ID", remoteMessage.getData().get("room_id"));
                PendingIntent markAsReadPendingIntent =
                        PendingIntent.getBroadcast(this, DateHelper.getTimestamp().intValue(), markIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                //Direct Reply
                String replyLabel = "Balas";
                RemoteInput remoteInput = new RemoteInput.Builder(KEY_REPLY)
                        .setLabel(replyLabel)
                        .build();

                NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(
                        R.drawable.ic_reply, replyLabel, getReplyPendingIntent(
                        remoteMessage.getData().get("room_id"),
                        "private",
                        remoteMessage.getData().get("title"),
                        remoteMessage.getData().get("avatar_url"),
                        remoteMessage.getData().get("sender_id"),
                        remoteMessage.getData().get("sender_name"),
                        remoteMessage.getData().get("content")
                ))
                        .addRemoteInput(remoteInput)
                        .setAllowGeneratedReplies(true)
                        .build();

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setLargeIcon(avatar)
                        .setStyle(bigTextStyle)
                        .setAutoCancel(true)
                        .setGroup(GROUP_ID)
                        .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                        .setContentTitle(remoteMessage.getData().get("title"))
                        .setContentText("Mengirim Berkas")
                        .setContentIntent(pendingIntent)
                        .setDeleteIntent(deletePendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    mBuilder.addAction(R.drawable.ic_refresh, "Tandai Sudah Dibaca", markAsReadPendingIntent)
                            .addAction(replyAction);
                }

                notificationManager.notify(remoteMessage.getData().get("room_id").hashCode(), mBuilder.build());
            } else {
                Intent resultIntent = new Intent(this, HomeActivity.class);
                resultIntent.setAction("FROM_CHAT_FILE_PRIVATE_NOTIFICATION");
                resultIntent.putExtra("EXTRA_ROOM_ID", remoteMessage.getData().get("room_id"));
                resultIntent.putExtra("EXTRA_ROOM_TITLE", remoteMessage.getData().get("title"));
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                Bitmap avatar = getBitmapfromUrl(remoteMessage.getData().get("avatar_url"));

                messages.add(remoteMessage.getData().get("id") + "UMSDAILY_DIVIDER" + "Mengirim Berkas");
                spm.setList(remoteMessage.getData().get("room_id"), messages);

                NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                inboxStyle.setBigContentTitle(remoteMessage.getData().get("title") + " (" + messages.size() + ")");
                inboxStyle.setSummaryText(remoteMessage.getData().get(messages.size() + " Pesan Belum Dibaca"));

                int fromIndex = 0;
                if (messages.size() > 7) {
                    fromIndex = messages.size() - 7;
                }
                for (String data : messages.subList(fromIndex, messages.size())) {
                    String message = data.split("UMSDAILY_DIVIDER")[1];
                    inboxStyle.addLine(message);
                }

                //Delete
                Intent deleteIntent = new Intent(this, NotificationBroadcastReceiver.class);
                deleteIntent.setAction(DELETE_ACTION);
                deleteIntent.putExtra("EXTRA_ROOM_ID", remoteMessage.getData().get("room_id"));
                PendingIntent deletePendingIntent =
                        PendingIntent.getBroadcast(this, DateHelper.getTimestamp().intValue(), deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                //Mark as Read
                Intent markIntent = new Intent(this, NotificationBroadcastReceiver.class);
                markIntent.setAction(MARK_ACTION);
                markIntent.putExtra("EXTRA_ROOM_ID", remoteMessage.getData().get("room_id"));
                PendingIntent markAsReadPendingIntent =
                        PendingIntent.getBroadcast(this, DateHelper.getTimestamp().intValue(), markIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                //Direct Reply
                String replyLabel = "Balas";
                RemoteInput remoteInput = new RemoteInput.Builder(KEY_REPLY)
                        .setLabel(replyLabel)
                        .build();

                NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(
                        R.drawable.ic_reply, replyLabel, getReplyPendingIntent(
                        remoteMessage.getData().get("room_id"),
                        "private",
                        remoteMessage.getData().get("title"),
                        remoteMessage.getData().get("avatar_url"),
                        remoteMessage.getData().get("sender_id"),
                        remoteMessage.getData().get("sender_name"),
                        remoteMessage.getData().get("content")
                        ))
                        .addRemoteInput(remoteInput)
                        .setAllowGeneratedReplies(true)
                        .build();

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setLargeIcon(avatar)
                        .setStyle(inboxStyle)
                        .setAutoCancel(true)
                        .setGroup(GROUP_ID)
                        .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                        .setContentTitle(remoteMessage.getData().get("title") + " (" + messages.size() + ")")
                        .setContentText("Mengirim Berkas")
                        .setContentIntent(pendingIntent)
                        .setDeleteIntent(deletePendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    mBuilder.addAction(R.drawable.ic_refresh, "Tandai Sudah Dibaca", markAsReadPendingIntent)
                            .addAction(replyAction);
                }

                notificationManager.notify(remoteMessage.getData().get("room_id").hashCode(), mBuilder.build());
            }
        }

        if (remoteMessage.getData().get("type").equals("ASSIGNMENT")) {
            Intent resultIntent = new Intent(this, HomeActivity.class);
            resultIntent.setAction("FROM_ASSIGNMENT_NOTIFICATION");
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            CharSequence boldTypeMessage = Html.fromHtml("<b>Tugas</b> " + remoteMessage.getData().get("content"));

            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            bigTextStyle.setBigContentTitle(remoteMessage.getData().get("title"));
            bigTextStyle.setSummaryText(boldTypeMessage);

            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_assignment_notification);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setLargeIcon(largeIcon)
                    .setStyle(bigTextStyle)
                    .setAutoCancel(true)
                    .setGroup(GROUP_ID)
                    .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    .setContentTitle(remoteMessage.getData().get("title"))
                    .setContentText(boldTypeMessage)
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationManager.createNotificationChannelGroup(new NotificationChannelGroup(remoteMessage.getData().get("room_id"), remoteMessage.getData().get("title")));
            }
            notificationManager.notify(remoteMessage.getData().get("id").hashCode(), mBuilder.build());
        }

        if (remoteMessage.getData().get("type").equals("ANNOUNCEMENT")) {
            Intent resultIntent = new Intent(this, HomeActivity.class);
            resultIntent.setAction("FROM_ANNOUNCEMENT_NOTIFICATION");
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            if (remoteMessage.getData().get("image_url").isEmpty()) {
                NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
                bigTextStyle.setBigContentTitle(remoteMessage.getData().get("title"));
                bigTextStyle.setSummaryText(remoteMessage.getData().get("content"));

                Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_announcement_notification);

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setLargeIcon(largeIcon)
                        .setStyle(bigTextStyle)
                        .setAutoCancel(true)
                        .setGroup(GROUP_ID)
                        .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                        .setContentTitle(remoteMessage.getData().get("title"))
                        .setContentText(remoteMessage.getData().get("content"))
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notificationManager.createNotificationChannelGroup(new NotificationChannelGroup("ANNOUNCEMENT", remoteMessage.getData().get("title")));
                }
                notificationManager.notify(remoteMessage.getData().get("id").hashCode(), mBuilder.build());
            } else {
                Bitmap image = getBitmapfromUrl(remoteMessage.getData().get("image_url"));
                Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_announcement_notification);

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setLargeIcon(largeIcon)
                        .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image))
                        .setAutoCancel(true)
                        .setGroup(GROUP_ID)
                        .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                        .setContentTitle(remoteMessage.getData().get("title"))
                        .setContentText(remoteMessage.getData().get("content"))
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notificationManager.createNotificationChannelGroup(new NotificationChannelGroup("ANNOUNCEMENT", remoteMessage.getData().get("title")));
                }

                notificationManager.notify(remoteMessage.getData().get("id").hashCode(), mBuilder.build());
            }
        }

    }
    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "UMS Daily";
            String description = "UMS Daily Notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            CharSequence nameSilent = "UMS Daily Silent";
            notificationManager.createNotificationChannel(new NotificationChannel(CHANNEL_SILENT_ID, nameSilent, NotificationManager.IMPORTANCE_LOW));

        }
    }

    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }

    public PendingIntent getReplyPendingIntent(String roomId, String roomType, String roomTitle, String avatarUrl, String receiverId, String receiverName, String message) {
        Intent intent = new Intent(getApplicationContext(), NotificationBroadcastReceiver.class);
        intent.setAction(REPLY_ACTION);
        intent.putExtra("EXTRA_ROOM_ID", roomId);
        intent.putExtra("EXTRA_ROOM_TYPE", roomType);
        intent.putExtra("EXTRA_ROOM_TITLE", roomTitle);
        intent.putExtra("EXTRA_AVATAR_URL", avatarUrl);
        intent.putExtra("EXTRA_RECEIVER_ID", receiverId);
        intent.putExtra("EXTRA_RECEIVER_NAME", receiverName);
        intent.putExtra("EXTRA_MESSAGE", message);
        return PendingIntent.getBroadcast(getApplicationContext(), DateHelper.getTimestamp().intValue(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

    }
}
