package com.mrizkypk.umsdaily.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mrizkypk.umsdaily.R;
import com.mrizkypk.umsdaily.activity.HomeActivity;
import com.mrizkypk.umsdaily.adapter.AssignmentAdapter;
import com.mrizkypk.umsdaily.adapter.BillAdapter;
import com.mrizkypk.umsdaily.model.AssignmentModel;
import com.mrizkypk.umsdaily.model.BillModel;

import java.util.LinkedList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AssignmentFragment extends Fragment {
    private HomeActivity activity;
    private RecyclerView recyclerview;
    private RecyclerView.LayoutManager layoutManager;

    @BindView(R.id.fragment_assignment_image_add)
    ImageView ivAdd;

    @BindView(R.id.fragment_assignment_text_empty)
    public TextView tvEmpty;

    @BindView(R.id.fragment_assignment_badge_home)
    public TextView tvBadgeHome;

    @BindView(R.id.fragment_assignment_badge_schedule)
    public TextView tvBadgeSchedule;

    @BindView(R.id.fragment_assignment_badge_assignment)
    public TextView tvBadgeAssignment;

    @BindView(R.id.fragment_assignment_badge_information)
    public TextView tvBadgeInformation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = ((HomeActivity) getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_assignment, container, false);
        ButterKnife.bind(this, view);

        if (!activity.spm.getUserType().equals("lecturer")) {
            ivAdd.setVisibility(View.GONE);
        }

        //Update badge

        activity.updateAllBadge();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerview = view.findViewById(R.id.fragment_assignment_recyclerview);
        recyclerview.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerview.setLayoutManager(layoutManager);

        activity.assignmentAdapter = new AssignmentAdapter(activity, activity.assignmentModelList);
        recyclerview.setAdapter(activity.assignmentAdapter);

        if (activity.assignmentModelList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.fragment_assignment_image_add)
    public void onButtonAddClick() {
        activity.openAssignmentAddFragment(1);
    }

    @OnClick({R.id.fragment_assignment_container_menu_home, R.id.fragment_assignment_button_menu_home, R.id.fragment_assignment_text_menu_home})
    public void onMenuHomeClick(View view) {
        activity.openChatHomeFragment(0);
    }

    @OnClick({R.id.fragment_assignment_container_menu_schedule, R.id.fragment_assignment_button_menu_schedule, R.id.fragment_assignment_text_menu_schedule})
    public void onMenuScheduleClick(View view) {
        activity.openScheduleFragment();
    }

    @OnClick({R.id.fragment_assignment_container_menu_information, R.id.fragment_assignment_button_menu_information, R.id.fragment_assignment_text_menu_information})
    public void onMenuInformationClick(View view) {
        activity.openInformationFragment(0);
    }

    @OnClick({R.id.fragment_assignment_container_menu_search, R.id.fragment_assignment_button_menu_search, R.id.fragment_assignment_text_menu_search})
    public void onMenuSearchClick(View view) {
        activity.openSearchFragment();
    }
}
