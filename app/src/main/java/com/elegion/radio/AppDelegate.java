package com.elegion.radio;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.elegion.radio.model.storage.AppDatabase;
import com.elegion.radio.model.storage.Storage;

public class AppDelegate extends Application {

    public static AppDelegate instance;
    private Storage mStorage;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        AppDatabase database = Room.databaseBuilder(this, AppDatabase.class, "database")
                .build();
        mStorage = new Storage(database.getStationDao());

    }

    public static AppDelegate getInstance() {
        return instance;
    }

    public Storage getStorage() {
        return mStorage;
    }
}
