package com.mrizkypk.umsdaily.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.mrizkypk.umsdaily.R;
import com.mrizkypk.umsdaily.activity.HomeActivity;
import com.mrizkypk.umsdaily.helper.DateHelper;
import com.mrizkypk.umsdaily.helper.NotificationHelper;
import com.mrizkypk.umsdaily.helper.PathHelper;
import com.mrizkypk.umsdaily.model.AnnouncementModel;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

public class AnnouncementAddFragment extends Fragment {
    public HomeActivity activity;

    @BindView(R.id.fragment_announcement_add_form_text_title)
    EditText etTitle;

    @BindView(R.id.fragment_announcement_add_form_text_content)
    EditText etContent;

    @BindView(R.id.fragment_announcement_add_form_text_url)
    EditText etUrl;

    @BindView(R.id.fragment_announcement_add_form_text_filter)
    EditText etFilter;

    @BindView(R.id.fragment_announcement_add_form_button_add)
    Button btnAdd;

    @BindView(R.id.fragment_announcement_add_progressbar)
    ProgressBar progressbar;

    @BindView(R.id.fragment_announcement_add_form_image_content)
    ImageView ivContent;

    public Uri fileUri;
    public String filePath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = ((HomeActivity) getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_announcement_add, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    public void sendNotification(String id, String filter, String title, String imageUrl) {
        NotificationHelper.send(id,"ANNOUNCEMENT", filter, "", activity.spm.getUserId(), activity.spm.getUserName(), "", "", "", imageUrl, "Pengumuman", title, "");
    }

    @OnClick(R.id.fragment_announcement_add_form_button_add)
    public void onButtonAddClick() {
        btnAdd.setVisibility(View.GONE);
        showProgressBar();

        final AnnouncementModel model = new AnnouncementModel();
        model.setSender_id(activity.spm.getUserId());
        model.setSender_name(activity.spm.getUserName());
        model.setTitle(etTitle.getText().toString());
        model.setContent(etContent.getText().toString());
        model.setUrl(etUrl.getText().toString());
        model.setTimestamp(DateHelper.getTimestamp());
        model.setFilter(etFilter.getText().toString());

        if (fileUri != null) {
            Long timestamp = DateHelper.getTimestamp();
            filePath = activity.spm.getUserId() + "_" + timestamp + "_" + getFileName(fileUri);

            StorageReference storageRef = activity.storage.getReference()
                    .child("announcement")
                    .child("images")
                    .child(fileUri.toString());

            StorageTask uploadTask = storageRef.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    hideProgressBar();

                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String fileDownloadUrl = uri.toString();

                            model.setImage_url(fileDownloadUrl);
                            DatabaseReference ref = activity.db.getReference("announcement");
                            model.setId(ref.push().getKey());
                            ref.child(model.getId()).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    fileUri = null;
                                    sendNotification(model.getId(), etFilter.getText().toString(), etTitle.getText().toString(), fileDownloadUrl);
                                    hideSoftKeyboard();
                                    activity.onBackPressed();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(Exception e) {
                                    fileUri = null;
                                    btnAdd.setVisibility(View.VISIBLE);
                                    hideProgressBar();
                                    hideSoftKeyboard();

                                    Log.d(activity.TAG, e.getMessage());
                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    fileUri = null;
                    hideSoftKeyboard();
                    Log.d(activity.TAG, e.getMessage());
                }
            });
        } else {
            model.setImage_url("");
            DatabaseReference ref = activity.db.getReference("announcement");
            model.setId(ref.push().getKey());
            ref.child(model.getId()).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    sendNotification(model.getId(), etFilter.getText().toString(), etTitle.getText().toString(), "");
                    hideSoftKeyboard();
                    activity.onBackPressed();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    fileUri = null;
                    btnAdd.setVisibility(View.VISIBLE);
                    hideSoftKeyboard();
                    hideProgressBar();

                    Log.d(activity.TAG, e.getMessage());
                }
            });
        }
    }

    public String getFileName(Uri uri) {
        String path = PathHelper.getPathFromUri(activity, uri);
        File file = new File(path);

        return file.getName();
    }

    public void selectImage() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            startActivityForResult(intent, 1);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            fileUri = data.getData();

            Glide.with(this).load(fileUri).into(ivContent);
            ivContent.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.fragment_announcement_add_form_image_attach)
    public void onImageAttachClick() {
        selectImage();
    }

    @OnClick({R.id.fragment_announcement_add_container_toolbar_arrow_back, R.id.fragment_announcement_add_button_toolbar_arrow_back})
    public void onToolbarArrowBackClick() {
        activity.onBackPressed();

    }

    public void showProgressBar() {
        progressbar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressbar.setVisibility(View.GONE);
    }

    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager)  getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }
}