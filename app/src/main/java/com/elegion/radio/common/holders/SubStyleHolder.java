package com.elegion.radio.common.holders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.elegion.radio.R;
import com.elegion.radio.common.RecyclerViewAdapter;
import com.elegion.radio.model.SubStyle;

public class SubStyleHolder extends RecyclerView.ViewHolder {


    private TextView mStyle;
    private ImageView mGoToStations;

    public SubStyleHolder(@NonNull View itemView) {
        super(itemView);
        mStyle = itemView.findViewById(R.id.tv_style_name);
        mGoToStations = itemView.findViewById(R.id.iv_next_sub_style);
    }

    public void bind(final SubStyle subStyle, final RecyclerViewAdapter.OnItemClickListener onItemClickListener) {

        mStyle.setText(subStyle.getTitle());
        if (onItemClickListener != null) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(
                            subStyle.getId()
                    );
                }
            });
        }
    }
}
