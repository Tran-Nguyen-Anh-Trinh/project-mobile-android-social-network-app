package com.midterm.cloneinstagram.Model;

import java.util.ArrayList;
import java.util.List;

public class Users {
    String uid;
    String name;
    String email;
    String imageUri;
    String status;
     List<String> following = new ArrayList<>();
     List<String > follower = new ArrayList<>();

    public Users(){

    }

    private static Users instance;
    public static Users getInstance(){
        if(instance==null){
            instance = new Users();
        }
        return instance;
    }

    public void setFollowing(List<String> following) {
        this.following.clear();
        this.following.addAll(following);
    }

    public void setFollower(List<String> follower) {
        this.follower.clear();
        this.follower.addAll(follower);
    }

    public List<String> getFollowing() {
        return following;
    }
    public void setOneFollower(String follower) {
        this.follower.add(follower);
    }
    public void setOneFollowing(String following) {
        this.following.add(following);
    }


    public List<String> getFollower() {
        return follower;
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
}
