package com.elegion.radio.data.storage;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.elegion.radio.entity.FavoriteStation;
import com.elegion.radio.entity.RecentStation;

@Database(entities = {FavoriteStation.class, RecentStation.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract StationDao getStationDao();
}
