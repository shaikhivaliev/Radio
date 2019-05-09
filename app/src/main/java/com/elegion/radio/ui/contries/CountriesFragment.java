package com.elegion.radio.ui.contries;

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
import com.elegion.radio.entity.Country;
import com.elegion.radio.presentation.countries.CountryPresenter;
import com.elegion.radio.presentation.countries.CountryView;
import com.elegion.radio.ui.stations.StationsFragment;

import java.util.List;

public class CountriesFragment extends Fragment implements
        OnItemClickListener,
        CountryView {

    private RecyclerView mRecyclerView;
    private CountriesAdapter mAdapter;

    private OnChangeFragment mChangeFragment;
    private CountryPresenter mPresenter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mChangeFragment = (OnChangeFragment) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_countries, container, false);
        mRecyclerView = v.findViewById(R.id.rv_countries);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new CountriesAdapter();
        mAdapter.setListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);

        mPresenter = new CountryPresenter(this);
        mPresenter.getCountries();
    }

    @Override
    public void onItemClick(String countryCode) {

        Bundle args = new Bundle();
        args.putString(StationsFragment.COUNTRY_CODE_KEY, countryCode);
        mChangeFragment.changeFragmentCallback(StationsFragment.newInstance(args));

    }

    @Override
    public void showCountries(List<Country> countries) {
        mAdapter.addData(countries);
    }
}
