package com.mrizkypk.umsdaily.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mrizkypk.umsdaily.R;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class SharedPreferenceManager {
    Context context;
    SharedPreferences sp;

    public SharedPreferenceManager(Context ctx) {
        sp = ctx.getSharedPreferences(ctx.getString(R.string.app_shared_preference), Context.MODE_PRIVATE);
    }

    public SharedPreferences getInstance() {
        return sp;
    }

    public String getUserId() {
        return sp.getString("USER_ID", "EMPTY");
    }

    public String getUserName() {
        return sp.getString("USER_NAME", "EMPTY");
    }

    public String getUserPassword() {
        return sp.getString("USER_PASSWORD", "EMPTY");
    }
    public String getUserAvatarUrl() {
        return sp.getString("USER_AVATAR_URL", "EMPTY");
    }

    public String getUserToken() {
        return sp.getString("USER_TOKEN", "EMPTY");
    }
    public String getUserType() {
        return sp.getString("USER_TYPE", "EMPTY");
    }
    public void setUserAvatarUrl(String url) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("USER_AVATAR_URL", url);
        editor.apply();
    }

    public void setList(String key, ArrayList<String> list){
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();
    }

    public ArrayList<String> getList(String key){
        Gson gson = new Gson();
        String json = sp.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void removeList(String key){
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, null);
        editor.apply();
    }

}
