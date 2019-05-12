package com.elegion.radio.presentation.recently;

import android.annotation.SuppressLint;

import com.elegion.radio.entity.RecentStation;
import com.elegion.radio.model.storage.Storage;

import java.util.List;

public class RecentlyPresenter {

    private RecentlyView mView;
    private Storage mStorage;


    public RecentlyPresenter(RecentlyView view, Storage storage) {
        mView = view;
        mStorage = storage;
    }

    @SuppressLint("CheckResult")
    public void getRecentlyStations() {
        List<RecentStation> recentStations = mStorage.getRecentlyStations();
        if (!recentStations.isEmpty()) {
            mView.showRecentlyStation(recentStations);
        } else {
            mView.showMock();
        }

    }
}
