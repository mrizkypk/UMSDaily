package com.mrizkypk.umsdaily.model;

public class LoginModel {
    private String id;
    private String nama;
    private String token;

    public LoginModel() {
    }

    public LoginModel(String id, String nama, String token) {
        this.id = id;
        this.nama = nama;
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
