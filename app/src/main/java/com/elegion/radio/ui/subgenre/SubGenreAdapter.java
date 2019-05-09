package com.elegion.radio.ui.subgenre;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elegion.radio.OnItemClickListener;
import com.elegion.radio.R;
import com.elegion.radio.entity.SubGenre;

import java.util.ArrayList;
import java.util.List;

public class SubGenreAdapter extends RecyclerView.Adapter<SubGenreHolder> {

    private List<SubGenre> mData = new ArrayList<>();
    private OnItemClickListener mListener;

    public void addData(List<SubGenre> data) {
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void setListener(OnItemClickListener listener) {
        mListener = listener;
    }


    @NonNull
    @Override
    public SubGenreHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View subgenre = layoutInflater.inflate(R.layout.holder_genre, viewGroup, false);
        return new SubGenreHolder(subgenre);
    }

    @Override
    public void onBindViewHolder(@NonNull SubGenreHolder holder, int position) {
        SubGenre subGenre = mData.get(position);
        holder.bind(subGenre, mListener);
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }
}
