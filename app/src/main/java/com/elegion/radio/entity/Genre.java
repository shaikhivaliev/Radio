package com.elegion.radio.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Genre implements Serializable {


    @SerializedName("id")
    private String mId;

    @SerializedName("title")
    private String mTitle;

    @SerializedName("children")
    private List<SubGenre> mSubGenres;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public Genre(String title) {
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public List<SubGenre> getSubGenres() {
        return mSubGenres;
    }

    public void setSubGenres(List<SubGenre> subGenres) {
        mSubGenres = subGenres;
    }


}
