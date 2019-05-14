package com.elegion.radio.model.storage;

import android.annotation.SuppressLint;
import android.widget.Toast;

import com.elegion.radio.AppDelegate;
import com.elegion.radio.entity.FavoriteStation;
import com.elegion.radio.entity.RecentStation;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class StorageData {

    private RadioDao mRadioDao;

    private StorageCallback.Favorites mFavoritesCallback;
    private StorageCallback.Recently mRecentlyCallback;
    private StorageCallback.FavoritesImage mFavoritesImageCallback;

    public StorageData() {
        mRadioDao = AppDelegate.getInstance().getDatabase().getStationDao();
    }

    public void setFavoritesCallback(StorageCallback.Favorites mFavoritesCallback) {
        this.mFavoritesCallback = mFavoritesCallback;
    }

    public void setRecentlyCallback(StorageCallback.Recently mRecentlyCallback) {
        this.mRecentlyCallback = mRecentlyCallback;
    }

    public void setFavoritesImageCallback(StorageCallback.FavoritesImage mFavoritesImageCallback) {
        this.mFavoritesImageCallback = mFavoritesImageCallback;
    }

    /*-----favorites--------*/
    @SuppressLint("CheckResult")
    public void getFavoritesStation() {
        mRadioDao.getFavoritesStations()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        favoriteStations -> {
                            if (favoriteStations.size() == 0) {
                                mFavoritesCallback.showMock();
                            } else {
                                mFavoritesCallback.showFavoritesStation(favoriteStations);
                            }
                        },
                        throwable -> mFavoritesCallback.showMock()
                );
    }

    public void deleteStationFromFavorites(String stationId) {
        Completable.fromAction(() -> mRadioDao.deleteStationFromFavorites(Integer.valueOf(stationId)))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void insertStationToFavorites(FavoriteStation favoriteStation) {
        Completable.fromAction(() -> mRadioDao.insertStationToFavorites(favoriteStation))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    @SuppressLint("CheckResult")
    public void isAddedInFavorites(String stationId) {
        mRadioDao.getFavoriteStationById(Integer.valueOf(stationId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        favoriteStation -> mFavoritesImageCallback.showFavoritesImage(),
                        throwable -> mFavoritesImageCallback.showFavoritesImageMock()
                );
    }


    /*-----recently--------*/

    @SuppressLint("CheckResult")
    public void getRecentlyStations() {
        mRadioDao.getRecentlyStations()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        recentlyStations -> {
                            if (recentlyStations.size() == 0) {
                                mRecentlyCallback.showMock();
                            } else {
                                mRecentlyCallback.showRecentlyStation(recentlyStations);
                            }
                        },
                        throwable -> mRecentlyCallback.showMock()
                );
    }

    @SuppressLint("CheckResult")
    public void addToRecent(String stationId, RecentStation recentStation) {

        mRadioDao.getRecentlyStations()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(recentlyStations -> {

                    int isAlreadyInRecently = 0;

                    for (RecentStation station : recentlyStations) {
                        if (station.getStationId() == Integer.valueOf(stationId)) {
                            isAlreadyInRecently++;
                        }
                    }

                    if (isAlreadyInRecently > 0) return;

                    if (recentlyStations.size() < 4) {
                        insertRecentStation(recentStation);
                    } else updateRecentStation(recentStation);

                });
    }


    private void updateRecentStation(RecentStation recentStation) {
        Completable.fromAction(() -> {
            mRadioDao.deleteStationFromRecently();
            mRadioDao.insertStationToRecently(recentStation);
        })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    private void insertRecentStation(RecentStation recentStation) {
        Completable.fromAction(() -> mRadioDao.insertStationToRecently(recentStation))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

}
