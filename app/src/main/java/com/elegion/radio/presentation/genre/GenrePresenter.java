package com.elegion.radio.presentation.genre;

import com.elegion.radio.AppDelegate;
import com.elegion.radio.entity.Genre;
import com.elegion.radio.JsonUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class GenrePresenter {

    private GenreView mView;

    public GenrePresenter(GenreView view) {
        mView = view;
    }

    public void getStyles() {

        Gson gson = new Gson();
        Type type = new TypeToken<List<Genre>>() {
        }.getType();
        try {
            List<Genre> genres = gson.fromJson(JsonUtils.loadJSONFromAsset(AppDelegate.getInstance(), "styles.json"), type);
            mView.showGenres(genres);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
