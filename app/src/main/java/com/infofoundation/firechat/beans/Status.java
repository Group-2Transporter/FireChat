package com.infofoundation.firechat.beans;

import java.io.Serializable;

public class Status implements Serializable {
    private String date,time,imageUrl,uri,text,type,statusId;
    private Long timeStamp;

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public Status() {
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Status(String date, String time, String imageUrl, String uri, String text, String type, String statusId, Long timeStamp) {
        this.date = date;
        this.time = time;
        this.imageUrl = imageUrl;
        this.uri = uri;
        this.text = text;
        this.type = type;
        this.statusId = statusId;
        this.timeStamp = timeStamp;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
