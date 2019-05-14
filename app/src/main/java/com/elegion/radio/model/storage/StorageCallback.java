package com.elegion.radio.model.storage;

import com.elegion.radio.entity.FavoriteStation;
import com.elegion.radio.entity.RecentStation;

import java.util.List;


public interface StorageCallback {

    interface Favorites {

        void showFavoritesStation(List<FavoriteStation> favoriteStations);

        void showMock();
    }

    interface Recently {

        void showRecentlyStation(List<RecentStation> recentStations);

        void showMock();
    }

    interface FavoritesImage {

        void showFavoritesImage();

        void showFavoritesImageMock();

    }
}
