package com.elegion.radio.ui.contries;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elegion.radio.OnItemClickListener;
import com.elegion.radio.R;
import com.elegion.radio.entity.Country;
import com.elegion.radio.entity.Station;
import com.elegion.radio.ui.stations.StationHolder;

import java.util.ArrayList;
import java.util.List;

public class CountriesAdapter extends RecyclerView.Adapter<CountryHolder> {

    private List<Country> mData = new ArrayList<>();
    private OnItemClickListener mListener;

    public void addData(List<Country> data) {
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void setListener(OnItemClickListener listener) {
        mListener = listener;
    }


    @NonNull
    @Override
    public CountryHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View country = layoutInflater.inflate(R.layout.holder_country, viewGroup, false);
        return new CountryHolder(country);


    }

    @Override
    public void onBindViewHolder(@NonNull CountryHolder holder, int position) {
        Country country = mData.get(position);
        holder.bind(country, mListener);
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }
}
