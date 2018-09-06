package com.mrizkypk.umsdaily.activity;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.mrizkypk.umsdaily.R;
import com.mrizkypk.umsdaily.manager.DatabaseManager;
import com.mrizkypk.umsdaily.manager.SharedPreferenceManager;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QuizActivity extends AppCompatActivity {

    @BindView(R.id.text_user_id)
    TextView tvUserId;

    @BindView(R.id.text_user_name)
    TextView tvUserName;

    @BindView(R.id.text_user_type)
    TextView tvUserType;

    @BindView(R.id.seekbar_quiz_1)
    SeekBar seekbar1;

    @BindView(R.id.text_count_quiz_1)
    TextView tvSeekbar1;

    @BindView(R.id.seekbar_quiz_2)
    SeekBar seekbar2;

    @BindView(R.id.text_count_quiz_2)
    TextView tvSeekbar2;

    @BindView(R.id.seekbar_quiz_3)
    SeekBar seekbar3;

    @BindView(R.id.text_count_quiz_3)
    TextView tvSeekbar3;

    @BindView(R.id.seekbar_quiz_4)
    SeekBar seekbar4;

    @BindView(R.id.text_count_quiz_4)
    TextView tvSeekbar4;

    @BindView(R.id.seekbar_quiz_5)
    SeekBar seekbar5;

    @BindView(R.id.text_count_quiz_5)
    TextView tvSeekbar5;

    @BindView(R.id.button_quiz_submit)
    Button buttonSubmit;

    @BindView(R.id.progressbar_quiz)
    ProgressBar progressBar;

    public FirebaseDatabase db;
    private SharedPreferenceManager spm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        spm = new SharedPreferenceManager(this);
        db = DatabaseManager.getInstance();

        ButterKnife.bind(this);

        tvUserId.setText("ID: " + spm.getUserId());
        tvUserName.setText("Nama: " + spm.getUserName());

        switch (spm.getUserType()) {
            case "staff":
                tvUserType.setText("Sebagai: Staf");
                break;
            case "lecturer":
                tvUserType.setText("Sebagai: Dosen");
                break;
            case "student":
                tvUserType.setText("Sebagai: Mahasiswa");
                break;
        }

        loadPrevAnswers();

        seekbar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tvSeekbar1.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekbar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tvSeekbar2.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekbar3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tvSeekbar3.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekbar4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tvSeekbar4.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekbar5.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tvSeekbar5.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void loadPrevAnswers() {
        db.getReference().child("quiz").child(spm.getUserId()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                switch (dataSnapshot.getKey()) {
                    case "0":
                        seekbar1.setProgress(((Long)dataSnapshot.getValue()).intValue());
                        break;
                    case "1":
                        seekbar2.setProgress(((Long)dataSnapshot.getValue()).intValue());
                        break;
                    case "2":
                        seekbar3.setProgress(((Long)dataSnapshot.getValue()).intValue());
                        break;
                    case "3":
                        seekbar4.setProgress(((Long)dataSnapshot.getValue()).intValue());
                        break;
                    case "4":
                        seekbar5.setProgress(((Long)dataSnapshot.getValue()).intValue());
                        break;
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @OnClick(R.id.button_quiz_submit)
    public void buttonQuizSubmitClick() {
        buttonSubmit.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        ArrayList<Integer> answers = new ArrayList<>();
        answers.add(seekbar1.getProgress());
        answers.add(seekbar2.getProgress());
        answers.add(seekbar3.getProgress());
        answers.add(seekbar4.getProgress());
        answers.add(seekbar5.getProgress());

        db.getReference().child("quiz").child(spm.getUserId()).setValue(answers).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressBar.setVisibility(View.GONE);
                buttonSubmit.setVisibility(View.GONE);

                Snackbar.make(findViewById(R.id.activity_quiz_layout), "Penilaian Anda Sudah Terkirim. Terima Kasih.",
                        Snackbar.LENGTH_LONG).addCallback(new Snackbar.Callback() {
                    @Override
                    public void onShown(Snackbar sb) {
                        super.onShown(sb);
                    }

                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);
                        finish();
                    }
                }).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                progressBar.setVisibility(View.GONE);
                buttonSubmit.setVisibility(View.VISIBLE);
            }
        });
    }
}
