package com.elegion.radio.ui.recently;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.elegion.radio.OnItemClickListener;
import com.elegion.radio.R;
import com.elegion.radio.data.storage.RecentStation;
import com.squareup.picasso.Picasso;

public class RecentlyHolder extends RecyclerView.ViewHolder {

    private TextView mNameStation;
    private TextView mStyleStation;
    private ImageView mLabelStation;

    public RecentlyHolder(@NonNull View itemView) {
        super(itemView);
        mNameStation = itemView.findViewById(R.id.tv_name_station);
        mStyleStation = itemView.findViewById(R.id.tv_style_station);
        mLabelStation = itemView.findViewById(R.id.iv_label_station);
    }

    public void bind(final RecentStation recentStation, final OnItemClickListener onItemClickListener) {

        if (recentStation.getUrl() == null) {
            mLabelStation.setImageResource(R.drawable.radio);

        } else {
            Picasso.get().load(recentStation.getUrl()).into(mLabelStation);
        }

        mNameStation.setText(recentStation.getStationName());
        mStyleStation.setText(recentStation.getTitle());

        if (onItemClickListener != null) {
            itemView.setOnClickListener(v -> {
                String id = String.valueOf(recentStation.getStationId());
                onItemClickListener.onItemClick(id);
            });
        }
    }
}
