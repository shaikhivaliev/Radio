package com.elegion.radio.ui.stationsList;

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

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.elegion.radio.R;
import com.elegion.radio.common.ContainerActivity;
import com.elegion.radio.common.PresenterFragment;
import com.elegion.radio.common.RecyclerViewAdapter;
import com.elegion.radio.model.Station;
import com.elegion.radio.ui.player.PlayerFragment;

import java.util.ArrayList;
import java.util.List;


public class StationListFragment extends PresenterFragment implements
        RecyclerViewAdapter.OnItemClickListener,
        StationsView {

    public static final String COUNTRY_CODE_KEY = "COUNTRY_CODE_KEY";
    public static final String STYLE_CODE_KEY = "STYLE_CODE_KEY";
    public static final String SEARCH_QUERY = "SEARCH_QUERY";

    private String mCountryCode;
    private String mStyleCode;
    private String mSearchQuery;

    private View mErrorView;

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mRecyclerViewAdapter;
    private List<Object> mSampleArrayList = new ArrayList<>();

    @InjectPresenter
    StationsPresenter mPresenter;

    @ProvidePresenter
    StationsPresenter providePresenter() {
        return new StationsPresenter();
    }

    @Override
    protected StationsPresenter getPresenter() {
        return mPresenter;
    }

    public static StationListFragment newInstance(Bundle args) {
        StationListFragment fragment = new StationListFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_stations, container, false);

        if (getArguments() != null) {
            mCountryCode = getArguments().getString(COUNTRY_CODE_KEY);
            mStyleCode = getArguments().getString(STYLE_CODE_KEY);
            mSearchQuery = getArguments().getString(SEARCH_QUERY);
        }

        mErrorView = v.findViewById(R.id.fr_error_view);

        mRecyclerView = v.findViewById(R.id.rv_stations_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerViewAdapter = new RecyclerViewAdapter(mSampleArrayList, this);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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
        mPresenter.openPlayerFragment(stationId);
    }

    @Override
    public void showStations(List<Station> stations) {
        mSampleArrayList.addAll(stations);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
    }

    @Override
    public void openPlayerFragment(String stationId) {
        Bundle args = new Bundle();
        args.putString(PlayerFragment.STATION_KEY, stationId);

        ((ContainerActivity) getActivity()).changeFragment(PlayerFragment.newInstance(args));
    }

    @Override
    public void showError() {
        mRecyclerView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.VISIBLE);
    }
}
