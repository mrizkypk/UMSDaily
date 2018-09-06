package com.mrizkypk.umsdaily.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.internal.LinkedTreeMap;
import com.mrizkypk.umsdaily.R;
import com.mrizkypk.umsdaily.adapter.ComposeAdapter;
import com.mrizkypk.umsdaily.activity.HomeActivity;
import com.mrizkypk.umsdaily.helper.ApiHelper;
import com.mrizkypk.umsdaily.model.UserModel;
import com.rx2androidnetworking.Rx2AndroidNetworking;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class ComposeFragment extends Fragment {
    public HomeActivity activity;
    public RecyclerView recyclerview;
    public RecyclerView.LayoutManager layoutmanager;

    @BindView(R.id.fragment_compose_layout)
    ConstraintLayout clLayout;

    @BindView(R.id.fragment_compose_container_search)
    RelativeLayout rlSearch;

    @BindView(R.id.fragment_compose_container_toolbar)
    RelativeLayout rlToolbar;

    @BindView(R.id.fragment_compose_text_toolbar_title)
    TextView tvTitle;

    @BindView(R.id.fragment_compose_input_search)
    EditText etSearch;

    @BindView(R.id.fragment_compose_progressbar)
    ProgressBar progressbar;

    @BindView(R.id.fragment_compose_text_empty)
    public TextView tvEmpty;

    public Query queryRef;
    public ChildEventListener queryListener;
    public ValueEventListener queryDoneListener;
    public LinkedList<UserModel> filteredList;
    public String query;
    public HashMap<String, List<UserModel>> searchByNameResponse;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = ((HomeActivity) getActivity());
        filteredList = new LinkedList<>();
        searchByNameResponse = new HashMap<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compose, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        layoutmanager = new LinearLayoutManager(getContext());

        filteredList.clear();
        filteredList.addAll(activity.composeModelList);
        activity.composeAdapter = new ComposeAdapter(getContext(), filteredList);

        recyclerview = view.findViewById(R.id.fragment_compose_recyclerview);
        recyclerview.setHasFixedSize(true);

        recyclerview.setLayoutManager(layoutmanager);

        recyclerview.setAdapter(activity.composeAdapter);

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    tvEmpty.setVisibility(View.GONE);
                    hideSoftKeyboard();
                    search();
                    return true;
                }
                return false;

            }
        });
    }

    @OnClick({R.id.fragment_compose_container_toolbar_search, R.id.fragment_compose_button_toolbar_search})
    public void onClickToolbarSearch() {
        rlToolbar.setVisibility(View.INVISIBLE);
        rlSearch.setVisibility(View.VISIBLE);
        etSearch.requestFocus();
        showSoftKeyboard();
    }

    @OnClick({R.id.fragment_compose_container_search_cancel, R.id.fragment_compose_button_search_cancel})
    public void onClickSearchCancel() {
        rlSearch.setVisibility(View.INVISIBLE);
        rlToolbar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        hideSoftKeyboard();
        etSearch.setText("");

        filteredList.clear();
        filteredList.addAll(activity.composeModelList);
        activity.composeAdapter.notifyDataSetChanged();
    }

    @OnClick({R.id.fragment_compose_container_do_search, R.id.fragment_compose_button_do_search})
    public void onClickDoSearch() {
        tvEmpty.setVisibility(View.GONE);
        hideSoftKeyboard();
        search();
    }

    @OnClick({R.id.fragment_compose_container_toolbar_arrow_back, R.id.fragment_compose_button_toolbar_arrow_back})
    public void onClickToolbarArrowBack() {
        activity.onBackPressed();
    }

    public void showProgressBar() {
        progressbar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressbar.setVisibility(View.GONE);
    }

    public void showSoftKeyboard() {
        ((InputMethodManager) (getContext()).getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager)  getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }

    public Observable<UserModel> getUserById() {
        return Rx2AndroidNetworking.get(ApiHelper.getFirebaseHost() + "/user/" + query + ".json")
                .getResponseOnlyFromNetwork()
                .build()
                .getObjectObservable(UserModel.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<HashMap> getUserByName() {
        return Rx2AndroidNetworking.get(ApiHelper.getFirebaseHost() + "/user.json")
                .addQueryParameter("orderBy", "\"name\"")
                .addQueryParameter("startAt", "\"" + query + "\"")
                .addQueryParameter("endAt", "\"" + query + "\uf8ff\"")
                .getResponseOnlyFromNetwork()
                .build()
                .getObjectObservable(HashMap.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void search() {
        query = etSearch.getText().toString().trim();
        if (query.isEmpty()) {
            hideProgressBar();
        } else {
            query = capitalize(query);
            Observable.zip(getUserByName(), getUserById().onErrorReturn(new Function<Throwable, UserModel>() {
                        @Override
                        public UserModel apply(Throwable throwable) throws Exception {
                            return new UserModel();
                        }
                    }),
                    new BiFunction<HashMap, UserModel, HashMap>() {
                        @Override
                        public HashMap apply(HashMap names, UserModel userModel) throws Exception {
                            if (userModel.getId() != null) {
                                HashMap<String, UserModel> userMap = new HashMap<>();
                                userMap.put(userModel.getId(), userModel);

                                return userMap;
                            } else {
                                return names;
                            }
                        }
                    }
            ).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<HashMap>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            showProgressBar();
                            filteredList.clear();
                            activity.composeAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onNext(HashMap hashMap) {
                            for (Object o : hashMap.values()) {
                                if (o instanceof UserModel) {
                                    UserModel user = (UserModel) o;
                                    filteredList.add(user);
                                    activity.userModelMap.put(user.getId(), user);
                                }
                                if (o instanceof LinkedTreeMap) {
                                    LinkedTreeMap userMap = (LinkedTreeMap) o;
                                    UserModel user = new UserModel();
                                    user.setId(userMap.get("id").toString());
                                    user.setAvatar_url(userMap.get("avatar_url").toString());
                                    user.setToken(userMap.get("token").toString());
                                    user.setType(userMap.get("type").toString());
                                    user.setName(userMap.get("name").toString());

                                    filteredList.add(user);
                                    activity.userModelMap.put(user.getId(), user);
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            hideProgressBar();
                        }

                        @Override
                        public void onComplete() {
                            hideProgressBar();
                            activity.composeAdapter.notifyDataSetChanged();
                        }
                    });
        }

    }

    public String capitalize(final String input) {
        StringBuilder output = new StringBuilder(input.length());
        boolean lastCharacterWasWhitespace = true;

        for(int i = 0; i < input.length(); i++) {
            char currentCharacter = input.charAt(i);

            if(lastCharacterWasWhitespace && Character.isLowerCase(currentCharacter)) {
                currentCharacter = Character.toTitleCase(currentCharacter);
            }

            output.append(currentCharacter);

            lastCharacterWasWhitespace = Character.isWhitespace(currentCharacter);
        }

        return output.toString();
    }
}