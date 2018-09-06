package com.mrizkypk.umsdaily.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mrizkypk.umsdaily.R;
import com.mrizkypk.umsdaily.model.BillModel;

import java.util.LinkedList;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.ViewHolder> {
    public Context context;
    public LinkedList<BillModel> dataList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView tvType;
        public TextView tvDate;
        public TextView tvStatus;
        public TextView tvTotal;

        public ViewHolder(View v) {
            super(v);
            view = v;
            tvType = view.findViewById(R.id.item_bill_text_type);
            tvDate = view.findViewById(R.id.item_bill_text_date);
            tvStatus = view.findViewById(R.id.item_bill_text_status);
            tvTotal = view.findViewById(R.id.item_bill_text_total);
        }
    }


    public BillAdapter(Context ctx, LinkedList<BillModel> list) {
        dataList = list;
        context = ctx;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bill, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BillModel khs = dataList.get(position);
        holder.tvType.setText(khs.getJenis_pembayaran());
        holder.tvDate.setText("Tanggal: " + khs.getTanggal_bayar());
        holder.tvStatus.setText("Status: " + khs.getStatus_tagihan());
        holder.tvTotal.setText("Total: Rp. " + khs.getJumlah_bayar());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
