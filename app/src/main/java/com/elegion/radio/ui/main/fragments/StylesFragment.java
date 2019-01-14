package com.elegion.radio.ui.main.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elegion.radio.R;
import com.elegion.radio.common.ContainerActivity;
import com.elegion.radio.common.RecyclerViewAdapter;
import com.elegion.radio.model.Style;
import com.elegion.radio.ui.player.PlayerFragment;
import com.elegion.radio.utils.JsonUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.List;

public class StylesFragment extends Fragment implements
        RecyclerViewAdapter.OnItemClickListener {

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mRecyclerViewAdapter;
    private List<Object> mStyleArrayList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mStyleArrayList = getStyles();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_styles, container, false);

        mRecyclerView = v.findViewById(R.id.rv_styles_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerViewAdapter = new RecyclerViewAdapter(mStyleArrayList, this);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        return v;
    }

    private List<Object> getStyles() throws IOException {

        Gson gson = new Gson();
        Type type = new TypeToken<List<Style>>() {
        }.getType();
        List<Object> styles = gson.fromJson(
                JsonUtils.loadJSONFromAsset(getActivity(), "styles.json"), type);

        return styles;
    }

    @Override
    public void onItemClick(String subStyleId) {

        Bundle args = new Bundle();
        args.putString(SubStyleFragment.KEY, subStyleId);

        ((ContainerActivity) getActivity()).changeFragment(SubStyleFragment.newInstance(args));
    }

}
