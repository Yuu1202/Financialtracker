package com.example.financialtracker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatbotActivity extends AppCompatActivity {
    private static final String TAG = "ChatbotActivity";
    private RecyclerView rvChat;
    private EditText etMessage;
    private ImageButton btnSend;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messageList;
    private DatabaseHelper db;
    private AiRepository aiRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        db = new DatabaseHelper(this);
        aiRepo = new AiRepository();

        initViews();
        setupRecyclerView();
        setupClickListeners();

        // Welcome message
        addBotMessage("Halo! Saya asisten keuangan Anda. 😊\n\n" +
                "Saya bisa membantu:\n" +
                "• Cek saldo & transaksi Anda\n" +
                "• Tips mengelola keuangan\n" +
                "• Saran investasi & menabung\n\n" +
                "Ada yang bisa saya bantu?");
    }

    private void initViews() {
        rvChat = findViewById(R.id.rvChat);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
    }

    private void setupRecyclerView() {
        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList);
        rvChat.setLayoutManager(new LinearLayoutManager(this));
        rvChat.setAdapter(chatAdapter);
    }

    private void setupClickListeners() {
        btnSend.setOnClickListener(v -> sendMessage());
        etMessage.setOnEditorActionListener((v, actionId, event) -> {
            sendMessage();
            return true;
        });
    }

    private void sendMessage() {
        String message = etMessage.getText().toString().trim();
        if (message.isEmpty()) return;

        addUserMessage(message);
        etMessage.setText("");

        // Show typing indicator
        showTypingIndicator();

        String lowerMessage = message.toLowerCase();

        // Cek apakah pertanyaan tentang data lokal
        if (isLocalDataQuery(lowerMessage)) {
            // Gunakan rule-based untuk data lokal (cepat & akurat)
            new Handler().postDelayed(() -> {
                hideTypingIndicator();
                processLocalDataResponse(lowerMessage);
            }, 800);
        } else if (isSimpleGreeting(lowerMessage)) {
            // Response cepat untuk greeting
            new Handler().postDelayed(() -> {
                hideTypingIndicator();
                processGreetingResponse(lowerMessage);
            }, 500);
        } else {
            // Gunakan AI untuk pertanyaan kompleks dengan context
            sendToAI(message);
        }
    }

    private boolean isLocalDataQuery(String message) {
        return message.contains("saldo") ||
                message.contains("uang") ||
                message.contains("ringkasan") ||
                message.contains("laporan") ||
                message.contains("pemasukan") ||
                message.contains("pendapatan") ||
                message.contains("pengeluaran") ||
                message.contains("belanja") ||
                message.contains("transaksi");
    }

    private boolean isSimpleGreeting(String message) {
        return message.contains("halo") ||
                message.contains("hai") ||
                message.contains("hi") ||
                message.contains("terima kasih") ||
                message.contains("thanks") ||
                message.contains("bye") ||
                message.contains("selamat");
    }

    private void sendToAI(String message) {
        Log.d(TAG, "Sending to AI: " + message);

        // Buat context dari data keuangan user
        String context = buildFinancialContext();
        String fullPrompt = context + "\n\nPertanyaan: " + message +
                "\n\nJawab dengan singkat, praktis, dan ramah dalam bahasa Indonesia (maks 3 kalimat).";

        aiRepo.askAI(fullPrompt).enqueue(new Callback<AiChatResponse>() {
            @Override
            public void onResponse(Call<AiChatResponse> call, Response<AiChatResponse> response) {
                hideTypingIndicator();

                if (response.isSuccessful() && response.body() != null) {
                    AiChatResponse body = response.body();

                    if (body.getMessage() != null && body.getMessage().getContent() != null) {
                        String aiReply = body.getMessage().getContent();
                        Log.d(TAG, "AI Reply: " + aiReply);

                        // Simpan response ke history
                        aiRepo.addAssistantResponse(aiReply);

                        addBotMessage(aiReply);
                    } else {
                        addBotMessage("Maaf, saya tidak bisa memproses pertanyaan Anda saat ini. 😅");
                    }
                } else {
                    Log.e(TAG, "AI Error: " + response.code());
                    // Fallback ke response default
                    addBotMessage("Maaf, saya sedang kesulitan memahami. Coba tanyakan tentang saldo, ringkasan, atau tips keuangan. 😊");
                }
            }

            @Override
            public void onFailure(Call<AiChatResponse> call, Throwable t) {
                Log.e(TAG, "AI Request failed", t);
                hideTypingIndicator();
                addBotMessage("Koneksi AI sedang bermasalah. Tapi saya masih bisa bantu cek saldo dan transaksi Anda! 💪");
            }
        });
    }

    private String buildFinancialContext() {
        List<Transaksi> allTransaksi = db.getAllTransaksi();
        long totalPemasukan = 0;
        long totalPengeluaran = 0;

        for (Transaksi t : allTransaksi) {
            if (t.getTipe().equals("pemasukan")) {
                totalPemasukan += t.getJumlah();
            } else {
                totalPengeluaran += t.getJumlah();
            }
        }

        long saldo = totalPemasukan - totalPengeluaran;

        return "Context: User memiliki:\n" +
                "- Saldo: " + formatRupiah(saldo) + "\n" +
                "- Total Pemasukan: " + formatRupiah(totalPemasukan) + "\n" +
                "- Total Pengeluaran: " + formatRupiah(totalPengeluaran) + "\n" +
                "- Total Transaksi: " + allTransaksi.size();
    }

    private void processLocalDataResponse(String userMessage) {
        String response;

        if (userMessage.contains("saldo") || userMessage.contains("uang")) {
            response = getBalanceResponse();
        } else if (userMessage.contains("ringkasan") || userMessage.contains("laporan")) {
            response = getSummaryResponse();
        } else if (userMessage.contains("pemasukan") || userMessage.contains("pendapatan")) {
            response = getIncomeResponse();
        } else if (userMessage.contains("pengeluaran") || userMessage.contains("belanja")) {
            response = getExpenseResponse();
        } else {
            response = "Saya bisa bantu cek saldo, ringkasan transaksi, atau detail pemasukan/pengeluaran Anda. Mau yang mana? 😊";
        }

        addBotMessage(response);
    }

    private void processGreetingResponse(String userMessage) {
        String response;

        if (userMessage.contains("halo") || userMessage.contains("hai") || userMessage.contains("hi")) {
            response = "Halo! Ada yang bisa saya bantu dengan keuangan Anda? 😊";
        } else if (userMessage.contains("terima kasih") || userMessage.contains("thanks")) {
            response = "Sama-sama! Senang bisa membantu. Ada lagi yang ingin ditanyakan? 😊";
        } else if (userMessage.contains("bye") || userMessage.contains("dadah")) {
            response = "Sampai jumpa! Jaga keuangan Anda dengan baik ya! 👋";
        } else {
            response = "Selamat! Semoga harimu menyenangkan! 😊";
        }

        addBotMessage(response);
    }

    private void addUserMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, getCurrentTime(), true);
        messageList.add(chatMessage);
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        rvChat.smoothScrollToPosition(messageList.size() - 1);
    }

    private void addBotMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, getCurrentTime(), false);
        messageList.add(chatMessage);
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        rvChat.smoothScrollToPosition(messageList.size() - 1);
    }

    private void showTypingIndicator() {
        ChatMessage typing = new ChatMessage();
        typing.setTyping(true);
        messageList.add(typing);
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        rvChat.smoothScrollToPosition(messageList.size() - 1);
    }

    private void hideTypingIndicator() {
        runOnUiThread(() -> {
            if (!messageList.isEmpty() && messageList.get(messageList.size() - 1).isTyping()) {
                messageList.remove(messageList.size() - 1);
                chatAdapter.notifyItemRemoved(messageList.size());
            }
        });
    }

    private String getBalanceResponse() {
        List<Transaksi> allTransaksi = db.getAllTransaksi();
        long totalPemasukan = 0;
        long totalPengeluaran = 0;

        for (Transaksi t : allTransaksi) {
            if (t.getTipe().equals("pemasukan")) {
                totalPemasukan += t.getJumlah();
            } else {
                totalPengeluaran += t.getJumlah();
            }
        }

        long saldo = totalPemasukan - totalPengeluaran;
        String status = saldo >= 0 ? "positif 👍" : "negatif ⚠️";

        return String.format("💰 Saldo Anda saat ini:\n\n" +
                        "Saldo: %s (%s)\n" +
                        "Total Pemasukan: %s\n" +
                        "Total Pengeluaran: %s",
                formatRupiah(saldo), status,
                formatRupiah(totalPemasukan),
                formatRupiah(totalPengeluaran));
    }

    private String getSummaryResponse() {
        List<Transaksi> allTransaksi = db.getAllTransaksi();
        List<Transaksi> pemasukanList = db.getTransaksiByTipe("pemasukan");
        List<Transaksi> pengeluaranList = db.getTransaksiByTipe("pengeluaran");

        return String.format("📊 Ringkasan Keuangan:\n\n" +
                        "Total Transaksi: %d\n" +
                        "Pemasukan: %d transaksi\n" +
                        "Pengeluaran: %d transaksi\n\n" +
                        "Lihat detail di halaman Riwayat! 📝",
                allTransaksi.size(),
                pemasukanList.size(),
                pengeluaranList.size());
    }

    private String getIncomeResponse() {
        List<Transaksi> pemasukanList = db.getTransaksiByTipe("pemasukan");
        long totalPemasukan = 0;

        for (Transaksi t : pemasukanList) {
            totalPemasukan += t.getJumlah();
        }

        return String.format("💰 Informasi Pemasukan:\n\n" +
                        "Total: %s\n" +
                        "Jumlah Transaksi: %d\n\n" +
                        "Pertahankan pemasukan yang stabil! 💪",
                formatRupiah(totalPemasukan),
                pemasukanList.size());
    }

    private String getExpenseResponse() {
        List<Transaksi> pengeluaranList = db.getTransaksiByTipe("pengeluaran");
        long totalPengeluaran = 0;

        for (Transaksi t : pengeluaranList) {
            totalPengeluaran += t.getJumlah();
        }

        return String.format("💳 Informasi Pengeluaran:\n\n" +
                        "Total: %s\n" +
                        "Jumlah Transaksi: %d\n\n" +
                        "Kontrol pengeluaran dengan bijak! 😊",
                formatRupiah(totalPengeluaran),
                pengeluaranList.size());
    }

    private String formatRupiah(long amount) {
        return String.format(Locale.US, "Rp %,d", amount).replace(",", ".");
    }

    private String getCurrentTime() {
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
    }
}