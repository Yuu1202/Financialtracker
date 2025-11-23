package com.example.financialtracker;

public class ChatMessage {
    private String text;
    private String time;
    private int type; // 0 = bot, 1 = user
    private boolean isTyping;

    // Constructor dengan int type (untuk compatibility dengan code lama)
    public ChatMessage(String text, int type) {
        this.text = text;
        this.type = type;
        this.time = getCurrentTime();
        this.isTyping = false;
    }

    // Constructor dengan boolean isUser (untuk code baru)
    public ChatMessage(String text, String time, boolean isUser) {
        this.text = text;
        this.time = time;
        this.type = isUser ? 1 : 0;
        this.isTyping = false;
    }

    // Constructor kosong (untuk typing indicator)
    public ChatMessage() {
        this.text = "";
        this.time = "";
        this.type = 0;
        this.isTyping = false;
    }

    // Getters
    public String getText() {
        return text;
    }

    public String getTime() {
        return time;
    }

    public int getType() {
        return type;
    }

    public boolean isUser() {
        return type == 1;
    }

    public boolean isTyping() {
        return isTyping;
    }

    // Setters
    public void setText(String text) {
        this.text = text;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setTyping(boolean typing) {
        isTyping = typing;
    }

    // Helper method
    private String getCurrentTime() {
        return new java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                .format(new java.util.Date());
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "text='" + text + '\'' +
                ", time='" + time + '\'' +
                ", type=" + type +
                ", isTyping=" + isTyping +
                '}';
    }
}