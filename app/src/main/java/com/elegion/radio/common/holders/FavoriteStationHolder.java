package com.elegion.radio.common.holders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.elegion.radio.R;
import com.elegion.radio.common.RecyclerViewAdapter;
import com.elegion.radio.database.FavoriteStation;
import com.squareup.picasso.Picasso;

public class FavoriteStationHolder extends RecyclerView.ViewHolder {

    private TextView mNameStation;
    private TextView mStyleStation;
    private ImageView mLabelStation;

    public FavoriteStationHolder(@NonNull View itemView) {
        super(itemView);
        mNameStation = itemView.findViewById(R.id.tv_name_station);
        mStyleStation = itemView.findViewById(R.id.tv_style_station);
        mLabelStation = itemView.findViewById(R.id.iv_label_station);
    }

    public void bind(final FavoriteStation favoriteStation, final RecyclerViewAdapter.OnItemClickListener onItemClickListener) {

        if (favoriteStation.getUrl() == null) {
            mLabelStation.setImageResource(R.drawable.radio);

        } else {
            Picasso.with(mLabelStation.getContext())
                    .load(favoriteStation.getUrl())
                    .into(mLabelStation);
        }

        mNameStation.setText(favoriteStation.getStationName());

        mStyleStation.setText(favoriteStation.getTitle());

        if (onItemClickListener != null) {
            itemView.setOnClickListener(v -> {
                String id = String.valueOf(favoriteStation.getId());
                onItemClickListener.onItemClick(id);
            });
        }
    }
}
