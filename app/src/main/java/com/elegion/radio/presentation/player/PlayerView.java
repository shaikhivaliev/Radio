package com.elegion.radio.presentation.player;

import com.elegion.radio.entity.FavoriteStation;
import com.elegion.radio.entity.RecentStation;
import com.elegion.radio.entity.Station;

public interface PlayerView {


    void showPlayButton();

    void showPauseButton();

    void showStation(Station station);

    void showError();

    RecentStation getRecentStation();

    FavoriteStation getFavoriteStation();

    void isAddedInDatabase(boolean isAdded);

}
