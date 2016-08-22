package com.livepost.javiergzzr.objects;

/**
 * Created by Vyz on 2015-12-29.
 */
public class User {
    private String avatar;
    private String firstName;
    private String lastName;
    private String email;
    private String twitter_username;
    private String twitter_bio;

    public String getAvatar() {
        return avatar;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getTwitter_username() {
        return twitter_username;
    }

    public String getTwitter_bio() {
        return twitter_bio;
    }

    public User(String avatar, String firstName, String lastName, String email, String twitter_username, String twitter_bio) {
        this.avatar = avatar;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.twitter_username = twitter_username;
        this.twitter_bio = twitter_bio;
    }

    public User() {
    }
}
