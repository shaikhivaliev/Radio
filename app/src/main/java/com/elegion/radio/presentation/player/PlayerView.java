package com.elegion.radio.presentation.player;

import com.elegion.radio.data.storage.FavoriteStation;
import com.elegion.radio.data.storage.RecentStation;
import com.elegion.radio.entity.Station;

public interface PlayerView {


    void showStation(Station station);

    void showError();

    RecentStation getRecentStation();

    FavoriteStation getFavoriteStation();

    void isAddedInDatabase(boolean isAdded);

}
