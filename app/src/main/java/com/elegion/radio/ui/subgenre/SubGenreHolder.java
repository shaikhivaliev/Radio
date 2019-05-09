package com.elegion.radio.ui.subgenre;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.elegion.radio.OnItemClickListener;
import com.elegion.radio.R;
import com.elegion.radio.entity.SubGenre;

public class SubGenreHolder extends RecyclerView.ViewHolder {

    private TextView mStyle;

    public SubGenreHolder(@NonNull View itemView) {
        super(itemView);
        mStyle = itemView.findViewById(R.id.tv_style_name);
    }

    public void bind(final SubGenre subGenre, OnItemClickListener onItemClickListener) {


        mStyle.setText(subGenre.getTitle());
        if (onItemClickListener != null) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(
                            subGenre.getId()
                    );
                }
            });
        }
    }
}
