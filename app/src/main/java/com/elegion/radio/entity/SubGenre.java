package com.elegion.radio.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SubGenre implements Serializable {


    @SerializedName("id")
    private String mId;

    @SerializedName("title")
    private String mTitle;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

}
