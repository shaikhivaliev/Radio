package com.elegion.radio.presentation.stations;

import com.elegion.radio.model.server.ServerCallback;
import com.elegion.radio.model.server.ServerData;
import com.elegion.radio.entity.Station;

import java.util.List;

public class StationsPresenter implements ServerCallback {

    private StationsView mView;
    private ServerData mServerData = new ServerData();

    public StationsPresenter(StationsView stationsView) {
        mView = stationsView;
        mServerData.setServerCallback(this);
    }


    public void getStationsByCountry(String country) {
        mServerData.getStationsByCountry(country);
    }

    public void getStationsByStyle(String style) {
        mServerData.getStationsByStyle(style);
    }

    public void getStationsBySearch(String query) {
        mServerData.getStationsBySearch(query);
    }

    @Override
    public void showStations(List<Station> stations) {
        mView.showStations(stations);
    }

    @Override
    public void showError() {
        mView.showError();
    }
}
