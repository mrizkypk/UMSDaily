package com.mrizkypk.umsdaily.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mrizkypk.umsdaily.R;
import com.mrizkypk.umsdaily.model.KhsModel;

import java.util.LinkedList;

/**
 * Created by mrizkypk on 01/03/18.
 */

public class KhsAdapter extends RecyclerView.Adapter<KhsAdapter.ViewHolder> {
    public Context context;
    public Activity activity;
    public LinkedList<KhsModel> dataList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView tvSubject;
        public TextView tvMark;
        public TextView tvSks;
        public TextView tvSemester;
        public TextView tvLecturer;

        public ViewHolder(View v) {
            super(v);
            view = v;
            tvSubject = view.findViewById(R.id.item_khs_text_subject);
            tvMark = view.findViewById(R.id.item_khs_text_mark);
            tvSks = view.findViewById(R.id.item_khs_text_sks);
            tvSemester = view.findViewById(R.id.item_khs_text_semester);
            tvLecturer = view.findViewById(R.id.item_khs_text_lecturer);
        }
    }


    public KhsAdapter(Context ctx, LinkedList<KhsModel> list) {
        dataList = list;
        context = ctx;
        activity = (Activity) ctx;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_khs, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        KhsModel khs = dataList.get(position);
        holder.tvSubject.setText(khs.getMatakuliah());
        holder.tvMark.setText("Nilai: " + khs.getNilai());
        holder.tvSks.setText("SKS: " + khs.getSks());
        holder.tvSemester.setText("Semester: " + khs.getSemester());
        holder.tvLecturer.setText("Pengampu: " + khs.getPengampu());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
