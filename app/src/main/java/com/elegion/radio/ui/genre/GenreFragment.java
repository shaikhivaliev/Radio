package com.elegion.radio.ui.genre;

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
import com.elegion.radio.entity.Genre;
import com.elegion.radio.presentation.genre.GenrePresenter;
import com.elegion.radio.presentation.genre.GenreView;
import com.elegion.radio.ui.subgenre.SubGenreFragment;

import java.util.List;

public class GenreFragment extends Fragment implements
        OnItemClickListener,
        GenreView {

    private RecyclerView mRecyclerView;
    private GenreAdapter mAdapter;

    private OnChangeFragment mChangeFragment;
    private GenrePresenter mPresenter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mChangeFragment = (OnChangeFragment) context;
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

        mAdapter = new GenreAdapter();
        mAdapter.setListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);

        mPresenter = new GenrePresenter(this);
        mPresenter.getStyles();

    }

    @Override
    public void onItemClick(String subStyleId) {
        Bundle args = new Bundle();
        args.putString(SubGenreFragment.KEY, subStyleId);
        mChangeFragment.changeFragmentCallback(SubGenreFragment.newInstance(args));
    }

    @Override
    public void showGenres(List<Genre> genres) {
        mAdapter.addData(genres);
    }
}
