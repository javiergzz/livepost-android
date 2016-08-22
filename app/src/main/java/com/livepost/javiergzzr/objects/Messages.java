package com.livepost.javiergzzr.objects;

import java.io.Serializable;
import java.util.Map;

public class Messages implements Serializable{
    private  String avatar;
    private int countLikes;
    private Map<String,Boolean> likes;
    private String message;
    private String sender;
    private String timeMessage;
    private long timestamp;
    public String getAvatar() {
        return avatar;
    }

    public int getCountLikes() {
        return countLikes;
    }

    public Map<String,Boolean> getLikes(){return likes;}

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }

    public String getTimeMessage() {
        return timeMessage;
    }

    public long getTimestamp() {
        return timestamp;
    }

    private Messages(){

    }
    public Messages(String avatar, int countLikes, Map<String,Boolean> likes, String message, String sender, String timeMessage, long timestamp) {
        this.avatar = avatar;
        this.countLikes = countLikes;
        this.likes = likes;
        this.message = message;
        this.sender = sender;
        this.timeMessage= timeMessage;
        this.timestamp = timestamp;
    }
}