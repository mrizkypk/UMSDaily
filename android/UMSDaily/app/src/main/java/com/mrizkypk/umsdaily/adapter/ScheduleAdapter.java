package com.mrizkypk.umsdaily.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mrizkypk.umsdaily.R;
import com.mrizkypk.umsdaily.model.ScheduleModel;

import java.util.List;

/**
 * Created by mrizkypk on 22/03/18.
 */


public class ScheduleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<Object> dataList;

    public class ViewHolderHeader extends RecyclerView.ViewHolder {
        public View view;
        public TextView tvHeader;

        public ViewHolderHeader(View v) {
            super(v);
            view = v;
            tvHeader = view.findViewById(R.id.item_schedule_text_header);
        }
    }

    public class ViewHolderBody extends RecyclerView.ViewHolder {
        public View view;
        public TextView tvTime;
        public TextView tvRoom;
        public TextView tvSubject;
        public TextView tvLecturer;

        public ViewHolderBody(View v) {
            super(v);
            view = v;
            tvTime = view.findViewById(R.id.item_schedule_body_text_time);
            tvRoom = view.findViewById(R.id.item_schedule_body_text_room);
            tvSubject = view.findViewById(R.id.item_schedule_body_text_subject);
            tvLecturer = view.findViewById(R.id.item_schedule_body_text_lecturer);
        }
    }

    public ScheduleAdapter() {
    }

    public ScheduleAdapter(Context ctx, List<Object> list) {
        context = ctx;
        dataList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case 0:
                View v1 = inflater.inflate(R.layout.item_schedule_header, parent, false);
                viewHolder = new ViewHolderHeader(v1);
                break;
            case 1:
                View v2 = inflater.inflate(R.layout.item_schedule_body, parent, false);
                viewHolder = new ViewHolderBody(v2);
                break;
            default:
                View v = inflater.inflate(R.layout.item_schedule_header, parent, false);
                viewHolder = new ViewHolderHeader(v);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Object object = dataList.get(position);
        switch (holder.getItemViewType()) {
            case 0:
                ViewHolderHeader vh1 = (ViewHolderHeader) holder;
                String header = (String) object;
                vh1.tvHeader.setText(header.toUpperCase());
                break;
            case 1:
                ViewHolderBody vh2 = (ViewHolderBody) holder;
                ScheduleModel schedule = (ScheduleModel) object;
                vh2.tvSubject.setText(schedule.getMatakuliah());
                vh2.tvRoom.setText(schedule.getRuang());
                vh2.tvTime.setText(schedule.getJam());
                vh2.tvLecturer.setText(schedule.getPengampu());
                break;
            default:
                ViewHolderHeader vh3 = (ViewHolderHeader) holder;
                String header2 = (String) object;
                vh3.tvHeader.setText(header2);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (dataList.get(position) instanceof String) {
            return 0;
        } else if (dataList.get(position) instanceof ScheduleModel) {
            return 1;
        }
        return -1;
    }
}

