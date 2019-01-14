package com.elegion.radio.common;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elegion.radio.R;
import com.elegion.radio.common.holders.CountriesHolder;
import com.elegion.radio.common.holders.FavoriteStationHolder;
import com.elegion.radio.common.holders.RecentlyStationHolder;
import com.elegion.radio.common.holders.StationHolder;
import com.elegion.radio.common.holders.StyleHolder;
import com.elegion.radio.common.holders.SubStyleHolder;
import com.elegion.radio.model.Country;
import com.elegion.radio.model.Station;
import com.elegion.radio.model.Style;
import com.elegion.radio.model.SubStyle;
import com.elegion.radio.database.FavoriteStation;
import com.elegion.radio.database.RecentStation;

import java.util.List;

import static com.elegion.radio.model.Constants.ViewType.COUNTRY;
import static com.elegion.radio.model.Constants.ViewType.FAVORITES;
import static com.elegion.radio.model.Constants.ViewType.RECENTLY;
import static com.elegion.radio.model.Constants.ViewType.STATION;
import static com.elegion.radio.model.Constants.ViewType.STYLE;
import static com.elegion.radio.model.Constants.ViewType.SUBSTYLE;

public class RecyclerViewAdapter extends RecyclerView.Adapter {

    private List<Object> mItems;
    private final OnItemClickListener mOnItemClickListener;

    public RecyclerViewAdapter(List<Object> items, OnItemClickListener onItemClickListener) {
        this.mItems = items;
        mOnItemClickListener = onItemClickListener;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case STATION:
                View station = layoutInflater.inflate(R.layout.holder_station, parent, false);
                viewHolder = new StationHolder(station);
                break;
            case COUNTRY:
                View country = layoutInflater.inflate(R.layout.holder_country, parent, false);
                viewHolder = new CountriesHolder(country);
                break;
            case STYLE:
                View style = layoutInflater.inflate(R.layout.holder_style, parent, false);
                viewHolder = new StyleHolder(style);
                break;
            case SUBSTYLE:
                View subStyle = layoutInflater.inflate(R.layout.holder_style, parent, false);
                viewHolder = new SubStyleHolder(subStyle);
                break;
            case RECENTLY:
                View recentlyStation = layoutInflater.inflate(R.layout.holder_station, parent, false);
                viewHolder = new RecentlyStationHolder(recentlyStation);
                break;
            case FAVORITES:
                View favoritesStation = layoutInflater.inflate(R.layout.holder_station, parent, false);
                viewHolder = new FavoriteStationHolder(favoritesStation);
                break;

            default:
                View v = layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                viewHolder = new StationHolder(v);
                break;
        }
        return viewHolder;
    }


    @Override
    @SuppressWarnings("unchecked")
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {
            case STATION:
                StationHolder stationHolder = (StationHolder) holder;
                configStationViewHolder(stationHolder, position);
                break;
            case COUNTRY:
                CountriesHolder countriesHolder = (CountriesHolder) holder;
                configCountryViewHolder(countriesHolder, position);
                break;
            case STYLE:
                StyleHolder styleHolder = (StyleHolder) holder;
                configStyleViewHolder(styleHolder, position);
                break;
            case SUBSTYLE:
                SubStyleHolder subStyleHolder = (SubStyleHolder) holder;
                configSubStyleViewHolder(subStyleHolder, position);
                break;
            case RECENTLY:
                RecentlyStationHolder recentlyHolder = (RecentlyStationHolder) holder;
                configRecentlyStationsViewHolder(recentlyHolder, position);
                break;
            case FAVORITES:
                FavoriteStationHolder favoritesHolder = (FavoriteStationHolder) holder;
                configFavoritesStationsViewHolder(favoritesHolder, position);
                break;

            default:
                break;
        }
    }


    @Override
    public int getItemCount() {
        return mItems != null ? mItems.size() : 0;
    }

    private void configStationViewHolder(StationHolder vh, int position) {
        Station station = (Station) mItems.get(position);
        vh.bind(station, mOnItemClickListener);
    }

    private void configCountryViewHolder(CountriesHolder vh, int position) {
        Country country = (Country) mItems.get(position);
        vh.bind(country, mOnItemClickListener);
    }

    private void configStyleViewHolder(StyleHolder vh, int position) {
        Style style = (Style) mItems.get(position);
        vh.bind(style, mOnItemClickListener);
    }

    private void configSubStyleViewHolder(SubStyleHolder vh, int position) {
        SubStyle subStyle = (SubStyle) mItems.get(position);
        vh.bind(subStyle, mOnItemClickListener);
    }

    private void configRecentlyStationsViewHolder(RecentlyStationHolder vh, int position) {
        RecentStation station = (RecentStation) mItems.get(position);
        vh.bind(station, mOnItemClickListener);
    }

    private void configFavoritesStationsViewHolder(FavoriteStationHolder vh, int position) {
        FavoriteStation station = (FavoriteStation) mItems.get(position);
        vh.bind(station, mOnItemClickListener);
    }


    @Override
    public int getItemViewType(int position) {
        if (mItems.get(position) instanceof Station) {
            return STATION;
        } else if (mItems.get(position) instanceof Country) {
            return COUNTRY;
        } else if (mItems.get(position) instanceof Style) {
            return STYLE;
        } else if (mItems.get(position) instanceof SubStyle) {
            return SUBSTYLE;
        } else if (mItems.get(position) instanceof RecentStation) {
            return RECENTLY;
        } else if (mItems.get(position) instanceof FavoriteStation) {
            return FAVORITES;
        }
        return -1;
    }

    public void addData(List<Object> data) {
        mItems.addAll(data);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(String id);
    }
}


