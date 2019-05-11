package com.elegion.radio.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.elegion.radio.AppDelegate;
import com.elegion.radio.MainActivity;
import com.elegion.radio.R;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;

import java.io.File;

import okhttp3.OkHttpClient;

public class AudioPlayerService extends Service {

    public static final String SERVICE = "SERVICE";

    private SimpleExoPlayer mExoPlayer;
    private ExtractorsFactory extractorsFactory;
    private DataSource.Factory dataSourceFactory;
    private String mStreamResources = "";

    private MediaSessionCompat mMediaSession;

    final MediaMetadataCompat.Builder mMediaMetadataBuilder = new MediaMetadataCompat.Builder();

    public void updateMetadata(String stationName) {
        mMediaMetadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, stationName);
        mMediaSession.setMetadata(mMediaMetadataBuilder.build());
    }

    final PlaybackStateCompat.Builder mActions = new PlaybackStateCompat.Builder()
            .setActions(PlaybackStateCompat.ACTION_PLAY
                    | PlaybackStateCompat.ACTION_PAUSE);


    @Override
    public void onCreate() {
        super.onCreate();
        initExoPlayer();
        initMediaSession();

    }

    private void initExoPlayer() {
        Log.d(SERVICE, "initExoPlayer");
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(this), new DefaultTrackSelector(), new DefaultLoadControl());
        mExoPlayer.addListener(exoPlayerListener);
        DataSource.Factory httpDataSourceFactory = new OkHttpDataSourceFactory(new OkHttpClient(), Util.getUserAgent(this, getString(R.string.app_name)), null);
        Cache cache = new SimpleCache(new File(this.getCacheDir().getAbsolutePath() + "/exoplayer"), new LeastRecentlyUsedCacheEvictor(1024 * 1024 * 100)); // 100 Mb max
        this.dataSourceFactory = new CacheDataSourceFactory(cache, httpDataSourceFactory, CacheDataSource.FLAG_BLOCK_ON_CACHE | CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);
        this.extractorsFactory = new DefaultExtractorsFactory();

    }

    public void initMediaSession() {
        Log.d(SERVICE, "initMediaSession");

        mMediaSession = new MediaSessionCompat(this, "AudioPlayerService");

        // FLAG_HANDLES_MEDIA_BUTTONS - хотим получать события от аппаратных кнопок(например, гарнитуры)
        // FLAG_HANDLES_TRANSPORT_CONTROLS - хотим получать события от кнопок на окне блокировки
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        mMediaSession.setCallback(mediaSessionCallback);

        // activity, которую запустит система, если пользователь заинтересуется подробностями данной сессии
        Intent activityIntent = new Intent(AppDelegate.getInstance(), MainActivity.class);
        mMediaSession.setSessionActivity(PendingIntent.getActivity(AppDelegate.getInstance(), 0, activityIntent, 0));

    }

    private MediaSessionCompat.Callback mediaSessionCallback = new MediaSessionCompat.Callback() {

        private Uri currentUri;
        int currentState = PlaybackStateCompat.STATE_STOPPED;

        @Override
        public void onPlay() {
            Log.d(SERVICE, "play");

            if (!mExoPlayer.getPlayWhenReady()) {

                Log.d(SERVICE, "play  - start service");
                startService(new Intent(getApplicationContext(), AudioPlayerService.class));

                Log.d(SERVICE, "updateMetadata");
                updateMetadata("Radio station name");

                Log.d(SERVICE, "onPlay - setActive(true)");
                mMediaSession.setActive(true);

                Log.d(SERVICE, "onPlay - cообщаем новое состояние");
                mMediaSession.setPlaybackState(mActions.setState(PlaybackStateCompat.STATE_PLAYING,
                        PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1).build());

                Log.d(SERVICE, "onPlay - загружаем URL в ExoPlayer");
                prepareToPlay(Uri.parse(mStreamResources));

                Log.d(SERVICE, "onPlay - запускаем проигрывание");
                mExoPlayer.setPlayWhenReady(true);
            }

        }

        @Override
        public void onPause() {
            Log.d(SERVICE, "pause");

            if (mExoPlayer.getPlayWhenReady()) {
                mExoPlayer.setPlayWhenReady(false);
            }

            Log.d(SERVICE, "onPause - setActive(false)");
            mMediaSession.setActive(false);

            Log.d(SERVICE, "onPause - cообщаем новое состояние");
            mMediaSession.setPlaybackState(mActions.setState(PlaybackStateCompat.STATE_PAUSED, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1).build());
            currentState = PlaybackStateCompat.STATE_PAUSED;

        }


        private void prepareToPlay(Uri uri) {
            if (!uri.equals(currentUri)) {
                currentUri = uri;
                ExtractorMediaSource mediaSource = new ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, null, null);
                mExoPlayer.prepare(mediaSource);
            }
        }


    };

    private ExoPlayer.EventListener exoPlayerListener = new ExoPlayer.EventListener() {
        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {
        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
        }

        @Override
        public void onPositionDiscontinuity() {
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
        }
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new AudioPlayerBinder();
    }

    public class AudioPlayerBinder extends Binder {
        public MediaSessionCompat.Token getMediaSessionToken() {
            Log.d(SERVICE, "Возвращаем токен сессии: " + mMediaSession.getSessionToken().toString());
            return mMediaSession.getSessionToken();
        }

        public void setStreamResources(String streamResources) {
            mStreamResources = streamResources;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(SERVICE, "onDestroy: ");
        mMediaSession.release();
    }
}
