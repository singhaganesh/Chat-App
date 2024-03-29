package com.example.chatapp.Models;

import java.io.Serializable;

public class User implements Serializable {
    private String name, image,username,token,id;

    public User(String name, String image, String username, String token,String id) {
        this.name = name;
        this.image = image;
        this.username = username;
        this.token = token;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String email) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
