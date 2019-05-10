package com.elegion.radio.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StreamBean implements Serializable {

    @SerializedName("stream")
    private String streamResource;

    public StreamBean(String streamResource) {
        this.streamResource = streamResource;
    }

    public String getStreamResource() {
        return streamResource;
    }

    public void setStreamResource(String streamResource) {
        this.streamResource = streamResource;
    }
}
