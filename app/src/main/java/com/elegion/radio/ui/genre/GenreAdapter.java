package com.elegion.radio.ui.genre;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elegion.radio.OnItemClickListener;
import com.elegion.radio.R;
import com.elegion.radio.entity.Genre;

import java.util.ArrayList;
import java.util.List;

public class GenreAdapter extends RecyclerView.Adapter<GenreHolder> {

    private List<Genre> mData = new ArrayList<>();
    private OnItemClickListener mListener;

    public void addData(List<Genre> data) {
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void setListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public GenreHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View genre = layoutInflater.inflate(R.layout.holder_genre, viewGroup, false);
        return new GenreHolder(genre);
    }

    @Override
    public void onBindViewHolder(@NonNull GenreHolder holder, int position) {
        Genre genre = mData.get(position);
        holder.bind(genre, mListener);
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }
}
