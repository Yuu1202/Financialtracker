package com.example.financialtracker;

public class Transaksi {
    private String jenis;
    private double jumlah;
    private String keterangan;

    public Transaksi(String jenis, double jumlah, String keterangan) {
        this.jenis = jenis;
        this.jumlah = jumlah;
        this.keterangan = keterangan;
    }

    public String getJenis() { return jenis; }
    public double getJumlah() { return jumlah; }
    public String getKeterangan() { return keterangan; }
}