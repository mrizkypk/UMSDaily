package com.mrizkypk.umsdaily.model;

public class UserModel {
    private String id;
    private String name;
    private String token;
    private String device_token;
    private String type;
    private String avatar_url;

    public UserModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    @Override
    public boolean equals(Object obj) {
        boolean retVal = false;

        if (obj instanceof UserModel){
            UserModel ptr = (UserModel) obj;
            retVal = ptr.getId().equals(this.getId());
        }

        return retVal;
    }
}
