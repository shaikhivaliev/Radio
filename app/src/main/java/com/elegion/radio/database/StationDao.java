package com.elegion.radio.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface StationDao {

    /*------Favorites--------*/

    @Query("SELECT * FROM favorites_stations")
    Single<List<FavoriteStation>> getFavoritesStations();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertStationToFavorites(FavoriteStation favoriteStation);

    @Query("DELETE FROM favorites_stations WHERE id_favorite = :id")
    void deleteStationFromFavorites(int id);

    @Query("SELECT * FROM favorites_stations WHERE id_favorite = :id")
    Single<FavoriteStation> getFavoriteStationById(int id);


    /*------Recently--------*/

    @Query("SELECT * FROM recently_stations ORDER BY _id DESC")
    Single<List<RecentStation>> getRecentlyStations();

    @Query("DELETE FROM recently_stations WHERE _id =(SELECT _id FROM recently_stations ORDER BY _id  LIMIT 1 )")
    void deleteStationFromRecently();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertStationToRecently(RecentStation recentStation);

}
