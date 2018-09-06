package com.mrizkypk.umsdaily.activity;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageTask;
import com.mrizkypk.umsdaily.adapter.AnnouncementAdapter;
import com.mrizkypk.umsdaily.adapter.AssignmentAdapter;
import com.mrizkypk.umsdaily.adapter.ComposeAdapter;
import com.mrizkypk.umsdaily.fragment.AccountFragment;
import com.mrizkypk.umsdaily.fragment.AnnouncementAddFragment;
import com.mrizkypk.umsdaily.fragment.AnnouncementFragment;
import com.mrizkypk.umsdaily.fragment.AssignmentAddFragment;
import com.mrizkypk.umsdaily.fragment.ComposeFragment;
import com.mrizkypk.umsdaily.fragment.ImageFragment;
import com.mrizkypk.umsdaily.R;
import com.mrizkypk.umsdaily.adapter.ChatAdapter;
import com.mrizkypk.umsdaily.adapter.ChatReadAdapter;
import com.mrizkypk.umsdaily.adapter.ChatReceivedAdapter;
import com.mrizkypk.umsdaily.adapter.HomeAdapter;
import com.mrizkypk.umsdaily.adapter.ScheduleAdapter;
import com.mrizkypk.umsdaily.adapter.SearchAdapter;
import com.mrizkypk.umsdaily.fragment.AssignmentFragment;
import com.mrizkypk.umsdaily.fragment.BillFragment;
import com.mrizkypk.umsdaily.fragment.ChatHomeFragment;
import com.mrizkypk.umsdaily.fragment.ChatInfoFragment;
import com.mrizkypk.umsdaily.fragment.ChatRoomFragment;
import com.mrizkypk.umsdaily.fragment.InformationFragment;
import com.mrizkypk.umsdaily.fragment.KhsFragment;
import com.mrizkypk.umsdaily.fragment.RoomFileFragment;
import com.mrizkypk.umsdaily.fragment.RoomFragment;
import com.mrizkypk.umsdaily.fragment.RoomImageFragment;
import com.mrizkypk.umsdaily.fragment.ScheduleFragment;
import com.mrizkypk.umsdaily.fragment.SearchFragment;
import com.mrizkypk.umsdaily.fragment.StudyFragment;
import com.mrizkypk.umsdaily.helper.ApiHelper;
import com.mrizkypk.umsdaily.helper.DateHelper;
import com.mrizkypk.umsdaily.helper.OkHttpClientHelper;
import com.mrizkypk.umsdaily.helper.SessionHelper;
import com.mrizkypk.umsdaily.manager.DatabaseManager;
import com.mrizkypk.umsdaily.manager.IntentManager;
import com.mrizkypk.umsdaily.manager.SharedPreferenceManager;
import com.mrizkypk.umsdaily.manager.StorageManager;
import com.mrizkypk.umsdaily.model.AnnouncementModel;
import com.mrizkypk.umsdaily.model.AssignmentModel;
import com.mrizkypk.umsdaily.model.ChatHomeModel;
import com.mrizkypk.umsdaily.model.ChatRoomModel;
import com.mrizkypk.umsdaily.model.ResponseErrorModel;
import com.mrizkypk.umsdaily.model.RoomModel;
import com.mrizkypk.umsdaily.model.ScheduleModel;
import com.mrizkypk.umsdaily.model.UserModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static butterknife.internal.Utils.arrayOf;

/**
 * Created by mrizkypk on 01/03/18.
 */

public class HomeActivity extends FragmentActivity {
    public static final String TAG = "MRPKHomeActivity";
    public static boolean isInBackground;

    public IntentManager im;
    public SharedPreferenceManager spm;
    public FirebaseStorage storage;
    public FirebaseDatabase db;
    public List<Object> scheduleModelList;
    public List<Object> searchModelList;
    public LinkedList<AnnouncementModel> announcementModelList;
    public LinkedList<Object> assignmentModelList;
    public LinkedList<AssignmentModel> assignmentRawModelList;
    public LinkedList<ChatHomeModel> homeModelList;
    public LinkedList<UserModel> composeModelList;
    public Map<String, LinkedList<ChatRoomModel>> roomModelMap;
    public static Map<String, Integer> unreadCountMap;
    public Map<String, RoomModel> roomDetailMap;
    public Map<String, String> roomMessageMap;
    public Map<String, List<String>> roomMemberListMap;
    public Set<String> roomIndexSet;
    public Query roomRef;
    public ValueEventListener roomListener;
    public Map<String, Query> roomRefMap;
    public Map<Query, ChildEventListener> roomRefListenerMap;
    public Map<Query, ChildEventListener> assignmentRefListenerMap;
    public Map<String, StorageTask> storageMap;
    public Map<String, LinkedList<ChatRoomModel>> selectedModelMap;
    public String currentScheduleHeader;
    public String currentAssignmentHeader;
    public int roomCount;
    public int roomCountCurrent;
    public boolean listReady;
    public boolean smootScroll;

    public Map<String, UserModel> userModelMap;
    public Map<String, ArrayList<UserModel>> roomUserModelMap;

    public static String currentFragment;
    public AccountFragment accountFragment;
    public ComposeFragment composeFragment;
    public ChatHomeFragment homeFragment;
    public static ChatRoomFragment roomFragment;
    public ChatInfoFragment infoFragment;
    public ScheduleFragment scheduleFragment;
    public AssignmentFragment assignmentFragment;
    public AssignmentAddFragment assignmentAddFragment;
    public AnnouncementAddFragment announcementAddFragment;
    public InformationFragment informationFragment;
    public KhsFragment khsFragment;
    public StudyFragment studyFragment;
    public AnnouncementFragment announcementFragment;
    public BillFragment billFragment;
    public SearchFragment searchFragment;
    public RoomFragment roomDetailFragment;
    public RoomFileFragment roomFileFragment;
    public RoomImageFragment roomImageFragment;
    public ImageFragment imageFragment;

    public RecyclerView.Adapter homeAdapter;
    public RecyclerView.Adapter composeAdapter;
    public RecyclerView.Adapter roomAdapter;
    public RecyclerView.Adapter scheduleAdapter;
    public RecyclerView.Adapter assignmentAdapter;
    public RecyclerView.Adapter announcementAdapter;
    public RecyclerView.Adapter searchAdapter;

    public RecyclerView.Adapter readAdapter;
    public RecyclerView.Adapter receivedAdapter;
    public DatabaseReference refRoom;
    public Intent intent;
    public String action;

    public SoundPool spMessageNew;
    public int spMessageNewId;
    public boolean spMessageNewReady;

    public SoundPool spMessageSent;
    public int spMessageSentId;
    public boolean spMessageSentReady;

    public SoundPool spMessageDelete;
    public int spMessageDeleteId;
    public boolean spMessageDeleteReady;
    public AudioManager audioManager;
    public float actVolume;
    public float maxVolume;
    public float volume;

    public Integer badgeHomeCount = 0;
    public Integer badgeScheduleCount = 0;
    public Integer badgeAssignmentCount = 0;
    public Integer badgeInformationCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        AndroidNetworking.initialize(getApplicationContext(), OkHttpClientHelper.getInstance());
        hideSoftKeyboard();

        storage = StorageManager.getInstance();
        db = DatabaseManager.getInstance();
        im = new IntentManager(this);
        spm = new SharedPreferenceManager(this);

        SessionHelper.check(spm, im);

        homeFragment = new ChatHomeFragment();
        roomFragment = new ChatRoomFragment();
        infoFragment = new ChatInfoFragment();
        accountFragment = new AccountFragment();
        composeFragment = new ComposeFragment();
        scheduleFragment = new ScheduleFragment();
        assignmentFragment = new AssignmentFragment();
        assignmentAddFragment = new AssignmentAddFragment();
        informationFragment = new InformationFragment();
        khsFragment = new KhsFragment();
        studyFragment = new StudyFragment();
        billFragment = new BillFragment();
        announcementFragment = new AnnouncementFragment();
        announcementAddFragment = new AnnouncementAddFragment();
        searchFragment = new SearchFragment();
        roomFileFragment = new RoomFileFragment();
        roomImageFragment = new RoomImageFragment();
        roomDetailFragment = new RoomFragment();
        imageFragment = new ImageFragment();

        roomRefMap = new HashMap<>();
        roomRefListenerMap = new HashMap<>();
        assignmentRefListenerMap = new HashMap<>();
        unreadCountMap = new HashMap<>();
        homeModelList = new LinkedList<>();
        composeModelList = new LinkedList<>();
        roomModelMap = new HashMap<>();
        storageMap = new HashMap<>();
        scheduleModelList = new ArrayList<>();
        announcementModelList = new LinkedList<>();
        assignmentModelList = new LinkedList<>();
        assignmentRawModelList = new LinkedList<>();
        searchModelList = new ArrayList<>();
        selectedModelMap = new HashMap<>();
        userModelMap = new HashMap<>();
        roomDetailMap = new HashMap<>();
        roomIndexSet = new HashSet<>();
        roomUserModelMap = new HashMap<>();
        roomMessageMap = new HashMap<>();
        roomMemberListMap = new HashMap<>();

