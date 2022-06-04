package com.midterm.cloneinstagram.Model;

public class Messages {
    String messages;
    String senderID;
    String UriImg;
    String UriVid;
    String timeStamp;

    public Messages() {
    }

    public Messages(String messages, String senderID, String uriImg, String uriVid, String timeStamp) {
        this.messages = messages;
        this.senderID = senderID;
        UriImg = uriImg;
        UriVid = uriVid;
        this.timeStamp = timeStamp;
    }
    public Messages(Messages messages) {
        this.messages = messages.messages;
        this.senderID = messages.senderID;
        UriImg = messages.UriImg;
        UriVid = messages.UriVid;
        this.timeStamp = messages.timeStamp;
    }

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getUriImg() {
        return UriImg;
    }

    public void setUriImg(String uriImg) {
        UriImg = uriImg;
    }

    public String getUriVid() {
        return UriVid;
    }

    public void setUriVid(String uriVid) {
        UriVid = uriVid;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
