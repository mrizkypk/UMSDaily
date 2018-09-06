package com.mrizkypk.umsdaily.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.mrizkypk.umsdaily.R;
import com.mrizkypk.umsdaily.activity.AttachImageActivity;
import com.mrizkypk.umsdaily.activity.HomeActivity;
import com.mrizkypk.umsdaily.helper.DateHelper;
import com.mrizkypk.umsdaily.model.ChatRoomModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

public class AccountFragment extends Fragment {
    public HomeActivity activity;
    public DatabaseReference ref;
    public ChatRoomModel model;

    @BindView(R.id.fragment_account_layout)
    ConstraintLayout layout;

    @BindView(R.id.fragment_account_text_id)
    TextView tvId;

    @BindView(R.id.fragment_account_text_name)
    TextView tvName;

    @BindView(R.id.fragment_account_image)
    ImageView ivProfile;

    @BindView(R.id.fragment_account_progressbar_percentage)
    ProgressBar progressbar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = ((HomeActivity) getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvId.setText(activity.spm.getUserId());
        tvName.setText(activity.spm.getUserName());

        Uri uri = Uri.parse(activity.spm.getUserAvatarUrl());
        Glide.with(this).load(uri).into(ivProfile);

        model = new ChatRoomModel();
        model.setSender_name(activity.spm.getUserName());
        model.setFile_download_url(uri.toString());
        model.setTimestamp(DateHelper.getTimestamp());

        ref = activity.db.getReference("user").child(activity.spm.getUserId());
    }

    @OnClick(R.id.fragment_account_button_change_avatar)
    public void onChangeAvatarButtonClick() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            startActivityForResult(intent, 1);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            progressbar.setVisibility(View.VISIBLE);

            Uri fileUri = data.getData();
            StorageReference storageRef = activity.storage.getReference()
                    .child("user")
                    .child("avatar")
                    .child(activity.spm.getUserId());

            StorageTask uploadTask = storageRef.putFile(fileUri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    int progress = (100 * (int) taskSnapshot.getBytesTransferred()) / (int) taskSnapshot.getTotalByteCount();
                    progressbar.setProgress(progress);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            progressbar.setVisibility(View.GONE);

                            String fileDownloadUrl = uri.toString();
                            model.setFile_download_url(fileDownloadUrl);
                            activity.spm.setUserAvatarUrl(fileDownloadUrl);
                            ref.getRef().child("avatar_url").setValue(fileDownloadUrl);
                            Glide.with(activity).load(fileDownloadUrl).into(ivProfile);
                        }
                    });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception exception) {
                            progressbar.setVisibility(View.GONE);
                            Log.d(activity.TAG, exception.getMessage());
                        }
                    });
        }
    }

    @OnClick(R.id.fragment_account_image)
    public void onAvatarImageClick() {
        activity.openImageFromAccountFragment(model);
    }

    @OnClick({R.id.fragment_account_container_toolbar_arrow_back, R.id.fragment_account_button_toolbar_arrow_back})
    public void onToolbarArrowBackClick() {
        activity.onBackPressed();
    }
}
