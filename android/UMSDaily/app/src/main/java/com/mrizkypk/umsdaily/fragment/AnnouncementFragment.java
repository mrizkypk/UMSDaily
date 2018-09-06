package com.mrizkypk.umsdaily.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mrizkypk.umsdaily.R;
import com.mrizkypk.umsdaily.activity.HomeActivity;
import com.mrizkypk.umsdaily.adapter.AnnouncementAdapter;
import com.mrizkypk.umsdaily.adapter.AssignmentAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AnnouncementFragment extends Fragment {
    private HomeActivity activity;
    private RecyclerView recyclerview;
    private RecyclerView.LayoutManager layoutManager;

    @BindView(R.id.fragment_announcement_image_add)
    ImageView ivAdd;

    @BindView(R.id.fragment_announcement_text_empty)
    public TextView tvEmpty;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = ((HomeActivity) getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_announcement, container, false);
        ButterKnife.bind(this, view);

        if (!activity.spm.getUserType().equals("staff")) {
            ivAdd.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerview = view.findViewById(R.id.fragment_announcement_recyclerview);
        recyclerview.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerview.setLayoutManager(layoutManager);

        activity.announcementAdapter = new AnnouncementAdapter(activity, activity.announcementModelList);
        recyclerview.setAdapter(activity.announcementAdapter);

        if (activity.announcementModelList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.fragment_announcement_container_toolbar_arrow_back, R.id.fragment_announcement_button_toolbar_arrow_back})
    public void onToolbarArrowBackClick() {
        activity.onBackPressed();
    }

    @OnClick(R.id.fragment_announcement_image_add)
    public void onButtonAddClick() {
        activity.openAnnouncementAddFragment(1);
    }

}