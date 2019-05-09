package com.elegion.radio.presentation.stations;

import com.elegion.radio.entity.Station;

import java.util.List;

public interface StationsView {

    void showError();

    void showStations(List<Station> stations);

}
