package com.elegion.radio.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "favorites_stations")
public class FavoriteStation  {

    @PrimaryKey()
    @ColumnInfo(name = "id_favorite")
    private int id;

    @ColumnInfo(name = "name_favorite")
    private String mStationName;

    @ColumnInfo(name = "image_station_favorites")
    private String url;

    @ColumnInfo(name = "style_favorites")
    private String title;

    public FavoriteStation(int id, String stationName, String url, String title) {
        this.id = id;
        mStationName = stationName;
        this.url = url;
        this.title = title;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStationName() {
        return mStationName;
    }

    public void setStationName(String stationName) {
        mStationName = stationName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

}
