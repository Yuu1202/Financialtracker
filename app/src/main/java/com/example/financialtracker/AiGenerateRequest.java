package com.example.financialtracker;

public class AiGenerateRequest {
    public String model;
    public String prompt;

    public AiGenerateRequest(String model, String prompt) {
        this.model = model;
        this.prompt = prompt;
    }
}

