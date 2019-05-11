package com.elegion.radio.ui.favorites;

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
import android.widget.TextView;

import com.elegion.radio.OnChangeFragment;
import com.elegion.radio.OnItemClickListener;
import com.elegion.radio.R;
import com.elegion.radio.entity.FavoriteStation;
import com.elegion.radio.presentation.favorites.FavoritesPresenter;
import com.elegion.radio.presentation.favorites.FavoritesView;
import com.elegion.radio.ui.player.PlayerFragment;

import java.util.List;

public class FavoritesFragment extends Fragment implements
        OnItemClickListener,
        FavoritesView {

    private RecyclerView mRecyclerView;
    private FavoriteAdapter mAdapter;
    private TextView mFavoritesMock;

    private OnChangeFragment mChangeFragment;
    private FavoritesPresenter mPresenter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mChangeFragment = (OnChangeFragment) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_favorites, container, false);
        mFavoritesMock = v.findViewById(R.id.tv_favorites_mock);
        mRecyclerView = v.findViewById(R.id.recycler_favorites);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new FavoriteAdapter();
        mAdapter.setListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);

        mPresenter = new FavoritesPresenter(this);
        mPresenter.getFavoritesStation();

    }


    @Override
    public void showFavoritesStation(List<FavoriteStation> favoriteStations) {
        mFavoritesMock.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mAdapter.addData(favoriteStations);

    }

    @Override
    public void showMock() {
        mFavoritesMock.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(String id) {
        Bundle args = new Bundle();
        args.putString(PlayerFragment.STATION_KEY, id);
        mChangeFragment.changeFragmentCallback(PlayerFragment.newInstance(args));
    }

}
