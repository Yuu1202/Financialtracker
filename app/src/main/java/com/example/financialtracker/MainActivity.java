package com.example.financialtracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
public class MainActivity extends AppCompatActivity {
    private TextView tvTotalSaldo, tvPemasukanAmount, tvPengeluaranAmount;
    private CardView btnPemasukan, btnPengeluaran;
    private RecyclerView rvTransaksi;
    private TransaksiAdapter adapter;
    private BottomNavigationView bottomNav;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);
        initViews();
        setupBottomNavigation();
        loadData();
        setupClickListeners();
    }

    private void initViews() {
        tvTotalSaldo = findViewById(R.id.tvTotalSaldo);
        tvPemasukanAmount = findViewById(R.id.tvPemasukanAmount);
        tvPengeluaranAmount = findViewById(R.id.tvPengeluaranAmount);
        btnPemasukan = findViewById(R.id.btnPemasukan);
        btnPengeluaran = findViewById(R.id.btnPengeluaran);
        rvTransaksi = findViewById(R.id.rvTransaksi);
        bottomNav = findViewById(R.id.bottomNavigation);

        rvTransaksi.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TransaksiAdapter(new ArrayList<>(), false);
        rvTransaksi.setAdapter(adapter);
    }

    private void setupBottomNavigation() {
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_chatbot) {
                // Chatbot belum tersedia
                Toast.makeText(this, "Fitur Chatbot segera hadir!", Toast.LENGTH_SHORT).show();
                return false;
            } else if (id == R.id.nav_riwayat) {
                startActivity(new Intent(MainActivity.this, RiwayatActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }

    private void setupClickListeners() {
        btnPemasukan.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TambahTransaksiActivity.class);
            intent.putExtra("TIPE", "pemasukan");
            startActivity(intent);
        });

        btnPengeluaran.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TambahTransaksiActivity.class);
            intent.putExtra("TIPE", "pengeluaran");
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
        bottomNav.setSelectedItemId(R.id.nav_home);
    }

    private void loadData() {
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

        tvTotalSaldo.setText(formatRupiah(saldo));
        tvPemasukanAmount.setText(formatRupiah(totalPemasukan));
        tvPengeluaranAmount.setText(formatRupiah(totalPengeluaran));

        // Tampilkan 2 transaksi terbaru
        List<Transaksi> recentTransaksi = allTransaksi.size() > 2 ?
                allTransaksi.subList(0, 2) : allTransaksi;
        adapter.updateData(recentTransaksi);
    }

    private String formatRupiah(long amount) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatter.applyPattern("#,###");
        return "Rp " + formatter.format(amount).replace(",", ".");
    }
}