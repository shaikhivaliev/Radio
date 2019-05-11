package com.elegion.radio.model.storage;

import android.annotation.SuppressLint;

import com.elegion.radio.entity.FavoriteStation;
import com.elegion.radio.entity.RecentStation;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class Storage {

    private RadioDao mRadioDao;
    boolean isAddedInDatabase = false;


    public Storage(RadioDao radioDao) {
        mRadioDao = radioDao;
    }

    @SuppressLint("CheckResult")
    public void addToRecent(String stationId, RecentStation recentStation) {

        mRadioDao.getRecentlyStations()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(recentlyStations -> {

                    if (recentlyStations.size() < 4) {
                        insertStation(recentStation);
                    } else updateStation(recentStation);
                });

    }

    private void updateStation(RecentStation recentStation) {
        Completable.fromAction(() -> {
            mRadioDao.deleteStationFromRecently();
            mRadioDao.insertStationToRecently(recentStation);
        })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    private void insertStation(RecentStation recentStation) {
        Completable.fromAction(() -> mRadioDao.insertStationToRecently(recentStation))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public boolean isAddedInDatabase(String stationId) {

        mRadioDao.getFavoriteStationById(Integer.valueOf(stationId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<FavoriteStation>() {
                    @Override
                    public void onSuccess(FavoriteStation favoriteStation) {
                        isAddedInDatabase = true;
                    }

                    @Override
                    public void onError(Throwable e) {
                        isAddedInDatabase = true;
                    }
                });
        return isAddedInDatabase;
    }

    public void insertStationToFavorites(FavoriteStation favoriteStation) {

        Completable.fromAction(() -> {
            mRadioDao.insertStationToFavorites(favoriteStation);
        })
                .subscribeOn(Schedulers.io())
                .subscribe();

    }

    public void deleteStationFromFavorites(String stationId) {

        Completable.fromAction(() -> mRadioDao.deleteStationFromFavorites(Integer.valueOf(stationId)))
                .subscribeOn(Schedulers.io())
                .subscribe();

    }

    @SuppressLint("CheckResult")
    public void getFavoritesStation() {
        mRadioDao.getFavoritesStations()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(favoriteStations -> {

                    if (!favoriteStations.isEmpty()) {
                        //mView.showFavoritesStation(favoriteStations);

                    } else {
                        // mView.showMock();
                    }
                });
    }

    @SuppressLint("CheckResult")
    public void getRecentlyStations() {
        mRadioDao.getRecentlyStations()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<RecentStation>>() {
                    @Override
                    public void accept(List<RecentStation> recentlyStations) throws Exception {

                        if (!recentlyStations.isEmpty()) {
                            //mView.showRecentlyStation(recentlyStations);
                        } else {
                            //mView.showMock();
                        }
                    }
                });
    }

    public interface StorageOwner {
        Storage obtainStorage();
    }


}
