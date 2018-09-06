package com.mrizkypk.umsdaily.model;

public class ResponseErrorModel {
    private String message;
    private String code;

    public ResponseErrorModel() {
    }

    public ResponseErrorModel(String message, String code) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
