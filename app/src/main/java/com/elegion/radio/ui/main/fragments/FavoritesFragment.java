package com.elegion.radio.ui.main.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.elegion.radio.AppDelegate;
import com.elegion.radio.R;
import com.elegion.radio.common.ContainerActivity;
import com.elegion.radio.common.RecyclerViewAdapter;
import com.elegion.radio.database.AppDatabase;
import com.elegion.radio.database.FavoriteStation;
import com.elegion.radio.database.StationDao;
import com.elegion.radio.ui.player.PlayerFragment;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class FavoritesFragment extends Fragment implements
        RecyclerViewAdapter.OnItemClickListener {

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mRecyclerViewAdapter;
    private TextView mFavoritesMock;
    private AppDatabase mDatabase;
    private StationDao mStationDao;

    List<Object> mSampleArrayList = new ArrayList<>();
    private int mFavoritesListSize = -1;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = AppDelegate.getInstance().getDatabase();
        mStationDao = mDatabase.getStationDao();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_favorites, container, false);

        mFavoritesMock = v.findViewById(R.id.tv_favorites_mock);
        mRecyclerView = v.findViewById(R.id.recycler_favorites);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getStationsFromRoom();
    }

    @SuppressLint("CheckResult")
    private void getStationsFromRoom() {

        mStationDao.getFavoritesStations()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        mRecyclerViewAdapter = new RecyclerViewAdapter(mSampleArrayList, FavoritesFragment.this);
                        mRecyclerView.setAdapter(mRecyclerViewAdapter);
                    }
                })
                .subscribe(new Consumer<List<FavoriteStation>>() {

                    @Override
                    public void accept(List<FavoriteStation> favoriteStations) throws Exception {

                        if (!favoriteStations.isEmpty()) {

                            mFavoritesMock.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.VISIBLE);

                            if (mFavoritesListSize != favoriteStations.size()) {
                                mSampleArrayList.clear();
                                mSampleArrayList.addAll(favoriteStations);
                            }

                            mFavoritesListSize = favoriteStations.size();

                        } else {
                            mFavoritesMock.setVisibility(View.VISIBLE);
                            mRecyclerView.setVisibility(View.GONE);
                        }
                    }
                });
    }


    @Override
    public void onItemClick(String id) {
        Bundle args = new Bundle();
        args.putString(PlayerFragment.STATION_KEY, id);

        ((ContainerActivity) getActivity()).changeFragment(PlayerFragment.newInstance(args));
    }
}
