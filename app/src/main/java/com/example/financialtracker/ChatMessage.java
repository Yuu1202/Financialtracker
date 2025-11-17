package com.example.financialtracker;

public class ChatMessage {
    private String message;
    private String time;
    private boolean isUser;
    private boolean isTyping;

    public ChatMessage(String message, String time, boolean isUser) {
        this.message = message;
        this.time = time;
        this.isUser = isUser;
        this.isTyping = false;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isUser() {
        return isUser;
    }

    public void setUser(boolean user) {
        isUser = user;
    }

    public boolean isTyping() {
        return isTyping;
    }

    public void setTyping(boolean typing) {
        isTyping = typing;
    }
}