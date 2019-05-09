package com.elegion.radio.presentation.stations;

import android.annotation.SuppressLint;

import com.elegion.radio.data.server.ApiUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class StationsPresenter {

    private StationsView mStationsView;

    public StationsPresenter(StationsView stationsView) {
        mStationsView = stationsView;
    }

    @SuppressLint("CheckResult")
    public void getStationsByCountry(String country) {

        ApiUtils.getApiService().getStationsByCountry(country, 1, 20)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> mStationsView.showStations(response),
                        throwable -> mStationsView.showError()
                );
    }

    @SuppressLint("CheckResult")
    public void getStationsByStyle(String style) {

        ApiUtils.getApiService().getStationsByStyle(style, 1, 20)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> mStationsView.showStations(response),
                        throwable -> mStationsView.showError()
                );
    }

    @SuppressLint("CheckResult")
    public void getStationsBySearch(String query) {

        ApiUtils.getApiService().getStationsBySearch(query, 1, 20)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> mStationsView.showStations(response),
                        throwable -> mStationsView.showError()
                );
    }

}
