package com.mrizkypk.umsdaily.helper;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FirebaseRestHelper {

    public void updateChatRead(String roomId, String pushId, String userId) {
        Long timestamp = DateHelper.getTimestamp();
        String url = ApiHelper.getFirebaseHost() + "/chat/" + roomId + "/" + pushId + "/read.json";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(userId, timestamp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AndroidNetworking.patch(url)
                .addJSONObjectBody(jsonObject)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // do anything with response
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                    }
                });
    }

    public void updateChatReceived(String roomId, String pushId, String userId) {
        Long timestamp = DateHelper.getTimestamp();
        String url = ApiHelper.getFirebaseHost() + "/chat/" + roomId + "/" + pushId + "/received.json";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(userId, timestamp);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.patch(url)
                .addJSONObjectBody(jsonObject)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // do anything with response
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                    }
                });
    }
}
