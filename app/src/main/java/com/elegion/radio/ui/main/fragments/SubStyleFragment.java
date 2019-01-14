package com.elegion.radio.ui.main.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elegion.radio.R;
import com.elegion.radio.common.ContainerActivity;
import com.elegion.radio.common.RecyclerViewAdapter;
import com.elegion.radio.model.Style;
import com.elegion.radio.model.SubStyle;
import com.elegion.radio.ui.stationsList.StationListFragment;
import com.elegion.radio.utils.JsonUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SubStyleFragment extends Fragment implements RecyclerViewAdapter.OnItemClickListener {

    public static final String KEY = "KEY";

    private String mStyleCode;

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mRecyclerViewAdapter;
    private List<Object> mSubStyleArrayList;


    public static SubStyleFragment newInstance(Bundle args) {
        SubStyleFragment fragment = new SubStyleFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mStyleCode = getArguments().getString(KEY);
        }

        try {
            mSubStyleArrayList = getSubStyle();
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
        mRecyclerViewAdapter = new RecyclerViewAdapter(mSubStyleArrayList, this);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        return v;

    }


    private List<Object> getSubStyle() throws IOException {

        Gson gson = new Gson();

        Type type = new TypeToken<List<Style>>() {
        }.getType();
        List<Style> styles = gson.fromJson(
                JsonUtils.loadJSONFromAsset(getActivity(), "styles.json"), type);

        List<SubStyle> subStyles = styles.get(Integer.valueOf(mStyleCode) - 1).getSubStyles();

        return new ArrayList<>(subStyles);

    }

    @Override
    public void onItemClick(String styleId) {
        Bundle args = new Bundle();
        args.putString(StationListFragment.STYLE_CODE_KEY, styleId);

        ((ContainerActivity) getActivity()).changeFragment(StationListFragment.newInstance(args));
    }

}
