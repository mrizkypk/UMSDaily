package com.mrizkypk.umsdaily.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mrizkypk.umsdaily.R;
import com.mrizkypk.umsdaily.activity.HomeActivity;
import com.mrizkypk.umsdaily.adapter.RoomFileAdapter;
import com.mrizkypk.umsdaily.adapter.RoomImageAdapter;
import com.mrizkypk.umsdaily.adapter.RoomUserAdapter;
import com.mrizkypk.umsdaily.model.ChatHomeModel;
import com.mrizkypk.umsdaily.model.ChatRoomModel;
import com.mrizkypk.umsdaily.model.RoomModel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RoomFragment extends Fragment implements PopupMenu.OnMenuItemClickListener{

    public HomeActivity activity;

    public String roomId;
    public String roomTitle;
    public String roomType;
    public Integer userCount;
    public Integer imageCount;
    public Integer fileCount;
    public RoomModel roomModel;
    public ArrayList<String> userList;
    public LinkedList<ChatRoomModel> fileList;
    public LinkedList<ChatRoomModel> imageList;
    public RecyclerView userRecyclerview;
    public RecyclerView fileRecyclerview;
    public RecyclerView imageRecyclerview;
    public RecyclerView.Adapter userAdapter;
    public RecyclerView.Adapter fileAdapter;
    public RecyclerView.Adapter imageAdapter;
    public RecyclerView.LayoutManager userLayoutmanager;
    public RecyclerView.LayoutManager fileLayoutmanager;
    public RecyclerView.LayoutManager imageLayoutmanager;

    @BindView(R.id.fragment_room_container_toolbar)
    RelativeLayout rlToolbar;

    @BindView(R.id.fragment_room_text_toolbar_title)
    TextView tvToolbarTitle;

    @BindView(R.id.fragment_room_text_toolbar_subtitle)
    TextView tvToolbarSubtitle;

    @BindView(R.id.fragment_room_text_label_user_count)
    TextView tvUserCount;

    @BindView(R.id.fragment_room_text_label_file_count)
    TextView tvFileCount;

    @BindView(R.id.fragment_room_text_label_image_count)
    TextView tvImageCount;

    public DatabaseReference roomRef;
    public Query roomImageQuery;
    public Query roomFileQuery;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = ((HomeActivity) getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        roomId = getArguments().getString("BUNDLE_ROOM_ID");
        roomTitle = getArguments().getString("BUNDLE_ROOM_TITLE");
        roomType = getArguments().getString("BUNDLE_ROOM_TYPE");

        tvToolbarTitle.setText(roomTitle);
        tvToolbarSubtitle.setText("Manajemen");

        fileList = new LinkedList<>();
        fileLayoutmanager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        fileRecyclerview = view.findViewById(R.id.fragment_room_recyclerview_file);
        fileRecyclerview.setHasFixedSize(true);
        fileRecyclerview.setLayoutManager(fileLayoutmanager);
        fileAdapter = new RoomFileAdapter(getContext(), fileList);
        fileRecyclerview.setAdapter(fileAdapter);

        imageList = new LinkedList<>();
        imageLayoutmanager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        imageRecyclerview = view.findViewById(R.id.fragment_room_recyclerview_image);
        imageRecyclerview.setHasFixedSize(true);
        imageRecyclerview.setLayoutManager(imageLayoutmanager);
        imageAdapter = new RoomImageAdapter(getContext(), imageList);
        imageRecyclerview.setAdapter(imageAdapter);

        userList = new ArrayList<>();
        userLayoutmanager = new LinearLayoutManager(getContext());
        userRecyclerview = view.findViewById(R.id.fragment_room_recyclerview_user);
        userRecyclerview.setHasFixedSize(true);
        userRecyclerview.setLayoutManager(userLayoutmanager);
        userAdapter = new RoomUserAdapter(getContext(), userList);
        userRecyclerview.setAdapter(userAdapter);

        if (roomType.equals("public")) {
            loadRoom(roomId);
        } else {
            tvUserCount.setVisibility(View.GONE);
        }
        loadRoomImage(roomId);
        loadRoomFile(roomId);

        rlToolbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
    }

    public void loadRoom(String id) {
        roomRef = activity.db
                .getReference("room")
                .child(id);

        roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                roomModel = dataSnapshot.getValue(RoomModel.class);

                userCount = roomModel.getMember().size();
                tvUserCount.setText("Anggota (" + userCount + ")");

                userList.clear();
                userList.addAll(roomModel.getMember().keySet());

                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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

        roomImageQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                imageCount = (int) dataSnapshot.getChildrenCount();
                tvImageCount.setText("Gambar (" + imageCount + ")");

                int limit = 0;

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (limit < 10) {
                        ChatRoomModel model = ds.getValue(ChatRoomModel.class);
                        if (model.getFile_download_url() != null) {
                            if (!imageList.contains(model)) {
                                imageList.addFirst(model);
                                limit++;
                            }
                        } else {
                            imageCount--;
                            tvImageCount.setText("Gambar (" + imageCount + ")");
                        }
                    }
                }
                imageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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

        roomFileQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fileCount = (int) dataSnapshot.getChildrenCount();
                tvFileCount.setText("Berkas (" + fileCount + ")");
                int limit = 0;

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (limit < 10) {
                        ChatRoomModel model = ds.getValue(ChatRoomModel.class);
                        if (model.getFile_download_url() != null) {
                            if (!fileList.contains(model)) {
                                fileList.addFirst(model);
                                limit++;
                            }
                        } else {
                            fileCount--;
                            tvFileCount.setText("Berkas (" + fileCount + ")");
                        }
                    }
                }
                fileAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @OnClick(R.id.fragment_room_text_label_file_count)
    public void onClickLabelFile() {
        if (fileCount > 0) {
            if (roomType.equals("public")) {
                activity.openRoomFileFragment(roomId, roomTitle, 1);
            } else {
                activity.openPrivateRoomFileFragment(roomId, roomTitle, 1);
            }
        }
    }

    @OnClick(R.id.fragment_room_text_label_image_count)
    public void onClickLabelImage() {
        if (imageCount > 0) {
            if (roomType.equals("public")) {
                activity.openRoomImageFragment(roomId, roomTitle, 1);
            } else {
                activity.openPrivateRoomImageFragment(roomId, roomTitle, 1);
            }
        }
    }

    @OnClick({R.id.fragment_room_container_toolbar_arrow_back, R.id.fragment_room_button_toolbar_arrow_back})
    public void onClickToolbarArrowBack() {
        activity.onBackPressed();
    }

    public void leave() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("Anda yakin untuk keluar dari obrolan ini?")
                .setTitle("Keluar Obrolan");
        builder.setPositiveButton("Keluar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                activity.db.getReference().child("room").child(roomId + "/member/" + activity.spm.getUserId()).setValue(false).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        ChatHomeModel model = new ChatHomeModel();
                        model.setRoom_id(roomId);
                        activity.homeModelList.remove(model);
                        activity.homeAdapter.notifyDataSetChanged();
                        activity.openHomeFromRoomFragment(2);
                    }
                });
            }
        });
        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @OnClick({R.id.fragment_room_container_toolbar_more, R.id.fragment_room_button_toolbar_more})
    public void onToolbarMoreClick(View view) {
        PopupMenu popup = new PopupMenu(getContext(), view);
        popup.setOnMenuItemClickListener(this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.toolbar_room, popup.getMenu());
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_menu_leave:
                leave();
                return true;
            default:
                return true;
        }
    }
}
