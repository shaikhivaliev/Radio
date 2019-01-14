package com.elegion.radio.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Country implements Serializable {

    @SerializedName("name")
    private String mCountry;

    @SerializedName("country_code")
    private String mCountryCode;

    public Country(String country, String countryCode) {
        mCountry = country;
        mCountryCode = countryCode;
    }

    public String getCountryCode() {
        return mCountryCode;
    }

    public void setCountryCode(String countryCode) {
        mCountryCode = countryCode;
    }

    public String getCountry() {
        return mCountry;
    }

    public void setCountry(String country) {
        mCountry = country;
    }
}
