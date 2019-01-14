package com.elegion.radio.ui.stationsList;

import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.elegion.radio.common.BaseView;
import com.elegion.radio.model.Station;

import java.util.List;

public interface StationsView extends BaseView {

    void showStations(List<Station> stations);

    @StateStrategyType(SkipStrategy.class)
    void openPlayerFragment(String stationId);

}
