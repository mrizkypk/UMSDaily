package com.mrizkypk.umsdaily.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mrizkypk.umsdaily.R;
import com.mrizkypk.umsdaily.helper.DateHelper;
import com.mrizkypk.umsdaily.helper.ApiHelper;
import com.mrizkypk.umsdaily.helper.OkHttpClientHelper;
import com.mrizkypk.umsdaily.helper.SessionHelper;
import com.mrizkypk.umsdaily.manager.DatabaseManager;
import com.mrizkypk.umsdaily.manager.IntentManager;
import com.mrizkypk.umsdaily.manager.SharedPreferenceManager;
import com.mrizkypk.umsdaily.model.ResponseErrorModel;
import com.mrizkypk.umsdaily.model.ChatRoomModel;
import com.mrizkypk.umsdaily.model.LoginModel;
import com.mrizkypk.umsdaily.model.ScheduleModel;
import com.mrizkypk.umsdaily.model.UserModel;
import com.rx2androidnetworking.Rx2AndroidNetworking;

import java.io.IOError;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "MRPKLoginActivity";
    private String loginAs;
    private IntentManager im;
    private SharedPreferenceManager spm;
    private FirebaseDatabase db;
    private DatabaseReference refChat;
    private DatabaseReference refUser;
    private DatabaseReference refUsers;
    private DatabaseReference refRoom;
    private DatabaseReference refRooms;

    @BindView(R.id.activity_login_input_id)
    EditText etId;

    @BindView(R.id.activity_login_input_password)
    EditText etPassword;

    @BindView(R.id.activity_login_button_login)
    Button btnLogin;

    @BindView(R.id.activity_login_progressbar)
    ProgressBar progressbar;

    @BindView(R.id.activity_login_text_as_student)
    TextView tvAsStudent;

    @BindView(R.id.activity_login_text_as_staff)
    TextView tvAsStaff;

    @BindView(R.id.activity_login_text_as_lecturer)
    TextView tvAsLecturer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        AndroidNetworking.initialize(getApplicationContext(), OkHttpClientHelper.getInstance());

        im = new IntentManager(this);
        spm = new SharedPreferenceManager(this);
        db = DatabaseManager.getInstance();
        SessionHelper.checkReverse(spm, im);
        loginAs = "student";
    }

    @OnClick(R.id.activity_login_button_login)
    public void onClickLoginButton() {

        String id = etId.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!id.isEmpty() && !password.isEmpty()) {
            hideSoftKeyboard();
            hideLoginButton();
            showProgressbar();

            switch (loginAs) {
                case "student":
                    loginAsStudent(id, password);
                    break;
                case "staff":
                    loginAsStaff(id, password);
                    break;
                case "lecturer":
                    loginAsLecturer(id, password);
                    break;
            }
        }
    }

    @OnClick(R.id.activity_login_text_as_student)
    public void onClickAsStudentText() {
        loginAs = "student";
        tvAsStudent.setBackgroundResource(R.color.colorPrimary);
        tvAsLecturer.setBackgroundResource(0);
        tvAsStaff.setBackgroundResource(0);
    }

    @OnClick(R.id.activity_login_text_as_staff)
    public void onClickAsStaffText() {
        loginAs = "staff";
        tvAsStudent.setBackgroundResource(0);
        tvAsLecturer.setBackgroundResource(0);
        tvAsStaff.setBackgroundResource(R.color.colorPrimary);
    }

    @OnClick(R.id.activity_login_text_as_lecturer)
    public void onClickAsLecturerText() {
        loginAs = "lecturer";
        tvAsStudent.setBackgroundResource(0);
        tvAsLecturer.setBackgroundResource(R.color.colorPrimary);
        tvAsStaff.setBackgroundResource(0);
    }

    public void storeLoginSession(String id, String password, String name, String token, String avatarUrl, String type) {
        SharedPreferences.Editor editor = spm.getInstance().edit();
        editor.putString("USER_ID", id);
        editor.putString("USER_PASSWORD", password);
        editor.putString("USER_NAME", name);
        editor.putString("USER_AVATAR_URL", avatarUrl);
        editor.putString("USER_TOKEN", token);
        editor.putString("USER_TYPE", type);
        editor.commit();
    }

    public Observable<List<ScheduleModel>> getStudentSchedule() {
        return Rx2AndroidNetworking.get(ApiHelper.getHost() + "/jadwal_mahasiswa.php")
                .addQueryParameter("nim", spm.getUserId())
                .addQueryParameter("password", spm.getUserPassword())
                .getResponseOnlyFromNetwork()
                .build()
                .getObjectListObservable(ScheduleModel.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<ScheduleModel>> getLecturerSchedule() {
        return Rx2AndroidNetworking.get(ApiHelper.getHost() + "/jadwal_dosen.php")
                .addQueryParameter("nama", spm.getUserName())
                .getResponseOnlyFromNetwork()
                .build()
                .getObjectListObservable(ScheduleModel.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<UserModel> getUser(String userId) {
        return Rx2AndroidNetworking.get(ApiHelper.getFirebaseHost() + "/user/" + userId + ".json")
                .getResponseOnlyFromNetwork()
                .build()
                .getObjectObservable(UserModel.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void loginAsStudent(final String id, final String password) {
        Rx2AndroidNetworking.get(ApiHelper.getHost() + "/masuk_mahasiswa.php")
                .addQueryParameter("id", id)
                .addQueryParameter("password", password)
                .getResponseOnlyFromNetwork()
                .build()
                .getObjectObservable(LoginModel.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<LoginModel, Observable<UserModel>>() {
                    @Override
                    public Observable<UserModel> apply(LoginModel loginModel) throws Exception {
                        if (loginModel.getId() == null) {
                            throw new IOException();
                        } else {
                            UserModel userModel = new UserModel();
                            userModel.setId(loginModel.getId());
                            userModel.setName(loginModel.getNama());
                            userModel.setToken(loginModel.getToken());

                            return getUser(loginModel.getId()).onErrorReturnItem(userModel);
                        }
                    }
                })
                .flatMap(new Function<UserModel, Observable<List<ScheduleModel>>>() {
                    @Override
                    public Observable<List<ScheduleModel>> apply(UserModel userModel) throws Exception {
                        refUser = db.getReference("user/" + userModel.getId());
                        if (userModel.getAvatar_url() == null) {
                            String avatarUrl = "https://api.adorable.io/avatars/56/" + userModel.getId() + ".png";
                            refUser.child("id").setValue(userModel.getId());
                            refUser.child("name").setValue(userModel.getName());
                            refUser.child("token").setValue(userModel.getToken());
                            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                                @Override
                                public void onSuccess(InstanceIdResult instanceIdResult) {
                                    refUser.child("device_token").setValue(instanceIdResult.getToken());
                                }
                            });
                            refUser.child("avatar_url").setValue(avatarUrl);
                            refUser.child("type").setValue(loginAs);

                            storeLoginSession(userModel.getId(), password, userModel.getName(), userModel.getToken(), avatarUrl, loginAs);
                        } else {
                            refUser.child("id").setValue(userModel.getId());
                            refUser.child("name").setValue(userModel.getName());
                            refUser.child("token").setValue(userModel.getToken());
                            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                                @Override
                                public void onSuccess(InstanceIdResult instanceIdResult) {
                                    refUser.child("device_token").setValue(instanceIdResult.getToken());
                                }
                            });
                            refUser.child("type").setValue(loginAs);

                            storeLoginSession(userModel.getId(), password, userModel.getName(), userModel.getToken(), userModel.getAvatar_url(), loginAs);
                        }

                        return getStudentSchedule();
                    }
                })
                .flatMap(new Function<List<ScheduleModel>, Observable<ScheduleModel>>() {
                    @Override
                    public Observable<ScheduleModel> apply(List<ScheduleModel> scheduleModels) throws Exception {
                        return Observable.fromIterable(scheduleModels);
                    }
                })
                .subscribe(new Observer<ScheduleModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ScheduleModel scheduleModel) {
                        refChat = db.getReference("chat/" + scheduleModel.getKode());

                        Long timestamp = DateHelper.getTimestamp();

                        HashMap<String, Long> read = new HashMap<>();
                        read.put(spm.getUserId(), timestamp);

                        HashMap<String, Long> received = new HashMap<>();
                        received.put(spm.getUserId(), timestamp);

                        ChatRoomModel chat = new ChatRoomModel();
                        chat.setRoom_id(scheduleModel.getKode());
                        chat.setTimestamp(timestamp);
                        chat.setRoom_title(scheduleModel.getMatakuliah());
                        chat.setSender_id(spm.getUserId());
                        chat.setSender_name(spm.getUserName());
                        chat.setRead(read);
                        chat.setReceived(received);
                        chat.setType("info_join");
                        chat.setRoom_type("public");

                        String key = refChat.push().getKey();
                        chat.setId(key);
                        refChat.child(key).setValue(chat);

                        refRoom = db.getReference("room").child(scheduleModel.getKode());
                        refRoom.child("id").setValue(scheduleModel.getKode());
                        refRoom.child("type").setValue("public");
                        refRoom.child("title").setValue(scheduleModel.getMatakuliah());
                        refRoom.child("member/" + spm.getUserId()).setValue(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideProgressbar();
                        showLoginButton();

                        if (e.getMessage() != null) {
                            Log.d(TAG, e.getMessage());
                        }

                        if (e instanceof IOException) {
                            String message = "ID atau Kata Sandi salah";
                            Snackbar.make(findViewById(R.id.activity_login_layout), message,
                                    Snackbar.LENGTH_LONG)
                                    .show();
                        } else if (e instanceof ANError) {
                            ANError ae = (ANError) e;
                            String message = "Error";
                            if (ae.getErrorCode() != 0) {
                                ResponseErrorModel error = ae.getErrorAsObject(ResponseErrorModel.class);
                                if (error.getMessage().equals("login")) {
                                    message = "ID atau Kata Sandi salah";
                                } else if (error.getMessage().equals("server")) {
                                    message = "Server error";
                                }
                            } else {
                                message = "Koneksi bermasalah";
                            }

                            Snackbar.make(findViewById(R.id.activity_login_layout), message,
                                    Snackbar.LENGTH_LONG)
                                    .show();
                        } else {
                            String message = "Koneksi Bermasalah";
                            Snackbar.make(findViewById(R.id.activity_login_layout), message,
                                    Snackbar.LENGTH_LONG)
                                    .show();
                        }
                    }

                    @Override
                    public void onComplete() {
                        im.goHome();
                    }
                });
    }

    public void loginAsStaff(final String id, final String password) {
        Rx2AndroidNetworking.get(ApiHelper.getHost() + "/masuk_staf.php")
                .addQueryParameter("id", id)
                .addQueryParameter("password", password)
                .getResponseOnlyFromNetwork()
                .build()
                .getObjectObservable(LoginModel.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<LoginModel, Observable<UserModel>>() {
                    @Override
                    public Observable<UserModel> apply(LoginModel loginModel) throws Exception {
                        if (loginModel.getId() == null) {
                            throw new IOException();
                        } else {
                            UserModel userModel = new UserModel();
                            userModel.setId(loginModel.getId());
                            userModel.setName(loginModel.getNama());
                            userModel.setToken(loginModel.getToken());

                            return getUser(loginModel.getId()).onErrorReturnItem(userModel);
                        }
                    }
                })
                .subscribe(new Observer<UserModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(UserModel userModel) {
                        refUser = db.getReference("user/" + userModel.getId());
                        if (userModel.getAvatar_url() == null) {
                            String avatarUrl = "https://api.adorable.io/avatars/56/" + userModel.getId() + ".png";
                            refUser.child("id").setValue(userModel.getId());
                            refUser.child("name").setValue(userModel.getName());
                            refUser.child("token").setValue(userModel.getToken());
                            refUser.child("device_token").setValue(FirebaseInstanceId.getInstance().getToken());
                            refUser.child("avatar_url").setValue(avatarUrl);
                            refUser.child("type").setValue(loginAs);

                            storeLoginSession(userModel.getId(), password, userModel.getName(), userModel.getToken(), avatarUrl, loginAs);
                        } else {
                            refUser.child("id").setValue(userModel.getId());
                            refUser.child("name").setValue(userModel.getName());
                            refUser.child("token").setValue(userModel.getToken());
                            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                                @Override
                                public void onSuccess(InstanceIdResult instanceIdResult) {
                                    refUser.child("device_token").setValue(instanceIdResult.getToken());
                                }
                            });
                            refUser.child("type").setValue(loginAs);

                            storeLoginSession(userModel.getId(), password, userModel.getName(), userModel.getToken(), userModel.getAvatar_url(), loginAs);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideProgressbar();
                        showLoginButton();

                        if (e.getMessage() != null) {
                            Log.d(TAG, e.getMessage());
                        }

                        if (e instanceof IOException) {
                            String message = "ID atau Kata Sandi salah";
                            Snackbar.make(findViewById(R.id.activity_login_layout), message,
                                    Snackbar.LENGTH_LONG)
                                    .show();
                        } else if (e instanceof ANError) {
                            ANError ae = (ANError) e;
                            String message = "Error";
                            if (ae.getErrorCode() != 0) {
                                ResponseErrorModel error = ae.getErrorAsObject(ResponseErrorModel.class);
                                if (error.getMessage().equals("login")) {
                                    message = "ID atau Kata Sandi salah";
                                } else if (error.getMessage().equals("server")) {
                                    message = "Server error";
                                }
                            } else {
                                message = "Koneksi bermasalah";
                            }

                            Snackbar.make(findViewById(R.id.activity_login_layout), message,
                                    Snackbar.LENGTH_LONG)
                                    .show();
                        } else {
                            String message = "Koneksi Bermasalah";
                            Snackbar.make(findViewById(R.id.activity_login_layout), message,
                                    Snackbar.LENGTH_LONG)
                                    .show();
                        }
                    }

                    @Override
                    public void onComplete() {
                        im.goHome();
                    }
                });
    }

    public void loginAsLecturer(final String id, final String password) {
        Rx2AndroidNetworking.get(ApiHelper.getHost() + "/masuk_dosen.php")
                .addQueryParameter("id", id)
                .addQueryParameter("password", password)
                .getResponseOnlyFromNetwork()
                .build()
                .getObjectObservable(LoginModel.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<LoginModel, Observable<UserModel>>() {
                    @Override
                    public Observable<UserModel> apply(LoginModel loginModel) throws Exception {
                        if (loginModel.getId() == null) {
                            throw new IOException();
                        } else {
                            UserModel userModel = new UserModel();
                            userModel.setId(loginModel.getId());
                            userModel.setName(loginModel.getNama());
                            userModel.setToken(loginModel.getToken());

                            return getUser(loginModel.getId()).onErrorReturnItem(userModel);
                        }
                    }
                })
                .flatMap(new Function<UserModel, Observable<List<ScheduleModel>>>() {
                    @Override
                    public Observable<List<ScheduleModel>> apply(UserModel userModel) throws Exception {
                        refUser = db.getReference("user/" + userModel.getId());
                        if (userModel.getAvatar_url() == null) {
                            String avatarUrl = "https://api.adorable.io/avatars/56/" + userModel.getId() + ".png";
                            refUser.child("id").setValue(userModel.getId());
                            refUser.child("name").setValue(userModel.getName());
                            refUser.child("token").setValue(userModel.getToken());
                            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                                @Override
                                public void onSuccess(InstanceIdResult instanceIdResult) {
                                    refUser.child("device_token").setValue(instanceIdResult.getToken());
                                }
                            });
                            refUser.child("type").setValue(loginAs);
                            refUser.child("avatar_url").setValue(avatarUrl);

                            storeLoginSession(userModel.getId(), password, userModel.getName(), userModel.getToken(), avatarUrl, loginAs);
                        } else {
                            refUser.child("id").setValue(userModel.getId());
                            refUser.child("name").setValue(userModel.getName());
                            refUser.child("token").setValue(userModel.getToken());
                            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                                @Override
                                public void onSuccess(InstanceIdResult instanceIdResult) {
                                    refUser.child("device_token").setValue(instanceIdResult.getToken());
                                }
                            });
                            refUser.child("type").setValue(loginAs);

                            storeLoginSession(userModel.getId(), password, userModel.getName(), userModel.getToken(), userModel.getAvatar_url(), loginAs);
                        }

                        return getLecturerSchedule();
                    }
                })
                .flatMap(new Function<List<ScheduleModel>, Observable<ScheduleModel>>() {
                    @Override
                    public Observable<ScheduleModel> apply(List<ScheduleModel> scheduleModels) throws Exception {
                        return Observable.fromIterable(scheduleModels);
                    }
                })
                .subscribe(new Observer<ScheduleModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ScheduleModel scheduleModel) {
                        refChat = db.getReference("chat/" + scheduleModel.getKode());

                        Long timestamp = DateHelper.getTimestamp();

                        HashMap<String, Long> read = new HashMap<>();
                        read.put(spm.getUserId(), timestamp);

                        HashMap<String, Long> received = new HashMap<>();
                        received.put(spm.getUserId(), timestamp);

                        ChatRoomModel chat = new ChatRoomModel();
                        chat.setRoom_id(scheduleModel.getKode());
                        chat.setTimestamp(timestamp);
                        chat.setRoom_title(scheduleModel.getMatakuliah());
                        chat.setSender_id(spm.getUserId());
                        chat.setSender_name(spm.getUserName());
                        chat.setRead(read);
                        chat.setReceived(received);
                        chat.setType("info_join");
                        chat.setRoom_type("public");

                        String key = refChat.push().getKey();
                        chat.setId(key);
                        refChat.child(key).setValue(chat);

                        refRoom = db.getReference("room").child(scheduleModel.getKode());
                        refRoom.child("id").setValue(scheduleModel.getKode());
                        refRoom.child("type").setValue("public");
                        refRoom.child("title").setValue(scheduleModel.getMatakuliah());
                        refRoom.child("member/" + spm.getUserId()).setValue(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideProgressbar();
                        showLoginButton();

                        if (e.getMessage() != null) {
                            Log.d(TAG, e.getMessage());
                        }

                        if (e instanceof IOException) {
                            String message = "ID atau Kata Sandi salah";
                            Snackbar.make(findViewById(R.id.activity_login_layout), message,
                                    Snackbar.LENGTH_LONG)
                                    .show();
                        } else if (e instanceof ANError) {
                            ANError ae = (ANError) e;
                            String message = "Error";
                            if (ae.getErrorCode() != 0) {
                                ResponseErrorModel error = ae.getErrorAsObject(ResponseErrorModel.class);
                                if (error.getMessage().equals("login")) {
                                    message = "ID atau Kata Sandi salah";
                                } else if (error.getMessage().equals("server")) {
                                    message = "Server error";
                                }
                            } else {
                                message = "Koneksi bermasalah";
                            }

                            Snackbar.make(findViewById(R.id.activity_login_layout), message,
                                    Snackbar.LENGTH_LONG)
                                    .show();
                        } else {
                            String message = "Koneksi Bermasalah";
                            Snackbar.make(findViewById(R.id.activity_login_layout), message,
                                    Snackbar.LENGTH_LONG)
                                    .show();
                        }
                    }

                    @Override
                    public void onComplete() {
                        im.goHome();
                    }
                });
    }

    public void hideSoftKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    public void showLoginButton() {
        btnLogin.setVisibility(View.VISIBLE);
    }

    public void hideLoginButton() {
        btnLogin.setVisibility(View.GONE);
    }

    public void showProgressbar() {
        progressbar.setVisibility(View.VISIBLE);
    }

    public void hideProgressbar() {
        progressbar.setVisibility(View.GONE);
    }

}
