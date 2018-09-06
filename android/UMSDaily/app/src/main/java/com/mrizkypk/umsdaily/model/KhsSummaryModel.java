package com.mrizkypk.umsdaily.model;

public class KhsSummaryModel {
    private String jumlah_sks;
    private String jumlah_matakuliah;
    private String indeks_prestasi_semester;

    public KhsSummaryModel() {
    }

    public KhsSummaryModel(String jumlah_sks, String jumlah_matakuliah, String indeks_prestasi_semester) {
        this.jumlah_sks = jumlah_sks;
        this.jumlah_matakuliah = jumlah_matakuliah;
        this.indeks_prestasi_semester = indeks_prestasi_semester;
    }

    public String getJumlah_sks() {
        return jumlah_sks;
    }

    public void setJumlah_sks(String jumlah_sks) {
        this.jumlah_sks = jumlah_sks;
    }

    public String getJumlah_matakuliah() {
        return jumlah_matakuliah;
    }

    public void setJumlah_matakuliah(String jumlah_matakuliah) {
        this.jumlah_matakuliah = jumlah_matakuliah;
    }

    public String getIndeks_prestasi_semester() {
        return indeks_prestasi_semester;
    }

    public void setIndeks_prestasi_semester(String indeks_prestasi_semester) {
        this.indeks_prestasi_semester = indeks_prestasi_semester;
    }
}
