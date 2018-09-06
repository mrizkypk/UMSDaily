package com.mrizkypk.umsdaily.helper;

import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONObject;

public class NotificationHelper {
    public static void send(String id,
                            String type,
                            String filter,
                            String roomId,
                            String senderId,
                            String senderName,
                            String receiverId,
                            String receiverName,
                            String avatarUrl,
                            String imageUrl,
                            String title,
                            String content,
                            String icon) {

        AndroidNetworking.get(ApiHelper.getHost() + "/notification.php")
                .addQueryParameter("id", id)
                .addQueryParameter("type", type)
                .addQueryParameter("filter", filter)
                .addQueryParameter("room_id", roomId)
                .addQueryParameter("sender_id", senderId)
                .addQueryParameter("sender_name", senderName)
                .addQueryParameter("receiver_id", receiverId)
                .addQueryParameter("receiver_name", receiverName)
                .addQueryParameter("avatar_url", avatarUrl)
                .addQueryParameter("image_url", imageUrl)
                .addQueryParameter("title", title)
                .addQueryParameter("content", content)
                .addQueryParameter("icon", icon)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                    }
                });
    }
}
