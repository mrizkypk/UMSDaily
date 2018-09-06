package com.mrizkypk.umsdaily.model;

public class AssignmentModel {
    private String id;
    private String room_id;
    private String room_title;
    private String sender_id;
    private String sender_name;
    private String title;
    private String description;
    private Long timestamp;
    private Long max_date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getMax_date() {
        return max_date;
    }

    public void setMax_date(Long max_date) {
        this.max_date = max_date;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object obj) {
        boolean retVal = false;

        if (obj instanceof AssignmentModel){
            AssignmentModel ptr = (AssignmentModel) obj;
            retVal = ptr.getId().equals(this.getId());
        }

        return retVal;
    }
}
