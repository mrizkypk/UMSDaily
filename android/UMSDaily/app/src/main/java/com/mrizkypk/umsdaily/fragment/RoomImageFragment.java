package com.mrizkypk.umsdaily.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.mrizkypk.umsdaily.adapter.RoomImageMoreAdapter;
import com.mrizkypk.umsdaily.model.ChatRoomModel;

import java.util.LinkedList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RoomImageFragment extends Fragment {
    public HomeActivity activity;
    public String roomId;
    public String roomTitle;
    public LinkedList<ChatRoomModel> imageList;
    public RecyclerView imageRecyclerview;
    public RecyclerView.Adapter imageAdapter;
    public RecyclerView.LayoutManager imageLayoutmanager;
    public Query roomImageQuery;

    @BindView(R.id.fragment_room_image_container_toolbar)
    RelativeLayout rlToolbar;

    @BindView(R.id.fragment_room_image_text_toolbar_title)
    TextView tvToolbarTitle;

    @BindView(R.id.fragment_room_image_text_toolbar_subtitle)
    TextView tvToolbarSubtitle;

    public RoomImageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = ((HomeActivity) getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room_image, container, false);
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
        tvToolbarSubtitle.setText("Manajemen Gambar");

        imageList = new LinkedList<>();
        imageLayoutmanager = new GridLayoutManager(getContext(), 3);
        imageRecyclerview = view.findViewById(R.id.fragment_room_image_recyclerview_image);
        imageRecyclerview.setHasFixedSize(true);
        imageRecyclerview.setLayoutManager(imageLayoutmanager);
        imageAdapter = new RoomImageMoreAdapter(getContext(), imageList);
        imageRecyclerview.setAdapter(imageAdapter);

        loadRoomImage(roomId);

        rlToolbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
    }

    public void loadRoomImage(String id) {
        roomImageQuery = activity.db
                .getReference()
                .child("chat")
                .child(id)
                .orderByChild("type")
                .equalTo("image");

        roomImageQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ChatRoomModel model = ds.getValue(ChatRoomModel.class);
                    if (model.getFile_download_url() != null) {
                        imageList.addFirst(model);
                    }
                }
                imageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @OnClick({R.id.fragment_room_image_container_toolbar_arrow_back, R.id.fragment_room_image_button_toolbar_arrow_back})
    public void onClickToolbarArrowBack() {
        activity.onBackPressed();
    }
}
