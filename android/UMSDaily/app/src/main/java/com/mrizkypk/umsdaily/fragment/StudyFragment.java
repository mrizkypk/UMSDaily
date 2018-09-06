package com.mrizkypk.umsdaily.fragment;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.mrizkypk.umsdaily.R;
import com.mrizkypk.umsdaily.activity.HomeActivity;
import com.mrizkypk.umsdaily.adapter.StudyAdapter;
import com.mrizkypk.umsdaily.helper.ApiHelper;
import com.mrizkypk.umsdaily.model.ResponseErrorModel;
import com.mrizkypk.umsdaily.model.StudyModel;
import com.mrizkypk.umsdaily.model.StudyResponseModel;
import com.mrizkypk.umsdaily.model.StudySummaryModel;

import java.util.LinkedList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StudyFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {
    private HomeActivity activity;
    private RecyclerView recyclerview;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private LinkedList<StudyModel> studyList;

    @BindView(R.id.fragment_study_layout)
    ConstraintLayout layout;

    @BindView(R.id.fragment_study_progressbar)
    ProgressBar progressbar;

    @BindView(R.id.fragment_study_text_sks)
    TextView tvSks;

    @BindView(R.id.fragment_study_text_mean)
    TextView tvMean;

    @BindView(R.id.fragment_study_container_summary)
    CardView cdSummary;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = ((HomeActivity) getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_study, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerview = view.findViewById(R.id.fragment_study_recyclerview);
        recyclerview.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerview.setLayoutManager(layoutManager);
        studyList = new LinkedList<>();

        adapter = new StudyAdapter(getContext(), studyList);
        recyclerview.setAdapter(adapter);

        loadStudy();
    }

    public void loadStudy() {
        showProgressBar();
        hideStudySummary();
        AndroidNetworking.get(ApiHelper.getHost() + "/perkembangan_studi.php")
                .addQueryParameter("nim", activity.spm.getUserId())
                .addQueryParameter("password", activity.spm.getUserPassword())
                .getResponseOnlyFromNetwork()
                .build()
                .getAsObject(StudyResponseModel.class, new ParsedRequestListener<StudyResponseModel>() {
                    @Override
                    public void onResponse(StudyResponseModel models) {
                        hideProgressBar();
                        for (StudyModel model : models.getDaftar()) {
                            studyList.add(model);
                            adapter.notifyItemInserted(studyList.size());
                        }

                        StudySummaryModel summary = models.getRangkuman();
                        tvSks.setText(summary.getJumlah_sks());
                        tvMean.setText(summary.getIndeks_prestasi_kumulatif());

                    }
                    @Override
                    public void onError(ANError e) {
                        hideProgressBar();
                        hideStudySummary();
                        String message = "Error";
                        if (e.getErrorCode() != 0) {
                            ResponseErrorModel error = e.getErrorAsObject(ResponseErrorModel.class);
                            if (error.getMessage().equals("empty")) {
                                message = "Tidak ada data";
                            } else if (error.getMessage().equals("server")) {
                                message = "Server error";
                            }
                        } else {
                            message = "Koneksi bermasalah";
                        }
                        Snackbar.make(layout, message,
                                Snackbar.LENGTH_LONG)
                                .show();
                    }
                });
    }

    public void showProgressBar() {
        progressbar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressbar.setVisibility(View.GONE);
    }

    public void showStudySummary() {
        cdSummary.setVisibility(View.VISIBLE);
    }

    public void hideStudySummary() {
        cdSummary.setVisibility(View.GONE);
    }

    @OnClick({R.id.fragment_study_container_toolbar_arrow_back, R.id.fragment_study_button_toolbar_arrow_back})
    public void onToolbarArrowBackClick() {
        activity.onBackPressed();
    }

    @OnClick({R.id.fragment_study_container_toolbar_more, R.id.fragment_study_button_toolbar_more})
    public void onToolbarMoreClick(View view) {
        PopupMenu popup = new PopupMenu(getContext(), view);
        popup.setOnMenuItemClickListener(this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.toolbar_study_development, popup.getMenu());
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.toolbar_menu_refresh:
                studyList.clear();
                adapter.notifyDataSetChanged();
                loadStudy();
                return true;
            case R.id.toolbar_menu_summary:
                if (cdSummary.getVisibility() == View.GONE) {
                    showStudySummary();
                } else {
                    hideStudySummary();
                }
                return true;
            default:
                return false;
        }
    }
}
