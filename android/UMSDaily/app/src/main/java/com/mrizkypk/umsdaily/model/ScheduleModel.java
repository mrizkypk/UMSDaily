package com.mrizkypk.umsdaily.model;

public class ScheduleModel {
    private String kode;
    private String matakuliah;
    private String sks;
    private String kelas;
    private String semester;
    private String hari;
    private String jam;
    private String ruang;
    private String pengampu;

    public ScheduleModel() {
    }

    public ScheduleModel(String kode, String matakuliah, String sks, String kelas, String semester, String hari, String jam, String ruang, String pengampu) {
        this.kode = kode;
        this.matakuliah = matakuliah;
        this.sks = sks;
        this.kelas = kelas;
        this.semester = semester;
        this.hari = hari;
        this.jam = jam;
        this.ruang = ruang;
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

    public String getKelas() {
        return kelas;
    }

    public void setKelas(String kelas) {
        this.kelas = kelas;
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

    public String getJam() {
        return jam;
    }

    public void setJam(String jam) {
        this.jam = jam;
    }

    public String getRuang() {
        return ruang;
    }

    public void setRuang(String ruang) {
        this.ruang = ruang;
    }

    public String getPengampu() {
        return pengampu;
    }

    public void setPengampu(String pengampu) {
        this.pengampu = pengampu;
    }
}
