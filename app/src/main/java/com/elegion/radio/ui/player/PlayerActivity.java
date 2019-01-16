package com.elegion.radio.ui.player;

import android.os.Bundle;

import com.elegion.radio.common.ContainerActivity;

public class PlayerActivity extends ContainerActivity {

    public static final String STATION_KEY = "USERNAME_KEY";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeFragment(PlayerFragment.newInstance(getIntent().getBundleExtra(STATION_KEY)));
    }

}
