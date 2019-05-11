package com.elegion.radio.presentation.player;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.elegion.radio.AppDelegate;
import com.elegion.radio.data.server.ApiUtils;
import com.elegion.radio.data.storage.AppDatabase;
import com.elegion.radio.data.storage.StationDao;
import com.elegion.radio.entity.FavoriteStation;
import com.elegion.radio.entity.RecentStation;
import com.elegion.radio.service.AudioPlayerService;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.BIND_AUTO_CREATE;

public class PlayerPresenter {

    public static final String SERVICE_PRESENTER = "SERVICE_PRESENTER";

    private PlayerView mView;

    private ServiceConnection mServiceConnection;
    private AudioPlayerService.AudioPlayerBinder mBinder;
    private MediaControllerCompat mMediaController;

    AppDatabase database = AppDelegate.getInstance().getDatabase();
    StationDao stationDao = database.getStationDao();

    public PlayerPresenter(PlayerView view) {
        mView = view;
    }

    public void startAudioService(String stationStreamUrl) {

        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder serviceBinder) {
                mBinder = (AudioPlayerService.AudioPlayerBinder) serviceBinder;
                mBinder.setStreamResources(stationStreamUrl);
                Log.d(SERVICE_PRESENTER, "onServiceConnected, получили экземпляр mBinder, засетили - " + stationStreamUrl);

                try {
                    mMediaController = new MediaControllerCompat(AppDelegate.getInstance(), mBinder.getMediaSessionToken());
                    mMediaController.registerCallback(mMediaCallback);
                    mMediaCallback.onPlaybackStateChanged(mMediaController.getPlaybackState());


                } catch (RemoteException e) {
                    mMediaController = null;
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mBinder = null;
                if (mMediaController != null) {
                    mMediaController.unregisterCallback(mMediaCallback);
                    mMediaController = null;
                }
            }
        };

        Log.d(SERVICE_PRESENTER, "bindService");
        AppDelegate.getInstance().bindService(new Intent(AppDelegate.getInstance(), AudioPlayerService.class), mServiceConnection, BIND_AUTO_CREATE);
    }

    MediaControllerCompat.Callback mMediaCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            if (state == null)
                return;
            boolean playing = state.getState() == PlaybackStateCompat.STATE_PLAYING;
            if (playing) {
                Log.d(SERVICE_PRESENTER, "mMediaCallback - showPauseButton");
                mView.showPauseButton();

            } else {
                Log.d(SERVICE_PRESENTER, "mMediaCallback - showPlayButton");
                mView.showPlayButton();
            }
        }
    };

    public void playRadio() {
        Log.d(SERVICE_PRESENTER, "mMediaController - play");

        if (mMediaController != null)
            mMediaController.getTransportControls().play();
    }

    public void pauseRadio() {
        Log.d(SERVICE_PRESENTER, "mMediaController - pause");

        if (mMediaController != null)
            mMediaController.getTransportControls().pause();
    }

    public void stopAudioService() {

        mBinder = null;
        if (mMediaController != null) {
            Log.d(SERVICE_PRESENTER, "unregisterCallback");
            mMediaController.unregisterCallback(mMediaCallback);
            mMediaController = null;
        }
        Log.d(SERVICE_PRESENTER, "unbindService");
        AppDelegate.getInstance().unbindService(mServiceConnection);
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
