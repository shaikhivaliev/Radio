package com.elegion.radio.ui.player;

import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.elegion.radio.AppDelegate;
import com.elegion.radio.R;
import com.elegion.radio.database.AppDatabase;
import com.elegion.radio.database.FavoriteStation;
import com.elegion.radio.database.RecentStation;
import com.elegion.radio.database.StationDao;
import com.elegion.radio.model.Station;
import com.elegion.radio.utils.ApiUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;


public class PlayerFragment extends Fragment implements
        MediaPlayer.OnPreparedListener {

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

    private MediaPlayer mMediaPlayer;
    private ImageButton mPlayStopButton;
    private ProgressBar mProgressBar;
    private SeekBar mSeekbarVolume;

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

        mSeekbarVolume = v.findViewById(R.id.seekBar);

        mSeekbarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                setProperty(null, "volume", seekBar.getProgress());
//                showProperty("volume", null, "%");
            }
        });
    }


    @SuppressLint("CheckResult")
    private void getStation() {
        ApiUtils.getApiService()
                .getStationById(String.valueOf(mStationId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        addToRecent();
                    }
                })
                .subscribe(new Consumer<Station>() {
                    @Override
                    public void accept(Station response) throws Exception {
                        mStyle = response.getCategoriesBean().get(0).getTitle();
                        mUrl = response.getImage().getUrl();
                        if (mUrl != null) {
                            Picasso.with(mStationLabel.getContext())
                                    .load(mUrl)
                                    .into(mStationLabel);
                        } else {
                            mStationLabel.setImageResource(R.drawable.radio);
                        }
                        mStationName.setText(response.getName());
                        mStreamResource = response.getStreamBeans().get(0).getStreamResource();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mErrorView.setVisibility(View.VISIBLE);
                        mPlayerView.setVisibility(View.GONE);

                    }
                });
    }


    /*------------------Recently-----------*/

    @SuppressLint("CheckResult")
    private void addToRecent() {

        mStationDao.getRecentlyStations()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<RecentStation>>() {
                    @Override
                    public void accept(List<RecentStation> recentlyStations) throws Exception {
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
                    }

                });
    }

    private void updateStation() {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                mStationDao.deleteStationFromRecently();

                RecentStation rs = new RecentStation(0, Integer.valueOf(mStationId), mStationName.getText().toString(), mUrl, mStyle);
                mStationDao.insertStationToRecently(rs);
            }
        })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    private void insertStation() {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                RecentStation rs = new RecentStation(0, Integer.valueOf(mStationId), mStationName.getText().toString(), mUrl, mStyle);
                mStationDao.insertStationToRecently(rs);
            }
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

            Completable.fromAction(new Action() {
                @Override
                public void run() throws Exception {
                    FavoriteStation fs = new FavoriteStation(Integer.valueOf(mStationId), mStationName.getText().toString(), mUrl, mStyle);
                    mStationDao.insertStationToFavorites(fs);
                }
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

            Completable.fromAction(new Action() {
                @Override
                public void run() throws Exception {
                    mStationDao.deleteStationFromFavorites(Integer.valueOf(mStationId));
                }
            })
                    .subscribeOn(Schedulers.io())
                    .subscribe();

        }
    };



    /*------------------Player--------------*/

    View.OnClickListener playButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            playRadio();
            mPlayStopButton.setOnClickListener(pauseButtonListener);
        }
    };

    View.OnClickListener pauseButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            pauseRadio();
            mPlayStopButton.setOnClickListener(playButtonListener);
        }
    };

    private void playRadio() {

        mPlayStopButton.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);

        mMediaPlayer = new MediaPlayer();

        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setDataSource(mStreamResource);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.prepareAsync();

    }


    private void pauseRadio() {

        mPlayStopButton.setImageResource(R.drawable.ic_play);
        mMediaPlayer.pause();
    }

    private void release() {
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.release();
                mMediaPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mPlayStopButton.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mPlayStopButton.setImageResource(R.drawable.ic_pause);
        mMediaPlayer.start();
    }


    @Override
    public void onPause() {
        super.onPause();
        release();
    }


}

