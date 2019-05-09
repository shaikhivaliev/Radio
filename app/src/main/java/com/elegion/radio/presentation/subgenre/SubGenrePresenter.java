package com.elegion.radio.presentation.subgenre;

import com.elegion.radio.AppDelegate;
import com.elegion.radio.entity.Genre;
import com.elegion.radio.entity.SubGenre;
import com.elegion.radio.JsonUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class SubGenrePresenter {

    private SubGenreView mView;

    public SubGenrePresenter(SubGenreView view) {
        mView = view;
    }

    public void getSubGenre(String styleCode) {

        Gson gson = new Gson();

        Type type = new TypeToken<List<Genre>>() {
        }.getType();
        try {
            List<Genre> genres = gson.fromJson(JsonUtils.loadJSONFromAsset(AppDelegate.getInstance(), "styles.json"), type);
            List<SubGenre> subGenres = genres.get(Integer.valueOf(styleCode) - 1).getSubGenres();
            mView.showSubGenres(subGenres);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
