package com.elegion.radio.ui.genre;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.elegion.radio.OnItemClickListener;
import com.elegion.radio.R;
import com.elegion.radio.entity.Genre;

public class GenreHolder extends RecyclerView.ViewHolder {

    private TextView mStyle;

    public GenreHolder(@NonNull View itemView) {
        super(itemView);
        mStyle = itemView.findViewById(R.id.tv_style_name);
    }

    public void bind(final Genre genre, OnItemClickListener onItemClickListener) {


        mStyle.setText(genre.getTitle());
        if (onItemClickListener != null) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(
                            genre.getId()
                    );
                }
            });
        }
    }
}
