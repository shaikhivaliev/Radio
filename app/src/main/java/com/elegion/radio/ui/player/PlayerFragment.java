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
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.elegion.radio.R;
import com.elegion.radio.data.storage.FavoriteStation;
import com.elegion.radio.data.storage.RecentStation;
import com.elegion.radio.entity.Station;
import com.elegion.radio.presentation.player.PlayerPresenter;
import com.elegion.radio.presentation.player.PlayerView;
import com.elegion.radio.service.AudioPlayerService;
import com.squareup.picasso.Picasso;

import static android.content.Context.BIND_AUTO_CREATE;


public class PlayerFragment extends Fragment implements
        PlayerView {

    public static final String STATION_KEY = "STATION_KEY";

    private View mErrorView;
    private View mPlayerView;

    private ImageView mStationLabel;
    private TextView mStationName;
    private ImageView mAddToFavorites;
    private String mStationId;
    private String mStreamResource = "";
    private String mUrl;
    private String mStyle;
    private ImageButton mPlayPauseButton;

    private ServiceConnection mServiceConnection;
    private AudioPlayerService.AudioPlayerBinder mBinder;

    private MediaControllerCompat mMediaController;

    private PlayerPresenter mPresenter;


    public static PlayerFragment newInstance(Bundle args) {
        PlayerFragment fragment = new PlayerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    MediaControllerCompat.Callback mMediaCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            if (state == null)
                return;
            boolean playing = state.getState() == PlaybackStateCompat.STATE_PLAYING;
            if (playing) {
                mPlayPauseButton.setImageResource(R.drawable.ic_pause);
                mPlayPauseButton.setOnClickListener(pauseButtonListener);

            } else {
                mPlayPauseButton.setImageResource(R.drawable.ic_play);
                mPlayPauseButton.setOnClickListener(playButtonListener);
            }
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            super.onMetadataChanged(metadata);

            if (metadata == null) {
                return;
            }

        }
    };

    View.OnClickListener playButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mPlayPauseButton.setImageResource(R.drawable.ic_pause);
            mPlayPauseButton.setOnClickListener(pauseButtonListener);



            if (mMediaController != null)
                mMediaController.getTransportControls().play();

        }
    };

    View.OnClickListener pauseButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mPlayPauseButton.setImageResource(R.drawable.ic_play);
            mPlayPauseButton.setOnClickListener(playButtonListener);

            if (mMediaController != null)
                mMediaController.getTransportControls().pause();

        }
    };

    View.OnClickListener addToFavorites = new View.OnClickListener() {
        @SuppressLint("CheckResult")
        @Override
        public void onClick(View v) {
            Toast.makeText(getActivity(), mStationName.getText() + getString(R.string.toast_add_favorites), Toast.LENGTH_SHORT).show();
            mAddToFavorites.setImageResource(R.drawable.ic_star_filling);
            mAddToFavorites.setOnClickListener(removeFromFavorites);
            mPresenter.insertStationToFavorites();
        }
    };

    View.OnClickListener removeFromFavorites = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getActivity(), mStationName.getText() + getString(R.string.toast_remove_favorites), Toast.LENGTH_SHORT).show();
            mAddToFavorites.setImageResource(R.drawable.ic_star);
            mAddToFavorites.setOnClickListener(addToFavorites);
            mPresenter.deleteStationFromFavorites(mStationId);
        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mStationId = getArguments().getString(STATION_KEY);
        }


    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_player, container, false);
        mErrorView = v.findViewById(R.id.fr_error_view);
        mPlayerView = v.findViewById(R.id.player_view);

        mStationLabel = v.findViewById(R.id.iv_station);
        mStationName = v.findViewById(R.id.tv_name);

        mAddToFavorites = v.findViewById(R.id.iv_add_to_favorites);
        mAddToFavorites.setOnClickListener(addToFavorites);

        mPlayPauseButton = v.findViewById(R.id.btn_play_stop);
        mPlayPauseButton.setOnClickListener(playButtonListener);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mPresenter = new PlayerPresenter(this);
        mPresenter.isAddedInDatabase(mStationId);
        //getStationMock();

        initService();

        //todo сравнить станцию которая играет (или ее отсутствие) и станцию на которую мы только перешли ->
        // отобразить/показать play/stop -> начать проигрывать новую станцию/сделать паузу в текущей

    }

    private void getStationMock() {
        mStreamResource = "http://http-live.sr.se/p3-mp3-192";
    }


    private void initService() {

        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder serviceBinder) {
                mBinder = (AudioPlayerService.AudioPlayerBinder) serviceBinder;
                mPresenter.getStation(mStationId);

                    try {
                        mMediaController = new MediaControllerCompat(getActivity(), mBinder.getMediaSessionToken());
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

        getActivity().bindService(new Intent(getActivity(), AudioPlayerService.class), mServiceConnection, BIND_AUTO_CREATE);


    }


    @Override
    public void isAddedInDatabase(boolean isAdded) {
        if (isAdded) {
            mAddToFavorites.setImageResource(R.drawable.ic_star_filling);
            mAddToFavorites.setOnClickListener(removeFromFavorites);
        } else {
            mAddToFavorites.setImageResource(R.drawable.ic_star);
            mAddToFavorites.setOnClickListener(addToFavorites);
        }
    }


    @Override
    public void showStation(Station station) {
        mStyle = station.getCategoriesBean().get(0).getTitle();
        mUrl = station.getImage().getUrl();
        if (mUrl != null) {
            Picasso.get().load(mUrl).into(mStationLabel);
        } else {
            mStationLabel.setImageResource(R.drawable.radio);
        }
        mStationName.setText(station.getName());
        mStreamResource = station.getStreamBeans().get(0).getStreamResource();

        mBinder.setStreamResources(mStreamResource);

    }

    @Override
    public RecentStation getRecentStation() {
        return new RecentStation(0, Integer.valueOf(mStationId), mStationName.getText().toString(), mUrl, mStyle);
    }

    @Override
    public FavoriteStation getFavoriteStation() {
        return new FavoriteStation(Integer.valueOf(mStationId), mStationName.getText().toString(), mUrl, mStyle);
    }


    @Override
    public void showError() {
        mErrorView.setVisibility(View.VISIBLE);
        mPlayerView.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBinder = null;
        if (mMediaController != null) {
            mMediaController.unregisterCallback(mMediaCallback);
            mMediaController = null;
        }
        getActivity().unbindService(mServiceConnection);

    }
}

