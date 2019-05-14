package com.elegion.radio.presentation.favorites;

import android.annotation.SuppressLint;

import com.elegion.radio.AppDelegate;
import com.elegion.radio.entity.FavoriteStation;
import com.elegion.radio.model.storage.StorageCallback;
import com.elegion.radio.model.storage.StorageData;

import java.util.List;

public class FavoritesPresenter implements StorageCallback.Favorites {

    private FavoritesView mView;
    private StorageData mStorageData = new StorageData();

    public FavoritesPresenter(FavoritesView view) {
        mView = view;
        mStorageData.setFavoritesCallback(this);
    }

    @SuppressLint("CheckResult")
    public void getFavoritesStation() {
        mStorageData.getFavoritesStation();
    }

    @Override
    public void showFavoritesStation(List<FavoriteStation> favoriteStations) {
        mView.showFavoritesStation(favoriteStations);
    }

    @Override
    public void showMock() {
        mView.showMock();
    }
}
