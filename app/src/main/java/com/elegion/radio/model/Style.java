package com.elegion.radio.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Style implements Serializable {


    @SerializedName("id")
    private String mId;

    @SerializedName("title")
    private String mTitle;

    @SerializedName("children")
    private List<SubStyle> mSubStyles;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public Style(String title) {
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public List<SubStyle> getSubStyles() {
        return mSubStyles;
    }

    public void setSubStyles(List<SubStyle> subStyles) {
        mSubStyles = subStyles;
    }


}