        homeAdapter = new HomeAdapter();
        composeAdapter = new ComposeAdapter();
        roomAdapter = new ChatAdapter();
        scheduleAdapter = new ScheduleAdapter();
        assignmentAdapter = new AssignmentAdapter();
        announcementAdapter = new AnnouncementAdapter();
        searchAdapter = new SearchAdapter();
        readAdapter = new ChatReadAdapter();
        receivedAdapter = new ChatReceivedAdapter();


        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        volume = actVolume / maxVolume / 2;


        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        spMessageNew = new SoundPool.Builder().setAudioAttributes(audioAttributes).setMaxStreams(1).build();
        spMessageNewId = spMessageNew.load(this, R.raw.message_new, 1);
        spMessageNew.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                spMessageNewReady = true;
            }
        });

        spMessageSent = new SoundPool.Builder().setAudioAttributes(audioAttributes).setMaxStreams(1).build();
        spMessageSentId = spMessageSent.load(this, R.raw.message_sent, 1);
        spMessageSent.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                spMessageSentReady = true;

            }
        });

        spMessageDelete = new SoundPool.Builder().setAudioAttributes(audioAttributes).setMaxStreams(1).build();
        spMessageDeleteId = spMessageDelete.load(this, R.raw.message_delete, 1);
        spMessageDelete.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                spMessageDeleteReady = true;
            }
        });

        if (savedInstanceState != null) {
            return;
        }

        //Open default fragment
        openChatHomeFragment(0);

        //Load rooms and chats
        loadChat();

        //Load Announcement
        loadAnnouncement();

        //Load schedule
        currentScheduleHeader = "minggu";

        //Load Assignment
        currentAssignmentHeader = "matakuliah";

        switch (spm.getUserType()) {
            case "student":
                loadStudentSchedule("cache");
                break;
            case "lecturer":
                loadLecturerSchedule("cache");
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        isInBackground = false;

        //Update Read Status

        if (currentFragment.equals("ROOM_FRAGMENT") || currentFragment.equals("PRIVATE_ROOM_FRAGMENT") || currentFragment.equals("PRIVATE_ROOM_FROM_ROOM_FRAGMENT") || currentFragment.equals("PRIVATE_ROOM_FROM_COMPOSE_FRAGMENT")) {
            roomFragment.updateReadStatus(roomFragment.roomId);
            updateBadgeHome();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        isInBackground = true;
    }

    public void handleNotificationPendingIntent(Intent intent) {
        action = intent.getAction();
        if (action != null) {
            switch (action) {
                case "FROM_CHAT_IMAGE_PUBLIC_NOTIFICATION":
                    openChatRoomFragment(intent.getStringExtra("EXTRA_ROOM_ID"), intent.getStringExtra("EXTRA_ROOM_TITLE"), 1);

                    action = "";
                    intent.setAction("");
                    intent.putExtra("EXTRA_ROOM_ID", "");
                    intent.putExtra("EXTRA_ROOM_TITLE", "");
                    setIntent(intent);
                    break;
                case "FROM_CHAT_IMAGE_PRIVATE_NOTIFICATION":
                    openPrivateChatRoomFragment(intent.getStringExtra("EXTRA_ROOM_ID"), intent.getStringExtra("EXTRA_ROOM_TITLE"));

                    action = "";
                    intent.setAction("");
                    intent.putExtra("EXTRA_ROOM_ID", "");
                    intent.putExtra("EXTRA_ROOM_TITLE", "");
                    setIntent(intent);
                    break;
                case "FROM_CHAT_FILE_PUBLIC_NOTIFICATION":
                    openChatRoomFragment(intent.getStringExtra("EXTRA_ROOM_ID"), intent.getStringExtra("EXTRA_ROOM_TITLE"), 1);

                    action = "";
                    intent.setAction("");
                    intent.putExtra("EXTRA_ROOM_ID", "");
                    intent.putExtra("EXTRA_ROOM_TITLE", "");
                    setIntent(intent);
                    break;
                case "FROM_CHAT_FILE_PRIVATE_NOTIFICATION":
                    action = "";
                    intent.setAction("");
                    intent.putExtra("EXTRA_ROOM_ID", "");
                    intent.putExtra("EXTRA_ROOM_TITLE", "");
                    setIntent(intent);
                    openPrivateChatRoomFragment(intent.getStringExtra("EXTRA_ROOM_ID"), intent.getStringExtra("EXTRA_ROOM_TITLE"));
                    break;
                case "FROM_CHAT_MESSAGE_PUBLIC_NOTIFICATION":
                    openChatRoomFragment(intent.getStringExtra("EXTRA_ROOM_ID"), intent.getStringExtra("EXTRA_ROOM_TITLE"), 1);

                    action = "";
                    intent.setAction("");
                    intent.putExtra("EXTRA_ROOM_ID", "");
                    intent.putExtra("EXTRA_ROOM_TITLE", "");
                    setIntent(intent);
                    break;
                case "FROM_CHAT_MESSAGE_PRIVATE_NOTIFICATION":
                    openPrivateChatRoomFragment(intent.getStringExtra("EXTRA_ROOM_ID"), intent.getStringExtra("EXTRA_ROOM_TITLE"));

                    action = "";
                    intent.setAction("");
                    intent.putExtra("EXTRA_ROOM_ID", "");
                    intent.putExtra("EXTRA_ROOM_TITLE", "");
                    setIntent(intent);
                    break;
                case "FROM_ASSIGNMENT_NOTIFICATION":
                    openAssignmentFragment();

                    action = "";
                    intent.setAction("");
                    intent.putExtra("EXTRA_ROOM_ID", "");
                    intent.putExtra("EXTRA_ROOM_TITLE", "");
                    setIntent(intent);
                    break;
                case "FROM_ANNOUNCEMENT_NOTIFICATION":
                    openAnnouncementFragment();

                    action = "";
                    intent.setAction("");
                    intent.putExtra("EXTRA_ROOM_ID", "");
                    intent.putExtra("EXTRA_ROOM_TITLE", "");
                    setIntent(intent);
                    break;
            }
        }
    }

    public void openChatHomeFragment(int transitionMode) {
        currentFragment = "HOME_FRAGMENT";
        hideSoftKeyboard();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (transitionMode == 1) {
            transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right);
        } else if (transitionMode == 2) {
            transaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_to_right, R.anim.slide_from_right, R.anim.slide_to_left);
        }

        if (homeFragment.isAdded()) {
            transaction.attach(homeFragment);
        } else {
            transaction.add(R.id.activity_home_layout, homeFragment, "HOME_FRAGMENT");
            transaction.attach(homeFragment);
        }

        if (accountFragment.isAdded()) {
            transaction.detach(accountFragment);
        } else {
            transaction.add(R.id.activity_home_layout, accountFragment, "ACCOUNT_FRAGMENT");
            transaction.detach(accountFragment);
        }

        if (scheduleFragment.isAdded()) {
            transaction.detach(scheduleFragment);
        } else {
            transaction.add(R.id.activity_home_layout, scheduleFragment, "SCHEDULE_FRAGMENT");
            transaction.detach(scheduleFragment);
        }

        if (assignmentFragment.isAdded()) {
            transaction.detach(assignmentFragment);
        } else {
            transaction.add(R.id.activity_home_layout, assignmentFragment, "ASSIGNMENT_FRAGMENT");
            transaction.detach(assignmentFragment);
        }

        if (assignmentAddFragment.isAdded()) {
            transaction.detach(assignmentAddFragment);
        } else {
            transaction.add(R.id.activity_home_layout, assignmentAddFragment, "ADD_ASSIGNMENT_FRAGMENT");
            transaction.detach(assignmentAddFragment);
        }

        if (announcementFragment.isAdded()) {
            transaction.detach(announcementFragment);
        } else {
            transaction.add(R.id.activity_home_layout, announcementFragment, "ANNOUNCEMENT_FRAGMENT");
            transaction.detach(announcementFragment);
        }

        if (announcementAddFragment.isAdded()) {
            transaction.detach(announcementAddFragment);
        } else {
            transaction.add(R.id.activity_home_layout, announcementAddFragment, "ADD_ANNOUNCEMENT_FRAGMENT");
            transaction.detach(announcementAddFragment);
        }

        if (informationFragment.isAdded()) {
            transaction.detach(informationFragment);
        } else {
            transaction.add(R.id.activity_home_layout, informationFragment, "INFORMATION_FRAGMENT");
            transaction.detach(informationFragment);
        }

        if (khsFragment.isAdded()) {
            transaction.detach(khsFragment);
        } else {
            transaction.add(R.id.activity_home_layout, khsFragment, "KHS_FRAGMENT");
            transaction.detach(khsFragment);
        }

        if (studyFragment.isAdded()) {
            transaction.detach(studyFragment);
        } else {
            transaction.add(R.id.activity_home_layout, studyFragment, "STUDY_FRAGMENT");
            transaction.detach(studyFragment);
        }

        if (billFragment.isAdded()) {
            transaction.detach(billFragment);
        } else {
            transaction.add(R.id.activity_home_layout, billFragment, "BILL_FRAGMENT");
            transaction.detach(billFragment);
        }

        if (searchFragment.isAdded()) {
            transaction.detach(searchFragment);
        } else {
            transaction.add(R.id.activity_home_layout, searchFragment, "SEARCH_FRAGMENT");
            transaction.detach(searchFragment);
        }

        transaction.commit();
    }

    public void openHomeFromRoomFragment(int transitionMode) {
        currentFragment = "HOME_FRAGMENT";
        hideSoftKeyboard();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (transitionMode == 1) {
            transaction.setCustomAnimations(R.anim.slide_from_right_fade_in, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right_fade_out);
        } else if (transitionMode == 2) {
            transaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_to_right_fade_out, R.anim.slide_from_right_fade_in, R.anim.slide_to_left);
        }
        if (homeFragment.isAdded()) {
            transaction.detach(homeFragment);
            transaction.attach(homeFragment);
        } else {
            transaction.add(R.id.activity_home_layout, homeFragment, "HOME_FRAGMENT");
            transaction.attach(homeFragment);
        }
        transaction.commit();
    }

    public void openAccountFragment(int transitionMode) {
        currentFragment = "ACCOUNT_FRAGMENT";
        hideSoftKeyboard();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (transitionMode == 1) {
            transaction.setCustomAnimations(R.anim.slide_from_right_fade_in, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right_fade_out);
        } else if (transitionMode == 2) {
            transaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_to_right_fade_out, R.anim.slide_from_right_fade_in, R.anim.slide_to_left);
        }
        if (accountFragment.isAdded()) {
            transaction.detach(accountFragment);
            transaction.attach(accountFragment);
        } else {
            transaction.add(R.id.activity_home_layout, accountFragment, "ACCOUNT_FRAGMENT");
            transaction.attach(accountFragment);
        }
        transaction.commit();
    }

    public void openComposeFragment(int transitionMode) {
        currentFragment = "COMPOSE_FRAGMENT";
        hideSoftKeyboard();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (transitionMode == 1) {
            transaction.setCustomAnimations(R.anim.slide_from_right_fade_in, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right_fade_out);
        } else if (transitionMode == 2) {
            transaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_to_right_fade_out, R.anim.slide_from_right_fade_in, R.anim.slide_to_left);
        }
        if (composeFragment.isAdded()) {
            transaction.detach(composeFragment);
            transaction.attach(composeFragment);
        } else {
            transaction.add(R.id.activity_home_layout, composeFragment, "COMPOSE_FRAGMENT");
            transaction.attach(composeFragment);
        }
        transaction.commit();
    }

    public void openChatRoomFragment(String roomId, String roomTitle, int transitionMode) {
        currentFragment = "ROOM_FRAGMENT";
        hideSoftKeyboard();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        Bundle bundle = new Bundle();
        bundle.putString("BUNDLE_ROOM_ID", roomId);
        bundle.putString("BUNDLE_ROOM_TITLE", roomTitle);
        bundle.putString("BUNDLE_ROOM_TYPE", "public");

        roomFragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (transitionMode == 1) {
            transaction.setCustomAnimations(R.anim.slide_from_right_fade_in, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right_fade_out);
        } else if (transitionMode == 2) {
            transaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_to_right_fade_out, R.anim.slide_from_right_fade_in, R.anim.slide_to_left);
        }
        if (roomFragment.isAdded()) {
            transaction.detach(roomFragment);
            transaction.attach(roomFragment);
        } else {
            transaction.add(R.id.activity_home_layout, roomFragment, "ROOM_FRAGMENT");
            transaction.attach(roomFragment);
        }

        transaction.commit();
    }

    public void openPrivateChatRoomFragment(String roomId, String roomTitle) {
        currentFragment = "PRIVATE_ROOM_FRAGMENT";
        hideSoftKeyboard();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        Bundle bundle = new Bundle();
        bundle.putString("BUNDLE_ROOM_ID", roomId);
        bundle.putString("BUNDLE_ROOM_TITLE", roomTitle);
        bundle.putString("BUNDLE_ROOM_TYPE", "private");

        roomFragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.setCustomAnimations(R.anim.slide_from_right_fade_in, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right_fade_out);
        if (roomFragment.isAdded()) {
            transaction.detach(roomFragment);
            transaction.attach(roomFragment);
        } else {
            transaction.add(R.id.activity_home_layout, roomFragment, "ROOM_FRAGMENT");
            transaction.attach(roomFragment);
        }

        transaction.commit();
    }

    public void openPrivateChatRoomFromRoomFragment(String roomId, String roomTitle) {
        currentFragment = "PRIVATE_ROOM_FROM_ROOM_FRAGMENT";
        hideSoftKeyboard();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        Bundle bundle = new Bundle();
        bundle.putString("BUNDLE_ROOM_ID", roomId);
        bundle.putString("BUNDLE_ROOM_TITLE", roomTitle);
        bundle.putString("BUNDLE_ROOM_TYPE", "private");
        bundle.putString("BUNDLE_ROOM_REFF", "room");

        roomFragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_from_right_fade_in, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right_fade_out);
        transaction.detach(roomDetailFragment);
        if (roomFragment.isAdded()) {
            transaction.detach(roomFragment);
            transaction.attach(roomFragment);
        } else {
            transaction.add(R.id.activity_home_layout, roomFragment, "ROOM_FRAGMENT");
            transaction.attach(roomFragment);
        }
        transaction.commit();
    }

    public void openPrivateChatRoomFromComposeFragment(String roomId, String roomTitle) {
        currentFragment = "PRIVATE_ROOM_FROM_COMPOSE_FRAGMENT";
        hideSoftKeyboard();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        Bundle bundle = new Bundle();
        bundle.putString("BUNDLE_ROOM_ID", roomId);
        bundle.putString("BUNDLE_ROOM_TITLE", roomTitle);
        bundle.putString("BUNDLE_ROOM_TYPE", "private");
        bundle.putString("BUNDLE_ROOM_REFF", "room");

        roomFragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_from_right_fade_in, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right_fade_out);
        transaction.detach(composeFragment);
        if (roomFragment.isAdded()) {
            transaction.detach(roomFragment);
            transaction.attach(roomFragment);
        } else {
            transaction.add(R.id.activity_home_layout, roomFragment, "ROOM_FRAGMENT");
            transaction.attach(roomFragment);
        }
        transaction.commit();
    }

    public void openChatInfoFragment(String roomId, String roomTitle, int transitionMode) {
        currentFragment = "INFO_FRAGMENT";
        hideSoftKeyboard();
        Bundle bundle = new Bundle();
        bundle.putString("BUNDLE_ROOM_ID", roomId);
        bundle.putString("BUNDLE_ROOM_TITLE", roomTitle);

        infoFragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (transitionMode == 1) {
            transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right);
        } else if (transitionMode == 2) {
            transaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_to_right, R.anim.slide_from_right, R.anim.slide_to_left);
        }

        if (infoFragment.isAdded()) {
            transaction.detach(infoFragment);
            transaction.attach(infoFragment);
        } else {
            transaction.add(R.id.activity_home_layout, infoFragment, "INFO_FRAGMENT");
            transaction.attach(infoFragment);
        }

        transaction.commit();
    }

    public void openPrivateChatInfoFragment(String roomId, String roomTitle, int transitionMode) {
        currentFragment = "PRIVATE_INFO_FRAGMENT";
        hideSoftKeyboard();
        Bundle bundle = new Bundle();
        bundle.putString("BUNDLE_ROOM_ID", roomId);
        bundle.putString("BUNDLE_ROOM_TITLE", roomTitle);

        infoFragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (transitionMode == 1) {
            transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right);
        } else if (transitionMode == 2) {
            transaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_to_right, R.anim.slide_from_right, R.anim.slide_to_left);
        }

        if (infoFragment.isAdded()) {
            transaction.detach(infoFragment);
            transaction.attach(infoFragment);
        } else {
            transaction.add(R.id.activity_home_layout, infoFragment, "INFO_FRAGMENT");
            transaction.attach(infoFragment);
        }

        transaction.commit();
    }

    public void openImageFragment(ChatRoomModel model) {
        currentFragment = "IMAGE_FRAGMENT";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }
        hideSoftKeyboard();
        Bundle bundle = new Bundle();
        bundle.putString("BUNDLE_IMAGE_URL", model.getFile_download_url());
        bundle.putString("BUNDLE_IMAGE_NAME", model.getFile_name());
        bundle.putString("BUNDLE_IMAGE_SIZE", model.getFile_size());
        bundle.putString("BUNDLE_MESSAGE", model.getMessage());
        bundle.putString("BUNDLE_SENDER", model.getSender_name());
        bundle.putLong("BUNDLE_TIMESTAMP", model.getTimestamp());

        imageFragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);

        if (imageFragment.isAdded()) {
            transaction.detach(imageFragment);
            transaction.attach(imageFragment);
        } else {
            transaction.add(R.id.activity_home_layout, imageFragment, "IMAGE_FRAGMENT");
            transaction.attach(imageFragment);
        }

        transaction.commit();
    }

    public void openPrivateImageFragment(ChatRoomModel model) {
        currentFragment = "PRIVATE_IMAGE_FRAGMENT";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }
        hideSoftKeyboard();
        Bundle bundle = new Bundle();
        bundle.putString("BUNDLE_IMAGE_URL", model.getFile_download_url());
        bundle.putString("BUNDLE_IMAGE_NAME", model.getFile_name());
        bundle.putString("BUNDLE_IMAGE_SIZE", model.getFile_size());
        bundle.putString("BUNDLE_MESSAGE", model.getMessage());
        bundle.putString("BUNDLE_SENDER", model.getSender_name());
        bundle.putLong("BUNDLE_TIMESTAMP", model.getTimestamp());

        imageFragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);

        if (imageFragment.isAdded()) {
            transaction.detach(imageFragment);
            transaction.attach(imageFragment);
        } else {
            transaction.add(R.id.activity_home_layout, imageFragment, "IMAGE_FRAGMENT");
            transaction.attach(imageFragment);
        }

        transaction.commit();
    }

    public void openImageFromHomeFragment(ChatRoomModel model) {
        currentFragment = "IMAGE_FROM_HOME_FRAGMENT";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }
        hideSoftKeyboard();
        Bundle bundle = new Bundle();
        bundle.putString("BUNDLE_ROOM_ID", model.getRoom_id());
        bundle.putString("BUNDLE_ROOM_TYPE", model.getRoom_type());
        bundle.putString("BUNDLE_IMAGE_URL", model.getFile_download_url());
        bundle.putString("BUNDLE_IMAGE_NAME", model.getFile_name());
        bundle.putString("BUNDLE_IMAGE_SIZE", model.getFile_size());
        bundle.putString("BUNDLE_MESSAGE", model.getMessage());
        bundle.putString("BUNDLE_SENDER", model.getSender_name());
        bundle.putLong("BUNDLE_TIMESTAMP", model.getTimestamp());

        imageFragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);

        if (imageFragment.isAdded()) {
            transaction.detach(imageFragment);
            transaction.attach(imageFragment);
        } else {
            transaction.add(R.id.activity_home_layout, imageFragment, "IMAGE_FRAGMENT");
            transaction.attach(imageFragment);
        }

        transaction.commit();
    }

    public void openImageFromAccountFragment(ChatRoomModel model) {
        currentFragment = "IMAGE_FROM_ACCOUNT_FRAGMENT";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }
        hideSoftKeyboard();
        Bundle bundle = new Bundle();
        bundle.putString("BUNDLE_IMAGE_URL", model.getFile_download_url());
        bundle.putString("BUNDLE_IMAGE_NAME", model.getFile_name());
        bundle.putString("BUNDLE_IMAGE_SIZE", model.getFile_size());
        bundle.putString("BUNDLE_MESSAGE", model.getMessage());
        bundle.putString("BUNDLE_SENDER", model.getSender_name());
        bundle.putLong("BUNDLE_TIMESTAMP", model.getTimestamp());

        imageFragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);

        if (imageFragment.isAdded()) {
            transaction.detach(imageFragment);
            transaction.attach(imageFragment);
        } else {
            transaction.add(R.id.activity_home_layout, imageFragment, "IMAGE_FRAGMENT");
            transaction.attach(imageFragment);
        }

        transaction.commit();
    }

    public void openImageFromRoomFragment(ChatRoomModel model) {
        currentFragment = "IMAGE_FROM_ROOM_FRAGMENT";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }
        hideSoftKeyboard();
        Bundle bundle = new Bundle();
        bundle.putString("BUNDLE_ROOM_ID", model.getRoom_id());
        bundle.putString("BUNDLE_ROOM_TYPE", model.getRoom_type());
        bundle.putString("BUNDLE_IMAGE_URL", model.getFile_download_url());
        bundle.putString("BUNDLE_IMAGE_NAME", model.getFile_name());
        bundle.putString("BUNDLE_IMAGE_SIZE", model.getFile_size());
        bundle.putString("BUNDLE_MESSAGE", model.getMessage());
        bundle.putString("BUNDLE_SENDER", model.getSender_name());
        bundle.putLong("BUNDLE_TIMESTAMP", model.getTimestamp());

        imageFragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);

        if (imageFragment.isAdded()) {
            transaction.detach(imageFragment);
            transaction.attach(imageFragment);
        } else {
            transaction.add(R.id.activity_home_layout, imageFragment, "IMAGE_FRAGMENT");
            transaction.attach(imageFragment);
        }

        transaction.commit();
    }

    public void openImageFromRoomImageFragment(ChatRoomModel model) {
        currentFragment = "IMAGE_FROM_ROOM_IMAGE_FRAGMENT";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }
        hideSoftKeyboard();
        Bundle bundle = new Bundle();
        bundle.putString("BUNDLE_IMAGE_URL", model.getFile_download_url());
        bundle.putString("BUNDLE_IMAGE_NAME", model.getFile_name());
        bundle.putString("BUNDLE_IMAGE_SIZE", model.getFile_size());
        bundle.putString("BUNDLE_MESSAGE", model.getMessage());
        bundle.putString("BUNDLE_SENDER", model.getSender_name());
        bundle.putLong("BUNDLE_TIMESTAMP", model.getTimestamp());

        imageFragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);

        if (imageFragment.isAdded()) {
            transaction.detach(imageFragment);
            transaction.attach(imageFragment);
        } else {
            transaction.add(R.id.activity_home_layout, imageFragment, "IMAGE_FRAGMENT");
            transaction.attach(imageFragment);
        }

        transaction.commit();
    }

    public void openImageFromAnnouncementFragment(AnnouncementModel model) {
        currentFragment = "IMAGE_FROM_ANNOUNCEMENT_FRAGMENT";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }
        hideSoftKeyboard();
        Bundle bundle = new Bundle();
        bundle.putString("BUNDLE_IMAGE_URL", model.getImage_url());
        bundle.putString("BUNDLE_SENDER", model.getSender_name());
        bundle.putLong("BUNDLE_TIMESTAMP", model.getTimestamp());

        imageFragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);

        if (imageFragment.isAdded()) {
            transaction.detach(imageFragment);
            transaction.attach(imageFragment);
        } else {
            transaction.add(R.id.activity_home_layout, imageFragment, "IMAGE_FRAGMENT");
            transaction.attach(imageFragment);
        }

        transaction.commit();
    }

    public void openPrivateImageFromRoomImageFragment(ChatRoomModel model) {
        currentFragment = "PRIVATE_IMAGE_FROM_ROOM_IMAGE_FRAGMENT";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }
        hideSoftKeyboard();
        Bundle bundle = new Bundle();
        bundle.putString("BUNDLE_IMAGE_URL", model.getFile_download_url());
        bundle.putString("BUNDLE_IMAGE_NAME", model.getFile_name());
        bundle.putString("BUNDLE_IMAGE_SIZE", model.getFile_size());
        bundle.putString("BUNDLE_MESSAGE", model.getMessage());
        bundle.putString("BUNDLE_SENDER", model.getSender_name());
        bundle.putLong("BUNDLE_TIMESTAMP", model.getTimestamp());

        imageFragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);

        if (imageFragment.isAdded()) {
            transaction.detach(imageFragment);
            transaction.attach(imageFragment);
        } else {
            transaction.add(R.id.activity_home_layout, imageFragment, "IMAGE_FRAGMENT");
            transaction.attach(imageFragment);
        }

        transaction.commit();
    }

    public void openPrivateImageFromRoomFragment(ChatRoomModel model) {
        currentFragment = "PRIVATE_IMAGE_FROM_ROOM_FRAGMENT";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }
        hideSoftKeyboard();
        Bundle bundle = new Bundle();
        bundle.putString("BUNDLE_ROOM_ID", model.getRoom_id());
        bundle.putString("BUNDLE_ROOM_TYPE", model.getRoom_type());
        bundle.putString("BUNDLE_IMAGE_URL", model.getFile_download_url());
        bundle.putString("BUNDLE_IMAGE_NAME", model.getFile_name());
        bundle.putString("BUNDLE_IMAGE_SIZE", model.getFile_size());
        bundle.putString("BUNDLE_MESSAGE", model.getMessage());
        bundle.putString("BUNDLE_SENDER", model.getSender_name());
        bundle.putLong("BUNDLE_TIMESTAMP", model.getTimestamp());

        imageFragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);

        if (imageFragment.isAdded()) {
            transaction.detach(imageFragment);
            transaction.attach(imageFragment);
        } else {
            transaction.add(R.id.activity_home_layout, imageFragment, "IMAGE_FRAGMENT");
            transaction.attach(imageFragment);
        }

        transaction.commit();
    }

    public void openRoomFragment(String roomId, String roomTitle, String roomType, int transitionMode) {
        currentFragment = "ROOM_DETAIL_FRAGMENT";
        hideSoftKeyboard();
        Bundle bundle = new Bundle();
        bundle.putString("BUNDLE_ROOM_ID", roomId);
        bundle.putString("BUNDLE_ROOM_TITLE", roomTitle);
        bundle.putString("BUNDLE_ROOM_TYPE", roomType);

        roomDetailFragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (transitionMode == 1) {
            transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right);
        } else if (transitionMode == 2) {
            transaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_to_right, R.anim.slide_from_right, R.anim.slide_to_left);
        }

        if (roomDetailFragment.isAdded()) {
            transaction.detach(roomDetailFragment);
            transaction.attach(roomDetailFragment);
        } else {
            transaction.add(R.id.activity_home_layout, roomDetailFragment, "ROOM_DETAIL_FRAGMENT");
            transaction.attach(roomDetailFragment);
        }

        transaction.commit();
    }

    public void openPrivateRoomFragment(String roomId, String roomTitle, String roomType, int transitionMode) {
        currentFragment = "PRIVATE_ROOM_DETAIL_FRAGMENT";
        hideSoftKeyboard();
        Bundle bundle = new Bundle();
        bundle.putString("BUNDLE_ROOM_ID", roomId);
        bundle.putString("BUNDLE_ROOM_TITLE", roomTitle);
        bundle.putString("BUNDLE_ROOM_TYPE", roomType);

        roomDetailFragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (transitionMode == 1) {
            transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right);
        } else if (transitionMode == 2) {
            transaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_to_right, R.anim.slide_from_right, R.anim.slide_to_left);
        }

        if (roomDetailFragment.isAdded()) {
            transaction.detach(roomDetailFragment);
            transaction.attach(roomDetailFragment);
        } else {
            transaction.add(R.id.activity_home_layout, roomDetailFragment, "ROOM_DETAIL_FRAGMENT");
            transaction.attach(roomDetailFragment);
        }

        transaction.commit();
    }


    public void openRoomFileFragment(String roomId, String roomTitle, int transitionMode) {
        currentFragment = "ROOM_FILE_FRAGMENT";
        hideSoftKeyboard();
        Bundle bundle = new Bundle();
        bundle.putString("BUNDLE_ROOM_ID", roomId);
        bundle.putString("BUNDLE_ROOM_TITLE", roomTitle);

        roomFileFragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (transitionMode == 1) {
            transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right);
        } else if (transitionMode == 2) {
            transaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_to_right, R.anim.slide_from_right, R.anim.slide_to_left);
        }

        if (roomFileFragment.isAdded()) {
            transaction.detach(roomFileFragment);
            transaction.attach(roomFileFragment);
        } else {
            transaction.add(R.id.activity_home_layout, roomFileFragment, "ROOM_FILE_FRAGMENT");
            transaction.attach(roomFileFragment);
        }

        transaction.commit();
    }

    public void openPrivateRoomFileFragment(String roomId, String roomTitle, int transitionMode) {
        currentFragment = "PRIVATE_ROOM_FILE_FRAGMENT";
        hideSoftKeyboard();
        Bundle bundle = new Bundle();
        bundle.putString("BUNDLE_ROOM_ID", roomId);
        bundle.putString("BUNDLE_ROOM_TITLE", roomTitle);

        roomFileFragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (transitionMode == 1) {
            transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right);
        } else if (transitionMode == 2) {
            transaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_to_right, R.anim.slide_from_right, R.anim.slide_to_left);
        }

        if (roomFileFragment.isAdded()) {
            transaction.detach(roomFileFragment);
            transaction.attach(roomFileFragment);
        } else {
            transaction.add(R.id.activity_home_layout, roomFileFragment, "ROOM_FILE_FRAGMENT");
            transaction.attach(roomFileFragment);
        }

        transaction.commit();
    }

    public void openRoomImageFragment(String roomId, String roomTitle, int transitionMode) {
        currentFragment = "ROOM_IMAGE_FRAGMENT";

        hideSoftKeyboard();
        Bundle bundle = new Bundle();
        bundle.putString("BUNDLE_ROOM_ID", roomId);
        bundle.putString("BUNDLE_ROOM_TITLE", roomTitle);

        roomImageFragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (transitionMode == 1) {
            transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right);
        } else if (transitionMode == 2) {
            transaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_to_right, R.anim.slide_from_right, R.anim.slide_to_left);
        }

        if (roomImageFragment.isAdded()) {
            transaction.detach(roomImageFragment);
            transaction.attach(roomImageFragment);
        } else {
            transaction.add(R.id.activity_home_layout, roomImageFragment, "ROOM_IMAGE_FRAGMENT");
            transaction.attach(roomImageFragment);
        }

        transaction.commit();
    }

    public void openPrivateRoomImageFragment(String roomId, String roomTitle, int transitionMode) {
        currentFragment = "PRIVATE_ROOM_IMAGE_FRAGMENT";

        hideSoftKeyboard();
        Bundle bundle = new Bundle();
        bundle.putString("BUNDLE_ROOM_ID", roomId);
        bundle.putString("BUNDLE_ROOM_TITLE", roomTitle);

        roomImageFragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (transitionMode == 1) {
            transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right);
        } else if (transitionMode == 2) {
            transaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_to_right, R.anim.slide_from_right, R.anim.slide_to_left);
        }

        if (roomImageFragment.isAdded()) {
            transaction.detach(roomImageFragment);
            transaction.attach(roomImageFragment);
        } else {
            transaction.add(R.id.activity_home_layout, roomImageFragment, "ROOM_IMAGE_FRAGMENT");
            transaction.attach(roomImageFragment);
        }

        transaction.commit();
    }

    public void openScheduleFragment() {
        currentFragment = "SCHEDULE_FRAGMENT";
        hideSoftKeyboard();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (scheduleFragment.isAdded()) {
            transaction.detach(scheduleFragment);
            transaction.attach(scheduleFragment);
        } else {
            transaction.add(R.id.activity_home_layout, scheduleFragment, "SCHEDULE_FRAGMENT");
            transaction.attach(scheduleFragment);
        }
        transaction.commit();
    }

    public void openAssignmentFragment() {
        currentFragment = "ASSIGNMENT_FRAGMENT";
        hideSoftKeyboard();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (assignmentFragment.isAdded()) {
            transaction.detach(assignmentFragment);
            transaction.attach(assignmentFragment);
        } else {
            transaction.add(R.id.activity_home_layout, assignmentFragment, "ASSIGNMENT_FRAGMENT");
            transaction.attach(assignmentFragment);
        }
        transaction.commit();
    }

    public void openAssignmentAddFragment(int transitionMode) {
        currentFragment = "ADD_ASSIGNMENT_FRAGMENT";
        hideSoftKeyboard();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (transitionMode == 1) {
            transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right);
        } else if (transitionMode == 2) {
            transaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_to_right, R.anim.slide_from_right, R.anim.slide_to_left);
        }
        if (assignmentAddFragment.isAdded()) {
            transaction.detach(assignmentAddFragment);
            transaction.attach(assignmentAddFragment);
        } else {
            transaction.add(R.id.activity_home_layout, assignmentAddFragment, "ADD_ASSIGNMENT_FRAGMENT");
            transaction.attach(assignmentAddFragment);
        }
        transaction.commit();

    }

    public void openInformationFragment(int transitionMode) {
        currentFragment = "INFORMATION_FRAGMENT";
        hideSoftKeyboard();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (transitionMode == 1) {
            transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right);
        } else if (transitionMode == 2) {
            transaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_to_right, R.anim.slide_from_right, R.anim.slide_to_left);
        }
        if (informationFragment.isAdded()) {
            transaction.detach(informationFragment);
            transaction.attach(informationFragment);
        } else {
            transaction.add(R.id.activity_home_layout, informationFragment, "INFORMATION_FRAGMENT");
            transaction.attach(informationFragment);
        }
        transaction.commit();
    }

    public void openKhsFragment(String id, String name) {
        currentFragment = "KHS_FRAGMENT";
        hideSoftKeyboard();
        Bundle bundle = new Bundle();
        bundle.putString("BUNDLE_KHS_ID", id);
        bundle.putString("BUNDLE_KHS_NAME", name);

        khsFragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right);
        if (khsFragment.isAdded()) {
            transaction.detach(khsFragment);
            transaction.attach(khsFragment);
        } else {
            transaction.add(R.id.activity_home_layout, khsFragment, "KHS_FRAGMENT");
            transaction.attach(khsFragment);
        }
        transaction.commit();
    }

    public void openStudyFragment() {
        currentFragment = "STUDY_FRAGMENT";
        hideSoftKeyboard();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right);
        if (studyFragment.isAdded()) {
            transaction.detach(studyFragment);
            transaction.attach(studyFragment);
        } else {
            transaction.add(R.id.activity_home_layout, studyFragment, "STUDY_FRAGMENT");
            transaction.attach(studyFragment);
        }
        transaction.commit();
    }

    public void openBillFragment() {
        currentFragment = "BILL_FRAGMENT";
        hideSoftKeyboard();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right);
        if (billFragment.isAdded()) {
            transaction.detach(billFragment);
            transaction.attach(billFragment);
        } else {
            transaction.add(R.id.activity_home_layout, billFragment, "BILL_FRAGMENT");
            transaction.attach(billFragment);
        }
        transaction.commit();
    }

    public void openAnnouncementFragment() {
        currentFragment = "ANNOUNCEMENT_FRAGMENT";
        hideSoftKeyboard();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right);
        if (announcementFragment.isAdded()) {
            transaction.detach(announcementFragment);
            transaction.attach(announcementFragment);
        } else {
            transaction.add(R.id.activity_home_layout, announcementFragment, "ANNOUNCEMENT_FRAGMENT");
            transaction.attach(announcementFragment);
        }
        transaction.commit();
    }

    public void openAnnouncementAddFragment(int transitionMode) {
        currentFragment = "ADD_ANNOUNCEMENT_FRAGMENT";
        hideSoftKeyboard();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (transitionMode == 1) {
            transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right);
        } else if (transitionMode == 2) {
            transaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_to_right, R.anim.slide_from_right, R.anim.slide_to_left);
        }
        if (announcementAddFragment.isAdded()) {
            transaction.detach(announcementAddFragment);
            transaction.attach(announcementAddFragment);
        } else {
            transaction.add(R.id.activity_home_layout, announcementAddFragment, "ADD_ANNOUNCEMENT_FRAGMENT");
            transaction.attach(announcementAddFragment);
        }
        transaction.commit();
    }

    public void openSearchFragment() {
        currentFragment = "SEARCH_FRAGMENT";
        hideSoftKeyboard();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (searchFragment.isAdded()) {
            transaction.detach(searchFragment);
            transaction.attach(searchFragment);
        } else {
            transaction.add(R.id.activity_home_layout, searchFragment, "SEARCH_FRAGMENT");
            transaction.attach(searchFragment);
        }
        transaction.commit();
    }

    public boolean isPassAnnouncementFilter(String filters) {
        if (filters.contains(",")) {
            boolean pass = false;
            String[] filtersSplit = filters.split(",");
            for (String filter : filtersSplit) {
                if (spm.getUserId().toLowerCase().startsWith(filter.toLowerCase().trim())) {
                    pass = true;
                    break;
                }
            }

            return pass;
        } else {
            return spm.getUserId().toLowerCase().startsWith(filters.toLowerCase().trim());
        }
    }

    public Long getNextTimestamp(long start, int step) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(start);

        calendar.add(Calendar.DAY_OF_YEAR, step);
        Date next = calendar.getTime();

        return next.getTime();
    }

    public void loadAnnouncement() {
        final Query announcementRef = db
                .getReference("announcement")
                .orderByChild("timestamp");

        announcementRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                AnnouncementModel model = dataSnapshot.getValue(AnnouncementModel.class);
                if (spm.getUserType().equals("staff") || model.getFilter() == null) {
                    announcementModelList.addFirst(model);
                    announcementAdapter.notifyDataSetChanged();

                    if (getNextTimestamp(model.getTimestamp(), 3) > DateHelper.getTimestamp()) {
                        badgeInformationCount++;
                        updateBadgeInformation();
                    }

                    if (announcementFragment.tvEmpty != null) {
                        if (announcementModelList.isEmpty()) {
                            announcementFragment.tvEmpty.setVisibility(View.VISIBLE);
                        } else {
                            announcementFragment.tvEmpty.setVisibility(View.GONE);
                        }
                    }
                } else {
                    if (model.getFilter().equals("")) {
                        announcementModelList.addFirst(model);
                        announcementAdapter.notifyDataSetChanged();

                        if (getNextTimestamp(model.getTimestamp(), 3) > DateHelper.getTimestamp()) {
                            badgeInformationCount++;
                            updateBadgeInformation();
                        }

                        if (announcementFragment.tvEmpty != null) {
                            if (announcementModelList.isEmpty()) {
                                announcementFragment.tvEmpty.setVisibility(View.VISIBLE);
                            } else {
                                announcementFragment.tvEmpty.setVisibility(View.GONE);
                            }
                        }
                    } else {
                        if (isPassAnnouncementFilter(model.getFilter())) {
                            announcementModelList.addFirst(model);
                            announcementAdapter.notifyDataSetChanged();

                            if (getNextTimestamp(model.getTimestamp(), 3) > DateHelper.getTimestamp()) {
                                badgeInformationCount++;
                                updateBadgeInformation();
                            }

                            if (announcementFragment.tvEmpty != null) {
                                if (announcementModelList.isEmpty()) {
                                    announcementFragment.tvEmpty.setVisibility(View.VISIBLE);
                                } else {
                                    announcementFragment.tvEmpty.setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                AnnouncementModel model = dataSnapshot.getValue(AnnouncementModel.class);
                if (announcementModelList.contains(model)) {
                    announcementModelList.remove(model);
                    announcementAdapter.notifyDataSetChanged();

                    if (getNextTimestamp(model.getTimestamp(), 3) > DateHelper.getTimestamp()) {
                        badgeInformationCount--;
                        updateBadgeInformation();
                    }
                }
                if (announcementFragment.tvEmpty != null) {
                    if (announcementModelList.isEmpty()) {
                        announcementFragment.tvEmpty.setVisibility(View.VISIBLE);
                    } else {
                        announcementFragment.tvEmpty.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void loadChat() {
        //Load rooms
        roomRef = db
                .getReference("room")
                .orderByChild("member/" + spm.getUserId())
                .equalTo(true);

        roomListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Store rooms count
                roomCount = (int) dataSnapshot.getChildrenCount();
                if (homeFragment.tvEmpty != null) {
                    if (roomCount > 0) {
                        homeFragment.tvEmpty.setVisibility(View.GONE);
                    } else {
                        homeFragment.tvEmpty.setVisibility(View.VISIBLE);
                    }
                }

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //Store room model
                    RoomModel roomModel = ds.getValue(RoomModel.class);
                    roomDetailMap.put(roomModel.getId(), roomModel);


                    //Store room index

                    roomIndexSet.add(roomModel.getId());

                    //Update Room Avatar
                    if (currentFragment.equals("ROOM_FRAGMENT")) {
                        if (roomFragment.roomId.equals(roomModel.getId())) {
                            if (roomModel.getAvatar_url() == null) {
                                roomModel.setAvatar_url(getRoomAvatarUrl(roomModel.getId()));
                            }
                            roomFragment.updateRoomAvatar(roomModel.getAvatar_url());
                        }
                    }

                    //Store current Room ID
                    final String roomId = ds.getKey();

                    //Store current Room Member
                    for (DataSnapshot dsMember : ds.child("member").getChildren()) {
                        if (roomMemberListMap.containsKey(roomId)) {
                            roomMemberListMap.get(roomId).add(dsMember.getKey());
                        } else {
                            ArrayList users = new ArrayList();
                            users.add(dsMember.getKey());
                            roomMemberListMap.put(roomId, users);
                        }

                    }

                    //Load users detail
                    loadRoomUsers(roomId);

                    if (!spm.getUserType().equals("staff")) {
                        //Ref of current room to load assignments

                        Query assignmentRef = db
                                .getReference("assignment")
                                .child(roomId)
                                .orderByChild("timestamp");

                        ChildEventListener assignmentListener = new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                AssignmentModel assignment = dataSnapshot.getValue(AssignmentModel.class);
                                if (assignment.getMax_date() > DateHelper.getTimestamp()) {
                                    badgeAssignmentCount++;
                                    updateBadgeAssignment();
                                }

                                if (!assignmentRawModelList.contains(assignment)) {
                                    assignmentRawModelList.add(assignment);
                                }

                                if (assignmentFragment.tvEmpty != null) {
                                    if (assignmentRawModelList.isEmpty()) {
                                        assignmentFragment.tvEmpty.setVisibility(View.VISIBLE);
                                    } else {
                                        assignmentFragment.tvEmpty.setVisibility(View.GONE);
                                    }
                                }

                                assignmentModelList.clear();
                                assignmentAdapter.notifyDataSetChanged();
                                currentAssignmentHeader = "matakuliah";

                                for (AssignmentModel model : assignmentRawModelList) {
                                    if (spm.getUserType().equals("student")) {
                                        String studentTitle = model.getRoom_title().split(" \\(")[0];
                                        model.setRoom_title(studentTitle);
                                    }
                                    if (currentAssignmentHeader.equals("matakuliah")) {
                                        if (!assignmentModelList.contains(model)) {
                                            currentAssignmentHeader = model.getRoom_title();
                                            assignmentModelList.add(currentAssignmentHeader);
                                            assignmentAdapter.notifyItemInserted(assignmentModelList.size());
                                            assignmentModelList.add(model);
                                            assignmentAdapter.notifyItemInserted(assignmentModelList.size());
                                        }
                                    } else {
                                        if (!assignmentModelList.contains(model)) {
                                            if (currentAssignmentHeader.equals(model.getRoom_title())) {
                                                assignmentModelList.add(model);
                                                assignmentAdapter.notifyItemInserted(assignmentModelList.size());
                                            } else {
                                                currentAssignmentHeader = model.getRoom_title();
                                                assignmentModelList.add(currentAssignmentHeader);
                                                assignmentAdapter.notifyItemInserted(assignmentModelList.size());
                                                assignmentModelList.add(model);
                                                assignmentAdapter.notifyItemInserted(assignmentModelList.size());
                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {
                                AssignmentModel assignment = dataSnapshot.getValue(AssignmentModel.class);
                                if (assignmentRawModelList.contains(assignment)) {
                                    assignmentRawModelList.remove(assignment);

                                    if (assignment.getMax_date() > DateHelper.getTimestamp()) {
                                        badgeAssignmentCount--;
                                        updateBadgeAssignment();
                                    }

                                    assignmentModelList.clear();
                                    assignmentAdapter.notifyDataSetChanged();
                                    currentAssignmentHeader = "matakuliah";

                                    for (AssignmentModel model : assignmentRawModelList) {
                                        if (spm.getUserType().equals("student")) {
                                            String studentTitle = model.getRoom_title().split(" \\(")[0];
                                            model.setRoom_title(studentTitle);
                                        }
                                        if (currentAssignmentHeader.equals("matakuliah")) {
                                            if (!assignmentModelList.contains(model)) {
                                                currentAssignmentHeader = model.getRoom_title();
                                                assignmentModelList.add(currentAssignmentHeader);
                                                assignmentAdapter.notifyItemInserted(assignmentModelList.size());
                                                assignmentModelList.add(model);
                                                assignmentAdapter.notifyItemInserted(assignmentModelList.size());
                                            }
                                        } else {
                                            if (!assignmentModelList.contains(model)) {
                                                if (currentAssignmentHeader.equals(model.getRoom_title())) {
                                                    assignmentModelList.add(model);
                                                    assignmentAdapter.notifyItemInserted(assignmentModelList.size());
                                                } else {
                                                    currentAssignmentHeader = model.getRoom_title();
                                                    assignmentModelList.add(currentAssignmentHeader);
                                                    assignmentAdapter.notifyItemInserted(assignmentModelList.size());
                                                    assignmentModelList.add(model);
                                                    assignmentAdapter.notifyItemInserted(assignmentModelList.size());
                                                }
                                            }
                                        }
                                    }
                                }

                                if (assignmentFragment.tvEmpty != null) {
                                    if (assignmentRawModelList.isEmpty()) {
                                        assignmentFragment.tvEmpty.setVisibility(View.VISIBLE);
                                    } else {
                                        assignmentFragment.tvEmpty.setVisibility(View.GONE);
                                    }
                                }

                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        };

                        assignmentRef.addChildEventListener(assignmentListener);
                        assignmentRefListenerMap.put(assignmentRef, assignmentListener);
                    }

                    if (!roomRefMap.containsKey(roomId)) {
                        //Ref of current room to load chats
                        Query chatRef = db
                                .getReference("chat")
                                .child(roomId)
                                .orderByChild("timestamp");

                        ChildEventListener chatListener = new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                //Store to types of model
                                ChatHomeModel chatHome = dataSnapshot.getValue(ChatHomeModel.class);
                                ChatRoomModel chatRoom = dataSnapshot.getValue(ChatRoomModel.class);

                                if (homeFragment.tvEmpty != null) {
                                    if (homeFragment.tvEmpty.getVisibility() == View.VISIBLE) {
                                        homeFragment.tvEmpty.setVisibility(View.GONE);
                                    }
                                }

                                //If current chat is already in home list, remove it first
                                int prevChat = homeModelList.indexOf(chatHome);
                                if (prevChat != -1) {
                                    homeModelList.remove(prevChat);
                                }
                                //Add current chat to home list
                                homeModelList.add(chatHome);

                                //Update unread count and update chat received value with current timestamp
                                if (!chatHome.getSender_id().equals(spm.getUserId())) {
                                    //Update unread count
                                    if (!chatHome.getRead().containsKey(spm.getUserId())) {
                                        if (currentFragment.equals("ROOM_FRAGMENT") || currentFragment.equals("PRIVATE_ROOM_FRAGMENT") || currentFragment.equals("PRIVATE_ROOM_FROM_ROOM_FRAGMENT") || currentFragment.equals("PRIVATE_ROOM_FROM_COMPOSE_FRAGMENT")) {
                                            if (roomFragment.roomId.equals(roomId)) {
                                                if (!isInBackground && spMessageNewReady) {
                                                    spMessageNew.play(spMessageNewId, volume, volume, 1, 0, 1f);
                                                }
                                            }
                                        }

                                        if (!chatHome.getType().equals("info_join")) {
                                            if (unreadCountMap.containsKey(chatHome.getRoom_id())) {
                                                Integer newCount = unreadCountMap.get(chatHome.getRoom_id()) + 1;
                                                unreadCountMap.put(chatHome.getRoom_id(), newCount);
                                            } else {
                                                unreadCountMap.put(chatHome.getRoom_id(), 1);
                                            }
                                        }

                                        //Update badge home
                                        updateBadgeHome();
                                    }

                                    if (currentFragment.equals("ROOM_FRAGMENT") || currentFragment.equals("PRIVATE_ROOM_FRAGMENT") || currentFragment.equals("PRIVATE_ROOM_FROM_ROOM_FRAGMENT") || currentFragment.equals("PRIVATE_ROOM_FROM_COMPOSE_FRAGMENT")) {
                                        if (roomFragment.roomId.equals(roomId)) {
                                            final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                                            String key = dataSnapshot.getKey();
                                            unreadCountMap.remove(roomId);
                                            //Update badge home
                                            updateBadgeHome();

                                            DatabaseReference readRef = db
                                                    .getReference("chat")
                                                    .child(roomId)
                                                    .child(key)
                                                    .child("read")
                                                    .child(spm.getUserId());

                                            readRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    Long timestamp = DateHelper.getTimestamp();
                                                    if (!dataSnapshot.exists() && !isInBackground) {
                                                        dataSnapshot.getRef().setValue(timestamp);

                                                        //Clear notification after read
                                                        notificationManager.cancel(roomId.hashCode());
                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                            if (notificationManager.getActiveNotifications().length == 1) {
                                                                notificationManager.cancel(0);
                                                            }
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    }

                                    DatabaseReference receivedRef = db
                                            .getReference("chat")
                                            .child(roomId)
                                            .child(dataSnapshot.getKey())
                                            .child("received")
                                            .child(spm.getUserId());

                                    receivedRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Long timestamp = System.currentTimeMillis();
                                            if (!dataSnapshot.exists()) {
                                                dataSnapshot.getRef().setValue(timestamp);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }

                                //Add chat to list based on room id

                                if (roomModelMap.containsKey(roomId)) {
                                    if (!chatRoom.getType().equals("info_join")) {
                                        roomModelMap.get(roomId).add(chatRoom);
                                    }
                                } else {
                                    if (!chatRoom.getType().equals("info_join")) {
                                        roomModelMap.put(roomId, new LinkedList<ChatRoomModel>());
                                        roomModelMap.get(roomId).add(chatRoom);
                                    }
                                }

                                roomAdapter.notifyDataSetChanged();
                                if (smootScroll) {
                                    if (roomFragment.recyclerview != null) {
                                        roomFragment.recyclerview.scrollToPosition(roomAdapter.getItemCount() - 1);
                                    }
                                }

                                //Sort and show the data when all rooms load finished

                                if (listReady) {
                                    Collections.sort(homeModelList);
                                    homeAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                ChatRoomModel chatRoom = dataSnapshot.getValue(ChatRoomModel.class);
                                ChatHomeModel chatHome = dataSnapshot.getValue(ChatHomeModel.class);

                                //If current chat is already in home list then update it
                                //Otherwise add new chat
                                int prevKey = homeModelList.indexOf(chatHome);
                                if (prevKey != -1) {
                                    homeModelList.get(prevKey).setRead(chatHome.getRead());
                                    homeModelList.get(prevKey).setReceived(chatHome.getReceived());
                                    //Add current chat to home list
                                    homeAdapter.notifyItemChanged(prevKey);
                                } else {
                                    homeModelList.addFirst(chatHome);
                                    homeAdapter.notifyItemInserted(0);
                                }
                                int prevChatRoom = 0;
                                if (roomModelMap.containsKey(roomId)) {
                                    prevChatRoom = roomModelMap.get(roomId).indexOf(chatRoom);
                                    if (prevChatRoom != -1) {
                                        roomModelMap.get(roomId).set(prevChatRoom, chatRoom);
                                    }
                                }

                                if (currentFragment.equals("INFO_FRAGMENT") || currentFragment.equals("PRIVATE_INFO_FRAGMENT")) {
                                    if (chatRoom.getId().equals(infoFragment.currentChat.getId())) {
                                        chatRoom.getRead().remove(spm.getUserId());
                                        chatRoom.getReceived().remove(spm.getUserId());

                                        infoFragment.readIdList.clear();
                                        infoFragment.readIdList.addAll(chatRoom.getRead().keySet());

                                        infoFragment.readTimeList.clear();
                                        infoFragment.readTimeList.addAll(chatRoom.getRead().values());

                                        infoFragment.receivedIdList.clear();
                                        infoFragment.receivedIdList.addAll(chatRoom.getReceived().keySet());

                                        infoFragment.receivedTimeList.clear();
                                        infoFragment.receivedTimeList.addAll(chatRoom.getReceived().values());

                                        readAdapter.notifyDataSetChanged();
                                        receivedAdapter.notifyDataSetChanged();

                                        infoFragment.setCurrentMessage(chatRoom);
                                        infoFragment.updateHistory(chatRoom);
                                    }
                                }

                                roomAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {
                                //Store to types of model
                                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                                ChatHomeModel chatHome = dataSnapshot.getValue(ChatHomeModel.class);
                                ChatRoomModel chatRoom = dataSnapshot.getValue(ChatRoomModel.class);

                                //Clear notification after read
                                notificationManager.cancel(chatHome.getRoom_id().hashCode());
                                notificationManager.cancel(chatRoom.getRoom_id().hashCode());

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    if (notificationManager.getActiveNotifications().length == 1) {
                                        notificationManager.cancel(0);
                                    }
                                }

                                int prevKey = homeModelList.indexOf(chatHome);
                                if (prevKey != -1) {
                                    if (homeModelList.get(prevKey).getId().equals(chatHome.getId())) {
                                        homeModelList.get(prevKey).setMessage("Pesan Telah Dihapus");
                                        homeAdapter.notifyItemChanged(prevKey);
                                    }

                                    if (unreadCountMap.containsKey(chatHome.getRoom_id()) && !chatHome.getType().equals("info_join")) {
                                        Integer newCount = unreadCountMap.get(chatHome.getRoom_id()) - 1;
                                        if (newCount > 0) {
                                            unreadCountMap.put(chatHome.getRoom_id(), newCount);
                                        } else {
                                            unreadCountMap.remove(chatHome.getRoom_id());
                                        }

                                        //Update badge home
                                        updateBadgeHome();
                                    }
                                }

                                if (roomModelMap.containsKey(roomId)) {
                                    int prevChatRoom = roomModelMap.get(roomId).indexOf(chatRoom);
                                    if (prevChatRoom != -1) {
                                        roomModelMap.get(roomId).remove(prevChatRoom);
                                        roomAdapter.notifyItemRemoved(prevChatRoom);
                                    }
                                }

                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        };

                        chatRef.addChildEventListener(chatListener);

                        //This is to notify when the list is ready
                        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                roomCountCurrent++;
                                if (roomCountCurrent == roomCount) {
                                    listReady = true;
                                    roomCountCurrent = 0;
                                    Collections.sort(homeModelList);
                                    homeAdapter.notifyDataSetChanged();
                                }

                                if (homeFragment.tvEmpty != null) {
                                    if (homeModelList.size() > 0) {
                                        homeFragment.tvEmpty.setVisibility(View.GONE);
                                    } else {
                                        homeFragment.tvEmpty.setVisibility(View.VISIBLE);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        roomRefMap.put(roomId, chatRef);
                        roomRefListenerMap.put(chatRef, chatListener);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        roomRef.addValueEventListener(roomListener);
    }

    public void loadRoomUsers(final String roomId) {
        for (final String userId : roomMemberListMap.get(roomId)) {
            roomRef = db
                    .getReference("user")
                    .child(userId);

            roomRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserModel user = dataSnapshot.getValue(UserModel.class);
                    if (user != null) {
                        userModelMap.put(user.getId(), user);
                        if (!composeModelList.contains(user) && !user.getId().equals(spm.getUserId())) {
                            composeModelList.add(user);
                        }
                        if (roomUserModelMap.containsKey(roomId)) {
                            if (!roomUserModelMap.get(roomId).contains(user)) {
                                roomUserModelMap.get(roomId).add(user);
                            }
                        } else {
                            ArrayList<UserModel> userList = new ArrayList<>();
                            userList.add(user);
                            roomUserModelMap.put(roomId, userList);
                        }
                        if (currentFragment.equals("PRIVATE_ROOM_FRAGMENT") || currentFragment.equals("PRIVATE_ROOM_FROM_ROOM_FRAGMENT") || currentFragment.equals("PRIVATE_ROOM_FROM_COMPOSE_FRAGMENT")) {
                            if (roomFragment.receiverId.equals(user.getId())) {
                                roomFragment.updateRoomAvatar(user.getAvatar_url());
                            }
                        }
                        homeAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //
                }
            });
        }
    }

    public void loadStudentSchedule(String mode) {
        badgeScheduleCount = 0;
        scheduleModelList.clear();
        scheduleAdapter.notifyDataSetChanged();
        scheduleFragment.showProgressBar();
        if (mode.equals("network")) {
            AndroidNetworking.get(ApiHelper.getHost() + "/jadwal_mahasiswa.php")
                    .addQueryParameter("nim", spm.getUserId())
                    .addQueryParameter("password", spm.getUserPassword())
                    .getResponseOnlyFromNetwork()
                    .build()
                    .getAsObjectList(ScheduleModel.class, new ParsedRequestListener<List<ScheduleModel>>() {
                        @Override
                        public void onResponse(List<ScheduleModel> models) {
                            scheduleFragment.hideProgressBar();
                            if (models.size() == 0) {
                                //Update schedule badge
                                badgeScheduleCount = 0;
                                updateBadgeSchedule();

                                if (scheduleFragment.tvEmpty != null) {
                                    scheduleFragment.tvEmpty.setVisibility(View.VISIBLE);
                                }
                            } else {
                                if (scheduleFragment.tvEmpty != null) {
                                    scheduleFragment.tvEmpty.setVisibility(View.GONE);
                                }
                            }
                            for (ScheduleModel model : models) {
                                if (model.getHari().equals(getCurrentDay())) {
                                    badgeScheduleCount++;
                                    updateBadgeSchedule();
                                }
                                if (models.size() == 1) {
                                    currentScheduleHeader = model.getHari();
                                    scheduleModelList.add(currentScheduleHeader);
                                    scheduleAdapter.notifyItemInserted(scheduleModelList.size());
                                    scheduleModelList.add(model);
                                    scheduleAdapter.notifyItemInserted(scheduleModelList.size());
                                } else {
                                    if (currentScheduleHeader.equals("minggu")) {
                                        currentScheduleHeader = model.getHari();
                                        scheduleModelList.add(currentScheduleHeader);
                                        scheduleAdapter.notifyItemInserted(scheduleModelList.size());
                                        scheduleModelList.add(model);
                                        scheduleAdapter.notifyItemInserted(scheduleModelList.size());
                                    } else {
                                        if (currentScheduleHeader.equals(model.getHari())) {
                                            scheduleModelList.add(model);
                                            scheduleAdapter.notifyItemInserted(scheduleModelList.size());
                                        } else {
                                            currentScheduleHeader = model.getHari();
                                            scheduleModelList.add(currentScheduleHeader);
                                            scheduleAdapter.notifyItemInserted(scheduleModelList.size());
                                            scheduleModelList.add(model);
                                            scheduleAdapter.notifyItemInserted(scheduleModelList.size());
                                        }
                                    }
                                }
                            }
                        }
                        @Override
                        public void onError(ANError e) {
                            scheduleFragment.hideProgressBar();
                            String message = "Error";
                            if (e.getErrorCode() != 0) {
                                ResponseErrorModel error = e.getErrorAsObject(ResponseErrorModel.class);
                                if (error.getMessage().equals("empty")) {
                                    message = "Tidak ada data";
                                } else if (error.getMessage().equals("server")) {
                                    message = "Server error";
                                }
                            } else {
                                message = "Koneksi bermasalah";
                            }
                            Snackbar.make(findViewById(R.id.fragment_schedule_layout), message,
                                    Snackbar.LENGTH_LONG)
                                    .show();
                        }
                    });
        } else if (mode.equals("cache")) {
            AndroidNetworking.get(ApiHelper.getHost() + "/jadwal_mahasiswa.php")
                    .addQueryParameter("nim", spm.getUserId())
                    .addQueryParameter("password", spm.getUserPassword())
                    .getResponseOnlyIfCached()
                    .build()
                    .getAsObjectList(ScheduleModel.class, new ParsedRequestListener<List<ScheduleModel>>() {
                        @Override
                        public void onResponse(List<ScheduleModel> models) {
                            scheduleFragment.hideProgressBar();
                            if (models.size() == 0) {
                                //Update schedule badge
                                badgeScheduleCount = 0;
                                updateBadgeSchedule();

                                if (scheduleFragment.tvEmpty != null) {
                                    scheduleFragment.tvEmpty.setVisibility(View.VISIBLE);
                                }
                            } else {
                                if (scheduleFragment.tvEmpty != null) {
                                    scheduleFragment.tvEmpty.setVisibility(View.GONE);
                                }
                            }
                            for (ScheduleModel model : models) {
                                if (model.getHari().equals(getCurrentDay())) {
                                    badgeScheduleCount++;
                                    updateBadgeSchedule();
                                }

                                if (currentScheduleHeader.equals("minggu")) {
                                    currentScheduleHeader = model.getHari();
                                    scheduleModelList.add(currentScheduleHeader);
                                    scheduleAdapter.notifyItemInserted(scheduleModelList.size());
                                    scheduleModelList.add(model);
                                    scheduleAdapter.notifyItemInserted(scheduleModelList.size());
                                } else {
                                    if (currentScheduleHeader.equals(model.getHari())) {
                                        scheduleModelList.add(model);
                                        scheduleAdapter.notifyItemInserted(scheduleModelList.size());
                                    } else {
                                        currentScheduleHeader = model.getHari();
                                        scheduleModelList.add(currentScheduleHeader);
                                        scheduleAdapter.notifyItemInserted(scheduleModelList.size());
                                        scheduleModelList.add(model);
                                        scheduleAdapter.notifyItemInserted(scheduleModelList.size());
                                    }
                                }
                            }
                        }
                        @Override
                        public void onError(ANError e) {
                            scheduleFragment.hideProgressBar();
                            String message = "Error";
                            if (e.getErrorCode() != 0) {
                                ResponseErrorModel error = e.getErrorAsObject(ResponseErrorModel.class);
                                if (error.getMessage().equals("empty")) {
                                    message = "Tidak ada data";
                                } else if (error.getMessage().equals("server")) {
                                    message = "Server error";
                                }
                            } else {
                                message = "Koneksi bermasalah";
                            }
                            Snackbar.make(findViewById(R.id.fragment_schedule_layout), message,
                                    Snackbar.LENGTH_LONG)
                                    .show();
                        }
                    });
        }
    }

    public void loadLecturerSchedule(String mode) {
        badgeScheduleCount = 0;
        scheduleModelList.clear();
        scheduleAdapter.notifyDataSetChanged();
        scheduleFragment.showProgressBar();
        if (mode.equals("network")) {
            AndroidNetworking.get(ApiHelper.getHost() + "/jadwal_dosen.php")
                    .addQueryParameter("nama", spm.getUserName())
                    .getResponseOnlyFromNetwork()
                    .build()
                    .getAsObjectList(ScheduleModel.class, new ParsedRequestListener<List<ScheduleModel>>() {
                        @Override
                        public void onResponse(List<ScheduleModel> models) {
                            scheduleFragment.hideProgressBar();
                            if (models.size() == 0) {
                                //Update schedule badge
                                badgeScheduleCount = 0;
                                updateBadgeSchedule();

                                if (scheduleFragment.tvEmpty != null) {
                                    scheduleFragment.tvEmpty.setVisibility(View.VISIBLE);
                                }
                            } else {
                                if (scheduleFragment.tvEmpty != null) {
                                    scheduleFragment.tvEmpty.setVisibility(View.GONE);
                                }
                            }
                            for (ScheduleModel model : models) {
                                if (model.getHari().equals(getCurrentDay())) {
                                    badgeScheduleCount++;
                                    updateBadgeSchedule();
                                }

                                if (currentScheduleHeader.equals("minggu")) {
                                    currentScheduleHeader = model.getHari();
                                    scheduleModelList.add(currentScheduleHeader);
                                    scheduleAdapter.notifyItemInserted(scheduleModelList.size());
                                    scheduleModelList.add(model);
                                    scheduleAdapter.notifyItemInserted(scheduleModelList.size());
                                } else {
                                    if (currentScheduleHeader.equals(model.getHari())) {
                                        scheduleModelList.add(model);
                                        scheduleAdapter.notifyItemInserted(scheduleModelList.size());
                                    } else {
                                        currentScheduleHeader = model.getHari();
                                        scheduleModelList.add(currentScheduleHeader);
                                        scheduleAdapter.notifyItemInserted(scheduleModelList.size());
                                        scheduleModelList.add(model);
                                        scheduleAdapter.notifyItemInserted(scheduleModelList.size());
                                    }
                                }
                            }
                        }
                        @Override
                        public void onError(ANError e) {
                            scheduleFragment.hideProgressBar();
                            String message = "Error";
                            if (e.getErrorCode() != 0) {
                                ResponseErrorModel error = e.getErrorAsObject(ResponseErrorModel.class);
                                if (error.getMessage().equals("empty")) {
                                    message = "Tidak ada data";
                                } else if (error.getMessage().equals("server")) {
                                    message = "Server error";
                                }
                            } else {
                                message = "Koneksi bermasalah";
                            }
                            Snackbar.make(findViewById(R.id.fragment_schedule_layout), message,
                                    Snackbar.LENGTH_LONG)
                                    .show();
                        }
                    });
        } else if (mode.equals("cache")) {
            AndroidNetworking.get(ApiHelper.getHost() + "/jadwal_dosen.php")
                    .addQueryParameter("nama", spm.getUserName())
                    .getResponseOnlyIfCached()
                    .build()
                    .getAsObjectList(ScheduleModel.class, new ParsedRequestListener<List<ScheduleModel>>() {
                        @Override
                        public void onResponse(List<ScheduleModel> models) {
                            scheduleFragment.hideProgressBar();
                            if (models.size() == 0) {
                                //Update schedule badge
                                badgeScheduleCount = 0;
                                updateBadgeSchedule();

                                if (scheduleFragment.tvEmpty != null) {
                                    scheduleFragment.tvEmpty.setVisibility(View.VISIBLE);
                                }
                            } else {
                                if (scheduleFragment.tvEmpty != null) {
                                    scheduleFragment.tvEmpty.setVisibility(View.GONE);
                                }
                            }
                            for (ScheduleModel model : models) {
                                if (model.getHari().equals(getCurrentDay())) {
                                    badgeScheduleCount++;
                                    updateBadgeSchedule();
                                }

                                if (currentScheduleHeader.equals("minggu")) {
                                    currentScheduleHeader = model.getHari();
                                    scheduleModelList.add(currentScheduleHeader);
                                    scheduleAdapter.notifyItemInserted(scheduleModelList.size());
                                    scheduleModelList.add(model);
                                    scheduleAdapter.notifyItemInserted(scheduleModelList.size());
                                } else {
                                    if (currentScheduleHeader.equals(model.getHari())) {
                                        scheduleModelList.add(model);
                                        scheduleAdapter.notifyItemInserted(scheduleModelList.size());
                                    } else {
                                        currentScheduleHeader = model.getHari();
                                        scheduleModelList.add(currentScheduleHeader);
                                        scheduleAdapter.notifyItemInserted(scheduleModelList.size());
                                        scheduleModelList.add(model);
                                        scheduleAdapter.notifyItemInserted(scheduleModelList.size());
                                    }
                                }
                            }
                        }
                        @Override
                        public void onError(ANError e) {
                            scheduleFragment.hideProgressBar();
                            String message = "Error";
                            if (e.getErrorCode() != 0) {
                                ResponseErrorModel error = e.getErrorAsObject(ResponseErrorModel.class);
                                if (error.getMessage().equals("empty")) {
                                    message = "Tidak ada data";
                                } else if (error.getMessage().equals("server")) {
                                    message = "Server error";
                                }
                            } else {
                                message = "Koneksi bermasalah";
                            }
                            Snackbar.make(findViewById(R.id.fragment_schedule_layout), message,
                                    Snackbar.LENGTH_LONG)
                                    .show();
                        }
                    });
        }
    }

    public boolean isChatReadByAll(ChatRoomModel chat) {
        if (chat.getRead() == null) {
            return false;
        } else {
            if (roomUserModelMap.containsKey(chat.getRoom_id())) {
                if (chat.getRead().size() == roomUserModelMap.get(chat.getRoom_id()).size() && roomUserModelMap.get(chat.getRoom_id()).size() > 1) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    public boolean isChatReadByAll(ChatHomeModel chat) {
        if (chat.getRead() == null) {
            return false;
        } else {
            if (roomUserModelMap.containsKey(chat.getRoom_id())) {
                if (chat.getRead().size() == roomUserModelMap.get(chat.getRoom_id()).size() && roomUserModelMap.get(chat.getRoom_id()).size() > 1) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    public void hideSoftKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void showSoftKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    public void checkUserPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1);
            } else {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1);
            }
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 2);
            } else {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 2);
            }
        }
    }

    public void logout() {
        SharedPreferences.Editor editor = spm.getInstance().edit();
        editor.putString("USER_ID", "EMPTY");
        editor.putString("USER_PASSWORD", "EMPTY");
        editor.putString("USER_NAME", "EMPTY");
        editor.putString("USER_TOKEN", "EMPTY");
        editor.commit();
        im.backLogin();
        finish();
    }

    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_to_right, R.anim.slide_from_right, R.anim.slide_to_left);

        switch (currentFragment) {
            case "HOME_FRAGMENT":
                moveTaskToBack(true);
                break;
            case "HOME_FROM_ROOM_FRAGMENT":
                currentFragment = "HOME_FRAGMENT";
                transaction.detach(roomDetailFragment);
                transaction.detach(roomFragment);
                transaction.commit();
                break;
            case "ACCOUNT_FRAGMENT":
                currentFragment = "HOME_FRAGMENT";
                transaction.detach(accountFragment);
                transaction.commit();
                break;
            case "COMPOSE_FRAGMENT":
                currentFragment = "HOME_FRAGMENT";
                transaction.detach(composeFragment);
                transaction.commit();
                break;
            case "ROOM_FRAGMENT":
                roomFragment.replyModel = null;

                currentFragment = "HOME_FRAGMENT";
                roomFragment.onToolbarOptionArrowBackClick();
                transaction.detach(roomFragment);
                transaction.commit();
                break;
            case "PRIVATE_ROOM_FRAGMENT":
                roomFragment.replyModel = null;

                currentFragment = "HOME_FRAGMENT";
                roomFragment.onToolbarOptionArrowBackClick();
                transaction.detach(roomFragment);
                transaction.commit();
                break;
            case "PRIVATE_ROOM_FROM_ROOM_FRAGMENT":
                roomFragment.replyModel = null;

                currentFragment = "HOME_FRAGMENT";
                roomFragment.onToolbarOptionArrowBackClick();
                transaction.detach(roomFragment);
                transaction.commit();
                break;
            case "PRIVATE_ROOM_FROM_COMPOSE_FRAGMENT":
                roomFragment.replyModel = null;

                currentFragment = "COMPOSE_FRAGMENT";
                roomFragment.onToolbarOptionArrowBackClick();
                transaction.detach(roomFragment);
                transaction.commit();
                break;
            case "INFO_FRAGMENT":
                currentFragment = "ROOM_FRAGMENT";
                transaction.detach(infoFragment);
                transaction.commit();
                break;
            case "PRIVATE_INFO_FRAGMENT":
                currentFragment = "PRIVATE_ROOM_FRAGMENT";
                transaction.detach(infoFragment);
                transaction.commit();
                break;
            case "IMAGE_FRAGMENT":
                if (imageFragment.isFullscreen) {
                    imageFragment.fullscreen();
                } else {
                    currentFragment = "ROOM_FRAGMENT";
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
                    getWindow().getDecorView().setSystemUiVisibility(0);
                    transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
                    transaction.detach(imageFragment);
                    transaction.commit();
                }
                break;
            case "PRIVATE_IMAGE_FRAGMENT":
                if (imageFragment.isFullscreen) {
                    imageFragment.fullscreen();
                } else {
                    currentFragment = "PRIVATE_ROOM_FRAGMENT";
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
                    getWindow().getDecorView().setSystemUiVisibility(0);
                    transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
                    transaction.detach(imageFragment);
                    transaction.commit();
                }
                break;
            case "IMAGE_FROM_HOME_FRAGMENT":
                if (imageFragment.isFullscreen) {
                    imageFragment.fullscreen();
                } else {
                    currentFragment = "HOME_FRAGMENT";
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
                    getWindow().getDecorView().setSystemUiVisibility(0);
                    transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
                    transaction.detach(imageFragment);
                    transaction.commit();
                }
                break;
            case "IMAGE_FROM_ACCOUNT_FRAGMENT":
                if (imageFragment.isFullscreen) {
                    imageFragment.fullscreen();
                } else {
                    currentFragment = "ACCOUNT_FRAGMENT";
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
                    getWindow().getDecorView().setSystemUiVisibility(0);
                    transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
                    transaction.detach(imageFragment);
                    transaction.commit();
                }
                break;
            case "IMAGE_FROM_ROOM_FRAGMENT":
                if (imageFragment.isFullscreen) {
                    imageFragment.fullscreen();
                } else {
                    currentFragment = "ROOM_FRAGMENT";
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
                    getWindow().getDecorView().setSystemUiVisibility(0);
                    transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
                    transaction.detach(imageFragment);
                    transaction.commit();
                }
                break;
            case "IMAGE_FROM_ANNOUNCEMENT_FRAGMENT":
                if (imageFragment.isFullscreen) {
                    imageFragment.fullscreen();
                } else {
                    currentFragment = "ANNOUNCEMENT_FRAGMENT";
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
                    getWindow().getDecorView().setSystemUiVisibility(0);
                    transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
                    transaction.detach(imageFragment);
                    transaction.commit();
                }
                break;
            case "PRIVATE_IMAGE_FROM_ROOM_FRAGMENT":
                if (imageFragment.isFullscreen) {
                    imageFragment.fullscreen();
                } else {
                    currentFragment = "PRIVATE_ROOM_FRAGMENT";
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
                    getWindow().getDecorView().setSystemUiVisibility(0);
                    transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
                    transaction.detach(imageFragment);
                    transaction.commit();
                }
                break;
            case "IMAGE_FROM_ROOM_IMAGE_FRAGMENT":
                if (imageFragment.isFullscreen) {
                    imageFragment.fullscreen();
                } else {
                    currentFragment = "ROOM_IMAGE_FRAGMENT";
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
                    getWindow().getDecorView().setSystemUiVisibility(0);
                    transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
                    transaction.detach(imageFragment);
                    transaction.commit();
                }
                break;
            case "PRIVATE_IMAGE_FROM_ROOM_IMAGE_FRAGMENT":
                if (imageFragment.isFullscreen) {
                    imageFragment.fullscreen();
                } else {
                    currentFragment = "PRIVATE_ROOM_IMAGE_FRAGMENT";
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
                    getWindow().getDecorView().setSystemUiVisibility(0);
                    transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
                    transaction.detach(imageFragment);
                    transaction.commit();
                }
                break;
            case "ROOM_DETAIL_FRAGMENT":
                currentFragment = "ROOM_FRAGMENT";
                transaction.detach(roomDetailFragment);
                transaction.commit();
                break;
            case "PRIVATE_ROOM_DETAIL_FRAGMENT":
                currentFragment = "ROOM_FRAGMENT";
                transaction.detach(roomDetailFragment);
                transaction.commit();
                break;
            case "ROOM_IMAGE_FRAGMENT":
                currentFragment = "ROOM_DETAIL_FRAGMENT";
                transaction.detach(roomImageFragment);
                transaction.commit();
                break;
            case "PRIVATE_ROOM_IMAGE_FRAGMENT":
                currentFragment = "PRIVATE_ROOM_DETAIL_FRAGMENT";
                transaction.detach(roomImageFragment);
                transaction.commit();
                break;
            case "ROOM_FILE_FRAGMENT":
                currentFragment = "ROOM_DETAIL_FRAGMENT";
                transaction.detach(roomFileFragment);
                transaction.commit();
                break;
            case "PRIVATE_ROOM_FILE_FRAGMENT":
                currentFragment = "PRIVATE_ROOM_DETAIL_FRAGMENT";
                transaction.detach(roomFileFragment);
                transaction.commit();
                break;
            case "SCHEDULE_FRAGMENT":
                currentFragment = "HOME_FRAGMENT";
                transaction.setCustomAnimations(0, 0, 0, 0);
                transaction.detach(scheduleFragment);
                transaction.commit();
                break;
            case "ASSIGNMENT_FRAGMENT":
                currentFragment = "SCHEDULE_FRAGMENT";
                transaction.setCustomAnimations(0, 0, 0, 0);
                transaction.attach(scheduleFragment);
                transaction.detach(assignmentFragment);
                transaction.commit();
                break;
            case "ADD_ASSIGNMENT_FRAGMENT":
                currentFragment = "ASSIGNMENT_FRAGMENT";
                transaction.detach(assignmentAddFragment);
                transaction.commit();
                break;
            case "INFORMATION_FRAGMENT":
                if (spm.getUserType().equals("staff")) {
                    currentFragment = "HOME_FRAGMENT";
                    transaction.setCustomAnimations(0, 0, 0, 0);
                    transaction.detach(informationFragment);
                    transaction.attach(homeFragment);
                    transaction.commit();
                    break;
                } else {
                    currentFragment = "ASSIGNMENT_FRAGMENT";
                    transaction.setCustomAnimations(0, 0, 0, 0);
                    transaction.detach(informationFragment);
                    transaction.attach(assignmentFragment);
                    transaction.commit();
                    break;
                }

            case "KHS_FRAGMENT":
                currentFragment = "INFORMATION_FRAGMENT";
                transaction.detach(khsFragment);
                transaction.commit();
                break;
            case "STUDY_FRAGMENT":
                currentFragment = "INFORMATION_FRAGMENT";
                transaction.detach(studyFragment);
                transaction.commit();
                break;
            case "BILL_FRAGMENT":
                currentFragment = "INFORMATION_FRAGMENT";
                transaction.detach(billFragment);
                transaction.commit();
                break;
            case "ANNOUNCEMENT_FRAGMENT":
                currentFragment = "INFORMATION_FRAGMENT";
                transaction.detach(announcementFragment);
                transaction.attach(informationFragment);
                transaction.commit();
                break;
            case "ADD_ANNOUNCEMENT_FRAGMENT":
                currentFragment = "ANNOUNCEMENT_FRAGMENT";
                transaction.detach(announcementAddFragment);
                transaction.commit();
                break;
            case "SEARCH_FRAGMENT":
                currentFragment = "INFORMATION_FRAGMENT";
                transaction.setCustomAnimations(0, 0, 0, 0);
                transaction.detach(searchFragment);
                transaction.attach(informationFragment);
                transaction.commit();
                break;
            default:
                super.onBackPressed();

        }
    }

    public int getColor(String s) {
        String opacity = "#ff";
        String hexColor = String.format(
                opacity + "%06X", (0xeeeeee & s.hashCode()));

        return Color.parseColor(hexColor);
    }

    public String getFirstWord(String text) {
        int index = text.indexOf(' ');
        if (index > -1) {
            return text.substring(0, index);
        } else {
            return text;
        }
    }

    public String getFormattedTime(Long timestamp) {
        return DateUtils.formatDateTime(this, timestamp, DateUtils.FORMAT_SHOW_TIME);
    }

    public String getRelativeDateTimeString(Long timestamp) {
        String relativeTime = DateUtils
                .getRelativeDateTimeString(this, timestamp, DateUtils.DAY_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0).toString();

        return relativeTime;
    }

    public String getRoomAvatarUrl(String roomId) {
        ArrayList<String> roomIndexList = new ArrayList<>(roomIndexSet);
        int roomIndex = roomIndexList.indexOf(roomId);

        if (roomDetailMap.containsKey(roomId)) {
            RoomModel model = roomDetailMap.get(roomId);
            if (model.getAvatar_url() != null) {
                return model.getAvatar_url();
            } else {
                if (roomIndexSet.contains(roomId)) {
                    return "https://picsum.photos/200/300?image=" + roomIndex;
                } else {
                    return "https://source.unsplash.com/56x56/?book&uid=" + roomId;
                }
            }
        } else {
            if (roomIndexSet.contains(roomId)) {
                return "https://picsum.photos/200/300?image=" + roomIndex;
            } else {
                return "https://source.unsplash.com/56x56/?book&uid=" + roomId;
            }
        }
    }

    public void openFile(String path) {
        File file = new File(path);
        Uri fileUri = FileProvider.getUriForFile(this,getPackageName() + ".fileprovider", file);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(fileUri, getMimeType(fileUri));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }else {
            Toast.makeText(this, "Tidak terdapat aplikasi yang bisa digunakan untuk membuka.", Toast.LENGTH_SHORT).show();
        }
    }

    public String getMimeType(Uri uri) {
        String mimeType = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }

    public void createPrivateRoom(String privateRoomId) {
        refRoom = db.getReference("room").child(privateRoomId);
        refRoom.child("id").setValue(privateRoomId);
        refRoom.child("type").setValue("private");
        refRoom.child("member/" + spm.getUserId()).setValue(true);
    }

    public void updatePrivateRoomMemberStatus(String privateRoomId, String sender, String receiver) {
        refRoom = db.getReference("room").child(privateRoomId);
        refRoom.child("id").setValue(privateRoomId);
        refRoom.child("type").setValue("private");
        refRoom.child("member/" + sender).setValue(true);
        refRoom.child("member/" + receiver).setValue(true);
    }

    public String getCurrentDay() {
        String day = "minggu";
        Calendar c = Calendar.getInstance();
        int dayNum = c.get(Calendar.DAY_OF_WEEK);

        switch (dayNum) {
            case Calendar.MONDAY:
                day = "senin";
                break;
            case Calendar.TUESDAY:
                day = "selasa";
                break;
            case Calendar.WEDNESDAY:
                day = "rabu";
                break;
            case Calendar.THURSDAY:
                day = "kamis";
                break;
            case Calendar.FRIDAY:
                day = "jumat";
                break;
            case Calendar.SATURDAY:
                day = "sabtu";
                break;
            case Calendar.SUNDAY:
                day = "minggu";
                break;
        }

        return day;
    }

    public void updateAllBadge() {
        updateBadgeHome();
        updateBadgeSchedule();
        updateBadgeAssignment();
        updateBadgeInformation();
    }

    public void updateBadgeHome() {
        int step = 0;
        badgeHomeCount = 0;

        if (unreadCountMap.values().size() == 0) {
            if (homeFragment.tvBadgeHome != null) {
                homeFragment.tvBadgeHome.setVisibility(View.GONE);
                homeFragment.tvBadgeHome.setText("0");
            }
            if (scheduleFragment.tvBadgeHome != null) {
                scheduleFragment.tvBadgeHome.setVisibility(View.GONE);
                scheduleFragment.tvBadgeHome.setText("0");
            }
            if (assignmentFragment.tvBadgeHome != null) {
                assignmentFragment.tvBadgeHome.setVisibility(View.GONE);
                assignmentFragment.tvBadgeHome.setText("0");
            }
            if (informationFragment.tvBadgeHome != null) {
                informationFragment.tvBadgeHome.setVisibility(View.GONE);
                informationFragment.tvBadgeHome.setText("0");
            }
            if (searchFragment.tvBadgeHome != null) {
                searchFragment.tvBadgeHome.setVisibility(View.GONE);
                searchFragment.tvBadgeHome.setText("0");
            }
        } else {
            for (int count : unreadCountMap.values()) {
                badgeHomeCount += count;
                if (step == unreadCountMap.values().size() - 1) {
                    if (homeFragment.tvBadgeHome != null) {
                        if (badgeHomeCount < 100) {
                            homeFragment.tvBadgeHome.setVisibility(View.VISIBLE);
                            homeFragment.tvBadgeHome.setText(badgeHomeCount.toString());
                        } else {
                            homeFragment.tvBadgeHome.setVisibility(View.VISIBLE);
                            homeFragment.tvBadgeHome.setText("99");
                        }
                    }
                    if (scheduleFragment.tvBadgeHome != null) {
                        if (badgeHomeCount < 100) {
                            scheduleFragment.tvBadgeHome.setVisibility(View.VISIBLE);
                            scheduleFragment.tvBadgeHome.setText(badgeHomeCount.toString());
                        } else {
                            scheduleFragment.tvBadgeHome.setVisibility(View.VISIBLE);
                            scheduleFragment.tvBadgeHome.setText("99");
                        }
                    }
                    if (assignmentFragment.tvBadgeHome != null) {
                        if (badgeHomeCount < 100) {
                            assignmentFragment.tvBadgeHome.setVisibility(View.VISIBLE);
                            assignmentFragment.tvBadgeHome.setText(badgeHomeCount.toString());
                        } else {
                            assignmentFragment.tvBadgeHome.setVisibility(View.VISIBLE);
                            assignmentFragment.tvBadgeHome.setText("99");
                        }
                    }
                    if (informationFragment.tvBadgeHome != null) {
                        if (badgeHomeCount < 100) {
                            informationFragment.tvBadgeHome.setVisibility(View.VISIBLE);
                            informationFragment.tvBadgeHome.setText(badgeHomeCount.toString());
                        } else {
                            informationFragment.tvBadgeHome.setVisibility(View.VISIBLE);
                            informationFragment.tvBadgeHome.setText("99");
                        }
                    }
                    if (searchFragment.tvBadgeHome != null) {
                        if (badgeHomeCount < 100) {
                            searchFragment.tvBadgeHome.setVisibility(View.VISIBLE);
                            searchFragment.tvBadgeHome.setText(badgeHomeCount.toString());
                        } else {
                            searchFragment.tvBadgeHome.setVisibility(View.VISIBLE);
                            searchFragment.tvBadgeHome.setText("99");
                        }
                    }
                }
                step++;
            }
        }
    }

    public void updateBadgeSchedule() {
        if (homeFragment.tvBadgeSchedule != null) {
            if (badgeScheduleCount == 0) {
                homeFragment.tvBadgeSchedule.setVisibility(View.GONE);
                homeFragment.tvBadgeSchedule.setText("0");
            } else {
                if (badgeScheduleCount < 100) {
                    homeFragment.tvBadgeSchedule.setVisibility(View.VISIBLE);
                    homeFragment.tvBadgeSchedule.setText(badgeScheduleCount.toString());
                } else {
                    homeFragment.tvBadgeSchedule.setVisibility(View.VISIBLE);
                    homeFragment.tvBadgeSchedule.setText("99");
                }
            }
        }
        if (scheduleFragment.tvBadgeSchedule != null) {
            if (badgeScheduleCount == 0) {
                scheduleFragment.tvBadgeSchedule.setVisibility(View.GONE);
                scheduleFragment.tvBadgeSchedule.setText("0");
            } else {
                if (badgeScheduleCount < 100) {
                    scheduleFragment.tvBadgeSchedule.setVisibility(View.VISIBLE);
                    scheduleFragment.tvBadgeSchedule.setText(badgeScheduleCount.toString());
                } else {
                    scheduleFragment.tvBadgeSchedule.setVisibility(View.VISIBLE);
                    scheduleFragment.tvBadgeSchedule.setText("99");
                }
            }
        }
        if (assignmentFragment.tvBadgeSchedule != null) {
            if (badgeScheduleCount == 0) {
                assignmentFragment.tvBadgeSchedule.setVisibility(View.GONE);
                assignmentFragment.tvBadgeSchedule.setText("0");
            } else {
                if (badgeScheduleCount < 100) {
                    assignmentFragment.tvBadgeSchedule.setVisibility(View.VISIBLE);
                    assignmentFragment.tvBadgeSchedule.setText(badgeScheduleCount.toString());
                } else {
                    assignmentFragment.tvBadgeSchedule.setVisibility(View.VISIBLE);
                    assignmentFragment.tvBadgeSchedule.setText("99");
                }
            }
        }
        if (informationFragment.tvBadgeSchedule != null) {
            if (badgeScheduleCount == 0) {
                informationFragment.tvBadgeSchedule.setVisibility(View.GONE);
                informationFragment.tvBadgeSchedule.setText("0");
            } else {
                if (badgeScheduleCount < 100) {
                    informationFragment.tvBadgeSchedule.setVisibility(View.VISIBLE);
                    informationFragment.tvBadgeSchedule.setText(badgeScheduleCount.toString());
                } else {
                    informationFragment.tvBadgeSchedule.setVisibility(View.VISIBLE);
                    informationFragment.tvBadgeSchedule.setText("99");
                }
            }
        }
        if (searchFragment.tvBadgeSchedule != null) {
            if (badgeScheduleCount == 0) {
                searchFragment.tvBadgeSchedule.setVisibility(View.GONE);
                searchFragment.tvBadgeSchedule.setText("0");
            } else {
                if (badgeScheduleCount < 100) {
                    searchFragment.tvBadgeSchedule.setVisibility(View.VISIBLE);
                    searchFragment.tvBadgeSchedule.setText(badgeScheduleCount.toString());
                } else {
                    searchFragment.tvBadgeSchedule.setVisibility(View.VISIBLE);
                    searchFragment.tvBadgeSchedule.setText("99");
                }
            }
        }
    }

    public void updateBadgeAssignment() {
        if (homeFragment.tvBadgeAssignment != null) {
            if (badgeAssignmentCount == 0) {
                homeFragment.tvBadgeAssignment.setVisibility(View.GONE);
                homeFragment.tvBadgeAssignment.setText("0");
            } else {
                if (badgeAssignmentCount < 100) {
                    homeFragment.tvBadgeAssignment.setVisibility(View.VISIBLE);
                    homeFragment.tvBadgeAssignment.setText(badgeAssignmentCount.toString());
                } else {
                    homeFragment.tvBadgeAssignment.setVisibility(View.VISIBLE);
                    homeFragment.tvBadgeAssignment.setText("99");
                }
            }
        }
        if (scheduleFragment.tvBadgeAssignment != null) {
            if (badgeAssignmentCount == 0) {
                scheduleFragment.tvBadgeAssignment.setVisibility(View.GONE);
                scheduleFragment.tvBadgeAssignment.setText("0");
            } else {
                if (badgeAssignmentCount < 100) {
                    scheduleFragment.tvBadgeAssignment.setVisibility(View.VISIBLE);
                    scheduleFragment.tvBadgeAssignment.setText(badgeAssignmentCount.toString());
                } else {
                    scheduleFragment.tvBadgeAssignment.setVisibility(View.VISIBLE);
                    scheduleFragment.tvBadgeAssignment.setText("99");
                }
            }
        }
        if (assignmentFragment.tvBadgeAssignment != null) {
            if (badgeAssignmentCount == 0) {
                assignmentFragment.tvBadgeAssignment.setVisibility(View.GONE);
                assignmentFragment.tvBadgeAssignment.setText("0");
            } else {
                if (badgeAssignmentCount < 100) {
                    assignmentFragment.tvBadgeAssignment.setVisibility(View.VISIBLE);
                    assignmentFragment.tvBadgeAssignment.setText(badgeAssignmentCount.toString());
                } else {
                    assignmentFragment.tvBadgeAssignment.setVisibility(View.VISIBLE);
                    assignmentFragment.tvBadgeAssignment.setText("99");
                }
            }
        }
        if (informationFragment.tvBadgeAssignment != null) {
            if (badgeAssignmentCount == 0) {
                informationFragment.tvBadgeAssignment.setVisibility(View.GONE);
                informationFragment.tvBadgeAssignment.setText("0");
            } else {
                if (badgeAssignmentCount < 100) {
                    informationFragment.tvBadgeAssignment.setVisibility(View.VISIBLE);
                    informationFragment.tvBadgeAssignment.setText(badgeAssignmentCount.toString());
                } else {
                    informationFragment.tvBadgeAssignment.setVisibility(View.VISIBLE);
                    informationFragment.tvBadgeAssignment.setText("99");
                }
            }
        }
        if (searchFragment.tvBadgeAssignment != null) {
            if (badgeAssignmentCount == 0) {
                searchFragment.tvBadgeAssignment.setVisibility(View.GONE);
                searchFragment.tvBadgeAssignment.setText("0");
            } else {
                if (badgeAssignmentCount < 100) {
                    searchFragment.tvBadgeAssignment.setVisibility(View.VISIBLE);
                    searchFragment.tvBadgeAssignment.setText(badgeAssignmentCount.toString());
                } else {
                    searchFragment.tvBadgeAssignment.setVisibility(View.VISIBLE);
                    searchFragment.tvBadgeAssignment.setText("99");
                }
            }
        }
    }

    public void updateBadgeInformation() {
        if (homeFragment.tvBadgeInformation != null) {
            if (badgeInformationCount == 0) {
                homeFragment.tvBadgeInformation.setVisibility(View.GONE);
                homeFragment.tvBadgeInformation.setText("0");
            } else {
                if (badgeInformationCount < 100) {
                    homeFragment.tvBadgeInformation.setVisibility(View.VISIBLE);
                    homeFragment.tvBadgeInformation.setText(badgeInformationCount.toString());
                } else {
                    homeFragment.tvBadgeInformation.setVisibility(View.VISIBLE);
                    homeFragment.tvBadgeInformation.setText("99");
                }
            }
        }
        if (scheduleFragment.tvBadgeInformation != null) {
            if (badgeInformationCount == 0) {
                scheduleFragment.tvBadgeInformation.setVisibility(View.GONE);
                scheduleFragment.tvBadgeInformation.setText("0");
            } else {
                if (badgeInformationCount < 100) {
                    scheduleFragment.tvBadgeInformation.setVisibility(View.VISIBLE);
                    scheduleFragment.tvBadgeInformation.setText(badgeInformationCount.toString());
                } else {
                    scheduleFragment.tvBadgeInformation.setVisibility(View.VISIBLE);
                    scheduleFragment.tvBadgeInformation.setText("99");
                }
            }
        }
        if (assignmentFragment.tvBadgeInformation != null) {
            if (badgeInformationCount == 0) {
                assignmentFragment.tvBadgeInformation.setVisibility(View.GONE);
                assignmentFragment.tvBadgeInformation.setText("0");
            } else {
                if (badgeInformationCount < 100) {
                    assignmentFragment.tvBadgeInformation.setVisibility(View.VISIBLE);
                    assignmentFragment.tvBadgeInformation.setText(badgeInformationCount.toString());
                } else {
                    assignmentFragment.tvBadgeInformation.setVisibility(View.VISIBLE);
                    assignmentFragment.tvBadgeInformation.setText("99");
                }
            }
        }
        if (informationFragment.tvBadgeInformation != null) {
            if (badgeInformationCount == 0) {
                informationFragment.tvBadgeInformation.setVisibility(View.GONE);
                informationFragment.tvBadgeInformation.setText("0");
            } else {
                if (badgeInformationCount < 100) {
                    informationFragment.tvBadgeInformation.setVisibility(View.VISIBLE);
                    informationFragment.tvBadgeInformation.setText(badgeInformationCount.toString());
                } else {
                    informationFragment.tvBadgeInformation.setVisibility(View.VISIBLE);
                    informationFragment.tvBadgeInformation.setText("99");
                }
            }
        }
        if (informationFragment.tvBadgeInformation2 != null) {
            if (badgeInformationCount == 0) {
                informationFragment.tvBadgeInformation2.setVisibility(View.GONE);
                informationFragment.tvBadgeInformation2.setText("0");
            } else {
                if (badgeInformationCount < 100) {
                    informationFragment.tvBadgeInformation2.setVisibility(View.VISIBLE);
                    informationFragment.tvBadgeInformation2.setText(badgeInformationCount.toString());
                } else {
                    informationFragment.tvBadgeInformation2.setVisibility(View.VISIBLE);
                    informationFragment.tvBadgeInformation2.setText("99");
                }
            }
        }
        if (searchFragment.tvBadgeInformation != null) {
            if (badgeInformationCount == 0) {
                searchFragment.tvBadgeInformation.setVisibility(View.GONE);
                searchFragment.tvBadgeInformation.setText("0");
            } else {
                if (badgeInformationCount < 100) {
                    searchFragment.tvBadgeInformation.setVisibility(View.VISIBLE);
                    searchFragment.tvBadgeInformation.setText(badgeInformationCount.toString());
                } else {
                    searchFragment.tvBadgeInformation.setVisibility(View.VISIBLE);
                    searchFragment.tvBadgeInformation.setText("99");
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserPermission();
        if (getIntent() != null) {
            intent = getIntent();
            handleNotificationPendingIntent(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //Remove rooms listener
        if (roomListener != null) {
            roomRef.removeEventListener(roomListener);
        }

        //Remove chats listener
        for(Map.Entry<Query, ChildEventListener> entry : roomRefListenerMap.entrySet()) {
            Query key = entry.getKey();
            ChildEventListener value = entry.getValue();
            key.removeEventListener(value);
        }

        //Remove assignments listener
        for(Map.Entry<Query, ChildEventListener> entry : assignmentRefListenerMap.entrySet()) {
            Query key = entry.getKey();
            ChildEventListener value = entry.getValue();
            key.removeEventListener(value);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }
}