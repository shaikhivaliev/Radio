package com.elegion.radio.ui.stationsList;

import com.arellomobile.mvp.InjectViewState;
import com.elegion.radio.common.BasePresenter;
import com.elegion.radio.utils.ApiUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class StationsPresenter extends BasePresenter<StationsView> {

    public void getStationsByCountry(String country) {

        mCompositeDisposable.add(ApiUtils.getApiService().getStationsByCountry(country, 1, 20)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> getViewState().showStations(response),
                        throwable -> getViewState().showError()
                ));
    }

    public void getStationsByStyle(String style) {

        mCompositeDisposable.add(ApiUtils.getApiService().getStationsByStyle(style, 1, 20)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> getViewState().showStations(response),
                        throwable -> getViewState().showError()
                ));
    }

    public void getStationsBySearch(String query) {

        mCompositeDisposable.add(ApiUtils.getApiService().getStationsBySearch(query, 1, 20)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> getViewState().showStations(response),
                        throwable -> getViewState().showError()
                ));
    }


    public void openPlayerFragment(String stationId) {
        getViewState().openPlayerFragment(stationId);
    }

}
