package com.elegion.radio.ui.favorites;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elegion.radio.OnItemClickListener;
import com.elegion.radio.R;
import com.elegion.radio.entity.FavoriteStation;

import java.util.ArrayList;
import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteHolder> {

    private List<FavoriteStation> mData = new ArrayList<>();
    private OnItemClickListener mListener;

    public void addData(List<FavoriteStation> data) {
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void setListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public FavoriteHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View favoriteStation = layoutInflater.inflate(R.layout.holder_station, viewGroup, false);
        return new FavoriteHolder(favoriteStation);

    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteHolder holder, int position) {
        FavoriteStation favoriteStation = mData.get(position);
        holder.bind(favoriteStation, mListener);
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }
}
