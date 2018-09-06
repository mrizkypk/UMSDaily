package com.mrizkypk.umsdaily.model;

import java.util.Map;

/**
 * Created by mrizkypk on 02/03/18.
 */

public class ChatRoomModel implements Comparable<ChatRoomModel>{
    private String id;
    private String type;
    private String room_id;
    private String room_type;
    private String room_title;
    private String sender_id;
    private String sender_name;
    private String receiver_id;
    private String receiver_name;
    private String message;
    private Long timestamp;
    private String file_type;
    private String file_size;
    private String file_name;
    private String file_path;
    private String file_local_path;
    private String file_extension;
    private String file_download_url;
    private String file_upload_status;
    private Integer file_upload_progress;
    private Map<String, Long> read;
    private Map<String, Long> received;
    private String reply_id;
    private String reply_type;
    private String reply_sender_id;
    private String reply_sender_name;
    private String reply_message;
    private String reply_file_name;
    private String reply_file_extension;
    private String reply_file_download_url;

    public ChatRoomModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoom_type() {
        return room_type;
    }

    public void setRoom_type(String room_type) {
        this.room_type = room_type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public String getRoom_title() {
        return room_title;
    }

    public void setRoom_title(String room_title) {
        this.room_title = room_title;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getSender_name() {
        return sender_name;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getReceiver_name() {
        return receiver_name;
    }

    public void setReceiver_name(String receiver_name) {
        this.receiver_name = receiver_name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getFile_type() {
        return file_type;
    }

    public void setFile_type(String file_type) {
        this.file_type = file_type;
    }

    public String getFile_size() {
        return file_size;
    }

    public void setFile_size(String file_size) {
        this.file_size = file_size;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public String getFile_local_path() {
        return file_local_path;
    }

    public void setFile_local_path(String file_local_path) {
        this.file_local_path = file_local_path;
    }

    public String getFile_extension() {
        return file_extension;
    }

    public void setFile_extension(String file_extension) {
        this.file_extension = file_extension;
    }

    public String getFile_upload_status() {
        return file_upload_status;
    }

    public void setFile_upload_status(String file_upload_status) {
        this.file_upload_status = file_upload_status;
    }

    public String getReply_file_name() {
        return reply_file_name;
    }

    public void setReply_file_name(String reply_file_name) {
        this.reply_file_name = reply_file_name;
    }

    public String getFile_download_url() {
        return file_download_url;
    }

    public void setFile_download_url(String file_download_url) {
        this.file_download_url = file_download_url;
    }

    public Integer getFile_upload_progress() {
        return file_upload_progress;
    }

    public void setFile_upload_progress(Integer file_upload_progress) {
        this.file_upload_progress = file_upload_progress;
    }

    public Map<String, Long> getRead() {
        return read;
    }

    public void setRead(Map<String, Long> read) {
        this.read = read;
    }

    public Map<String, Long> getReceived() {
        return received;
    }

    public void setReceived(Map<String, Long> received) {
        this.received = received;
    }

    public String getReply_id() {
        return reply_id;
    }

    public void setReply_id(String reply_id) {
        this.reply_id = reply_id;
    }

    public String getReply_type() {
        return reply_type;
    }

    public void setReply_type(String reply_type) {
        this.reply_type = reply_type;
    }

    public String getReply_sender_id() {
        return reply_sender_id;
    }

    public void setReply_sender_id(String reply_sender_id) {
        this.reply_sender_id = reply_sender_id;
    }

    public String getReply_sender_name() {
        return reply_sender_name;
    }

    public void setReply_sender_name(String reply_sender_name) {
        this.reply_sender_name = reply_sender_name;
    }

    public String getReply_message() {
        return reply_message;
    }

    public void setReply_message(String reply_message) {
        this.reply_message = reply_message;
    }

    public String getReply_file_download_url() {
        return reply_file_download_url;
    }

    public void setReply_file_download_url(String reply_file_download_url) {
        this.reply_file_download_url = reply_file_download_url;
    }

    public String getReply_file_extension() {
        return reply_file_extension;
    }

    public void setReply_file_extension(String reply_file_extension) {
        this.reply_file_extension = reply_file_extension;
    }

    @Override
    public boolean equals(Object obj) {
        boolean retVal = false;

        if (obj instanceof ChatRoomModel){
            ChatRoomModel ptr = (ChatRoomModel) obj;
            retVal = ptr.getId().equals(this.getId());
        }

        return retVal;
    }

    @Override
    public int compareTo(ChatRoomModel o) {
        if (getId() == null || o.getTimestamp() == null) {
            return 0;
        }
        return o.getTimestamp().compareTo(getTimestamp());
    }
}
