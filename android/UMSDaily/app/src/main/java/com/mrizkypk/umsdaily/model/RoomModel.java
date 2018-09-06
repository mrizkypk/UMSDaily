package com.mrizkypk.umsdaily.model;

import java.util.HashMap;

public class RoomModel {
    private String id;
    private String title;
    private HashMap<String, Boolean> member;
    private String avatar_url;
    private String type;

    public RoomModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public HashMap<String, Boolean> getMember() {
        return member;
    }

    public void setMember(HashMap<String, Boolean> member) {
        this.member = member;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
