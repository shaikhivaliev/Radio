package com.elegion.radio.presentation.favorites;

import android.annotation.SuppressLint;

import com.elegion.radio.model.storage.Storage;

public class FavoritesPresenter {

    private FavoritesView mView;
    private Storage mStorage;


    public FavoritesPresenter(FavoritesView view, Storage storage) {
        mView = view;
        mStorage = storage;
    }

    @SuppressLint("CheckResult")
    public void getFavoritesStation() {
        mStorage.getFavoritesStation();
    }

}
