package com.elegion.radio;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.elegion.radio.model.storage.AppDatabase;

public class AppDelegate extends Application {

    public static AppDelegate instance;
    private AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        database = Room.databaseBuilder(this, AppDatabase.class, "database")
                .build();
    }

    public static AppDelegate getInstance() {
        return instance;
    }

    public AppDatabase getDatabase() {
        return database;
    }
}
