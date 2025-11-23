package com.example.financialtracker;
import retrofit2.Callback;
import retrofit2.Call;
import retrofit2.Response;

public class ChatbotRepository {

    private AiApiService api;

    public ChatbotRepository() {
        api = ApiClient.getClient().create(AiApiService.class);
    }

    public void askAI(String userMessage, Callback<AiGenerateResponse> callback) {
        AiGenerateRequest req = new AiGenerateRequest("llama3.2", userMessage);
        api.generate(req).enqueue(callback);
    }
}


