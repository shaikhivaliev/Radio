package com.elegion.radio.ui.player;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.elegion.radio.R;
import com.elegion.radio.entity.Station;
import com.elegion.radio.model.storage.Storage;
import com.elegion.radio.presentation.player.PlayerPresenter;
import com.elegion.radio.presentation.player.PlayerView;
import com.squareup.picasso.Picasso;


public class PlayerFragment extends Fragment implements
        PlayerView {

    public static final String SERVICE_FRAGMENT = "SERVICE_FRAGMENT";
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

    private Storage mStorage;
    private PlayerPresenter mPresenter;


    public static PlayerFragment newInstance(Bundle args) {
        PlayerFragment fragment = new PlayerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mStorage = context instanceof Storage.StorageOwner ? ((Storage.StorageOwner) context).obtainStorage() : null;
    }

    View.OnClickListener playButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PlayerFragment.this.showPauseButton();
            Log.d(SERVICE_FRAGMENT, "Play");
            mPresenter.playRadio();
        }
    };

    View.OnClickListener pauseButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PlayerFragment.this.showPlayButton();
            Log.d(SERVICE_FRAGMENT, "Pause");
            mPresenter.pauseRadio();
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

        mPresenter = new PlayerPresenter(this, mStorage);
        isAddedInDatabase();
        Log.d(SERVICE_FRAGMENT, "Запрос к API, поиск станции по id - " + mStationId);
        mPresenter.getStation(mStationId);

    }


    @Override
    public void isAddedInDatabase() {
        if (mPresenter.isAddedInDatabase(mStationId)) {
            mAddToFavorites.setImageResource(R.drawable.ic_star_filling);
            mAddToFavorites.setOnClickListener(removeFromFavorites);
        } else {
            mAddToFavorites.setImageResource(R.drawable.ic_star);
            mAddToFavorites.setOnClickListener(addToFavorites);
        }
    }


    @Override
    public void showPlayButton() {
        mPlayPauseButton.setImageResource(R.drawable.ic_play);
        mPlayPauseButton.setOnClickListener(playButtonListener);
    }

    @Override
    public void showPauseButton() {
        mPlayPauseButton.setImageResource(R.drawable.ic_pause);
        mPlayPauseButton.setOnClickListener(pauseButtonListener);
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

        //FIXME
        Log.d(SERVICE_FRAGMENT, "Получили URL - " + mStreamResource);
        mPresenter.startAudioService(mStreamResource);

    }


    @Override
    public void showError() {
        mErrorView.setVisibility(View.VISIBLE);
        mPlayerView.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(SERVICE_FRAGMENT, "onDestroy: ");
        mPresenter.stopAudioService();
    }
}

