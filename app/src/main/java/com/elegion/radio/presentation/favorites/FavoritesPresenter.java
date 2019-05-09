package com.elegion.radio.presentation.favorites;

import android.annotation.SuppressLint;

import com.elegion.radio.AppDelegate;
import com.elegion.radio.data.storage.AppDatabase;
import com.elegion.radio.data.storage.StationDao;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class FavoritesPresenter {

    private FavoritesView mView;

    public FavoritesPresenter(FavoritesView view) {
        mView = view;
    }

    @SuppressLint("CheckResult")
    public void getFavoritesStation() {

        AppDatabase mDatabase = AppDelegate.getInstance().getDatabase();
        StationDao mStationDao = mDatabase.getStationDao();


        mStationDao.getFavoritesStations()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(favoriteStations -> {

                    if (!favoriteStations.isEmpty()) {
                        mView.showFavoritesStation(favoriteStations);

                    } else {
                        mView.showMock();
                    }
                });
    }

}
