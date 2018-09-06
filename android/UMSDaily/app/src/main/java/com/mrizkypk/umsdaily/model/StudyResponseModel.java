package com.mrizkypk.umsdaily.model;

import java.util.List;

public class StudyResponseModel {
    private List<StudyModel> daftar;
    private StudySummaryModel rangkuman;

    public StudyResponseModel() {
    }

    public StudyResponseModel(List<StudyModel> daftar, StudySummaryModel rangkuman) {
        this.daftar = daftar;
        this.rangkuman = rangkuman;
    }

    public List<StudyModel> getDaftar() {
        return daftar;
    }

    public void setDaftar(List<StudyModel> daftar) {
        this.daftar = daftar;
    }

    public StudySummaryModel getRangkuman() {
        return rangkuman;
    }

    public void setRangkuman(StudySummaryModel rangkuman) {
        this.rangkuman = rangkuman;
    }
}
