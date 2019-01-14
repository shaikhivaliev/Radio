package com.elegion.radio.common.holders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.elegion.radio.R;
import com.elegion.radio.common.RecyclerViewAdapter;
import com.elegion.radio.model.Style;

public class StyleHolder extends RecyclerView.ViewHolder {

    private TextView mStyle;
    private ImageView mGoToSubStyle;

    public StyleHolder(@NonNull View itemView) {
        super(itemView);
        mStyle = itemView.findViewById(R.id.tv_style_name);
        mGoToSubStyle = itemView.findViewById(R.id.iv_next_sub_style);
    }

    public void bind(final Style style, final RecyclerViewAdapter.OnItemClickListener onItemClickListener) {


        mStyle.setText(style.getTitle());
        if (onItemClickListener != null) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(
                            style.getId()
                    );
                }
            });
        }
    }
}
