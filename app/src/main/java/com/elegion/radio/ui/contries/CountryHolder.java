package com.elegion.radio.ui.contries;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.elegion.radio.OnItemClickListener;
import com.elegion.radio.R;
import com.elegion.radio.entity.Country;

public class CountryHolder extends RecyclerView.ViewHolder {

    private TextView mCountryStyleName;

    public CountryHolder(@NonNull View itemView) {
        super(itemView);
        mCountryStyleName = itemView.findViewById(R.id.tv_country_name);
    }

    public void bind(final Country country, OnItemClickListener onItemClickListener) {
        mCountryStyleName.setText(country.getCountry());

        if (onItemClickListener != null) {
            itemView.setOnClickListener(v -> onItemClickListener.onItemClick(
                    country.getCountryCode()
            ));
        }

    }
}
