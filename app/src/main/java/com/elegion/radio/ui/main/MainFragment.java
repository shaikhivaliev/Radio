package com.elegion.radio.ui.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elegion.radio.R;

public class MainFragment extends Fragment {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    public static MainFragment newInstance() {
        Bundle args = new Bundle();

        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_main, container, false);

        mTabLayout = v.findViewById(R.id.tab_layout);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setElevation(0);
        mViewPager = v.findViewById(R.id.view_pager);

        PageAdapter pageAdapter = new PageAdapter(getChildFragmentManager());
        mViewPager.setAdapter(pageAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        pageAdapter.notifyDataSetChanged();

        return v;
    }
}
