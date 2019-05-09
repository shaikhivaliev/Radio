package com.elegion.radio.ui.stations;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.elegion.radio.R;
import com.elegion.radio.entity.Station;
import com.elegion.radio.OnItemClickListener;
import com.squareup.picasso.Picasso;

public class StationHolder extends RecyclerView.ViewHolder {


    private TextView mNameStation;
    private TextView mStyleStation;
    private ImageView mLabelStation;

    public StationHolder(@NonNull View itemView) {
        super(itemView);
        mNameStation = itemView.findViewById(R.id.tv_name_station);
        mStyleStation = itemView.findViewById(R.id.tv_style_station);
        mLabelStation = itemView.findViewById(R.id.iv_label_station);
    }

    public void bind(final Station response, final OnItemClickListener onItemClickListener) {

        if (response.getImage().getUrl() == null) {
            mLabelStation.setImageResource(R.drawable.radio);

        } else {
            Picasso.get()
                    .load(response.getImage().getUrl())
                    .into(mLabelStation);
        }

        mNameStation.setText(response.getName());

        mStyleStation.setText(response.getCategoriesBean().get(0).getTitle());

        if (onItemClickListener != null) {
            itemView.setOnClickListener(v -> onItemClickListener.onItemClick(
                    response.getId()
            ));
        }
    }
}
