package com.elegion.radio.ui.recently;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elegion.radio.OnItemClickListener;
import com.elegion.radio.R;
import com.elegion.radio.entity.RecentStation;

import java.util.ArrayList;
import java.util.List;

public class RecentlyAdapter extends RecyclerView.Adapter<RecentlyHolder> {

    private List<RecentStation> mData = new ArrayList<>();
    private OnItemClickListener mListener;

    public void addData(List<RecentStation> data) {
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void setListener(OnItemClickListener listener) {
        mListener = listener;
    }


    @NonNull
    @Override
    public RecentlyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View recentStation = layoutInflater.inflate(R.layout.holder_station, viewGroup, false);
        return new RecentlyHolder(recentStation);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentlyHolder holder, int position) {
        RecentStation recentStation = mData.get(position);
        holder.bind(recentStation, mListener);
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }
}
