package com.midterm.cloneinstagram.comment;

public class userCmt {
    String Name;
    String Content;

    public userCmt(String name, String content) {
        Name = name;
        Content = content;
    }
    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }
}
