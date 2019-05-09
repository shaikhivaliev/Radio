package com.elegion.radio.presentation.recently;

import com.elegion.radio.data.storage.RecentStation;

import java.util.List;

public interface RecentlyView {

    void showRecentlyStation(List<RecentStation> recentStations);
    void showMock();
}
