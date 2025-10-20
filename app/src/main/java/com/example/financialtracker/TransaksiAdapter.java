package com.example.financialtracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TransaksiAdapter extends RecyclerView.Adapter<TransaksiAdapter.ViewHolder> {

    private List<Transaksi> daftarTransaksi;
    private OnHapusListener listener;

    public interface OnHapusListener {
        void onHapus(int posisi);
    }

    public TransaksiAdapter(List<Transaksi> daftarTransaksi, OnHapusListener listener) {
        this.daftarTransaksi = daftarTransaksi;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaksi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaksi transaksi = daftarTransaksi.get(position);
        holder.tvJenis.setText(transaksi.getJenis());
        holder.tvJumlah.setText("Rp " + String.format("%,.0f", transaksi.getJumlah()));
        holder.tvKeterangan.setText(transaksi.getKeterangan());
        holder.btnHapus.setOnClickListener(v -> listener.onHapus(position));
    }

    @Override
    public int getItemCount() {
        return daftarTransaksi.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvJenis, tvJumlah, tvKeterangan;
        Button btnHapus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJenis = itemView.findViewById(R.id.tv_item_jenis);
            tvJumlah = itemView.findViewById(R.id.tv_item_jumlah);
            tvKeterangan = itemView.findViewById(R.id.tv_item_keterangan);
            btnHapus = itemView.findViewById(R.id.btn_item_hapus);
        }
    }
}
