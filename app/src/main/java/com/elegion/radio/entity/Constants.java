package com.elegion.radio.entity;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.elegion.radio.entity.Constants.ViewType.COUNTRY;
import static com.elegion.radio.entity.Constants.ViewType.FAVORITES;
import static com.elegion.radio.entity.Constants.ViewType.RECENTLY;
import static com.elegion.radio.entity.Constants.ViewType.STATION;
import static com.elegion.radio.entity.Constants.ViewType.STYLE;
import static com.elegion.radio.entity.Constants.ViewType.SUBSTYLE;

public class Constants {

    @IntDef({COUNTRY, STATION, STYLE, SUBSTYLE, RECENTLY, FAVORITES})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ViewType {
        int COUNTRY = 100;
        int STATION = 200;
        int STYLE = 300;
        int SUBSTYLE = 400;
        int RECENTLY = 500;
        int FAVORITES = 600;
    }

}
