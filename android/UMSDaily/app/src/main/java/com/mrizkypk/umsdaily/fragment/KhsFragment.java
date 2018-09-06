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
import com.mrizkypk.umsdaily.adapter.KhsAdapter;
import com.mrizkypk.umsdaily.helper.ApiHelper;
import com.mrizkypk.umsdaily.model.KhsModel;
import com.mrizkypk.umsdaily.model.KhsResponseModel;
import com.mrizkypk.umsdaily.model.KhsSummaryModel;
import com.mrizkypk.umsdaily.model.ResponseErrorModel;

import java.util.LinkedList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class KhsFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {
    private String khsId;
    private String khsName;
    private HomeActivity activity;
    private RecyclerView recyclerview;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private LinkedList<KhsModel> khsList;

    @BindView(R.id.fragment_khs_layout)
    ConstraintLayout layout;

    @BindView(R.id.fragment_khs_text_toolbar_subtitle)
    TextView tvToolbarSubtitle;

    @BindView(R.id.fragment_khs_progressbar)
    ProgressBar progressbar;

    @BindView(R.id.fragment_khs_text_sks)
    TextView tvSks;

    @BindView(R.id.fragment_khs_text_mean)
    TextView tvMean;

    @BindView(R.id.fragment_khs_text_subject)
    TextView tvSubject;

    @BindView(R.id.fragment_khs_container_summary)
    CardView cdSummary;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = ((HomeActivity) getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_khs, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        khsId = getArguments().getString("BUNDLE_KHS_ID");
        khsName = getArguments().getString("BUNDLE_KHS_NAME");

        recyclerview = view.findViewById(R.id.fragment_khs_recyclerview);
        recyclerview.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerview.setLayoutManager(layoutManager);
        khsList = new LinkedList<>();

        adapter = new KhsAdapter(getContext(), khsList);
        recyclerview.setAdapter(adapter);

        tvToolbarSubtitle.setText(khsName);

        loadKhs();
    }

    public void loadKhs() {
        showProgressBar();
        hideKhsSummary();
        AndroidNetworking.get(ApiHelper.getHost() + "/khs.php")
                .addQueryParameter("nim", activity.spm.getUserId())
                .addQueryParameter("password", activity.spm.getUserPassword())
                .addQueryParameter("semester", khsId)
                .getResponseOnlyFromNetwork()
                .build()
                .getAsObject(KhsResponseModel.class, new ParsedRequestListener<KhsResponseModel>() {
                    @Override
                    public void onResponse(KhsResponseModel models) {
                        hideProgressBar();
                        for (KhsModel model : models.getDaftar()) {
                            khsList.add(model);
                            adapter.notifyItemInserted(khsList.size());
                        }
                        KhsSummaryModel summary = models.getRangkuman();
                        tvSks.setText(summary.getJumlah_sks());
                        tvSubject.setText(summary.getJumlah_matakuliah());
                        tvMean.setText(summary.getIndeks_prestasi_semester());
                    }
                    @Override
                    public void onError(ANError e) {
                        hideProgressBar();
                        hideKhsSummary();
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

    public void showKhsSummary() {
        cdSummary.setVisibility(View.VISIBLE);
    }

    public void hideKhsSummary() {
        cdSummary.setVisibility(View.GONE);
    }

    @OnClick({R.id.fragment_khs_container_toolbar_arrow_back, R.id.fragment_khs_button_toolbar_arrow_back})
    public void onToolbarArrowBackClick() {
        activity.onBackPressed();
    }

    @OnClick({R.id.fragment_khs_container_toolbar_more, R.id.fragment_khs_button_toolbar_more})
    public void onClickToolbarMore(View view) {
        PopupMenu popup = new PopupMenu(getContext(), view);
        popup.setOnMenuItemClickListener(this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.toolbar_khs, popup.getMenu());
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int itemId = item.getItemId();
        switch(itemId) {
            case R.id.toolbar_menu_refresh:
                khsList.clear();
                adapter.notifyDataSetChanged();
                loadKhs();
                return true;
            case R.id.toolbar_menu_summary:
                if (cdSummary.getVisibility() == View.GONE) {
                    showKhsSummary();
                } else {
                    hideKhsSummary();
                }
                return true;
            default:
                return false;
        }
    }
}