package com.example.financialtracker;

import java.util.List;

public class AiResponse {
    public List<Choice> choices;

    public static class Choice {
        public AiMessage message;
    }
}
