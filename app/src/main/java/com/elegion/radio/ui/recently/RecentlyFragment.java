package com.elegion.radio.ui.recently;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.elegion.radio.OnChangeFragment;
import com.elegion.radio.OnItemClickListener;
import com.elegion.radio.R;
import com.elegion.radio.entity.RecentStation;
import com.elegion.radio.model.storage.Storage;
import com.elegion.radio.presentation.recently.RecentlyPresenter;
import com.elegion.radio.presentation.recently.RecentlyView;
import com.elegion.radio.ui.player.PlayerFragment;

import java.util.List;

public class RecentlyFragment extends Fragment implements
        OnItemClickListener,
        RecentlyView {

    private Button mGetRandomStation;
    private TextView mRecentStationMock;
    private TextView mRecentStationHeader;

    private RecyclerView mRecyclerView;
    private RecentlyAdapter mAdapter;

    private OnChangeFragment mChangeFragment;
    private Storage mStorage;
    private RecentlyPresenter mPresenter;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mChangeFragment = (OnChangeFragment) context;
        mStorage = context instanceof Storage.StorageOwner ? ((Storage.StorageOwner) context).obtainStorage() : null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recently, container, false);

        mGetRandomStation = v.findViewById(R.id.btn_get_random_station);
        mGetRandomStation.setOnClickListener(v1 -> {
            int id = 727;

            Bundle args = new Bundle();
            args.putString(PlayerFragment.STATION_KEY, String.valueOf(id));
            mChangeFragment.changeFragmentCallback(PlayerFragment.newInstance(args));

        });

        mRecentStationMock = v.findViewById(R.id.tv_recent_mock);
        mRecentStationHeader = v.findViewById(R.id.tv_recently_stations);
        mRecyclerView = v.findViewById(R.id.recycler_recently_station);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new RecentlyAdapter();
        mAdapter.setListener(this);
        mRecyclerView.setAdapter(mAdapter);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPresenter = new RecentlyPresenter(this, mStorage);
        mPresenter.getRecentlyStations();

    }

    @Override
    public void onItemClick(String id) {
        Bundle args = new Bundle();
        args.putString(PlayerFragment.STATION_KEY, id);
        mChangeFragment.changeFragmentCallback(PlayerFragment.newInstance(args));
    }

    @Override
    public void showRecentlyStation(List<RecentStation> stations) {
        mRecentStationHeader.setVisibility(View.VISIBLE);
        mRecentStationMock.setVisibility(View.GONE);
        mAdapter.addData(stations);
    }

    @Override
    public void showMock() {
        mRecentStationMock.setVisibility(View.VISIBLE);
        mRecentStationHeader.setVisibility(View.GONE);
    }
}
