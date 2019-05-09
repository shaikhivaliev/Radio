package com.elegion.radio.presentation.favorites;

import com.elegion.radio.data.storage.FavoriteStation;

import java.util.List;

public interface FavoritesView {

    void showFavoritesStation(List<FavoriteStation> favoriteStations);
    void showMock();
}
