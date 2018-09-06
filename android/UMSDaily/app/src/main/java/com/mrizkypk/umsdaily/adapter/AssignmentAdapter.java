package com.mrizkypk.umsdaily.adapter;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.mrizkypk.umsdaily.R;
import com.mrizkypk.umsdaily.activity.HomeActivity;
import com.mrizkypk.umsdaily.model.AssignmentModel;

import java.text.SimpleDateFormat;
import java.util.List;

public class AssignmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private HomeActivity activity;
    private Context context;
    private List<Object> dataList;

    public class ViewHolderHeader extends RecyclerView.ViewHolder {
        public View view;
        public TextView tvHeader;

        public ViewHolderHeader(View v) {
            super(v);
            view = v;
            tvHeader = view.findViewById(R.id.item_assignment_header_text_header);
        }
    }

    public class ViewHolderBody extends RecyclerView.ViewHolder {
        public View view;
        public TextView tvTitle;
        public TextView tvMaxDate;
        public TextView tvDescription;
        public ImageView ivDelete;

        public ViewHolderBody(View v) {
            super(v);
            view = v;
            tvTitle = view.findViewById(R.id.item_assignment_body_text_title);
            tvMaxDate = view.findViewById(R.id.item_assignment_body_text_max_date);
            tvDescription = view.findViewById(R.id.item_assignment_body_text_description);
            ivDelete = view.findViewById(R.id.item_assignment_body_image_delete);
        }
    }

    public AssignmentAdapter() {
    }

    public AssignmentAdapter(Context ctx, List<Object> list) {
        context = ctx;
        dataList = list;
        activity = (HomeActivity) ctx;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case 0:
                View v1 = inflater.inflate(R.layout.item_assignment_header, parent, false);
                viewHolder = new ViewHolderHeader(v1);
                break;
            case 1:
                View v2 = inflater.inflate(R.layout.item_assignment_body, parent, false);
                viewHolder = new ViewHolderBody(v2);
                break;
            default:
                View v = inflater.inflate(R.layout.item_assignment_header, parent, false);
                viewHolder = new ViewHolderHeader(v);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Object object = dataList.get(position);
        NotificationManager notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);

        switch (holder.getItemViewType()) {
            case 0:
                ViewHolderHeader vh1 = (ViewHolderHeader) holder;
                String header = (String) object;
                vh1.tvHeader.setText(header.toUpperCase());
                break;
            case 1:
                ViewHolderBody vh2 = (ViewHolderBody) holder;
                final AssignmentModel model = (AssignmentModel) dataList.get(position);

                //Clear notification after read
                notificationManager.cancel(model.getId().hashCode());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (notificationManager.getActiveNotifications().length == 1) {
                        notificationManager.cancel(0);
                    }
                }

                String dateString = new SimpleDateFormat("dd - MM - yyyy").format(model.getMax_date());

                vh2.tvTitle.setText(model.getTitle());
                vh2.tvMaxDate.setText("Tanggal Maksimal: " + dateString);
                vh2.tvDescription.setText(model.getDescription());

                if (activity.spm.getUserType().equals("lecturer")) {
                    vh2.ivDelete.setVisibility(View.VISIBLE);
                    vh2.ivDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setMessage("Anda yakin untuk menghapus tugas ini?")
                                    .setTitle("Hapus Tugas");
                            builder.setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    DatabaseReference reference = activity.db.getReference().child("assignment").child(model.getRoom_id()).child(model.getId());
                                    reference.removeValue();
                                }
                            });
                            builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    });
                } else {
                    vh2.ivDelete.setVisibility(View.GONE);
                }
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
        } else if (dataList.get(position) instanceof AssignmentModel) {
            return 1;
        }
        return -1;
    }
}
