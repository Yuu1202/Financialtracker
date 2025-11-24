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
import java.util.*;
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

        addBotMessage("Halo! Saya asisten keuangan Anda yang lebih canggih. Saya bisa membantu Anda dengan:\n\n" +
                "📊 Analisis keuangan\n" +
                "💡 Saran pengeluaran yang bisa dihindari\n" +
                "📈 Evaluasi pola pengeluaran\n" +
                "💰 Tips menghemat\n" +
                "🎯 Perencanaan keuangan\n\n" +
                "Tanyakan apa saja tentang keuangan Anda!");
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

        showTypingIndicator();

        new Handler().postDelayed(() -> {
            hideTypingIndicator();
            processBotResponse(message.toLowerCase());
        }, 1500);
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

        if (matchesKeywords(userMessage, "saldo", "berapa saldo", "cek saldo")) {
            response = getBalanceResponse();
        } else if (matchesKeywords(userMessage, "hindari", "dihindari", "bisa dikurangi", "pengeluaran berlebih")) {
            response = getExpensesToAvoidResponse();
        } else if (matchesKeywords(userMessage, "evaluasi", "analisis", "pola", "kebiasaan")) {
            response = getEvaluationResponse();
        } else if (matchesKeywords(userMessage, "ringkasan", "laporan", "summary", "statistik")) {
            response = getSummaryResponse();
        } else if (matchesKeywords(userMessage, "tips", "saran", "nasihat", "rekomendasi")) {
            response = getTipsResponse();
        } else if (matchesKeywords(userMessage, "pemasukan", "pendapatan", "penghasilan")) {
            response = getIncomeResponse();
        } else if (matchesKeywords(userMessage, "pengeluaran", "belanja", "pengeluaran", "spending")) {
            response = getExpenseResponse();
        } else if (matchesKeywords(userMessage, "target", "goal", "tujuan", "rencana")) {
            response = getPlanningAdviceResponse();
        } else if (matchesKeywords(userMessage, "halo", "hai", "hi", "hello")) {
            response = "Halo! Selamat datang! Saya siap membantu Anda mengelola keuangan. Ada yang ingin ditanyakan? 😊";
        } else if (matchesKeywords(userMessage, "terima kasih", "thanks", "makasih")) {
            response = "Sama-sama! Senang bisa membantu Anda. Ada pertanyaan lain tentang keuangan Anda?";
        } else {
            response = processNaturalLanguageQuery(userMessage);
        }

        addBotMessage(response);
    }

    private boolean matchesKeywords(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private String processNaturalLanguageQuery(String query) {
        // Proses query yang lebih fleksibel
        if (query.contains("bulan") || query.contains("minggu") || query.contains("hari")) {
            return getTimeBasedAnalysisResponse(query);
        } else if (query.contains("rata") || query.contains("rata-rata")) {
            return getAverageSpendingResponse();
        } else if (query.contains("terbanyak") || query.contains("terbesar")) {
            return getLargestExpenseResponse();
        } else if (query.contains("perbandingan") || query.contains("dibanding")) {
            return getComparisonResponse();
        } else if (query.contains("hemat") || query.contains("menghemat") || query.contains("budaya")) {
            return getSavingTipsResponse();
        } else {
            return getDefaultResponse();
        }
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
        String status = saldo >= 0 ? "sehat ✅" : "negatif ⚠️";

        double persentaseTabungan = totalPemasukan > 0 ?
                ((double)(totalPemasukan - totalPengeluaran) / totalPemasukan * 100) : 0;

        return String.format("💰 Saldo Anda saat ini:\n\n" +
                        "Saldo Bersih: %s (%s)\n" +
                        "Total Pemasukan: %s\n" +
                        "Total Pengeluaran: %s\n" +
                        "Tingkat Penghematan: %.1f%%\n\n" +
                        "%s",
                formatRupiah(saldo),
                status,
                formatRupiah(totalPemasukan),
                formatRupiah(totalPengeluaran),
                persentaseTabungan,
                persentaseTabungan >= 20 ?
                        "Bagus! Anda berhasil menabung lebih dari 20% penghasilan 👍" :
                        "Cobalah tingkatkan tingkat penghematan Anda 💪");
    }

    private String getExpensesToAvoidResponse() {
        List<Transaksi> pengeluaranList = db.getTransaksiByTipe("pengeluaran");

        if (pengeluaranList.isEmpty()) {
            return "Anda belum memiliki data pengeluaran. Mulai catat pengeluaran Anda untuk mendapat saran! 📝";
        }

        // Urutkan pengeluaran dari terbesar
        pengeluaranList.sort((a, b) -> Long.compare(b.getJumlah(), a.getJumlah()));

        StringBuilder response = new StringBuilder("🚨 Pengeluaran yang Bisa Dikurangi:\n\n");

        long totalBesar = 0;
        for (int i = 0; i < Math.min(5, pengeluaranList.size()); i++) {
            Transaksi t = pengeluaranList.get(i);
            response.append(String.format("• %s: %s\n", t.getKeterangan(), formatRupiah(t.getJumlah())));
            totalBesar += t.getJumlah();
        }

        List<Transaksi> allTransaksi = db.getAllTransaksi();
        long totalPengeluaran = 0;
        for (Transaksi t : allTransaksi) {
            if (t.getTipe().equals("pengeluaran")) {
                totalPengeluaran += t.getJumlah();
            }
        }

        double persentase = totalPengeluaran > 0 ?
                (double)totalBesar / totalPengeluaran * 100 : 0;

        response.append(String.format("\n📊 5 pengeluaran terbesar ini = %.1f%% dari total\n\n", persentase));
        response.append("💡 Saran: Coba evaluasi apakah semua item ini benar-benar diperlukan!\n");
        response.append("Fokus pada pengeluaran diskresioner (yang bisa dihindari).");

        return response.toString();
    }

    private String getEvaluationResponse() {
        List<Transaksi> allTransaksi = db.getAllTransaksi();

        if (allTransaksi.isEmpty()) {
            return "Belum ada data untuk dievaluasi. Mulai catat transaksi Anda terlebih dahulu! 📝";
        }

        long totalPemasukan = 0;
        long totalPengeluaran = 0;
        Map<String, Long> categorySpending = new HashMap<>();

        for (Transaksi t : allTransaksi) {
            if (t.getTipe().equals("pemasukan")) {
                totalPemasukan += t.getJumlah();
            } else {
                totalPengeluaran += t.getJumlah();
                String keterangan = t.getKeterangan();
                categorySpending.put(keterangan, categorySpending.getOrDefault(keterangan, 0L) + t.getJumlah());
            }
        }

        StringBuilder response = new StringBuilder("📈 Evaluasi Keuangan Anda:\n\n");

        double savingsRate = totalPemasukan > 0 ?
                ((double)(totalPemasukan - totalPengeluaran) / totalPemasukan * 100) : 0;

        response.append(String.format("✓ Tingkat Penghematan: %.1f%%\n", savingsRate));

        if (savingsRate >= 30) {
            response.append("  Status: Sangat Baik! 🌟\n\n");
        } else if (savingsRate >= 20) {
            response.append("  Status: Baik 👍\n\n");
        } else if (savingsRate >= 10) {
            response.append("  Status: Cukup, butuh perbaikan 📌\n\n");
        } else {
            response.append("  Status: Perlu perhatian serius ⚠️\n\n");
        }

        response.append("📊 Analisis Pengeluaran:\n");
        response.append(String.format("• Total Pengeluaran: %s\n", formatRupiah(totalPengeluaran)));
        response.append(String.format("• Rata-rata/Item: %s\n", formatRupiah(totalPengeluaran / Math.max(1, allTransaksi.size()))));

        // Kategori terbanyak
        if (!categorySpending.isEmpty()) {
            String topCategory = categorySpending.entrySet().stream()
                    .max((a, b) -> Long.compare(a.getValue(), b.getValue()))
                    .map(Map.Entry::getKey)
                    .orElse("");
            response.append(String.format("• Pengeluaran terbanyak: %s\n", topCategory));
        }

        response.append("\n💡 Rekomendasi:\n");
        if (savingsRate < 20) {
            response.append("- Kurangi pengeluaran non-esensial\n");
            response.append("- Buat budget bulanan yang ketat\n");
        }
        response.append("- Pantau pengeluaran secara rutin");

        return response.toString();
    }

    private String getSummaryResponse() {
        List<Transaksi> allTransaksi = db.getAllTransaksi();
        List<Transaksi> pemasukanList = db.getTransaksiByTipe("pemasukan");
        List<Transaksi> pengeluaranList = db.getTransaksiByTipe("pengeluaran");

        long totalPemasukan = 0;
        long totalPengeluaran = 0;

        for (Transaksi t : pemasukanList) totalPemasukan += t.getJumlah();
        for (Transaksi t : pengeluaranList) totalPengeluaran += t.getJumlah();

        return String.format("📊 Ringkasan Keuangan Lengkap:\n\n" +
                        "📈 Pemasukan: %s (%d transaksi)\n" +
                        "📉 Pengeluaran: %s (%d transaksi)\n" +
                        "💰 Saldo Bersih: %s\n" +
                        "📋 Total Transaksi: %d\n\n" +
                        "Lihat detail lebih lanjut di halaman Riwayat! 📱",
                formatRupiah(totalPemasukan),
                pemasukanList.size(),
                formatRupiah(totalPengeluaran),
                pengeluaranList.size(),
                formatRupiah(totalPemasukan - totalPengeluaran),
                allTransaksi.size());
    }

    private String getIncomeResponse() {
        List<Transaksi> pemasukanList = db.getTransaksiByTipe("pemasukan");
        long totalPemasukan = 0;

        for (Transaksi t : pemasukanList) {
            totalPemasukan += t.getJumlah();
        }

        return String.format("💰 Informasi Pemasukan:\n\n" +
                        "Total Pemasukan: %s\n" +
                        "Jumlah Transaksi: %d\n" +
                        "Rata-rata: %s\n\n" +
                        "✨ Pertahankan stabilitas pemasukan Anda!",
                formatRupiah(totalPemasukan),
                pemasukanList.size(),
                formatRupiah(totalPemasukan / Math.max(1, pemasukanList.size())));
    }

    private String getExpenseResponse() {
        List<Transaksi> pengeluaranList = db.getTransaksiByTipe("pengeluaran");
        long totalPengeluaran = 0;

        for (Transaksi t : pengeluaranList) {
            totalPengeluaran += t.getJumlah();
        }

        return String.format("💳 Informasi Pengeluaran:\n\n" +
                        "Total Pengeluaran: %s\n" +
                        "Jumlah Transaksi: %d\n" +
                        "Rata-rata: %s\n\n" +
                        "💪 Jangan lupa kontrol pengeluaran Anda!",
                formatRupiah(totalPengeluaran),
                pengeluaranList.size(),
                formatRupiah(totalPengeluaran / Math.max(1, pengeluaranList.size())));
    }

    private String getTipsResponse() {
        String[] tips = {
                "💡 Sisihkan 10-20% dari penghasilan untuk tabungan darurat.",
                "💡 Terapkan aturan 50/30/20: 50% kebutuhan, 30% keinginan, 20% tabungan.",
                "💡 Hindari hutang konsumtif yang tidak perlu.",
                "💡 Investasi adalah kunci untuk mencapai kebebasan finansial.",
                "💡 Catat setiap pengeluaran, sekecil apapun itu.",
                "💡 Bedakan antara kebutuhan dan keinginan sebelum berbelanja.",
                "💡 Buat budget bulanan dan patuhi setiap kategori pengeluaran.",
                "💡 Kompilasikan resep sebelum berbelanja untuk menghindari pembelian impulsif.",
                "💡 Gunakan cashback dan reward untuk mengurangi pengeluaran efektif."
        };

        int randomIndex = (int) (Math.random() * tips.length);
        return tips[randomIndex];
    }

    private String getSavingTipsResponse() {
        return "💰 Tips Menghemat Uang:\n\n" +
                "1. Buat rencana belanja sebelum pergi ke toko\n" +
                "2. Hindari belanja saat emosi sedang tidak stabil\n" +
                "3. Gunakan public transport untuk hemat bensin\n" +
                "4. Masak di rumah daripada makan di luar\n" +
                "5. Cari promo dan diskon sebelum membeli\n" +
                "6. Bayar tagihan tepat waktu untuk hindari denda\n" +
                "7. Hentikan langganan yang tidak digunakan\n" +
                "8. Ajak teman untuk berbagi biaya (ride sharing, dll)\n\n" +
                "Mulai dari tips yang paling mudah untuk diterapkan! 🚀";
    }

    private String getAverageSpendingResponse() {
        List<Transaksi> pengeluaranList = db.getTransaksiByTipe("pengeluaran");

        if (pengeluaranList.isEmpty()) {
            return "Belum ada data pengeluaran. Mulai catat untuk mendapat analisis!";
        }

        long totalPengeluaran = 0;
        for (Transaksi t : pengeluaranList) {
            totalPengeluaran += t.getJumlah();
        }

        long rataRata = totalPengeluaran / pengeluaranList.size();

        return String.format("📊 Analisis Rata-rata Pengeluaran:\n\n" +
                        "Total Pengeluaran: %s\n" +
                        "Jumlah Item: %d\n" +
                        "Rata-rata per Item: %s\n\n" +
                        "💡 Monitor apakah ada pengeluaran yang di atas rata-rata!",
                formatRupiah(totalPengeluaran),
                pengeluaranList.size(),
                formatRupiah(rataRata));
    }

    private String getLargestExpenseResponse() {
        List<Transaksi> pengeluaranList = db.getTransaksiByTipe("pengeluaran");

        if (pengeluaranList.isEmpty()) {
            return "Belum ada data pengeluaran.";
        }

        pengeluaranList.sort((a, b) -> Long.compare(b.getJumlah(), a.getJumlah()));

        StringBuilder response = new StringBuilder("🔴 Pengeluaran Terbesar:\n\n");
        for (int i = 0; i < Math.min(3, pengeluaranList.size()); i++) {
            response.append(String.format("%d. %s: %s (%s)\n",
                    i + 1,
                    pengeluaranList.get(i).getKeterangan(),
                    formatRupiah(pengeluaranList.get(i).getJumlah()),
                    pengeluaranList.get(i).getTanggal()));
        }

        return response.toString();
    }

    private String getComparisonResponse() {
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

        double perbandingan = totalPemasukan > 0 ?
                ((double)totalPengeluaran / totalPemasukan * 100) : 0;

        return String.format("⚖️ Perbandingan Pemasukan vs Pengeluaran:\n\n" +
                        "Pemasukan: %s (100%%)\n" +
                        "Pengeluaran: %s (%.1f%%)\n" +
                        "Sisa: %s (%.1f%%)\n\n" +
                        "%s",
                formatRupiah(totalPemasukan),
                formatRupiah(totalPengeluaran),
                perbandingan,
                formatRupiah(totalPemasukan - totalPengeluaran),
                100 - perbandingan,
                perbandingan <= 80 ?
                        "✅ Pengeluaran Anda terkontrol dengan baik!" :
                        "⚠️ Pengeluaran mendekati atau melebihi pemasukan!");
    }

    private String getPlanningAdviceResponse() {
        return "🎯 Panduan Perencanaan Keuangan:\n\n" +
                "📌 Target Jangka Pendek (1-3 bulan):\n" +
                "- Bayar hutang mendesak\n" +
                "- Bangun dana darurat\n\n" +
                "📌 Target Jangka Menengah (6-12 bulan):\n" +
                "- Tabungan untuk liburan atau pembelian\n" +
                "- Investasi kecil\n\n" +
                "📌 Target Jangka Panjang (>1 tahun):\n" +
                "- Investasi properti atau aset\n" +
                "- Dana pensiun\n" +
                "- Asuransi kesehatan\n\n" +
                "💡 Mulai dari yang paling mendesak terlebih dahulu!";
    }

    private String getTimeBasedAnalysisResponse(String query) {
        return "📅 Analisis Waktu Periodik:\n\n" +
                "Fitur analisis berdasarkan periode waktu masih dalam pengembangan.\n" +
                "Untuk saat ini, silakan lihat riwayat transaksi di halaman Riwayat\n" +
                "dan gunakan fitur kalender untuk melihat transaksi per hari/periode!\n\n" +
                "💡 Anda bisa filter transaksi berdasarkan tanggal di halaman Riwayat 📱";
    }

    private String getDefaultResponse() {
        return "Saya tidak sepenuhnya memahami pertanyaan Anda. 🤔\n\n" +
                "Coba tanyakan tentang:\n" +
                "• Saldo dan ringkasan keuangan\n" +
                "• Pengeluaran apa yang bisa dihindari\n" +
                "• Evaluasi dan analisis pola pengeluaran\n" +
                "• Tips menghemat dan saran keuangan\n" +
                "• Perencanaan keuangan jangka panjang\n\n" +
                "Atau jelaskan dengan lebih detail! 😊";
    }

    private String formatRupiah(long amount) {
        return String.format(Locale.US, "Rp %,d", amount).replace(",", ".");
    }

    private String getCurrentTime() {
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
    }
}