package com.example.campusmart.vo;

import java.util.Date;

public class RecentChatVo {
    private String otherNickname;
    private String otherID;
    private String otherAvatarURL;
    private String lastestMessage;
    private Date lastestMessageTime;

    public String getLastestMessage() {
        return lastestMessage;
    }

    public void setLastestMessage(String lastestMessage) {
        this.lastestMessage = lastestMessage;
    }

    public String getOtherNickname() {
        return otherNickname;
    }

    public void setOtherNickname(String otherNickname) {
        this.otherNickname = otherNickname;
    }

    public String getOtherID() {
        return otherID;
    }

    public void setOtherID(String otherID) {
        this.otherID = otherID;
    }

    public String getOtherAvatarURL() {
        return otherAvatarURL;
    }

    public void setOtherAvatarURL(String otherAvatarURL) {
        this.otherAvatarURL = otherAvatarURL;
    }

    public Date getLastestMessageTime() {
        return lastestMessageTime;
    }

    public void setLastestMessageTime(Date lastestMessageTime) {
        this.lastestMessageTime = lastestMessageTime;
    }
}
