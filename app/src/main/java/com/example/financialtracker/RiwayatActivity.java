package com.example.financialtracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RiwayatActivity extends AppCompatActivity {
    private TextView tvTotalPemasukan, tvTotalPengeluaran, tvTotalTransaksi;
    private TabLayout tabLayout;
    private RecyclerView rvRiwayat;
    private TransaksiAdapter adapter;
    private BottomNavigationView bottomNav;
    private DatabaseHelper db;
    private String currentFilter = "semua";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat);

        db = new DatabaseHelper(this);
        initViews();
        setupBottomNavigation();
        setupTabs();
        loadData();
    }

    private void initViews() {
        tvTotalPemasukan = findViewById(R.id.tvTotalPemasukan);
        tvTotalPengeluaran = findViewById(R.id.tvTotalPengeluaran);
        tvTotalTransaksi = findViewById(R.id.tvTotalTransaksi);
        tabLayout = findViewById(R.id.tabLayout);
        rvRiwayat = findViewById(R.id.rvRiwayat);
        bottomNav = findViewById(R.id.bottomNavigation);

        rvRiwayat.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TransaksiAdapter(new ArrayList<>(), true);
        rvRiwayat.setAdapter(adapter);
    }

    private void setupBottomNavigation() {
        bottomNav.setSelectedItemId(R.id.nav_riwayat);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(RiwayatActivity.this, MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_chatbot) {
                // Chatbot belum tersedia
                Toast.makeText(this, "Fitur Chatbot segera hadir!", Toast.LENGTH_SHORT).show();
                return false;
            } else if (id == R.id.nav_riwayat) {
                return true;
            }
            return false;
        });
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Semua"));
        tabLayout.addTab(tabLayout.newTab().setText("Pemasukan"));
        tabLayout.addTab(tabLayout.newTab().setText("Pengeluaran"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0) {
                    currentFilter = "semua";
                } else if (position == 1) {
                    currentFilter = "pemasukan";
                } else {
                    currentFilter = "pengeluaran";
                }
                loadTransaksi();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
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

        tvTotalPemasukan.setText(formatRupiah(totalPemasukan));
        tvTotalPengeluaran.setText(formatRupiah(totalPengeluaran));
        tvTotalTransaksi.setText(String.valueOf(allTransaksi.size()));

        loadTransaksi();
    }

    private void loadTransaksi() {
        List<Transaksi> transaksiList;
        if (currentFilter.equals("semua")) {
            transaksiList = db.getAllTransaksi();
        } else {
            transaksiList = db.getTransaksiByTipe(currentFilter);
        }
        adapter.updateData(transaksiList);
    }

    private String formatRupiah(long amount) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatter.applyPattern("#,###");
        return "Rp " + formatter.format(amount).replace(",", ".");
    }
}