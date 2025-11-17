package com.example.financialtracker;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BottomSheetTambahTransaksi extends BottomSheetDialogFragment {
    private EditText etKeterangan, etJumlah;
    private Button btnSimpan;
    private TextView tvTitle;
    private DatabaseHelper db;
    private String tipe;
    private OnTransaksiAddedListener listener;

    public interface OnTransaksiAddedListener {
        void onTransaksiAdded();
    }

    public static BottomSheetTambahTransaksi newInstance(String tipe) {
        BottomSheetTambahTransaksi fragment = new BottomSheetTambahTransaksi();
        Bundle args = new Bundle();
        args.putString("TIPE", tipe);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnTransaksiAddedListener(OnTransaksiAddedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tipe = getArguments().getString("TIPE", "pemasukan");
        }
        db = new DatabaseHelper(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_tambah_transaksi, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupUI();
        setupClickListeners();
    }

    private void initViews(View view) {
        tvTitle = view.findViewById(R.id.tvTitle);
        etKeterangan = view.findViewById(R.id.etKeterangan);
        etJumlah = view.findViewById(R.id.etJumlah);
        btnSimpan = view.findViewById(R.id.btnSimpan);
    }

    private void setupUI() {
        if (tipe.equals("pemasukan")) {
            tvTitle.setText("Tambah Pemasukan");
            tvTitle.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            btnSimpan.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            tvTitle.setText("Tambah Pengeluaran");
            tvTitle.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            btnSimpan.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        }
    }

    private void setupClickListeners() {
        btnSimpan.setOnClickListener(v -> simpanTransaksi());
    }

    private void simpanTransaksi() {
        String keterangan = etKeterangan.getText().toString().trim();
        String jumlahStr = etJumlah.getText().toString().trim();

        if (keterangan.isEmpty()) {
            etKeterangan.setError("Keterangan tidak boleh kosong");
            etKeterangan.requestFocus();
            return;
        }

        if (jumlahStr.isEmpty()) {
            etJumlah.setError("Jumlah tidak boleh kosong");
            etJumlah.requestFocus();
            return;
        }

        try {
            long jumlah = Long.parseLong(jumlahStr);

            if (jumlah <= 0) {
                etJumlah.setError("Jumlah harus lebih dari 0");
                etJumlah.requestFocus();
                return;
            }

            String tanggal = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

            long result = db.addTransaksi(keterangan, jumlah, tipe, tanggal);

            if (result != -1) {
                Toast.makeText(getContext(), "Transaksi berhasil ditambahkan", Toast.LENGTH_SHORT).show();

                if (listener != null) {
                    listener.onTransaksiAdded();
                }

                dismiss();
            } else {
                Toast.makeText(getContext(), "Gagal menambahkan transaksi", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            etJumlah.setError("Jumlah tidak valid");
            etJumlah.requestFocus();
        }
    }

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }
}