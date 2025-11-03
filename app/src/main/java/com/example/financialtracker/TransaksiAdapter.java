package com.example.financialtracker;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransaksiAdapter extends RecyclerView.Adapter<TransaksiAdapter.ViewHolder> {
    private List<Transaksi> transaksiList;
    private boolean showDeleteButton;

    public TransaksiAdapter(List<Transaksi> transaksiList, boolean showDeleteButton) {
        this.transaksiList = transaksiList;
        this.showDeleteButton = showDeleteButton;
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
        Transaksi transaksi = transaksiList.get(position);

        holder.tvKeterangan.setText(transaksi.getKeterangan());
        holder.tvTanggal.setText(formatTanggal(transaksi.getTanggal()));

        String formattedAmount = formatRupiah(transaksi.getJumlah());
        holder.tvJumlah.setText((transaksi.getTipe().equals("pemasukan") ? "+" : "-") + formattedAmount);

        if (transaksi.getTipe().equals("pemasukan")) {
            holder.cardIcon.setCardBackgroundColor(holder.itemView.getContext().getColor(android.R.color.holo_green_light));
            holder.ivIcon.setImageResource(android.R.drawable.arrow_up_float);
            holder.ivIcon.setColorFilter(holder.itemView.getContext().getColor(android.R.color.white));
            holder.tvJumlah.setTextColor(holder.itemView.getContext().getColor(android.R.color.holo_green_dark));
        } else {
            holder.cardIcon.setCardBackgroundColor(holder.itemView.getContext().getColor(android.R.color.holo_red_light));
            holder.ivIcon.setImageResource(android.R.drawable.arrow_down_float);
            holder.ivIcon.setColorFilter(holder.itemView.getContext().getColor(android.R.color.white));
            holder.tvJumlah.setTextColor(holder.itemView.getContext().getColor(android.R.color.holo_red_dark));
        }

        if (showDeleteButton) {
            holder.ivDelete.setVisibility(View.VISIBLE);
            holder.ivDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Hapus Transaksi")
                        .setMessage("Yakin ingin menghapus transaksi ini?")
                        .setPositiveButton("Hapus", (dialog, which) -> {
                            DatabaseHelper db = new DatabaseHelper(v.getContext());
                            db.deleteTransaksi(transaksi.getId());
                            transaksiList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, transaksiList.size());

                            if (v.getContext() instanceof RiwayatActivity) {
                                ((RiwayatActivity) v.getContext()).onResume();
                            }
                        })
                        .setNegativeButton("Batal", null)
                        .show();
            });
        } else {
            holder.ivDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return transaksiList.size();
    }

    public void updateData(List<Transaksi> newList) {
        this.transaksiList = newList;
        notifyDataSetChanged();
    }

    private String formatRupiah(long amount) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatter.applyPattern("#,###");
        return "Rp " + formatter.format(amount).replace(",", ".");
    }

    private String formatTanggal(String tanggal) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("d MMMM yyyy", new Locale("id", "ID"));
            Date date = inputFormat.parse(tanggal);
            return date != null ? outputFormat.format(date) : tanggal;
        } catch (ParseException e) {
            return tanggal;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardIcon;
        ImageView ivIcon, ivDelete;
        TextView tvKeterangan, tvTanggal, tvJumlah;

        ViewHolder(View itemView) {
            super(itemView);
            cardIcon = itemView.findViewById(R.id.cardIcon);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            ivDelete = itemView.findViewById(R.id.ivDelete);
            tvKeterangan = itemView.findViewById(R.id.tvKeterangan);
            tvTanggal = itemView.findViewById(R.id.tvTanggal);
            tvJumlah = itemView.findViewById(R.id.tvJumlah);
        }
    }
}