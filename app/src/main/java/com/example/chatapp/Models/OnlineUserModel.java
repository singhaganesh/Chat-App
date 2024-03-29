package com.example.chatapp.Models;

import java.io.Serializable;

public class OnlineUserModel implements Serializable {
    private String image,userName;

    public OnlineUserModel(String image, String userName) {
        this.image = image;
        this.userName = userName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
