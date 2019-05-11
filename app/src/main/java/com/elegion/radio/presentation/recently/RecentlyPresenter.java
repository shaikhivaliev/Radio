package com.elegion.radio.presentation.recently;

import android.annotation.SuppressLint;

import com.elegion.radio.AppDelegate;
import com.elegion.radio.data.storage.AppDatabase;
import com.elegion.radio.entity.RecentStation;
import com.elegion.radio.data.storage.StationDao;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class RecentlyPresenter {

    private RecentlyView mView;

    public RecentlyPresenter(RecentlyView view) {
        mView = view;
    }

    @SuppressLint("CheckResult")
    public void getRecentlyStations() {

        AppDatabase mDatabase = AppDelegate.getInstance().getDatabase();
        StationDao mStationDao = mDatabase.getStationDao();

        mStationDao.getRecentlyStations()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<RecentStation>>() {
                    @Override
                    public void accept(List<RecentStation> recentlyStations) throws Exception {

                        if (!recentlyStations.isEmpty()) {
                            mView.showRecentlyStation(recentlyStations);
                        } else {
                            mView.showMock();
                        }
                    }
                });
    }

}
