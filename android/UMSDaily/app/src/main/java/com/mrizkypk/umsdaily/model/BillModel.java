package com.mrizkypk.umsdaily.model;

public class BillModel {
    private String jenis_pembayaran;
    private String jumlah_bayar;
    private String tanggal_bayar;
    private String status_tagihan;

    public BillModel() {
    }

    public BillModel(String jenis_pembayaran, String jumlah_bayar, String tanggal_bayar, String status_tagihan) {
        this.jenis_pembayaran = jenis_pembayaran;
        this.jumlah_bayar = jumlah_bayar;
        this.tanggal_bayar = tanggal_bayar;
        this.status_tagihan = status_tagihan;
    }

    public String getJenis_pembayaran() {
        return jenis_pembayaran;
    }

    public void setJenis_pembayaran(String jenis_pembayaran) {
        this.jenis_pembayaran = jenis_pembayaran;
    }

    public String getJumlah_bayar() {
        return jumlah_bayar;
    }

    public void setJumlah_bayar(String jumlah_bayar) {
        this.jumlah_bayar = jumlah_bayar;
    }

    public String getTanggal_bayar() {
        return tanggal_bayar;
    }

    public void setTanggal_bayar(String tanggal_bayar) {
        this.tanggal_bayar = tanggal_bayar;
    }

    public String getStatus_tagihan() {
        return status_tagihan;
    }

    public void setStatus_tagihan(String status_tagihan) {
        this.status_tagihan = status_tagihan;
    }
}
