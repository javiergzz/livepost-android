package com.livepost.javiergzzr.objects;

/**
 * Created by Vyz on 2015-12-28.
 */
public class Session {
    private String author;
    private String authorName;
    private String category;
    private int followers;
    private String lastMessage;
    private String lastTime;
    private String picture;
    private long timestamp;
    private String title;
    public Session(){}
    public Session(String author,String authorName, String category, String lastMessage, String lastTime, String picture, long timestamp, String title) {
        this.author = author;
        this.authorName = authorName;
        this.category = category;
        this.followers = followers;
        this.lastMessage = lastMessage;
        this.lastTime = lastTime;
        this.picture = picture;
        this.timestamp = timestamp;
        this.title = title;
    }
    public String getAuthor() {
        return author;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getCategory() {
        return category;
    }

    public int getFollowers() {
        return followers;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getLastTime() {
        return lastTime;
    }

    public String getPicture() {
        return picture;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getTitle() {
        return title;
    }
}
