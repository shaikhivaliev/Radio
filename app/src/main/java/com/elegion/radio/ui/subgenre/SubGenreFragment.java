package com.elegion.radio.ui.subgenre;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elegion.radio.OnChangeFragment;
import com.elegion.radio.OnItemClickListener;
import com.elegion.radio.R;
import com.elegion.radio.entity.SubGenre;
import com.elegion.radio.presentation.subgenre.SubGenrePresenter;
import com.elegion.radio.presentation.subgenre.SubGenreView;
import com.elegion.radio.ui.stations.StationsFragment;

import java.util.List;

public class SubGenreFragment extends Fragment implements
        OnItemClickListener,
        SubGenreView {

    public static final String KEY = "KEY";
    private String mStyleCode;

    private RecyclerView mRecyclerView;
    private SubGenreAdapter mAdapter;

    private OnChangeFragment mChangeFragment;
    private SubGenrePresenter mPresenter;


    public static SubGenreFragment newInstance(Bundle args) {
        SubGenreFragment fragment = new SubGenreFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mChangeFragment = (OnChangeFragment) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mStyleCode = getArguments().getString(KEY);
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_styles, container, false);
        mRecyclerView = v.findViewById(R.id.rv_styles_list);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new SubGenreAdapter();
        mAdapter.setListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);

        mPresenter = new SubGenrePresenter(this);
        mPresenter.getSubGenre(mStyleCode);

    }

    @Override
    public void showSubGenres(List<SubGenre> genres) {
        mAdapter.addData(genres);
    }

    @Override
    public void onItemClick(String styleId) {
        Bundle args = new Bundle();
        args.putString(StationsFragment.STYLE_CODE_KEY, styleId);
        mChangeFragment.changeFragmentCallback(StationsFragment.newInstance(args));
    }
}
