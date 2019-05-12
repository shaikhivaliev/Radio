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
import com.elegion.radio.entity.FavoriteStation;
import com.elegion.radio.entity.RecentStation;
import com.elegion.radio.model.server.ApiUtils;
import com.elegion.radio.model.storage.Storage;
import com.elegion.radio.service.AudioPlayerService;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.BIND_AUTO_CREATE;

public class PlayerPresenter {

    private static final String SERVICE_PRESENTER = "SERVICE_PRESENTER";

    private PlayerView mView;
    private Storage mStorage;

    private RecentStation mRecentStation;
    private FavoriteStation mFavoriteStation;

    private ServiceConnection mServiceConnection;
    private AudioPlayerService.AudioPlayerBinder mBinder;
    private MediaControllerCompat mMediaController;

    public PlayerPresenter(PlayerView view, Storage storage) {
        mView = view;
        mStorage = storage;
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

    private MediaControllerCompat.Callback mMediaCallback = new MediaControllerCompat.Callback() {
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
        if (mServiceConnection != null) {
            AppDelegate.getInstance().unbindService(mServiceConnection);
        }
    }


    @SuppressLint("CheckResult")
    public void getStation(String stationId) {

        ApiUtils.getApiService()
                .getStationById(String.valueOf(stationId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> mStorage.addToRecent(stationId, getRecentStation()))
                .subscribe(station -> {
                            mView.showStation(station);
                            mRecentStation = new RecentStation(0, Integer.valueOf(stationId), station.getName(), station.getImage().getUrl(), station.getCategoriesBean().get(0).getTitle());
                            mFavoriteStation = new FavoriteStation(Integer.valueOf(stationId), station.getName(), station.getImage().getUrl(), station.getCategoriesBean().get(0).getTitle());

                        },
                        throwable -> mView.showError());

    }


    private FavoriteStation getFavoriteStation() {
        return mFavoriteStation;
    }

    private RecentStation getRecentStation() {
        return mRecentStation;
    }

    public boolean isAddedInDatabase(String stationId) {
        return mStorage.isAddedInDatabase(stationId);
    }

    public void insertStationToFavorites() {
        mStorage.insertStationToFavorites(getFavoriteStation());
    }

    public void deleteStationFromFavorites(String stationId) {
        mStorage.deleteStationFromFavorites(stationId);
    }

}
