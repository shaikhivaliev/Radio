package com.elegion.radio.database;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {FavoriteStation.class, RecentStation.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract StationDao getStationDao();
}
