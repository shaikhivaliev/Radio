package com.elegion.radio.model.server;

import android.annotation.SuppressLint;


import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ServerData {

    private ServerCallback mServerCallback;

    public void setServerCallback(ServerCallback mServerCallback) {
        this.mServerCallback = mServerCallback;
    }

    @SuppressLint("CheckResult")
    public void getStationsByCountry(String country) {
        ApiUtils.getApiService().getStationsByCountry(country, 1, 20)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        stations -> mServerCallback.showStations(stations),
                        throwable -> mServerCallback.showError()
                );
    }

    @SuppressLint("CheckResult")
    public void getStationsByStyle(String style) {
        ApiUtils.getApiService().getStationsByStyle(style, 1, 20)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        stations -> mServerCallback.showStations(stations),
                        throwable -> mServerCallback.showError()
                );
    }

    @SuppressLint("CheckResult")
    public void getStationsBySearch(String query) {
        ApiUtils.getApiService().getStationsBySearch(query, 1, 20)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        stations -> mServerCallback.showStations(stations),
                        throwable -> mServerCallback.showError()
                );
    }

}
