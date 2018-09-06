package com.mrizkypk.umsdaily.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.mrizkypk.umsdaily.R;
import com.mrizkypk.umsdaily.activity.HomeActivity;
import com.mrizkypk.umsdaily.helper.DateHelper;
import com.mrizkypk.umsdaily.helper.NotificationHelper;
import com.mrizkypk.umsdaily.model.AssignmentModel;
import com.mrizkypk.umsdaily.model.RoomModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AssignmentAddFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    public String subject;
    public HomeActivity activity;
    public List<String> roomList;
    public Map<String, String> roomMap;
    public Calendar calendar;
    public DatePickerDialog.OnDateSetListener date;

    @BindView(R.id.fragment_assignment_add_form_text_title)
    EditText etTitle;

    @BindView(R.id.fragment_assignment_add_form_text_description)
    EditText etDescription;

    @BindView(R.id.fragment_assignment_add_form_text_end_date)
    TextView tvEndDate;

    @BindView(R.id.fragment_assignment_add_spinner_subject)
    Spinner spinner;

    @BindView(R.id.fragment_assignment_add_form_button_add)
    Button btnAdd;

    @BindView(R.id.fragment_assignment_add_form_button_date_picker)
    Button btnDatePicker;

    @BindView(R.id.fragment_assignment_add_progressbar)
    ProgressBar progressbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = ((HomeActivity) getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_assignment_add, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        roomMap = new HashMap<>();
        roomList = new ArrayList<>();

        for (String id : activity.roomIndexSet) {
            RoomModel model = activity.roomDetailMap.get(id);
            if (model.getTitle() != null) {
                roomList.add(model.getTitle() + " (" + model.getId() + ")");
                roomMap.put(model.getTitle() + " (" + model.getId() + ")", model.getId());
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, roomList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        calendar = Calendar.getInstance();

        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                hideSoftKeyboard();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                tvEndDate.setText("Tanggal Maksimal: " + dayOfMonth + " / " + monthOfYear + " / " + year);
                btnDatePicker.setText("Ganti Tanggal Maksimal");
            }
        };
    }

    public void sendNotification(String id, String title, String content, String roomId) {
        String justTitle = title.split(" \\(")[0];
        NotificationHelper.send(id,"ASSIGNMENT", "", roomId, activity.spm.getUserId(), activity.spm.getUserName(), "", "","", "", justTitle, content, "");
    }

    @OnClick(R.id.fragment_assignment_add_form_button_add)
    public void onButtonAddClick() {
        final String title = etTitle.getText().toString();
        String description = etDescription.getText().toString();
        String maxDate = tvEndDate.getText().toString();

        if (title.isEmpty() || description.isEmpty() || maxDate.isEmpty()) {
            Toast.makeText(activity, "Semua Kolom Harus Terisi", Toast.LENGTH_LONG).show();
        } else {

            btnAdd.setVisibility(View.GONE);
            showProgressBar();

            final String id = roomMap.get(subject);
            DatabaseReference ref = activity.db.getReference("assignment").child(id);
            final AssignmentModel model = new AssignmentModel();

            model.setId(ref.push().getKey());
            model.setRoom_id(id);
            model.setRoom_title(subject);
            model.setSender_id(activity.spm.getUserId());
            model.setSender_name(activity.spm.getUserName());
            model.setMax_date(calendar.getTimeInMillis());
            model.setTitle(title);
            model.setDescription(description);
            model.setTimestamp(DateHelper.getTimestamp());

            ref.child(model.getId()).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    sendNotification(model.getId(), subject, title, id);
                    activity.onBackPressed();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    btnAdd.setVisibility(View.VISIBLE);
                    hideProgressBar();

                    Log.d(activity.TAG, e.getMessage());
                }
            });
        }
    }

    @OnClick(R.id.fragment_assignment_add_form_button_date_picker)
    public void showDatePickerDialog(View v) {
        new DatePickerDialog(activity, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    @OnClick({R.id.fragment_assignment_add_container_toolbar_arrow_back, R.id.fragment_assignment_add_button_toolbar_arrow_back})
    public void onToolbarArrowBackClick() {
        activity.onBackPressed();

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        subject = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        subject = null;
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
