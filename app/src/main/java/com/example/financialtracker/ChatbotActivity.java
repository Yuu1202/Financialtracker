package com.example.financialtracker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatbotActivity extends AppCompatActivity {
    private RecyclerView rvChat;
    private EditText etMessage;
    private ImageButton btnSend;
    private BottomNavigationView bottomNav;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messageList;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        db = new DatabaseHelper(this);
        initViews();
        setupRecyclerView();
        setupBottomNavigation();
        setupClickListeners();

        // Welcome message
        addBotMessage("Halo! Saya asisten keuangan Anda. Saya bisa membantu Anda:\n\n" +
                "• Cek saldo\n" +
                "• Ringkasan transaksi\n" +
                "• Tips keuangan\n\n" +
                "Ada yang bisa saya bantu?");
    }

    private void initViews() {
        rvChat = findViewById(R.id.rvChat);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        bottomNav = findViewById(R.id.bottomNavigation);
    }

    private void setupRecyclerView() {
        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList);
        rvChat.setLayoutManager(new LinearLayoutManager(this));
        rvChat.setAdapter(chatAdapter);
    }

    private void setupBottomNavigation() {
        bottomNav.setSelectedItemId(R.id.nav_chatbot);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(ChatbotActivity.this, MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_chatbot) {
                return true;
            } else if (id == R.id.nav_riwayat) {
                startActivity(new Intent(ChatbotActivity.this, RiwayatActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
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

        // Simulate bot response after delay
        new Handler().postDelayed(() -> {
            hideTypingIndicator();
            processBotResponse(message.toLowerCase());
        }, 1500);
    }

    private void addUserMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(
                message,
                getCurrentTime(),
                true
        );
        messageList.add(chatMessage);
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        rvChat.smoothScrollToPosition(messageList.size() - 1);
    }

    private void addBotMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(
                message,
                getCurrentTime(),
                false
        );
        messageList.add(chatMessage);
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        rvChat.smoothScrollToPosition(messageList.size() - 1);
    }

    private void showTypingIndicator() {
        ChatMessage typing = new ChatMessage("", "", false);
        typing.setTyping(true);
        messageList.add(typing);
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        rvChat.smoothScrollToPosition(messageList.size() - 1);
    }

    private void hideTypingIndicator() {
        if (!messageList.isEmpty() && messageList.get(messageList.size() - 1).isTyping()) {
            messageList.remove(messageList.size() - 1);
            chatAdapter.notifyItemRemoved(messageList.size());
        }
    }

    private void processBotResponse(String userMessage) {
        String response;

        if (userMessage.contains("saldo") || userMessage.contains("uang")) {
            response = getBalanceResponse();
        } else if (userMessage.contains("ringkasan") || userMessage.contains("laporan")) {
            response = getSummaryResponse();
        } else if (userMessage.contains("tips") || userMessage.contains("saran")) {
            response = getTipsResponse();
        } else if (userMessage.contains("halo") || userMessage.contains("hai") || userMessage.contains("hi")) {
            response = "Halo! Ada yang bisa saya bantu dengan keuangan Anda?";
        } else if (userMessage.contains("terima kasih") || userMessage.contains("thanks")) {
            response = "Sama-sama! Senang bisa membantu. Ada yang lain yang ingin ditanyakan?";
        } else if (userMessage.contains("pemasukan") || userMessage.contains("pendapatan")) {
            response = getIncomeResponse();
        } else if (userMessage.contains("pengeluaran") || userMessage.contains("belanja")) {
            response = getExpenseResponse();
        } else {
            response = "Maaf, saya belum mengerti pertanyaan Anda. Coba tanyakan tentang:\n\n" +
                    "• Saldo saya\n" +
                    "• Ringkasan transaksi\n" +
                    "• Tips keuangan";
        }

        addBotMessage(response);
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

        return String.format("Saldo Anda saat ini adalah %s (%s)\n\n" +
                        "Total Pemasukan: %s\n" +
                        "Total Pengeluaran: %s",
                formatRupiah(saldo),
                status,
                formatRupiah(totalPemasukan),
                formatRupiah(totalPengeluaran));
    }

    private String getSummaryResponse() {
        List<Transaksi> allTransaksi = db.getAllTransaksi();
        int totalTransaksi = allTransaksi.size();

        List<Transaksi> pemasukanList = db.getTransaksiByTipe("pemasukan");
        List<Transaksi> pengeluaranList = db.getTransaksiByTipe("pengeluaran");

        return String.format("📊 Ringkasan Keuangan:\n\n" +
                        "Total Transaksi: %d\n" +
                        "Jumlah Pemasukan: %d transaksi\n" +
                        "Jumlah Pengeluaran: %d transaksi\n\n" +
                        "Lihat detail lengkap di halaman Riwayat!",
                totalTransaksi,
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
                        "Total Pemasukan: %s\n" +
                        "Jumlah Transaksi: %d\n\n" +
                        "Pertahankan pemasukan yang stabil!",
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
                        "Total Pengeluaran: %s\n" +
                        "Jumlah Transaksi: %d\n\n" +
                        "Jangan lupa kontrol pengeluaran Anda!",
                formatRupiah(totalPengeluaran),
                pengeluaranList.size());
    }

    private String getTipsResponse() {
        String[] tips = {
                "💡 Tips: Sisihkan 10-20% dari penghasilan untuk tabungan darurat.",
                "💡 Tips: Buat budget bulanan dan patuhi setiap kategori pengeluaran.",
                "💡 Tips: Hindari hutang konsumtif yang tidak perlu.",
                "💡 Tips: Investasi adalah kunci untuk mencapai kebebasan finansial.",
                "💡 Tips: Catat setiap pengeluaran, sekecil apapun itu.",
                "💡 Tips: Bedakan antara kebutuhan dan keinginan sebelum berbelanja."
        };

        int randomIndex = (int) (Math.random() * tips.length);
        return tips[randomIndex];
    }

    private String formatRupiah(long amount) {
        return String.format(Locale.US, "Rp %,d", amount).replace(",", ".");
    }

    private String getCurrentTime() {
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
    }
}