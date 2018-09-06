package com.mrizkypk.umsdaily.model;

public class SearchModel {
    private String sks;
    private String semester;
    private String hari;
    private String ruang;
    private String jam;
    private String kode;
    private String matakuliah;
    private String pengampu;
    private String kelas;
    private String pradi;

    public SearchModel() {
    }

    public SearchModel(String sks, String semester, String hari, String ruang, String jam, String kode, String matakuliah, String pengampu, String kelas, String pradi) {
        this.sks = sks;
        this.semester = semester;
        this.hari = hari;
        this.ruang = ruang;
        this.jam = jam;
        this.kode = kode;
        this.matakuliah = matakuliah;
        this.pengampu = pengampu;
        this.kelas = kelas;
        this.pradi = pradi;
    }

    public String getSks() {
        return sks;
    }

    public void setSks(String sks) {
        this.sks = sks;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getHari() {
        return hari;
    }

    public void setHari(String hari) {
        this.hari = hari;
    }

    public String getRuang() {
        return ruang;
    }

    public void setRuang(String ruang) {
        this.ruang = ruang;
    }

    public String getJam() {
        return jam;
    }

    public void setJam(String jam) {
        this.jam = jam;
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getMatakuliah() {
        return matakuliah;
    }

    public void setMatakuliah(String matakuliah) {
        this.matakuliah = matakuliah;
    }

    public String getPengampu() {
        return pengampu;
    }

    public void setPengampu(String pengampu) {
        this.pengampu = pengampu;
    }

    public String getKelas() {
        return kelas;
    }

    public void setKelas(String kelas) {
        this.kelas = kelas;
    }

    public String getPradi() {
        return pradi;
    }

    public void setPradi(String pradi) {
        this.pradi = pradi;
    }
}
