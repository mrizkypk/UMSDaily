package com.mrizkypk.umsdaily.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.google.firebase.database.DatabaseReference;
import com.mrizkypk.umsdaily.R;
import com.mrizkypk.umsdaily.activity.HomeActivity;
import com.mrizkypk.umsdaily.helper.ApiHelper;
import com.mrizkypk.umsdaily.model.ResponseErrorModel;
import com.mrizkypk.umsdaily.model.SemesterModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InformationFragment extends Fragment {
    public HomeActivity activity;
    public List<SemesterModel> semesterList;

    @BindView(R.id.fragment_information_layout)
    ConstraintLayout layout;

    @BindView(R.id.fragment_information_progressbar)
    ProgressBar progressbar;

    @BindView(R.id.fragment_information_container_information_khs)
    LinearLayout llKhs;

    @BindView(R.id.fragment_information_container_information_study)
    LinearLayout llStudy;

    @BindView(R.id.fragment_information_container_information_bill)
    LinearLayout llBill;

    @BindView(R.id.fragment_information_container_menu_schedule)
    LinearLayout llSchedule;

    @BindView(R.id.fragment_information_container_menu_assignment)
    LinearLayout llAssignment;

    @BindView(R.id.fragment_information_container_menu_information)
    LinearLayout llInformation;

    @BindView(R.id.fragment_information_badge_home)
    public TextView tvBadgeHome;

    @BindView(R.id.fragment_information_badge_schedule)
    public TextView tvBadgeSchedule;

    @BindView(R.id.fragment_information_badge_assignment)
    public TextView tvBadgeAssignment;

    @BindView(R.id.fragment_information_badge_information)
    public TextView tvBadgeInformation;

    @BindView(R.id.fragment_information_badge_information2)
    public TextView tvBadgeInformation2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = ((HomeActivity) getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_information, container, false);
        ButterKnife.bind(this, view);

        if (!activity.spm.getUserType().equals("student")) {
            llKhs.setVisibility(View.GONE);
            llStudy.setVisibility(View.GONE);
            llBill.setVisibility(View.GONE);
        }

        if (activity.spm.getUserType().equals("staff")) {
            llSchedule.setVisibility(View.GONE);
            llAssignment.setVisibility(View.GONE);

            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) llInformation.getLayoutParams();
            params.startToEnd = R.id.guideline2;
            params.endToStart = R.id.guideline3;

            llInformation.setLayoutParams(params);
        }

        //Update badge

        activity.updateAllBadge();

        return view;
    }

    @OnClick(R.id.fragment_information_container_information_khs)
    public void onInformationKhsClick() {
        showProgressBar();
        AndroidNetworking.get(ApiHelper.getHost() + "/daftar_semester_khs.php")
                .addQueryParameter("nim", activity.spm.getUserId())
                .getResponseOnlyFromNetwork()
                .build()
                .getAsObjectList(SemesterModel.class, new ParsedRequestListener<List<SemesterModel>>() {
                    @Override
                    public void onResponse(List<SemesterModel> models) {
                        hideProgressBar();
                        semesterList = models;
                        String[] cs = new String[models.size()];
                        for (SemesterModel model : models) {
                            cs[models.indexOf(model)] = model.getNama();
                        }
                        showDialogSemesterKhs(cs);
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

    @OnClick(R.id.fragment_information_container_information_about)
    public void onInformationAboutClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("Version 1.0.6")
                .setTitle("UMS Daily");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @OnClick(R.id.fragment_information_container_information_study)
    public void onInformationStudyClick() {
        activity.openStudyFragment();
    }

    @OnClick(R.id.fragment_information_container_information_bill)
    public void onInformationBillClick() {
        activity.openBillFragment();
    }
    public void showDialogSemesterKhs(String[] cs) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Pilih Semester")
                .setItems(cs, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SemesterModel model = semesterList.get(which);
                        activity.openKhsFragment(model.getId(), model.getNama());
                    }
                });
        builder.create();
        builder.show();
    }

    @OnClick(R.id.fragment_information_container_information_announcement)
    public void onInformationAnnouncementClick() {
        activity.openAnnouncementFragment();
    }

    public void showProgressBar() {
        progressbar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressbar.setVisibility(View.GONE);
    }

    @OnClick({R.id.fragment_information_container_menu_schedule, R.id.fragment_information_button_menu_schedule, R.id.fragment_information_text_menu_schedule})
    public void onMenuScheduleClick() {
        activity.openScheduleFragment();
    }

    @OnClick({R.id.fragment_information_container_menu_assignment, R.id.fragment_information_button_menu_assignment, R.id.fragment_information_text_menu_assignment})
    public void onMenuAssignmentClick() {
        activity.openAssignmentFragment();
    }

    @OnClick({R.id.fragment_information_container_menu_home, R.id.fragment_information_button_menu_home, R.id.fragment_information_text_menu_home})
    public void onMenuHomeClick() {
        activity.openChatHomeFragment(0);
    }

    @OnClick({R.id.fragment_information_container_menu_search, R.id.fragment_information_button_menu_search, R.id.fragment_information_text_menu_search})
    public void onMenuSearchClick() {
        activity.openSearchFragment();
    }
}
