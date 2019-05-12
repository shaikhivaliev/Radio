package com.elegion.radio.presentation.favorites;

import android.annotation.SuppressLint;
import android.util.Log;

import com.elegion.radio.entity.FavoriteStation;
import com.elegion.radio.model.storage.Storage;
import com.elegion.radio.ui.favorites.FavoritesFragment;

import java.util.List;

public class FavoritesPresenter {

    private FavoritesView mView;
    private Storage mStorage;


    public FavoritesPresenter(FavoritesView view, Storage storage) {
        mView = view;
        mStorage = storage;
    }

    @SuppressLint("CheckResult")
    public void getFavoritesStation() {
        List<FavoriteStation> favoriteStations = mStorage.getFavoritesStation();
        if (!favoriteStations.isEmpty()) {
            mView.showFavoritesStation(favoriteStations);
        } else {
            mView.showMock();
        }
    }

}
