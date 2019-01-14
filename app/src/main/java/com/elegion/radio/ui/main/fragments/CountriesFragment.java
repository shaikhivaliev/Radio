package com.elegion.radio.ui.main.fragments;

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

import com.elegion.radio.R;
import com.elegion.radio.common.RecyclerViewAdapter;
import com.elegion.radio.model.Country;
import com.elegion.radio.ui.stationsList.StationListFragment;
import com.elegion.radio.utils.JsonUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CountriesFragment extends Fragment implements
        RecyclerViewAdapter.OnItemClickListener {

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mRecyclerViewAdapter;
    private List<Object> mSampleArrayList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mSampleArrayList = getCountries();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_countries, container, false);

        mRecyclerView = v.findViewById(R.id.rv_countries);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerViewAdapter = new RecyclerViewAdapter(mSampleArrayList, this);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        return v;
    }

    private List<Object> getCountries() throws IOException {

        Gson gson = new Gson();
        Type type = new TypeToken<List<Country>>() {
        }.getType();

        List<Country> countries = gson.fromJson(
                JsonUtils.loadJSONFromAsset(getActivity(), "countries.json"), type);

        Collections.sort(countries, (o1, o2) -> o1.getCountry().compareToIgnoreCase(o2.getCountry()));

        return new ArrayList<>(countries);
    }

    @Override
    public void onItemClick(String countryCode) {


        Bundle args = new Bundle();
        args.putString(StationListFragment.COUNTRY_CODE_KEY, countryCode);


        Fragment fragment = StationListFragment.newInstance(args);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }
}
