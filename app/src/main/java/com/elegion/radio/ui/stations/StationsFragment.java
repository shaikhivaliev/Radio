package com.elegion.radio.ui.stations;

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

import com.elegion.radio.OnChangeFragment;
import com.elegion.radio.OnItemClickListener;
import com.elegion.radio.R;
import com.elegion.radio.entity.Station;
import com.elegion.radio.model.server.ServerData;
import com.elegion.radio.model.storage.StorageData;
import com.elegion.radio.presentation.stations.StationsPresenter;
import com.elegion.radio.presentation.stations.StationsView;
import com.elegion.radio.ui.player.PlayerFragment;

import java.util.List;


public class StationsFragment extends Fragment implements
        OnItemClickListener,
        StationsView {

    public static final String COUNTRY_CODE_KEY = "COUNTRY_CODE_KEY";
    public static final String STYLE_CODE_KEY = "STYLE_CODE_KEY";
    public static final String SEARCH_QUERY = "SEARCH_QUERY";

    private String mCountryCode;
    private String mStyleCode;
    private String mSearchQuery;

    private View mErrorView;

    private RecyclerView mRecyclerView;
    private StationsAdapter mAdapter;

    private OnChangeFragment mChangeFragment;
    private StationsPresenter mPresenter;

    public static StationsFragment newInstance(Bundle args) {
        StationsFragment fragment = new StationsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mChangeFragment = (OnChangeFragment) context;

        if (getArguments() != null) {
            mCountryCode = getArguments().getString(COUNTRY_CODE_KEY);
            mStyleCode = getArguments().getString(STYLE_CODE_KEY);
            mSearchQuery = getArguments().getString(SEARCH_QUERY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stations, container, false);
        mErrorView = v.findViewById(R.id.fr_error_view);
        mRecyclerView = v.findViewById(R.id.rv_stations_list);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new StationsAdapter();
        mAdapter.setListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);

        mPresenter = new StationsPresenter(this);

        if (mCountryCode != null) {
            mPresenter.getStationsByCountry(mCountryCode);

        } else if (mStyleCode != null) {
            mPresenter.getStationsByStyle(mStyleCode);

        } else if (mSearchQuery != null) {
            mPresenter.getStationsBySearch(mSearchQuery);
        }

    }

    @Override
    public void onItemClick(String stationId) {
        Bundle args = new Bundle();
        args.putString(PlayerFragment.STATION_KEY, stationId);
        mChangeFragment.changeFragmentCallback(PlayerFragment.newInstance(args));
    }

    @Override
    public void showStations(List<Station> stations) {
        mAdapter.addData(stations);
    }

    @Override
    public void showError() {
        mRecyclerView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.VISIBLE);
    }


}
