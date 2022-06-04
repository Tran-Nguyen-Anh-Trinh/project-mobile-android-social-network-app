package com.midterm.cloneinstagram.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Users implements Serializable {
    String uid;
    String name;
    String email;
    String imageUri;
    String status;
    ManagerFollow follower;
    ManagerFollow following;
    String token;

    public Users(){

    }

    private static Users instance;
    public static Users getInstance(){
        if(instance==null){
            instance = new Users();
        }
        return instance;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ManagerFollow getFollower() {
        return follower;
    }

    public void setFollower(ManagerFollow follower) {
        this.follower = follower;
    }

    public ManagerFollow getFollowing() {
        return following;
    }

    public void setFollowing(ManagerFollow following) {
        this.following = following;
    }

    public static void setInstance(Users instance) {
        Users.instance = instance;
    }

    public Users(String uid, String name, String email, String imageUri, String status) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.imageUri = imageUri;
        this.status = status;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Users{" +
                "uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", imageUri='" + imageUri + '\'' +
                ", status='" + status + '\'' +
                ", follower=" + follower +
                ", following=" + following +
                ", token='" + token + '\'' +
                '}';
    }
}
