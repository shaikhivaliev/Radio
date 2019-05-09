package com.elegion.radio.presentation.player;

import android.annotation.SuppressLint;

import com.elegion.radio.AppDelegate;
import com.elegion.radio.data.server.ApiUtils;
import com.elegion.radio.data.storage.AppDatabase;
import com.elegion.radio.data.storage.FavoriteStation;
import com.elegion.radio.data.storage.RecentStation;
import com.elegion.radio.data.storage.StationDao;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class PlayerPresenter {

    private PlayerView mView;
    AppDatabase database = AppDelegate.getInstance().getDatabase();
    StationDao stationDao = database.getStationDao();


    public PlayerPresenter(PlayerView view) {
        mView = view;
    }

    public void isAddedInDatabase(String stationId) {

        stationDao.getFavoriteStationById(Integer.valueOf(stationId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<FavoriteStation>() {
                    @Override
                    public void onSuccess(FavoriteStation favoriteStation) {
                        mView.isAddedInDatabase(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.isAddedInDatabase(false);
                    }
                });
    }

    @SuppressLint("CheckResult")
    public void getStation(String stationId) {
        ApiUtils.getApiService()
                .getStationById(String.valueOf(stationId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> addToRecent(stationId))
                .subscribe(response -> mView.showStation(response),
                        throwable -> mView.showError());

    }

    @SuppressLint("CheckResult")
    private void addToRecent(String stationId) {

        stationDao.getRecentlyStations()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(recentlyStations -> {
                    int isAlreadyInRecently = 0;

                    for (RecentStation station : recentlyStations) {
                        if (station.getStationId() == Integer.valueOf(stationId)) {
                            isAlreadyInRecently++;
                        }
                    }

                    if (isAlreadyInRecently > 0) {
                        return;
                    } else if (recentlyStations.size() < 4) {
                        insertStation();
                    } else updateStation();
                });
    }

    private void updateStation() {
        Completable.fromAction(() -> {
            stationDao.deleteStationFromRecently();
            RecentStation recentStation = mView.getRecentStation();
            stationDao.insertStationToRecently(recentStation);
        })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    private void insertStation() {
        Completable.fromAction(() -> {
            RecentStation recentStation = mView.getRecentStation();
            stationDao.insertStationToRecently(recentStation);
        })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }


    public void insertStationToFavorites() {

        Completable.fromAction(() -> {
            FavoriteStation favoriteStation = mView.getFavoriteStation();
            stationDao.insertStationToFavorites(favoriteStation);
        })
                .subscribeOn(Schedulers.io())
                .subscribe();

    }

    public void deleteStationFromFavorites(String stationId) {

        Completable.fromAction(() -> stationDao.deleteStationFromFavorites(Integer.valueOf(stationId)))
                .subscribeOn(Schedulers.io())
                .subscribe();

    }
}
