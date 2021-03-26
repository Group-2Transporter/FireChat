package com.infofoundation.firechat.beans;

import java.io.Serializable;

public class User implements Serializable {
    private String name,status,imgUri,uid;
    private String date,time,state;
    private boolean isCheck=false;

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public User(String name, String status, String imgUri, String uid, String date, String time, String state, boolean isCheck) {
        this.name = name;
        this.status = status;
        this.imgUri = imgUri;
        this.uid = uid;
        this.date = date;
        this.time = time;
        this.state = state;
        this.isCheck = isCheck;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public User(String name, String status, String imgUri, String uid, String date, String time, String state) {
        this.name = name;
        this.status = status;
        this.imgUri = imgUri;
        this.uid = uid;
        this.date = date;
        this.time = time;
        this.state = state;
    }

    public User() {
    }

    public User(String name, String status, String imgUri, String uid) {
        this.name = name;
        this.status = status;
        this.imgUri = imgUri;
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImgUri() {
        return imgUri;
    }

    public void setImgUri(String imgUri) {
        this.imgUri = imgUri;
    }

    public User(String name, String status, String imgUri) {
        this.name = name;
        this.status = status;
        this.imgUri = imgUri;
    }

    public User(String name, String status) {
        this.name = name;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
