package com.mrizkypk.umsdaily.model;

public class KhsModel {
    private String kode;
    private String matakuliah;
    private String sks;
    private String semester;
    private String nilai;
    private String bobot;
    private String pengampu;

    public KhsModel() {
    }

    public KhsModel(String kode, String matakuliah, String sks, String semester, String nilai, String bobot, String pengampu) {
        this.kode = kode;
        this.matakuliah = matakuliah;
        this.sks = sks;
        this.semester = semester;
        this.nilai = nilai;
        this.bobot = bobot;
        this.pengampu = pengampu;
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

    public String getNilai() {
        return nilai;
    }

    public void setNilai(String nilai) {
        this.nilai = nilai;
    }

    public String getBobot() {
        return bobot;
    }

    public void setBobot(String bobot) {
        this.bobot = bobot;
    }

    public String getPengampu() {
        return pengampu;
    }

    public void setPengampu(String pengampu) {
        this.pengampu = pengampu;
    }
}
