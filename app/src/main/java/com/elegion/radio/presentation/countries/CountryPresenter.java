package com.elegion.radio.presentation.countries;

import com.elegion.radio.AppDelegate;
import com.elegion.radio.entity.Country;
import com.elegion.radio.JsonUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class CountryPresenter {

    private CountryView mView;

    public CountryPresenter(CountryView view) {
        mView = view;
    }

    public void getCountries()  {

        Gson gson = new Gson();
        Type type = new TypeToken<List<Country>>() {
        }.getType();

        try {
            List<Country> countries = gson.fromJson(JsonUtils.loadJSONFromAsset(AppDelegate.getInstance(),"countries.json"), type);
            Collections.sort(countries, (o1, o2) -> o1.getCountry().compareToIgnoreCase(o2.getCountry()));

            mView.showCountries(countries);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
