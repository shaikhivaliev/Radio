package com.elegion.radio.common.holders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.elegion.radio.R;
import com.elegion.radio.common.RecyclerViewAdapter;
import com.elegion.radio.model.Country;

public class CountriesHolder extends RecyclerView.ViewHolder {

    private TextView mCountryStyleName;

    public CountriesHolder(@NonNull View itemView) {
        super(itemView);
        mCountryStyleName = itemView.findViewById(R.id.tv_country_name);
    }

    public void bind(final Country country, final RecyclerViewAdapter.OnItemClickListener onItemClickListener) {
        mCountryStyleName.setText(country.getCountry());

        if (onItemClickListener != null) {
            itemView.setOnClickListener(v -> onItemClickListener.onItemClick(
                    country.getCountryCode()
            ));
        }

    }
}
