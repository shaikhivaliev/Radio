package com.elegion.radio.data.storage;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "recently_stations")
public class RecentStation {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private int id;

    @ColumnInfo(name = "id_recent")
    private int stationId;

    @ColumnInfo(name = "name_recent")
    private String mStationName;

    @ColumnInfo(name = "image_station_recent")
    private String url;

    @ColumnInfo(name = "style_recent")
    private String title;

    public RecentStation(int id, int stationId, String stationName, String url, String title) {
        this.id = id;
        this.stationId = stationId;
        mStationName = stationName;
        this.url = url;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStationId() {
        return stationId;
    }

    public void setStationId(int stationId) {
        this.stationId = stationId;
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

    public void setTitle(String title) {
        this.title = title;
    }
}
