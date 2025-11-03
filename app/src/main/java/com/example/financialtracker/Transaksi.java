package com.example.financialtracker;

public class Transaksi {
    private int id;
    private String keterangan;
    private long jumlah;
    private String tipe; // "pemasukan" atau "pengeluaran"
    private String tanggal;

    public Transaksi() {
    }

    public Transaksi(int id, String keterangan, long jumlah, String tipe, String tanggal) {
        this.id = id;
        this.keterangan = keterangan;
        this.jumlah = jumlah;
        this.tipe = tipe;
        this.tanggal = tanggal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public long getJumlah() {
        return jumlah;
    }

    public void setJumlah(long jumlah) {
        this.jumlah = jumlah;
    }

    public String getTipe() {
        return tipe;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }
}