package com.mrizkypk.umsdaily.model;

import java.util.List;

public class KhsResponseModel {
    private List<KhsModel> daftar;
    private KhsSummaryModel rangkuman;

    public KhsResponseModel() {
    }

    public KhsResponseModel(List<KhsModel> daftar, KhsSummaryModel rangkuman) {
        this.daftar = daftar;
        this.rangkuman = rangkuman;
    }

    public List<KhsModel> getDaftar() {
        return daftar;
    }

    public void setDaftar(List<KhsModel> daftar) {
        this.daftar = daftar;
    }

    public KhsSummaryModel getRangkuman() {
        return rangkuman;
    }

    public void setRangkuman(KhsSummaryModel rangkuman) {
        this.rangkuman = rangkuman;
    }
}
