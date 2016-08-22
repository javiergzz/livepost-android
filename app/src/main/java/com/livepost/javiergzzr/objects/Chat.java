package com.livepost.javiergzzr.objects;

public class Chat {

    private String message;
    private String author;
    private String name;
    private String imgProfile;
    private int likes;

    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private Chat() {
    }

    public Chat(String message, String author, String name, String imgProfile) {
        this.message = message;
        this.author = author;
        this.name = name;
        this.imgProfile = imgProfile;
        this.likes = 0;
    }

    public String getMessage() {
        return message;
    }

    public String getAuthor() {
        return author;
    }
    public String getName() {
        return name;
    }
    public String getImgProfile() {
        return imgProfile;
    }

    public int getLikes(){
        return likes;
    }
}
