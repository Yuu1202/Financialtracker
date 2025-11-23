package com.example.financialtracker;

public class AiRequest {
    public String model;
    public AiMessage messages[];

    public AiRequest(String model, String userMessage) {
        this.model = model;
        this.messages = new AiMessage[]{
                new AiMessage("user", userMessage)
        };
    }
}
