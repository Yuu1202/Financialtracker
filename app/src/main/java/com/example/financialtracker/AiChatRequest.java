package com.example.financialtracker;

import java.util.List;

public class AiChatRequest {
    private String model;
    private List<Message> messages;
    private boolean stream; // tambahkan ini

    public AiChatRequest(String model, List<Message> messages) {
        this.model = model;
        this.messages = messages;
        this.stream = false; // non-streaming
    }

    public static class Message {
        private String role;
        private String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public String getContent() {
            return content;
        }
    }

    // Getters
    public String getModel() {
        return model;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public boolean isStream() {
        return stream;
    }
}