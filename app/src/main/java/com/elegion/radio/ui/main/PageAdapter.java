package com.elegion.radio.ui.main;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.elegion.radio.ui.main.fragments.CountriesFragment;
import com.elegion.radio.ui.main.fragments.FavoritesFragment;
import com.elegion.radio.ui.main.fragments.RecentlyFragment;
import com.elegion.radio.ui.main.fragments.StylesFragment;

public class PageAdapter extends FragmentPagerAdapter {

    private final int NUM_TABS = 4;
    private String[] mTabTitles = new String[]{"You're lucky!", "Countries", "Styles", "Favorites"};


    public PageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new RecentlyFragment();
            case 1:
                return new CountriesFragment();
            case 2:
                return new StylesFragment();
            case 3:
                return new FavoritesFragment();

            default:
                return null;
        }
    }


    @Override
    public int getCount() {
        return NUM_TABS;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mTabTitles[position];
    }

}
