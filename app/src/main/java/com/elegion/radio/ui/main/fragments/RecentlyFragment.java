package com.elegion.radio.ui.main.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.elegion.radio.AppDelegate;
import com.elegion.radio.R;
import com.elegion.radio.common.ContainerActivity;
import com.elegion.radio.common.RecyclerViewAdapter;
import com.elegion.radio.database.AppDatabase;
import com.elegion.radio.database.RecentStation;
import com.elegion.radio.database.StationDao;
import com.elegion.radio.ui.player.PlayerFragment;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class RecentlyFragment extends Fragment implements
        RecyclerViewAdapter.OnItemClickListener {

    private Button mGetRandomStation;


    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mRecyclerViewAdapter;
    private TextView mRecentStationMock;
    private TextView mRecentStationHeader;
    private AppDatabase mDatabase;
    private StationDao mStationDao;
    List<Object> mSampleArrayList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = AppDelegate.getInstance().getDatabase();
        mStationDao = mDatabase.getStationDao();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_recently, container, false);

        mGetRandomStation = v.findViewById(R.id.btn_get_random_station);
        mGetRandomStation.setOnClickListener(v1 -> {
            //todo гененрировать случайный id, обработать ситуацию когда такого id нет
            int id = 727;

            Bundle args = new Bundle();
            args.putString(PlayerFragment.STATION_KEY, String.valueOf(id));

            ((ContainerActivity) getActivity()).changeFragment(PlayerFragment.newInstance(args));

        });

        mRecentStationMock = v.findViewById(R.id.tv_recent_mock);
        mRecentStationHeader = v.findViewById(R.id.tv_recently_stations);
        mRecyclerView = v.findViewById(R.id.recycler_recently_station);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getRecentlyStationsFromRoom();
    }

    @SuppressLint("CheckResult")
    private void getRecentlyStationsFromRoom() {

        mStationDao.getRecentlyStations()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {
                    mRecyclerViewAdapter = new RecyclerViewAdapter(mSampleArrayList, RecentlyFragment.this);
                    mRecyclerView.setAdapter(mRecyclerViewAdapter);
                })
                .subscribe(recentlyStations -> {

                    if (!recentlyStations.isEmpty()) {
                        mSampleArrayList.clear();
                        mSampleArrayList.addAll(recentlyStations);
                        mRecentStationHeader.setVisibility(View.VISIBLE);
                        mRecentStationMock.setVisibility(View.GONE);
                    } else {
                        mRecentStationMock.setVisibility(View.VISIBLE);
                        mRecentStationHeader.setVisibility(View.GONE);
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
