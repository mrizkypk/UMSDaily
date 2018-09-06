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
import com.mrizkypk.umsdaily.adapter.BillAdapter;
import com.mrizkypk.umsdaily.helper.ApiHelper;
import com.mrizkypk.umsdaily.model.BillModel;
import com.mrizkypk.umsdaily.model.ResponseErrorModel;

import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BillFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {
    private HomeActivity activity;
    private RecyclerView recyclerview;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private LinkedList<BillModel> billList;

    @BindView(R.id.fragment_bill_layout)
    ConstraintLayout layout;

    @BindView(R.id.fragment_bill_progressbar)
    ProgressBar progressbar;

    @BindView(R.id.fragment_bill_container_summary)
    CardView cdSummary;

    @BindView(R.id.fragment_bill_text_count)
    TextView tvCount;

    @BindView(R.id.fragment_bill_text_total)
    TextView tvTotal;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = ((HomeActivity) getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bill, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerview = view.findViewById(R.id.fragment_bill_recyclerview);
        recyclerview.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerview.setLayoutManager(layoutManager);
        billList = new LinkedList<>();

        adapter = new BillAdapter(getActivity(), billList);
        recyclerview.setAdapter(adapter);

        loadBill();
    }

    public void loadBill() {
        showProgressBar();
        AndroidNetworking.get(ApiHelper.getHost() + "/tagihan.php")
                .addQueryParameter("nim", activity.spm.getUserId())
                .addQueryParameter("password", activity.spm.getUserPassword())
                .getResponseOnlyFromNetwork()
                .build()
                .getAsObjectList(BillModel.class, new ParsedRequestListener<List<BillModel>>() {
                    @Override
                    public void onResponse(List<BillModel> models) {
                        hideProgressBar();
                        for (BillModel model : models) {
                            billList.add(model);
                            adapter.notifyItemInserted(billList.size());
                        }
                    }
                    @Override
                    public void onError(ANError e) {
                        hideProgressBar();
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

    @OnClick({R.id.fragment_bill_container_toolbar_more, R.id.fragment_bill_button_toolbar_more})
    public void onClickToolbarMore(View view) {
        PopupMenu popup = new PopupMenu(getContext(), view);
        popup.setOnMenuItemClickListener(this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.toolbar_bill, popup.getMenu());
        popup.show();
    }

    @OnClick({R.id.fragment_bill_container_toolbar_arrow_back, R.id.fragment_bill_button_toolbar_arrow_back})
    public void onToolbarArrowBackClick() {
        activity.onBackPressed();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.toolbar_menu_refresh:
                billList.clear();
                adapter.notifyDataSetChanged();
                loadBill();
                return true;
            case R.id.toolbar_menu_summary:
                if (cdSummary.getVisibility() == View.GONE) {
                    showStudySummary();
                    createSummary();
                } else {
                    hideStudySummary();
                }
                return true;
            default:
                return false;
        }
    }

    public void createSummary() {
        tvCount.setText(String.valueOf(billList.size()));
        Integer total = 0;
        int step = 0;
        for (BillModel model : billList) {
            total += Integer.valueOf(model.getJumlah_bayar().replaceAll("\\D+",""));
            if (step == billList.size() - 1) {
                String str = "Rp. " + NumberFormat.getNumberInstance(Locale.US).format(total).replace(",", ".");

                tvTotal.setText(str);
            }
            step++;
        }
    }
}
