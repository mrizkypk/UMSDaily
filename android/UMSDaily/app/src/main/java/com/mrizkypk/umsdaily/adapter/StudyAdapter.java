package com.mrizkypk.umsdaily.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mrizkypk.umsdaily.R;
import com.mrizkypk.umsdaily.model.StudyModel;

import java.util.LinkedList;

public class StudyAdapter extends RecyclerView.Adapter<StudyAdapter.ViewHolder> {
    public Context context;
    public Activity activity;
    public LinkedList<StudyModel> dataList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView tvSubject;
        public TextView tvMark;
        public TextView tvSks;
        public TextView tvSemester;

        public ViewHolder(View v) {
            super(v);
            view = v;
            tvSubject = view.findViewById(R.id.item_study_development_text_subject);
            tvMark = view.findViewById(R.id.item_study_development_text_mark);
            tvSks = view.findViewById(R.id.item_study_development_text_sks);
            tvSemester = view.findViewById(R.id.item_study_development_text_semester);
        }
    }

    public StudyAdapter(Context ctx, LinkedList<StudyModel> list) {
        dataList = list;
        context = ctx;
        activity = (Activity) ctx;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_study_development, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        StudyModel khs = dataList.get(position);
        holder.tvSubject.setText(khs.getMatakuliah());
        holder.tvMark.setText("Nilai: " + khs.getNilai());
        holder.tvSks.setText("SKS: " + khs.getSks());
        holder.tvSemester.setText("Semester: " + khs.getSemester());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}

