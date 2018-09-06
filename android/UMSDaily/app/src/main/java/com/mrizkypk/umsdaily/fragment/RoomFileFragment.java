package com.mrizkypk.umsdaily.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mrizkypk.umsdaily.R;
import com.mrizkypk.umsdaily.activity.HomeActivity;
import com.mrizkypk.umsdaily.adapter.RoomFileMoreAdapter;
import com.mrizkypk.umsdaily.adapter.RoomImageMoreAdapter;
import com.mrizkypk.umsdaily.model.ChatRoomModel;

import java.util.LinkedList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RoomFileFragment extends Fragment {
    public HomeActivity activity;
    public String roomId;
    public String roomTitle;
    public LinkedList<ChatRoomModel> fileList;
    public RecyclerView fileRecyclerview;
    public RecyclerView.Adapter fileAdapter;
    public RecyclerView.LayoutManager fileLayoutmanager;
    public Query roomFileQuery;

    @BindView(R.id.fragment_room_file_container_toolbar)
    RelativeLayout rlToolbar;

    @BindView(R.id.fragment_room_file_text_toolbar_title)
    TextView tvToolbarTitle;

    @BindView(R.id.fragment_room_file_text_toolbar_subtitle)
    TextView tvToolbarSubtitle;

    public RoomFileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = ((HomeActivity) getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room_file, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        roomId = getArguments().getString("BUNDLE_ROOM_ID");
        roomTitle = getArguments().getString("BUNDLE_ROOM_TITLE");

        tvToolbarTitle.setText(roomTitle);
        tvToolbarSubtitle.setText("Manajemen Berkas");

        fileList = new LinkedList<>();
        fileLayoutmanager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        fileRecyclerview = view.findViewById(R.id.fragment_room_file_recyclerview_file);
        fileRecyclerview.setHasFixedSize(true);
        fileRecyclerview.setLayoutManager(fileLayoutmanager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(fileRecyclerview.getContext(), LinearLayout.VERTICAL);
        fileRecyclerview.addItemDecoration(dividerItemDecoration);
        fileAdapter = new RoomFileMoreAdapter(getContext(), fileList);
        fileRecyclerview.setAdapter(fileAdapter);

        loadRoomFile(roomId);

        rlToolbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
    }

    public void loadRoomFile(String id) {
        roomFileQuery = activity.db
                .getReference()
                .child("chat")
                .child(id)
                .orderByChild("type")
                .equalTo("file");

        roomFileQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ChatRoomModel model = ds.getValue(ChatRoomModel.class);
                    if (model.getFile_download_url() != null) {
                        fileList.addFirst(model);
                    }
                }
                fileAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @OnClick({R.id.fragment_room_file_container_toolbar_arrow_back, R.id.fragment_room_file_button_toolbar_arrow_back})
    public void onClickToolbarArrowBack() {
        activity.onBackPressed();
    }
}
