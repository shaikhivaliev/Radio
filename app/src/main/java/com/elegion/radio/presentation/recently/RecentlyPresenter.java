package com.elegion.radio.presentation.recently;

import android.annotation.SuppressLint;

import com.elegion.radio.model.storage.Storage;

public class RecentlyPresenter {

    private RecentlyView mView;
    private Storage mStorage;


    public RecentlyPresenter(RecentlyView view, Storage storage) {
        mView = view;
        mStorage = storage;
    }

    @SuppressLint("CheckResult")
    public void getRecentlyStations() {
        mStorage.getRecentlyStations();
    }
}
