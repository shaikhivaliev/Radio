package com.elegion.radio.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
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
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        mTabLayout = v.findViewById(R.id.tab_layout);
        mViewPager = v.findViewById(R.id.view_pager);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        PageAdapter pageAdapter = new PageAdapter(getChildFragmentManager());
        mViewPager.setAdapter(pageAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        pageAdapter.notifyDataSetChanged();

    }
}
