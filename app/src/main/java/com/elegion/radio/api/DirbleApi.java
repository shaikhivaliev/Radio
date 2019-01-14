package com.elegion.radio.api;

import com.elegion.radio.model.Station;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DirbleApi {

    @GET("/v2/countries/{id}/stations")
    Single<List<Station>> getStationsByCountry(
            @Path("id") String id,
            @Query("page") int page,
            @Query("per_page") int perPage);

    @GET("/v2/category/{id}/stations")
    Single<List<Station>> getStationsByStyle(
            @Path("id") String id,
            @Query("page") int page,
            @Query("per_page") int perPage);

    @POST("/v2/search")
    @FormUrlEncoded
    Single<List<Station>> getStationsBySearch(
            @Field("query") String searchQuery,
            @Query("page") int page,
            @Query("per_page") int perPage);


    @GET("/v2/station/{id}")
    Single<Station> getStationById(@Path("id") String id);


}
