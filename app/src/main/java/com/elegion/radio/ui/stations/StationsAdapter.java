package com.elegion.radio.ui.stations;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elegion.radio.R;
import com.elegion.radio.entity.Station;
import com.elegion.radio.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class StationsAdapter extends RecyclerView.Adapter<StationHolder> {

    private List<Station> mData = new ArrayList<>();
    private OnItemClickListener mListener;

    public void addData(List<Station> data) {
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void setListener(OnItemClickListener listener) {
        mListener = listener;
    }


    @NonNull
    @Override
    public StationHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View station = layoutInflater.inflate(R.layout.holder_station, viewGroup, false);
        return new StationHolder(station);
    }

    @Override
    public void onBindViewHolder(@NonNull StationHolder holder, int position) {
        Station station = mData.get(position);
        holder.bind(station, mListener);

    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }
}
