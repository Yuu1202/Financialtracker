package com.example.financialtracker;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TambahTransaksiActivity extends AppCompatActivity {
    private EditText etKeterangan, etJumlah;
    private Button btnSimpan;
    private DatabaseHelper db;
    private String tipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_transaksi);

        db = new DatabaseHelper(this);
        tipe = getIntent().getStringExtra("TIPE");

        initViews();
        setupUI();
        setupClickListeners();
    }

    private void initViews() {
        etKeterangan = findViewById(R.id.etKeterangan);
        etJumlah = findViewById(R.id.etJumlah);
        btnSimpan = findViewById(R.id.btnSimpan);
    }

    private void setupUI() {
        if (tipe.equals("pemasukan")) {
            setTitle("Tambah Pemasukan");
            btnSimpan.setBackgroundColor(getColor(android.R.color.holo_green_dark));
        } else {
            setTitle("Tambah Pengeluaran");
            btnSimpan.setBackgroundColor(getColor(android.R.color.holo_red_dark));
        }
    }

    private void setupClickListeners() {
        btnSimpan.setOnClickListener(v -> {
            String keterangan = etKeterangan.getText().toString().trim();
            String jumlahStr = etJumlah.getText().toString().trim();

            if (keterangan.isEmpty()) {
                etKeterangan.setError("Keterangan tidak boleh kosong");
                return;
            }

            if (jumlahStr.isEmpty()) {
                etJumlah.setError("Jumlah tidak boleh kosong");
                return;
            }

            try {
                long jumlah = Long.parseLong(jumlahStr);
                String tanggal = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

                db.addTransaksi(keterangan, jumlah, tipe, tanggal);
                Toast.makeText(this, "Transaksi berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                finish();
            } catch (NumberFormatException e) {
                etJumlah.setError("Jumlah tidak valid");
            }
        });
    }
}
