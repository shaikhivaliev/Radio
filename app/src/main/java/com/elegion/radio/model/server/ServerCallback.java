package com.elegion.radio.model.server;

import com.elegion.radio.entity.Station;

import java.util.List;

public interface ServerCallback {

    void showError();

    void showStations(List<Station> stations);


}
