package com.example.financialtracker;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import android.util.Log;

public class AiRepository {
    private final AiApiService api;
    private static final String TAG = "AiRepository";
    private List<AiChatRequest.Message> conversationHistory;

    public AiRepository() {
        api = ApiClient.getClient().create(AiApiService.class);
        conversationHistory = new ArrayList<>();

        // System prompt yang lebih spesifik
        conversationHistory.add(new AiChatRequest.Message("system",
                "Kamu adalah asisten keuangan pribadi yang bernama 'Asisten Keuangan'. " +
                        "PENTING: Jawab HANYA dalam bahasa Indonesia. " +
                        "Berikan jawaban yang SINGKAT dan PADAT (maksimal 3-4 kalimat). " +
                        "Gunakan emoji yang relevan. " +
                        "Fokus pada saran praktis untuk pengelolaan keuangan pribadi."));
    }

    public Call<AiChatResponse> askAI(String userMessage) {
        // Tambahkan pesan user ke history
        conversationHistory.add(new AiChatRequest.Message("user", userMessage));

        // Gunakan model yang lebih kecil untuk response lebih cepat
        String model = "qwen2.5:14b"; // atau "qwen2.5:1.5b"

        Log.d(TAG, "Sending to model: " + model);
        Log.d(TAG, "Message: " + userMessage);
        Log.d(TAG, "History size: " + conversationHistory.size());

        AiChatRequest req = new AiChatRequest(model, new ArrayList<>(conversationHistory));
        return api.chat(req);
    }

    public void addAssistantResponse(String response) {
        // Simpan response AI ke history
        conversationHistory.add(new AiChatRequest.Message("assistant", response));

        // Batasi history agar tidak terlalu panjang (max 10 messages)
        if (conversationHistory.size() > 11) { // 1 system + 10 messages
            // Hapus pesan lama tapi keep system prompt
            conversationHistory.subList(1, 3).clear();
        }
    }

    public void clearHistory() {
        conversationHistory.clear();
        // Re-add system prompt
        conversationHistory.add(new AiChatRequest.Message("system",
                "Kamu adalah asisten keuangan pribadi yang bernama 'Asisten Keuangan'. " +
                        "PENTING: Jawab HANYA dalam bahasa Indonesia. " +
                        "Berikan jawaban yang SINGKAT dan PADAT (maksimal 3-4 kalimat). " +
                        "Gunakan emoji yang relevan. " +
                        "Fokus pada saran praktis untuk pengelolaan keuangan pribadi."));
    }
}