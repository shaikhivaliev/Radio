package com.elegion.radio.ui.player;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.elegion.radio.AppDelegate;
import com.elegion.radio.R;
import com.elegion.radio.database.AppDatabase;
import com.elegion.radio.database.FavoriteStation;
import com.elegion.radio.database.RecentStation;
import com.elegion.radio.database.StationDao;
import com.elegion.radio.utils.ApiUtils;
import com.squareup.picasso.Picasso;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.BIND_AUTO_CREATE;


public class PlayerFragment extends Fragment {

    public static final String STATION_KEY = "STATION_KEY";

    private View mErrorView;
    private View mPlayerView;
    private StationDao mStationDao;

    private ImageView mStationLabel;
    private TextView mStationName;
    private ImageView mAddToFavorites;

    private String mStationId;
    private String mStreamResource = "";
    private String mUrl;
    private String mStyle;

    private ImageButton mPlayStopButton;
    private ProgressBar mProgressBar;

    private PlayerService.PlayerServiceBinder mPlayerServiceBinder;

    //через этот класс передаем команды в сервис
    private MediaControllerCompat mMediaController;
    //...и отрабатываем изменения и команды от сервиса
    private MediaControllerCompat.Callback mCallback;

    private ServiceConnection mServiceConnection;

    public static PlayerFragment newInstance(Bundle args) {
        PlayerFragment fragment = new PlayerFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mStationId = getArguments().getString(STATION_KEY);
        }

        getStation();

        AppDatabase database = AppDelegate.getInstance().getDatabase();
        mStationDao = database.getStationDao();

        isAddedInDatabase();

        mCallback = new MediaControllerCompat.Callback() {
            @Override
            public void onPlaybackStateChanged(PlaybackStateCompat state) {

                if (state == null)
                    return;

                boolean playing = state.getState() == PlaybackStateCompat.STATE_PLAYING;

                if (playing) {
                    mPlayStopButton.setImageResource(R.drawable.ic_pause);
                    mPlayStopButton.setOnClickListener(pauseButtonListener);
                } else {
                    mPlayStopButton.setImageResource(R.drawable.ic_play);
                    mPlayStopButton.setOnClickListener(playButtonListener);
                }
            }
        };

        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mPlayerServiceBinder = (PlayerService.PlayerServiceBinder) service;
                try {
                    mMediaController = new MediaControllerCompat(getActivity(), mPlayerServiceBinder.getMediaSessionToken());
                    mMediaController.registerCallback(mCallback);
                    mCallback.onPlaybackStateChanged(mMediaController.getPlaybackState());
                } catch (RemoteException e) {
                    mMediaController = null;
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mPlayerServiceBinder = null;
                if (mMediaController != null) {
                    mMediaController.unregisterCallback(mCallback);
                    mMediaController = null;
                }
            }
        };

        getActivity().bindService(new Intent(getContext(), PlayerService.class), mServiceConnection, BIND_AUTO_CREATE);

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_player, container, false);
        initUi(v);
        return v;
    }

    private void initUi(View v) {
        mErrorView = v.findViewById(R.id.fr_error_view);
        mPlayerView = v.findViewById(R.id.player_view);

        mStationLabel = v.findViewById(R.id.iv_station);
        mStationName = v.findViewById(R.id.tv_name);

        mAddToFavorites = v.findViewById(R.id.iv_add_to_favorites);
        mAddToFavorites.setOnClickListener(addToFavorites);

        mPlayStopButton = v.findViewById(R.id.btn_play_stop);
        mPlayStopButton.setOnClickListener(playButtonListener);
        mProgressBar = v.findViewById(R.id.pb_audio_buffer);

    }

    /*------------------Player--------------*/

    View.OnClickListener playButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mMediaController != null)
                mMediaController.getTransportControls().play();
        }
    };

    View.OnClickListener pauseButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mMediaController != null)
                mMediaController.getTransportControls().pause();
        }
    };


    @SuppressLint("CheckResult")
    private void getStation() {
        ApiUtils.getApiService()
                .getStationById(String.valueOf(mStationId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> addToRecent())
                .subscribe(response -> {
                    mStyle = response.getCategoriesBean().get(0).getTitle();
                    mUrl = response.getImage().getUrl();
                    if (mUrl != null) {
                        Picasso.get()
                                .load(mUrl)
                                .into(mStationLabel);
                    } else {
                        mStationLabel.setImageResource(R.drawable.radio);
                    }
                    mStationName.setText(response.getName());
                    mStreamResource = response.getStreamBeans().get(0).getStreamResource();
                }, throwable -> {
                    mErrorView.setVisibility(View.VISIBLE);
                    mPlayerView.setVisibility(View.GONE);

                });
    }


    /*------------------Recently-----------*/

    @SuppressLint("CheckResult")
    private void addToRecent() {

        mStationDao.getRecentlyStations()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(recentlyStations -> {
                    int isAlreadyInRecently = 0;

                    for (RecentStation station : recentlyStations) {
                        if (station.getStationId() == Integer.valueOf(mStationId)) {
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
            mStationDao.deleteStationFromRecently();

            RecentStation rs = new RecentStation(0, Integer.valueOf(mStationId), mStationName.getText().toString(), mUrl, mStyle);
            mStationDao.insertStationToRecently(rs);
        })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    private void insertStation() {
        Completable.fromAction(() -> {
            RecentStation rs = new RecentStation(0, Integer.valueOf(mStationId), mStationName.getText().toString(), mUrl, mStyle);
            mStationDao.insertStationToRecently(rs);
        })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }



    /*-------------------Favorites-------------*/

    private void isAddedInDatabase() {
        mStationDao.getFavoriteStationById(Integer.valueOf(mStationId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<FavoriteStation>() {
                    @Override
                    public void onSuccess(FavoriteStation favoriteStation) {
                        mAddToFavorites.setImageResource(R.drawable.ic_star_filling);
                        mAddToFavorites.setOnClickListener(removeFromFavorites);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mAddToFavorites.setImageResource(R.drawable.ic_star);
                        mAddToFavorites.setOnClickListener(addToFavorites);
                    }
                });
    }


    View.OnClickListener addToFavorites = new View.OnClickListener() {
        @SuppressLint("CheckResult")
        @Override
        public void onClick(View v) {
            Toast.makeText(getActivity(), mStationName.getText() + getString(R.string.toast_add_favorites), Toast.LENGTH_SHORT).show();
            mAddToFavorites.setImageResource(R.drawable.ic_star_filling);
            mAddToFavorites.setOnClickListener(removeFromFavorites);

            Completable.fromAction(() -> {
                FavoriteStation fs = new FavoriteStation(Integer.valueOf(mStationId), mStationName.getText().toString(), mUrl, mStyle);
                mStationDao.insertStationToFavorites(fs);
            })
                    .subscribeOn(Schedulers.io())
                    .subscribe();
        }
    };

    View.OnClickListener removeFromFavorites = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getActivity(), mStationName.getText() + getString(R.string.toast_remove_favorites), Toast.LENGTH_SHORT).show();
            mAddToFavorites.setImageResource(R.drawable.ic_star);
            mAddToFavorites.setOnClickListener(addToFavorites);

            Completable.fromAction(() -> mStationDao.deleteStationFromFavorites(Integer.valueOf(mStationId)))
                    .subscribeOn(Schedulers.io())
                    .subscribe();

        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        mPlayerServiceBinder = null;
        if (mMediaController != null) {
            mMediaController.unregisterCallback(mCallback);
            mMediaController = null;
        }
        getActivity().unbindService(mServiceConnection);
    }
}

