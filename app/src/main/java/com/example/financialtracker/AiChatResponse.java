package com.example.financialtracker;

public class AiChatResponse {
    private String model;
    private String created_at;
    public Message message;
    private boolean done;

    public static class Message {
        private String role;
        public String content;

        public String getRole() {
            return role;
        }

        public String getContent() {
            return content;
        }
    }

    public Message getMessage() {
        return message;
    }

    public boolean isDone() {
        return done;
    }

    public String getModel() {
        return model;
    }
}