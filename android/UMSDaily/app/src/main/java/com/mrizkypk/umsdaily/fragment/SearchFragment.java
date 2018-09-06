package com.mrizkypk.umsdaily.fragment;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.github.javafaker.Faker;
import com.google.firebase.database.DatabaseReference;
import com.mrizkypk.umsdaily.R;
import com.mrizkypk.umsdaily.activity.HomeActivity;
import com.mrizkypk.umsdaily.adapter.SearchAdapter;
import com.mrizkypk.umsdaily.helper.ApiHelper;
import com.mrizkypk.umsdaily.model.ResponseErrorModel;
import com.mrizkypk.umsdaily.model.SearchModel;
import com.mrizkypk.umsdaily.model.UserModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchFragment extends Fragment {
    private View view;
    private HomeActivity activity;
    private String currentHeader;
    private RecyclerView recyclerview;
    private RecyclerView.LayoutManager layoutManager;

    @BindView(R.id.fragment_search_layout)
    ConstraintLayout layout;

    @BindView(R.id.fragment_search_input_query)
    EditText etQuery;

    @BindView(R.id.fragment_search_container_suggestion)
    LinearLayout llSuggestion;

    @BindView(R.id.fragment_search_text_suggestion_department)
    TextView tvDepartment;

    @BindView(R.id.fragment_search_text_suggestion_lecturer)
    TextView tvLecturer;

    @BindView(R.id.fragment_search_text_suggestion_room)
    TextView tvRoom;

    @BindView(R.id.fragment_search_text_suggestion_subject)
    TextView tvSubject;

    @BindView(R.id.fragment_search_text_empty)
    TextView tvEmpty;

    @BindView(R.id.fragment_search_progressbar)
    ProgressBar progressbar;

    @BindView(R.id.fragment_search_container_menu_schedule)
    LinearLayout llSchedule;

    @BindView(R.id.fragment_search_container_menu_assignment)
    LinearLayout llAssignment;

    @BindView(R.id.fragment_search_container_menu_information)
    LinearLayout llInformation;

    @BindView(R.id.fragment_search_badge_home)
    public TextView tvBadgeHome;

    @BindView(R.id.fragment_search_badge_schedule)
    public TextView tvBadgeSchedule;

    @BindView(R.id.fragment_search_badge_assignment)
    public TextView tvBadgeAssignment;

    @BindView(R.id.fragment_search_badge_information)
    public TextView tvBadgeInformation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = ((HomeActivity) getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (activity.searchModelList.size() == 0) {
            etQuery.requestFocus();
            showSoftKeyboard();
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
        }

        currentHeader = "minggu";
        recyclerview = view.findViewById(R.id.fragment_search_recylerview);
        recyclerview.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());

        recyclerview.setLayoutManager(layoutManager);

        activity.searchAdapter = new SearchAdapter(getContext(), activity.searchModelList);
        recyclerview.setAdapter(activity.searchAdapter);

        etQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                hideProgressBar();
                tvDepartment.setText(charSequence.toString());
                tvLecturer.setText(charSequence.toString());
                tvRoom.setText(charSequence.toString());
                tvSubject.setText(charSequence.toString());
                if (etQuery.getText().toString().trim().equals("")) {
                    hideSearchSuggestion();
                } else {
                    showSearchSuggestion();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etQuery.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    hideSearchSuggestion();
                    search("ruang", etQuery.getText().toString());
                    return true;
                }
                return false;

            }
        });

        if (activity.spm.getUserType().equals("staff")) {
            llSchedule.setVisibility(View.GONE);
            llAssignment.setVisibility(View.GONE);

            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) llInformation.getLayoutParams();
            params.startToEnd = R.id.guideline2;
            params.endToStart = R.id.guideline3;

            llInformation.setLayoutParams(params);
        }

        //Update badge
        activity.updateAllBadge();
    }

    public void showProgressBar() {
        progressbar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressbar.setVisibility(View.GONE);
    }

    private void showSearchSuggestion() {
        llSuggestion.setVisibility(View.VISIBLE);
    }

    private void hideSearchSuggestion() {
        llSuggestion.setVisibility(View.GONE);
    }

    private void search(String category, String query) {
        hideSoftKeyboard();
        activity.searchModelList.clear();
        activity.searchAdapter.notifyDataSetChanged();
        showProgressBar();
        tvEmpty.setVisibility(View.GONE);
        AndroidNetworking.get(ApiHelper.getHost() + "/pencarian_jadwal.php")
                .addQueryParameter("kategori", category)
                .addQueryParameter("query", query)
                .getResponseOnlyFromNetwork()
                .build()
                .getAsObjectList(SearchModel.class, new ParsedRequestListener<List<SearchModel>>() {
                    @Override
                    public void onResponse(List<SearchModel> models) {
                        hideProgressBar();
                        if (models.size() == 0) {
                            tvEmpty.setVisibility(View.VISIBLE);
                        } else {
                            tvEmpty.setVisibility(View.GONE);
                        }
                        for (SearchModel model : models) {
                            if (currentHeader.equals("minggu")) {
                                currentHeader = model.getHari();
                                activity.searchModelList.add(currentHeader);
                                activity.searchAdapter.notifyItemInserted(activity.searchModelList.size());
                                activity.searchModelList.add(model);
                                activity.searchAdapter.notifyItemInserted(activity.searchModelList.size());
                            } else {
                                if (currentHeader.equals(model.getHari())) {
                                    activity.searchModelList.add(model);
                                    activity.searchAdapter.notifyItemInserted(activity.searchModelList.size());
                                } else {
                                    currentHeader = model.getHari();
                                    activity.searchModelList.add(currentHeader);
                                    activity.searchAdapter.notifyItemInserted(activity.searchModelList.size());
                                    activity.searchModelList.add(model);
                                    activity.searchAdapter.notifyItemInserted(activity.searchModelList.size());
                                }
                            }
                        }
                    }
                    @Override
                    public void onError(ANError e) {
                        hideProgressBar();
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
                        Snackbar.make(layout, message,
                                Snackbar.LENGTH_LONG)
                                .show();
                    }
                });
    }

    @OnClick({R.id.fragment_search_container_menu_schedule, R.id.fragment_search_button_menu_schedule, R.id.fragment_search_text_menu_schedule})
    public void onMenuScheduleClick() {
        activity.openScheduleFragment();
    }

    @OnClick({R.id.fragment_search_container_menu_assignment, R.id.fragment_search_button_menu_assignment, R.id.fragment_search_text_menu_assignment})
    public void onMenuAssignmentClick() {
        activity.openAssignmentFragment();
    }

    @OnClick({R.id.fragment_search_container_menu_information, R.id.fragment_search_button_menu_information, R.id.fragment_search_text_menu_information})
    public void onMenuInformationClick() {
        activity.openInformationFragment(0);
    }

    @OnClick({R.id.fragment_search_container_menu_home, R.id.fragment_search_button_menu_home, R.id.fragment_search_text_menu_home})
    public void onMenuHomeClick() {
        activity.openChatHomeFragment(0);
    }

    @OnClick(R.id.fragment_search_container_suggestion_department)
    public void onSearchSuggestionDepartmentClick() {
        hideSearchSuggestion();
        search("jurusan", tvDepartment.getText().toString());
    }

    @OnClick(R.id.fragment_search_container_suggestion_lecturer)
    public void onSearchSuggestionLecturerClick() {
        hideSearchSuggestion();
        search("dosen", tvLecturer.getText().toString());
    }

    @OnClick(R.id.fragment_search_container_suggestion_room)
    public void onSearchSuggestionRoomClick() {
        hideSearchSuggestion();
        search("ruang", tvRoom.getText().toString());
    }

    @OnClick(R.id.fragment_search_container_suggestion_subject)
    public void onSearchSuggestionSubjectClick() {
        hideSearchSuggestion();
        search("matakuliah", tvSubject.getText().toString());
    }

    public void showSoftKeyboard() {
        ((InputMethodManager) (getContext()).getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager)  getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }

    /*
    @OnClick(R.id.fragment_search_text_empty)
    public void onClickEmpty() {
        generateRandomUser();
    }

    public void generateRandomUser() {
        int step = 1;
        boolean doRandom = true;
        while (doRandom) {
            step ++;
            Faker faker = new Faker();

            String name = faker.name().fullName();
            DatabaseReference ref = activity.db.getReference().child("user").push();

            UserModel user = new UserModel();
            user.setId(ref.getKey());
            user.setName(name);
            user.setAvatar_url("https://api.adorable.io/avatars/56/" + ref.getKey());
            user.setToken(ref.getKey());
            user.setType("student");

            ref.setValue(user);

            if (step == 20000) {
                doRandom = false;
            }
        }
    }
    */
}
