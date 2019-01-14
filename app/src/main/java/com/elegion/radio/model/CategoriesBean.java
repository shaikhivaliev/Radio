package com.elegion.radio.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CategoriesBean implements Serializable {

    @SerializedName("title")
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
