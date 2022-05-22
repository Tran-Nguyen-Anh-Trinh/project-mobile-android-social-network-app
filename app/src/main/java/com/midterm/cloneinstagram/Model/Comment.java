package com.midterm.cloneinstagram.Model;

public class Comment {
    private String id;
    private Users users;
    private String comment;

    public Comment(String id, Users users, String comment) {
        this.id = id;
        this.users = users;
        this.comment = comment;
    }

    public Comment() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
