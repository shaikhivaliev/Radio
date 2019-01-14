package com.elegion.radio.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Station implements Serializable {


    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("image")
    private ImageBean image;

    @SerializedName("categories")
    private List<CategoriesBean> mCategoriesBean;

    @SerializedName("streams")
    private List<StreamBean> mStreamBeans;


    public Station(String id, String name, ImageBean image, List<CategoriesBean> categoriesBean) {
        this.id = id;
        this.name = name;
        this.image = image;
        mCategoriesBean = categoriesBean;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ImageBean getImage() {
        return image;
    }

    public void setImage(ImageBean image) {
        this.image = image;
    }

    public List<CategoriesBean> getCategoriesBean() {
        return mCategoriesBean;
    }

    public void setCategoriesBean(List<CategoriesBean> categoriesBean) {
        mCategoriesBean = categoriesBean;
    }

    public List<StreamBean> getStreamBeans() {
        return mStreamBeans;
    }

    public void setStreamBeans(List<StreamBean> streamBeans) {
        mStreamBeans = streamBeans;
    }


    public Station(String id, String name, ImageBean image, List<CategoriesBean> categoriesBean, List<StreamBean> streamBeans) {
        this.id = id;
        this.name = name;
        this.image = image;
        mCategoriesBean = categoriesBean;
        mStreamBeans = streamBeans;
    }
}
