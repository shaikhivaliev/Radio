package com.elegion.radio.presentation.recently;

import com.elegion.radio.entity.RecentStation;
import com.elegion.radio.model.storage.StorageCallback;
import com.elegion.radio.model.storage.StorageData;

import java.util.List;

public class RecentlyPresenter implements StorageCallback.Recently {

    private StorageData mStorageData = new StorageData();
    private RecentlyView mView;

    public RecentlyPresenter(RecentlyView mView) {
        this.mView = mView;
        mStorageData.setRecentlyCallback(this);
    }

    public void getRecentlyStations() {
        mStorageData.getRecentlyStations();
    }

    @Override
    public void showRecentlyStation(List<RecentStation> recentStations) {
        mView.showRecentlyStation(recentStations);
    }

    @Override
    public void showMock() {
        mView.showMock();
    }
}
