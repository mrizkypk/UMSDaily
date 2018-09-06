package com.mrizkypk.umsdaily.fragment;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.mrizkypk.umsdaily.BuildConfig;
import com.mrizkypk.umsdaily.R;
import com.mrizkypk.umsdaily.activity.HomeActivity;
import com.mrizkypk.umsdaily.helper.DateHelper;
import com.mrizkypk.umsdaily.model.ChatRoomModel;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;


public class ImageFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {
    public HomeActivity activity;
    public ImageView ivImage;
    public TextView tvTitle;
    public TextView tvTitle2;
    public TextView tvSubtitle;
    public TextView tvMessage;
    public String roomId;
    public String roomType;
    public String sender;
    public String message;
    public String imageUrl;
    public String imageName;
    public String imageSize;
    public String localImagePath;
    public Long timestamp;
    public ProgressBar progressbar;
    public ProgressBar progressbarPercentage;
    public ConstraintLayout layout;
    public RelativeLayout toolbar;
    public DatabaseReference refRoom;
    public boolean isFullscreen;

    @BindView(R.id.fragment_image_container_toolbar_edit)
    LinearLayout llToolbarEdit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = ((HomeActivity) getActivity());
        localImagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/UMSDaily/";
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image, container, false);
        ButterKnife.bind(this, view);

        roomId = getArguments().getString("BUNDLE_ROOM_ID");
        roomType = getArguments().getString("BUNDLE_ROOM_TYPE");
        sender = getArguments().getString("BUNDLE_SENDER");
        message = getArguments().getString("BUNDLE_MESSAGE");
        imageUrl = getArguments().getString("BUNDLE_IMAGE_URL");
        imageName = getArguments().getString("BUNDLE_IMAGE_NAME");
        imageSize = getArguments().getString("BUNDLE_IMAGE_SIZE");
        timestamp = getArguments().getLong("BUNDLE_TIMESTAMP");

        layout = view.findViewById(R.id.fragment_image_layout);
        ivImage = view.findViewById(R.id.fragment_image_image);
        tvTitle = view.findViewById(R.id.fragment_image_text_toolbar_title);
        tvTitle2 = view.findViewById(R.id.fragment_image_text_toolbar_title2);
        tvSubtitle = view.findViewById(R.id.fragment_image_text_toolbar_subtitle);
        tvMessage = view.findViewById(R.id.fragment_image_text_message);
        progressbar = view.findViewById(R.id.fragment_image_progressbar);
        progressbarPercentage = view.findViewById(R.id.fragment_image_progressbar_percentage);
        toolbar = view.findViewById(R.id.fragment_image_container_toolbar);

        progressbar.setVisibility(View.VISIBLE);

        tvTitle.setText(sender);
        tvSubtitle.setText(activity.getRelativeDateTimeString(timestamp));
        if (message == null) {
            tvMessage.setVisibility(View.GONE);
        } else {
            tvMessage.setText(message);
        }

        ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullscreen();
            }
        });

        Glide.with(activity)
                .load(imageUrl)
                .listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                progressbar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                progressbar.setVisibility(View.GONE);
                return false;
            }
        }).into(ivImage);

        if (roomId != null) {
            if (roomType.equals("public")) {
                llToolbarEdit.setVisibility(View.VISIBLE);
            }
        }

        if (imageSize == null) {
            tvTitle.setVisibility(View.GONE);
            tvSubtitle.setVisibility(View.GONE);
            tvMessage.setVisibility(View.GONE);

            tvTitle2.setText(sender);
            tvTitle2.setVisibility(View.VISIBLE);
        }

        return view;
    }

    public void fullscreen() {
        if (toolbar.getVisibility() == View.VISIBLE) {
            isFullscreen = true;

            View decorView = activity.getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(uiOptions);
            toolbar.setVisibility(View.GONE);
        } else {
            isFullscreen = false;

            activity.getWindow().getDecorView().setSystemUiVisibility(0);
            toolbar.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.fragment_image_container_toolbar_more, R.id.fragment_image_button_toolbar_more})
    public void onToolbarMoreClick(View view) {
        PopupMenu popup = new PopupMenu(getContext(), view);
        popup.setOnMenuItemClickListener(this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.toolbar_image, popup.getMenu());
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_menu_open:
                downloadImage(true);
                return true;
            case R.id.toolbar_menu_download:
                downloadImage(false);
                return true;
            default:
                return false;
        }
    }

    public void openImage() {
        File file = new File(localImagePath + imageName);
        Uri imageUri = FileProvider.getUriForFile(
                activity,
                activity.getApplicationContext()
                        .getPackageName() + ".fileprovider", file);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(imageUri, "image/*");

        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            startActivity(intent);
        }else {
            Toast.makeText(activity,"Tidak terdapat aplikasi yang bisa digunakan untuk membuka.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            progressbar.setVisibility(View.VISIBLE);

            Uri fileUri = data.getData();
            StorageReference storageRef = activity.storage.getReference()
                    .child(roomId)
                    .child("images")
                    .child("avatar");

            StorageTask uploadTask = storageRef.putFile(fileUri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    int progress = (100 * (int) taskSnapshot.getBytesTransferred()) / (int) taskSnapshot.getTotalByteCount();
                    progressbarPercentage.setProgress(progress);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            progressbarPercentage.setVisibility(View.GONE);
                            refRoom = activity.db.getReference("room").child(roomId);

                            String fileDownloadUrl = uri.toString();

                            refRoom.getRef().child("avatar_url").setValue(fileDownloadUrl);
                            activity.roomFragment.changeAvatar(fileDownloadUrl);
                            Glide.with(activity).load(fileDownloadUrl).into(ivImage);
                        }
                    });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception exception) {
                            progressbarPercentage.setVisibility(View.GONE);
                            Log.d(activity.TAG, exception.getMessage());
                        }
                    });
        }
    }


    @OnClick({R.id.fragment_image_container_toolbar_arrow_back, R.id.fragment_image_button_toolbar_arrow_back})
    public void onToolbarArrowBackClick() {
        activity.onBackPressed();
    }

    @OnClick({R.id.fragment_image_container_toolbar_edit, R.id.fragment_image_button_toolbar_edit})
    public void onToolbarEditClick(View view) {
        if (llToolbarEdit.getVisibility() != View.GONE) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");
            if (intent.resolveActivity(activity.getPackageManager()) != null) {
                startActivityForResult(intent, 1);
            }
        }
    }

    public void downloadImage(final boolean open) {
        progressbarPercentage.setProgress(0);
        progressbarPercentage.setVisibility(View.VISIBLE);
        if (open) {
            File file = new File(localImagePath + imageName);
            if (file.exists()) {
                progressbarPercentage.setVisibility(View.GONE);
                openImage();
            } else {
                AndroidNetworking.download(imageUrl, localImagePath, imageName)
                        .build()
                        .setDownloadProgressListener(new DownloadProgressListener() {
                            @Override
                            public void onProgress(long bytesDownloaded, long totalBytes) {
                                int progress = (100 * (int) bytesDownloaded) / (int) totalBytes;
                                progressbarPercentage.setProgress(progress);
                            }
                        })
                        .startDownload(new DownloadListener() {
                            @Override
                            public void onDownloadComplete() {
                                progressbarPercentage.setVisibility(View.GONE);
                                openImage();
                            }
                            @Override
                            public void onError(ANError error) {
                                // handle error
                                progressbarPercentage.setVisibility(View.GONE);
                            }
                        });
            }
        } else {
            AndroidNetworking.download(imageUrl, localImagePath, imageName)
                    .build()
                    .setDownloadProgressListener(new DownloadProgressListener() {
                        @Override
                        public void onProgress(long bytesDownloaded, long totalBytes) {
                            int progress = (100 * (int) bytesDownloaded) / (int) totalBytes;
                            progressbarPercentage.setProgress(progress);
                        }
                    })
                    .startDownload(new DownloadListener() {
                        @Override
                        public void onDownloadComplete() {
                            progressbarPercentage.setVisibility(View.GONE);
                            String successMessage = "Unduhan selesai: " + localImagePath + imageName;
                            Snackbar.make(layout, successMessage,
                                    Snackbar.LENGTH_LONG)
                                    .show();
                        }
                        @Override
                        public void onError(ANError error) {
                            // handle error
                            progressbarPercentage.setVisibility(View.GONE);
                        }
                    });
        }
    }
}
