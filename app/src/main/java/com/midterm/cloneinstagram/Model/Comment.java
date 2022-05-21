package com.midterm.cloneinstagram.Model;

public class Comment {
    private Users users;
    private String comment;

    public Comment(Users users, String comment) {
        this.users = users;
        this.comment = comment;
    }

    public Comment() {
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
