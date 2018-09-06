package com.mrizkypk.umsdaily.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import com.mrizkypk.umsdaily.R;
import com.mrizkypk.umsdaily.adapter.ScheduleAdapter;
import com.mrizkypk.umsdaily.activity.HomeActivity;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScheduleFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {
    public HomeActivity activity;
    public RecyclerView recyclerview;
    public RecyclerView.LayoutManager layoutManager;

    @BindView(R.id.fragment_schedule_progressbar)
    ProgressBar progressbar;

    @BindView(R.id.fragment_schedule_text_empty)
    public TextView tvEmpty;

    @BindView(R.id.fragment_schedule_badge_home)
    public TextView tvBadgeHome;

    @BindView(R.id.fragment_schedule_badge_schedule)
    public TextView tvBadgeSchedule;

    @BindView(R.id.fragment_schedule_badge_assignment)
    public TextView tvBadgeAssignment;

    @BindView(R.id.fragment_schedule_badge_information)
    public TextView tvBadgeInformation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = ((HomeActivity) getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerview = view.findViewById(R.id.fragment_schedule_recylerview);
        recyclerview.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());

        recyclerview.setLayoutManager(layoutManager);

        if (activity.scheduleModelList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
        }

        activity.scheduleAdapter = new ScheduleAdapter(getContext(), activity.scheduleModelList);
        recyclerview.setAdapter(activity.scheduleAdapter);

        //Update badge home

        activity.updateAllBadge();
    }

    public void showProgressBar() {
        if (progressbar != null) {
            progressbar.setVisibility(View.VISIBLE);
        }
    }

    public void hideProgressBar() {
        if (progressbar != null) {
            progressbar.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.fragment_schedule_container_toolbar_more, R.id.fragment_schedule_button_toolbar_more})
    public void onToolbarMoreClick(View view) {
        PopupMenu popup = new PopupMenu(getContext(), view);
        popup.setOnMenuItemClickListener(this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.toolbar_schedule, popup.getMenu());
        popup.show();
    }

    @OnClick({R.id.fragment_schedule_container_menu_home, R.id.fragment_schedule_button_menu_home, R.id.fragment_schedule_text_menu_home})
    public void onMenuHomeClick() {
        activity.openChatHomeFragment(0);
    }

    @OnClick({R.id.fragment_schedule_container_menu_assignment, R.id.fragment_schedule_button_menu_assignment, R.id.fragment_schedule_text_menu_assignment})
    public void onMenuAssignmentClick() {
        activity.openAssignmentFragment();
    }

    @OnClick({R.id.fragment_schedule_container_menu_information, R.id.fragment_schedule_button_menu_information, R.id.fragment_schedule_text_menu_information})
    public void onMenuInformationClick() {
        activity.openInformationFragment(0);
    }

    @OnClick({R.id.fragment_schedule_container_menu_search, R.id.fragment_schedule_button_menu_search, R.id.fragment_schedule_text_menu_search})
    public void onMenuSearch() {
        activity.openSearchFragment();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.toolbar_menu_refresh:
                activity.scheduleModelList.clear();
                activity.scheduleAdapter.notifyDataSetChanged();
                switch (activity.spm.getUserType()) {
                    case "student":
                        activity.loadStudentSchedule("network");
                        break;
                    case "lecturer":
                        activity.loadLecturerSchedule("network");
                        break;
                }
                return true;
            default:
                return false;
        }
    }
}
