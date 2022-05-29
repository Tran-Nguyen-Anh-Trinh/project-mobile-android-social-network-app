package com.midterm.cloneinstagram.Model;

public class Notification {
    String idUser;
    String type;
    String date;
    String content;
    String idNotify;
    String idPost;
    String idPostLike;

    public Notification(String idUser, String type, String date, String content, String idNotify) {
        this.idUser = idUser;
        this.type = type;
        this.date = date;
        this.content = content;
        this.idNotify = idNotify;
    }

    public Notification(String idUser, String type, String date, String content, String idNotify, String idPost) {
        this.idUser = idUser;
        this.type = type;
        this.date = date;
        this.content = content;
        this.idNotify = idNotify;
        this.idPost = idPost;
    }

    public String getIdPostLike() {
        return idPostLike;
    }

    public void setIdPostLike(String idPostLike) {
        this.idPostLike = idPostLike;
    }

    public Notification() {
    }

    public String getIdPost() {
        return idPost;
    }

    public void setIdPost(String idPost) {
        this.idPost = idPost;
    }

    public String getIdNotify() {
        return idNotify;
    }

    public void setIdNotify(String idNotify) {
        this.idNotify = idNotify;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
