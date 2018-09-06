package com.mrizkypk.umsdaily.model;

public class StudySummaryModel {
    private String jumlah_sks;
    private String indeks_prestasi_kumulatif;

    public StudySummaryModel() {
    }

    public StudySummaryModel(String jumlah_sks, String indeks_prestasi_kumulatif) {
        this.jumlah_sks = jumlah_sks;
        this.indeks_prestasi_kumulatif = indeks_prestasi_kumulatif;
    }

    public String getJumlah_sks() {
        return jumlah_sks;
    }

    public void setJumlah_sks(String jumlah_sks) {
        this.jumlah_sks = jumlah_sks;
    }

    public String getIndeks_prestasi_kumulatif() {
        return indeks_prestasi_kumulatif;
    }

    public void setIndeks_prestasi_kumulatif(String indeks_prestasi_kumulatif) {
        this.indeks_prestasi_kumulatif = indeks_prestasi_kumulatif;
    }
}
