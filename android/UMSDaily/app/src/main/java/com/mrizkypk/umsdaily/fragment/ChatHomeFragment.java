package com.mrizkypk.umsdaily.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mrizkypk.umsdaily.R;
import com.mrizkypk.umsdaily.activity.QuizActivity;
import com.mrizkypk.umsdaily.adapter.HomeAdapter;
import com.mrizkypk.umsdaily.activity.HomeActivity;
import com.mrizkypk.umsdaily.other.NoAnimationItemAnimator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatHomeFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {
    public HomeActivity activity;
    public RecyclerView recyclerview;
    public RecyclerView.LayoutManager layoutmanager;

    @BindView(R.id.fragment_chat_home_text_empty)
    public TextView tvEmpty;

    @BindView(R.id.fragment_chat_home_container_menu_schedule)
    LinearLayout llSchedule;

    @BindView(R.id.fragment_chat_home_container_menu_assignment)
    LinearLayout llAssignment;

    @BindView(R.id.fragment_chat_home_container_menu_information)
    LinearLayout llInformation;

    @BindView(R.id.fragment_chat_home_badge_home)
    public TextView tvBadgeHome;

    @BindView(R.id.fragment_chat_home_badge_schedule)
    public TextView tvBadgeSchedule;

    @BindView(R.id.fragment_chat_home_badge_assignment)
    public TextView tvBadgeAssignment;

    @BindView(R.id.fragment_chat_home_badge_information)
    public TextView tvBadgeInformation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = ((HomeActivity) getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_home, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        layoutmanager = new LinearLayoutManager(getContext());
        activity.homeAdapter = new HomeAdapter(getContext(), activity.homeModelList, activity.spm);

        recyclerview = view.findViewById(R.id.fragment_chat_home_recyclerview);
        recyclerview.setItemAnimator(new NoAnimationItemAnimator());
        recyclerview.setHasFixedSize(true);

        recyclerview.setLayoutManager(layoutmanager);

        recyclerview.setAdapter(activity.homeAdapter);

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
    }

    @OnClick({R.id.fragment_chat_home_container_toolbar_plus, R.id.fragment_chat_home_button_toolbar_plus})
    public void onToolbarPlusClick(View view) {
        activity.openComposeFragment(1);
    }

    @OnClick({R.id.fragment_chat_home_container_toolbar_more, R.id.fragment_chat_home_button_toolbar_more})
    public void onToolbarMoreClick(View view) {
        PopupMenu popup = new PopupMenu(getContext(), view);
        popup.setOnMenuItemClickListener(this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.toolbar_home, popup.getMenu());
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_menu_account:
                activity.openAccountFragment(1);
                return true;
            case R.id.toolbar_menu_quiz:
                Intent intent = new Intent(activity, QuizActivity.class);
                activity.startActivity(intent);
                return true;
            case R.id.toolbar_menu_logout:
                activity.logout();
                return true;
            default:
                return true;
        }
    }

    @OnClick({R.id.fragment_chat_home_container_menu_schedule, R.id.fragment_chat_home_button_menu_schedule, R.id.fragment_chat_home_text_menu_schedule})
    public void onMenuScheduleClick(View view) {
        activity.openScheduleFragment();
    }

    @OnClick({R.id.fragment_chat_home_container_menu_assignment, R.id.fragment_chat_home_button_menu_assignment, R.id.fragment_chat_home_text_menu_assignment})
    public void onMenuAssignmentClick(View view) {
        activity.openAssignmentFragment();
    }

    @OnClick({R.id.fragment_chat_home_container_menu_information, R.id.fragment_chat_home_button_menu_information, R.id.fragment_chat_home_text_menu_information})
    public void onMenuInformationClick(View view) {
        activity.openInformationFragment(0);
    }

    @OnClick({R.id.fragment_chat_home_container_menu_search, R.id.fragment_chat_home_button_menu_search, R.id.fragment_chat_home_text_menu_search})
    public void onMenuSearchClick(View view) {
        activity.openSearchFragment();
    }
}
