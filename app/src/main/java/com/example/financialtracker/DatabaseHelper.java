package com.example.financialtracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "keuangan.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_TRANSAKSI = "transaksi";
    private static final String COL_ID = "id";
    private static final String COL_KETERANGAN = "keterangan";
    private static final String COL_JUMLAH = "jumlah";
    private static final String COL_TIPE = "tipe";
    private static final String COL_TANGGAL = "tanggal";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_TRANSAKSI + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_KETERANGAN + " TEXT, " +
                COL_JUMLAH + " INTEGER, " +
                COL_TIPE + " TEXT, " +
                COL_TANGGAL + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSAKSI);
        onCreate(db);
    }

    public long addTransaksi(String keterangan, long jumlah, String tipe, String tanggal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_KETERANGAN, keterangan);
        values.put(COL_JUMLAH, jumlah);
        values.put(COL_TIPE, tipe);
        values.put(COL_TANGGAL, tanggal);

        long id = db.insert(TABLE_TRANSAKSI, null, values);
        db.close();
        return id;
    }

    public List<Transaksi> getAllTransaksi() {
        List<Transaksi> transaksiList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TRANSAKSI, null, null, null, null, null, COL_ID + " DESC");

        if (cursor.moveToFirst()) {
            do {
                Transaksi transaksi = new Transaksi();
                transaksi.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
                transaksi.setKeterangan(cursor.getString(cursor.getColumnIndexOrThrow(COL_KETERANGAN)));
                transaksi.setJumlah(cursor.getLong(cursor.getColumnIndexOrThrow(COL_JUMLAH)));
                transaksi.setTipe(cursor.getString(cursor.getColumnIndexOrThrow(COL_TIPE)));
                transaksi.setTanggal(cursor.getString(cursor.getColumnIndexOrThrow(COL_TANGGAL)));
                transaksiList.add(transaksi);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return transaksiList;
    }

    public List<Transaksi> getTransaksiByTipe(String tipe) {
        List<Transaksi> transaksiList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TRANSAKSI, null, COL_TIPE + "=?",
                new String[]{tipe}, null, null, COL_ID + " DESC");

        if (cursor.moveToFirst()) {
            do {
                Transaksi transaksi = new Transaksi();
                transaksi.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
                transaksi.setKeterangan(cursor.getString(cursor.getColumnIndexOrThrow(COL_KETERANGAN)));
                transaksi.setJumlah(cursor.getLong(cursor.getColumnIndexOrThrow(COL_JUMLAH)));
                transaksi.setTipe(cursor.getString(cursor.getColumnIndexOrThrow(COL_TIPE)));
                transaksi.setTanggal(cursor.getString(cursor.getColumnIndexOrThrow(COL_TANGGAL)));
                transaksiList.add(transaksi);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return transaksiList;
    }

    public int deleteTransaksi(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_TRANSAKSI, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return result;
    }
}