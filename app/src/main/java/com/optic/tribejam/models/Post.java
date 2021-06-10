package com.optic.tribejam.models;

public class Post {

    private String id;
    private String title;
    private String description;
    private String image1;
    private String idUser;
    private String category;
    private long timestamp;

    //Modelo de Post
    public Post() {

    }
    //Constructor
    public Post(String id, String title, String description, String image1, String idUser, String category,long timestamp) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.image1 = image1;
        this.idUser = idUser;
        this.category = category;
        this.timestamp = timestamp;
    }

    /*---------------Getters & Setters*-----------*/
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getTimestamp() { return timestamp; }

    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
