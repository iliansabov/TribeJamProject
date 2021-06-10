package com.optic.tribejam.models;

//Modelo de Usuario
public class User {

    private String id;
    private String email;
    private String phone;
    private String username;
    private String password;
    private long timestamp;
    private String imageProfile;
    private String imageCover;
    private long lastConection;
    private boolean online;


    public  User(){

    }
    //Constructor


    public User(String id, String email, String phone, String username, String password, long timestamp, String imageProfile, String imageCover, long lastConection, boolean online) {
        this.id = id;
        this.email = email;
        this.phone = phone;
        this.username = username;
        this.password = password;
        this.timestamp = timestamp;
        this.imageProfile = imageProfile;
        this.imageCover = imageCover;
        this.lastConection = lastConection;
        this.online = online;
    }

    /*---------------Getters & Setters*-----------*/
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() { return phone; }

    public void setPhone(String phone) { this.phone = phone; }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getTimestamp() { return timestamp; }

    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getImageProfile() { return imageProfile; }

    public void setImageProfile(String imageProfile) { this.imageProfile = imageProfile; }

    public String getImageCover() { return imageCover; }

    public void setImageCover(String imageCover) { this.imageCover = imageCover; }

    public long getLastConection() { return lastConection; }

    public void setLastConection(long lastConection) { this.lastConection = lastConection;  }

    public boolean isOnline() { return online; }

    public void setOnline(boolean online) { this.online = online; }
}
