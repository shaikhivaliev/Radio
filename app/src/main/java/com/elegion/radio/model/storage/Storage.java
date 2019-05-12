package com.elegion.radio.model.storage;

import android.annotation.SuppressLint;

import com.elegion.radio.entity.FavoriteStation;
import com.elegion.radio.entity.RecentStation;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class Storage {

    private RadioDao mRadioDao;
    private boolean isAddedInDatabase = false;
    private List<FavoriteStation> mFavoriteStations = new ArrayList<>();
    private List<RecentStation> mRecentStations = new ArrayList<>();


    public Storage(RadioDao radioDao) {
        mRadioDao = radioDao;
    }

    @SuppressLint("CheckResult")

    //FIXME
    public void addToRecent(String stationId, RecentStation recentStation) {

        mRadioDao.getRecentlyStations()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<RecentStation>>() {
                    @Override
                    public void accept(List<RecentStation> recentlyStations) throws Exception {

                        int isAlreadyInRecently = 0;

                        for (RecentStation station : recentlyStations) {
                            if (station.getStationId() == Integer.valueOf(stationId)) {
                                isAlreadyInRecently++;
                            }
                        }

                        if (isAlreadyInRecently > 0) return;

                        if (recentlyStations.size() < 4) {
                            Storage.this.insertStation(recentStation);
                        } else Storage.this.updateStation(recentStation);

                    }
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

    @SuppressLint("CheckResult")
    //FIXME
    public boolean isAddedInDatabase(String stationId) {

        mRadioDao.getFavoriteStationById(Integer.valueOf(stationId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<FavoriteStation>() {
                    @Override
                    public void accept(FavoriteStation favoriteStation) throws Exception {
                        isAddedInDatabase = true;
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        isAddedInDatabase = false;
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
    //FIXME
    public List<FavoriteStation> getFavoritesStation() {
        mRadioDao.getFavoritesStations()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<FavoriteStation>>() {
                    @Override
                    public void accept(List<FavoriteStation> favoriteStations) throws Exception {
                        mFavoriteStations.clear();
                        mFavoriteStations.addAll(favoriteStations);
                    }
                });

        return mFavoriteStations;
    }

    @SuppressLint("CheckResult")
    //FIXME
    public List<RecentStation> getRecentlyStations() {
        mRadioDao.getRecentlyStations()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(recentlyStations -> {
                    mRecentStations.clear();
                    mRecentStations.addAll(recentlyStations);
                });

        return mRecentStations;
    }

    public interface StorageOwner {
        Storage obtainStorage();
    }


}
