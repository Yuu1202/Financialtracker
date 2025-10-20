package com.example.financialtracker;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText etJumlah, etKeterangan;
    private Spinner spnJenis;
    private Button btnTambah, btnHapusSemua;
    private TextView tvTotalPemasukan, tvTotalPengeluaran, tvSaldo;
    private RecyclerView rvTransaksi;
    private TransaksiAdapter adapter;
    private List<Transaksi> daftarTransaksi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi views
        etJumlah = findViewById(R.id.et_jumlah);
        etKeterangan = findViewById(R.id.et_keterangan);
        spnJenis = findViewById(R.id.spn_jenis);
        btnTambah = findViewById(R.id.btn_tambah);
        btnHapusSemua = findViewById(R.id.btn_hapus_semua);
        tvTotalPemasukan = findViewById(R.id.tv_total_pemasukan);
        tvTotalPengeluaran = findViewById(R.id.tv_total_pengeluaran);
        tvSaldo = findViewById(R.id.tv_saldo);
        rvTransaksi = findViewById(R.id.rv_transaksi);

        // Setup RecyclerView
        daftarTransaksi = new ArrayList<>();
        adapter = new TransaksiAdapter(daftarTransaksi, this::hapusTransaksi);
        rvTransaksi.setLayoutManager(new LinearLayoutManager(this));
        rvTransaksi.setAdapter(adapter);

        // Event listener tombol tambah
        btnTambah.setOnClickListener(v -> tambahTransaksi());

        // Event listener tombol hapus semua
        btnHapusSemua.setOnClickListener(v -> hapusSemua());
    }

    private void tambahTransaksi() {
        String jumlahStr = etJumlah.getText().toString().trim();
        String keterangan = etKeterangan.getText().toString().trim();
        String jenis = spnJenis.getSelectedItem().toString();

        if (jumlahStr.isEmpty()) {
            Toast.makeText(this, "Masukkan jumlah", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double jumlah = Double.parseDouble(jumlahStr);
            if (jumlah <= 0) {
                Toast.makeText(this, "Jumlah harus lebih dari 0", Toast.LENGTH_SHORT).show();
                return;
            }

            Transaksi transaksi = new Transaksi(jenis, jumlah, keterangan);
            daftarTransaksi.add(0, transaksi);
            adapter.notifyItemInserted(0);

            etJumlah.setText("");
            etKeterangan.setText("");

            hitungTotal();
            Toast.makeText(this, "Transaksi ditambahkan", Toast.LENGTH_SHORT).show();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Format jumlah tidak valid", Toast.LENGTH_SHORT).show();
        }
    }

    private void hapusTransaksi(int posisi) {
        daftarTransaksi.remove(posisi);
        adapter.notifyItemRemoved(posisi);
        hitungTotal();
    }

    private void hapusSemua() {
        if (!daftarTransaksi.isEmpty()) {
            daftarTransaksi.clear();
            adapter.notifyDataSetChanged();
            hitungTotal();
            Toast.makeText(this, "Semua transaksi dihapus", Toast.LENGTH_SHORT).show();
        }
    }

    private void hitungTotal() {
        double totalPemasukan = 0;
        double totalPengeluaran = 0;

        for (Transaksi t : daftarTransaksi) {
            if (t.getJenis().equals("Pemasukan")) {
                totalPemasukan += t.getJumlah();
            } else {
                totalPengeluaran += t.getJumlah();
            }
        }

        double saldo = totalPemasukan - totalPengeluaran;

        tvTotalPemasukan.setText("Rp " + formatRupiah(totalPemasukan));
        tvTotalPengeluaran.setText("Rp " + formatRupiah(totalPengeluaran));
        tvSaldo.setText("Rp " + formatRupiah(saldo));
    }

    private String formatRupiah(double jumlah) {
        return String.format("%,.0f", jumlah);
    }
}