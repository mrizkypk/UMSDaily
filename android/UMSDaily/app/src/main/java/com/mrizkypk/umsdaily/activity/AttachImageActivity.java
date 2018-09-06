package com.mrizkypk.umsdaily.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mrizkypk.umsdaily.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AttachImageActivity extends AppCompatActivity {
    private static final String TAG = "MRPKAttachImageActivity";
    private Uri fileUri;
    private String filePath;

    @BindView(R.id.activity_attach_image_text_toolbar_title)
    TextView tvToolbarTitle;

    @BindView(R.id.activity_attach_image_text_toolbar_subtitle)
    TextView tvToolbarSubtitle;

    @BindView(R.id.activity_attach_image_image_toolbar_logo)
    ImageView sdToolbarLogo;

    @BindView(R.id.activity_attach_image_container_image_attach)
    ImageView ivContainerImageAttach;

    @BindView(R.id.activity_attach_image_input_message)
    EditText etMessage;

    private String roomId;
    private String roomTitle;
    private String roomAvatarUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attach_image);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        filePath = intent.getStringExtra("FILE_PATH");
        roomId = intent.getStringExtra("CHAT_ROOM_ID");
        roomTitle = intent.getStringExtra("CHAT_ROOM_TITLE");
        roomAvatarUrl = intent.getStringExtra("CHAT_ROOM_AVATAR_URL");

        fileUri = Uri.parse(filePath);

        ivContainerImageAttach.setImageURI(fileUri);
        tvToolbarTitle.setText(roomTitle);
        tvToolbarSubtitle.setText(roomId);

        Glide.with(this)
                .load(roomAvatarUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(sdToolbarLogo);

    }

    @OnClick({R.id.activity_attach_image_container_send, R.id.activity_attach_image_button_send})
    public void send(View view) {
        Intent intent = getIntent();
        intent.putExtra("CHAT_ROOM_ID", roomId);
        intent.putExtra("CHAT_ROOM_TITLE", roomTitle);
        intent.putExtra("CHAT_MESSAGE", etMessage.getText().toString());
        intent.putExtra("FILE_PATH", filePath);
        intent.putExtra("FILE_TYPE", "image");
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    @OnClick({R.id.activity_attach_image_container_toolbar_arrow_back, R.id.activity_attach_image_button_toolbar_arrow_back})
    public void onToolbarArrowBackClick() {
        onBackPressed();
    }
}
